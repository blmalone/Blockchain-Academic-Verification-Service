package com.unilog.blockchain.client;

import com.typesafe.config.ConfigFactory;
import com.unilog.blockchain.service.FileReaderService;
import com.unilog.blockchain.service.FileReaderServiceImpl;
import com.unilog.blockchain.utils.SolidityCompilerService;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.program.ProgramResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.bouncycastle.util.encoders.Hex.decode;
import static org.bouncycastle.util.encoders.Hex.toHexString;

@Service
public class Client extends NodeConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static final int GAS_LIMIT = 3_000_000;
    private static final long TRANSACTION_VALUE = 2100000000000000000L;
    private final int TIME_TO_WAIT = 1000;
    private static final int ETHER_TO_TRANSFER_IN_WEI = 0;

    @Value("${blockchain.key.private}")
    private String privateKey;

    private final String ETHER_WEI = "1000000000000000000";

    private static final String CLIENT_CONFIG = "client.conf";

    private FileReaderService fileReaderService = new FileReaderServiceImpl();

    private SolidityCompilerService solidityCompilerService = new SolidityCompilerService();

    private ClientListenerAdapter clientListenerAdapter;

    @Override
    public void start() {
        setEthereum(EthereumFactory.createEthereum(Client.class));
        clientListenerAdapter = new ClientListenerAdapter(this);
        getEthereum().addListener(clientListenerAdapter);
        establishConnection();
    }

    /**
     * Helps us override the default system properties for the ethereum instance.
     */
    @Bean
    public SystemProperties systemProperties() throws IOException {
        SystemProperties props = new SystemProperties();
        props.overrideParams(ConfigFactory.parseString(
                fileReaderService.readConfigFile(CLIENT_CONFIG).replaceAll("'", "\"")));
        setSystemProperties(props);
        return props;
    }

    public Map<String, String> sendFundsToWallet(final String walletAddress) throws InterruptedException {
        LOGGER.info("Sending funds to users wallet {}", walletAddress);
        final ECKey senderKey = ECKey.fromPrivate(decode(privateKey));

        Transaction fundsTransferTransaction =
                sendTransaction(walletAddress, senderKey, TRANSACTION_VALUE, new byte[0]);
        if (fundsTransferTransaction != null) {
            Map<String, String> transactionsData = new HashMap<>();
            transactionsData.put("fundsTransactionHash", toHexString(fundsTransferTransaction.getHash()));
            return transactionsData;
        } else {
            return null;
        }
    }

    public String sendContractToNetwork(final boolean userContract) throws IOException, InterruptedException {
        LOGGER.info("Compiling contract...");
        final ECKey senderKey = ECKey.fromPrivate(decode(privateKey));
        CompilationResult.ContractMetadata contractBinary = null;
        if (userContract) {
            contractBinary = solidityCompilerService.compileUserContract();
        } else {
            contractBinary = solidityCompilerService.compileRegistryContract();
        }
        LOGGER.info("Preparing transaction to send contract to the network.");
        Transaction contractTransaction =
                sendTransaction(null, senderKey, ETHER_TO_TRANSFER_IN_WEI, decode(contractBinary.bin));
        if (contractTransaction != null) {
            return toHexString(contractTransaction.getContractAddress());
        } else {
            return null;
        }
    }

    public String writeToRegistryContract(final String functionName,
                                          final String contractAddress, final Object... args) throws IOException, InterruptedException {
        return writeToContract(functionName, contractAddress, false, args);
    }

    public String writeToUserContract(final String functionName,
                                      final String contractAddress, final Object... args) throws IOException, InterruptedException {
        return writeToContract(functionName, contractAddress, true, args);
    }

    private String writeToContract(final String functionName, final String contractAddress,
                                   final boolean isUserContract, final Object... args)
            throws IOException, InterruptedException {
        final ECKey senderKey = ECKey.fromPrivate(decode(privateKey));
        CompilationResult.ContractMetadata contractBinary = null;
        if (isUserContract) {
            contractBinary = solidityCompilerService.compileUserContract();
        } else {
            contractBinary = solidityCompilerService.compileRegistryContract();
        }
        CallTransaction.Contract compiledContract = new CallTransaction.Contract(contractBinary.abi);

        CallTransaction.Function function = compiledContract.getByName(functionName);
        byte[] functionCallBytes = function.encode(args);

        Transaction transaction =
                sendTransaction(contractAddress, senderKey, ETHER_TO_TRANSFER_IN_WEI, functionCallBytes);

        return toHexString(transaction.getHash());
    }

    public Object[] readContract(final String functionName,
                                 final String contractAddress, final Object... args) throws IOException {
        CompilationResult.ContractMetadata contractBinary = solidityCompilerService.compileUserContract();
        CallTransaction.Contract compiledContract = new CallTransaction.Contract(contractBinary.abi);

        CallTransaction.Function function = compiledContract.getByName(functionName);

        ProgramResult result;
        if (args != null) {
            result = getEthereum().callConstantFunction(contractAddress, function, args);
        } else {
            result = getEthereum().callConstantFunction(contractAddress, function);
        }
        Object[] functionResult = function.decodeResult(result.getHReturn());
        return functionResult;
    }


    private Transaction getTransaction(final String recipientWalletAddress, final long value,
                                       final byte[] data, final BigInteger nonce) {
        return new Transaction(
                ByteUtil.bigIntegerToBytes(nonce),
                ByteUtil.longToBytesNoLeadZeroes(getEthereum().getGasPrice()),
                ByteUtil.longToBytesNoLeadZeroes(GAS_LIMIT),
                recipientWalletAddress == null ? new byte[0] : decode(recipientWalletAddress),
                ByteUtil.longToBytesNoLeadZeroes(value),
                data,
                getEthereum().getChainIdForNextBlock());
    }


    private Transaction sendTransaction(final String recipientWalletAddress, final ECKey senderKey, final long value,
                                        final byte[] data)
            throws InterruptedException {
        BigInteger nonce = getEthereum().getRepository().getNonce(senderKey.getAddress());
        LOGGER.info("Sender's Address {}", toHexString(senderKey.getAddress()));
        //Creating the transaction to send.
        Transaction transaction = getTransaction(recipientWalletAddress, value, data, nonce);
        //Sign the transaction by the sender.
        transaction.sign(senderKey);

        String transactionHash = toHexString(transaction.getHash());
        LOGGER.info("Transaction created : {}", transactionHash);
        LOGGER.info("Sending transaction to the network ==> {}", transaction);
        getEthereum().submitTransaction(transaction);
        waitForTransactionToBeMined(transactionHash);
        return transaction;
    }

    protected void onSyncDone() {
        LOGGER.info("onSyncDone() called.");
        setSynced(true);
    }

    /**
     * Temporary fix so that we know when a transaction has been included into a block.
     * Helps us avoid invoking a function on the contract when it hasn't yet been published or
     * obtaining a nonce error.
     *
     * @throws InterruptedException
     */
    private void waitForTransactionToBeMined(final String transactionHash) throws InterruptedException {
        while (!hasTransactionHasBeenMined(transactionHash)) {
            Thread.sleep(TIME_TO_WAIT);
        }
    }

    public boolean hasTransactionHasBeenMined(final String transactionHash)
            throws InterruptedException {
        return clientListenerAdapter.hasTransactionBeenMined(transactionHash);
    }

    public String getBalance() {
        final ECKey senderKey = ECKey.fromPrivate(decode(privateKey));
        return getEthereum().getRepository().getBalance(senderKey.getAddress())
                .divide(new BigInteger(ETHER_WEI)).toString();
    }
}

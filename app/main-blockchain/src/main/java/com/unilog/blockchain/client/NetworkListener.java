package com.unilog.blockchain.client;

import com.typesafe.config.ConfigFactory;
import com.unilog.blockchain.service.FileReaderService;
import com.unilog.blockchain.service.FileReaderServiceImpl;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.facade.EthereumFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bouncycastle.util.encoders.Hex.decode;

@Service
public class NetworkListener extends NodeConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkListener.class);

    private Map<ByteArrayWrapper, TransactionReceipt> receivedTransactions
            = Collections.synchronizedMap(new HashMap<>());

    private FileReaderService fileReaderService = new FileReaderServiceImpl();

    private final String NETWORK_LISTENER_CONFIG = "networkListener.conf";

    @Override
    public void start() {
        setEthereum(EthereumFactory.createEthereum(NetworkListener.class));
        getEthereum().addListener(new NetworkListenerAdapter(this));
        establishConnection();
    }

    public synchronized boolean checkIfTransactionHasBeenMined(final String transactionHash)
            throws InterruptedException {
        LOGGER.info("Checking whether transaction has been mined for {}", transactionHash);
        ByteArrayWrapper key = new ByteArrayWrapper(decode(transactionHash));
        if (receivedTransactions.get(key) != null) {
            LOGGER.info("Transaction was successfully mined, now removing transaction.");
            receivedTransactions.remove(key);
            return true;
        }
        LOGGER.info("Transaction has not yet been mined.");
        return false;
    }

    protected void onBlock(final Block block, final List<TransactionReceipt> receipts) {
        LOGGER.info("Listener waiting for incoming blocks on the network...");
        LOGGER.info("Number of transactions in block: {}", receipts.size());
        LOGGER.info("Block number: {}", block.getNumber());
        for (TransactionReceipt receipt : receipts) {
            LOGGER.info("Transactions have been found in block...");
            ByteArrayWrapper txHashW = new ByteArrayWrapper(receipt.getTransaction().getHash());
            receivedTransactions.put(txHashW, receipt);
        }
    }
    /**
     * Helps me override the default system properties for the ethereum instance.
     */
    @Bean
    public SystemProperties systemProperties() throws IOException {
        SystemProperties props = new SystemProperties();
        props.overrideParams(ConfigFactory.parseString(
                fileReaderService.readConfigFile(NETWORK_LISTENER_CONFIG).replaceAll("'", "\"")));
        return props;
    }

}


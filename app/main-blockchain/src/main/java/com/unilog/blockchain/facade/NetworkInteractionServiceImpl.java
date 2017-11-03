package com.unilog.blockchain.facade;

import com.unilog.blockchain.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static org.bouncycastle.util.encoders.Hex.decode;

@Component
public class NetworkInteractionServiceImpl implements NetworkInteractionService {

    @Autowired
    private Client client;

    private final int VALID_FOR_10_YEARS = 520;

    @Override
    public Map<String, String> addFundsToUserWallet(final String walletAddress) throws InterruptedException {
        return client.sendFundsToWallet(walletAddress);
    }

    @Override
    public String publishContractToNetwork(final boolean isUserContract) throws IOException, InterruptedException {
        return client.sendContractToNetwork(isUserContract);
    }

    @Override
    public void addUserContractToRegistry(final String contractAddress, final String userContractAddress)
            throws IOException, InterruptedException {
        client.writeToRegistryContract("addContract", contractAddress, userContractAddress);
    }

    @Override
    public void addTranscriptToUsersContract(final String contractAddress, final String contentAddress)
            throws IOException, InterruptedException {
        client.writeToUserContract("addArtifact", contractAddress, contentAddress, VALID_FOR_10_YEARS);
    }

    @Override
    public void assignContractArtifactOwner(final String contractAddress, final String accountAddress)
            throws IOException, InterruptedException {
        client.writeToUserContract("assignArtifactOwner", contractAddress, accountAddress);
    }

    @Override
    public String getBalance() {
        return client.getBalance();
    }

    @Override
    public String addQualificationToContract(final String walletSignature,
                                                          final String contractAddress,
                                                          final String qualificationHash, final String transcriptHash)
            throws IOException, InterruptedException {
        return client.writeToUserContract("register",
                walletSignature, contractAddress, decode(qualificationHash), decode(transcriptHash));

    }

    @Override
    public String verifyQualificationTranscript(final String contractAddress,
                                                final String qualificationHash, final String transcriptHash)
            throws IOException {
        Object[] result = client.readContract(
                "lookup", contractAddress, decode(qualificationHash), decode(transcriptHash));
        return Boolean.toString((boolean) result[0]);
    }
}

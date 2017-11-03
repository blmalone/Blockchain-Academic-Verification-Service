package com.unilog.blockchain.facade;

import java.io.IOException;
import java.util.Map;

public interface NetworkInteractionService {

    Map<String, String> addFundsToUserWallet(final String walletAddress) throws InterruptedException;

    /**
     * Sends a contract to the block chain from Unilog's default account.
     * @param isUserContract decides on what type of contract is published to the block chain.
     *                       If true then user contract. If false then registry contract.
     * @return - The contract address
     */
    String publishContractToNetwork(final boolean isUserContract) throws IOException, InterruptedException;

    /**
     * Add user contract to the registry so that DApp clients can verify that a token contract address is valid.
     * @param contractAddress - registry contract address
     * @param userContractAddress - users contract address
     */
    void addUserContractToRegistry(final String contractAddress, final String userContractAddress)
            throws IOException, InterruptedException;

    String addQualificationToContract(final String walletSignature,
                                                   final String contractAddress,
                                                   final String qualificationHash,
                                                   final String transcriptHash)
            throws IOException, InterruptedException;

    String verifyQualificationTranscript(final String contractAddress,
                                         final String qualificationHash,
                                         final String transcriptHash)
            throws IOException;

    /**
     * Add the content address from the distributed file system to the users smart contract.
     * @param contractAddress - users smart contract
     * @param contentAddress - distributed file system content identifier
     *                       Note we will want to get the artifact ID returned for subsequent calls to this function.
     *                       Implement via polling mechanism.
     */
    void addTranscriptToUsersContract(final String contractAddress, final String contentAddress)
            throws IOException, InterruptedException;

    /**
     * Sets the owner of a contract to the transcript owner. This gives the owner more functionality and control over
     * their data.
     * @param contractAddress - smart contract in question
     * @param accountAddress - new owner of contract
     */
    void assignContractArtifactOwner(final String contractAddress, final String accountAddress)
            throws IOException, InterruptedException;

    /**
     * Returns the balance of Unilog account.
     * @return Balance of Unilog account for display on frontend.
     */
    String getBalance();
}

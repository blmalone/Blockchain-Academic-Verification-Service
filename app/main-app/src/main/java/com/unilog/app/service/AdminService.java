package com.unilog.app.service;

import com.unilog.app.representation.AdminWindow;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface AdminService {

    /**
     * Ensures that the Admin Portal has the most recent data.
     * @return - Data for the admin to act upon
     */
    AdminWindow generateAdminWindow();

    /**
     * Completes the final steps of setting up a users account i.e. publishing transcript. These include:
     * - Marking account as completed
     * - Create Unilog contract for user - obtain contract address
     * - Publish User transcript to network - obtain content address
     * - Add content address to users contract - obtain artifact ID
     * - Make sure owner of contract the users Ethereum account address
     * - Make Token
     * - Email token to user (Contract Address, artifact Id)
     * @return - result of completing an account
     */
    boolean completeAccount(final String id) throws IOException, InterruptedException;

    /**
     * This function provides the same functionality as completeAccount() only it
     * does this for all incomplete user profiles.
     * @return - result of completing an account
     */
    String completeAllAccounts() throws IOException, InterruptedException;

    /**
     * Publish the registry contract to the block chain. This should only ever happen once.
     * @return -  result of publication, contract address if successful.
     */
    String publishRegistry();

    /**
     * Creates an Institution user in the DB. Sends code to the institution and also adds the user to the in memory
     * authentication. It is up to the institution to login after this point.
     * Hashes the users password with a random salt so it's stored securely.
     * @param request - contains the email to register
     * @return result of registering an account
     */
    boolean registerAccount(AdminWindow request);
}

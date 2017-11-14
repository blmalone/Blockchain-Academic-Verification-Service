package com.unilog.app.service;

import com.google.gson.Gson;
import com.unilog.app.entity.Admin;
import com.unilog.app.entity.Institution;
import com.unilog.app.entity.Transcript;
import com.unilog.app.entity.User;
import com.unilog.app.representation.AdminWindow;
import com.unilog.app.security.SecureSessionCode;
import com.unilog.app.utils.ValidatorUtils;
import com.unilog.blockchain.facade.NetworkInteractionService;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private NetworkInteractionService networkInteractionService;

    @Autowired
    private DistributedFileSystem distributedFileSystem;

    @Autowired
    private Gson gson;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private MailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String SUBJECT = "Unilog - Activate your account";

    private final String BODY = "Congratulations, your request for a Unilog account has been accepted.\n"
            + "Please activate your account with the following code: \n\n";


    @PostConstruct
    public void addAdminToDatabase() {
        Admin admin = new Admin();
        databaseService.saveAdmin(admin);
        LOGGER.info("Admin added to the Database.");
    }

    @Override
    public AdminWindow generateAdminWindow() {
        AdminWindow adminWindow = new AdminWindow();
        Admin admin = databaseService.findAdmin();
        if (null != admin) {
            adminWindow.setRegistryPublished(admin.isRegistryPublished());
            String balance = networkInteractionService.getBalance();
            if (balance == null) {
                adminWindow.setAccountBalance("Cannot retrieve balance at this time.");
            } else {
                adminWindow.setAccountBalance(balance + " Ether");
            }
            adminWindow.setUsers(databaseService.findUsersWithNonPublishedTranscripts());
            adminWindow.setRegistryContractAddress(admin.getRegistryContractAddress());
        }
        return adminWindow;
    }

    @Override
    public String completeAllAccounts() throws IOException, InterruptedException {
        List<User> usersWithNonPublishedTranscripts = databaseService.findUsersWithNonPublishedTranscripts();
        if (usersWithNonPublishedTranscripts.size() != 0) {
            List<String> userIDs = getIDs(usersWithNonPublishedTranscripts);
            for (int i = 0; i < userIDs.size(); i++) {
                boolean completed = completeSingleAccount(userIDs.get(i));
                if (!completed) {
                    if (i > 0) {
                        return "partialSuccess";
                    } else {
                        return "zeroSuccess";
                    }
                }
            }
            return "allSuccess";
        }
        return "zeroSuccess";
    }

    private ArrayList<String> getIDs(final List<User> usersWithNonPublishedTranscripts) {
        ArrayList<String> result = new ArrayList<>();
        for (User user : usersWithNonPublishedTranscripts) {
            result.add(String.valueOf(user.getId()));
        }
        return result;
    }

    @Override
    public boolean completeAccount(final String id) throws IOException, InterruptedException {
        return completeSingleAccount(id);
    }

    private boolean completeSingleAccount(final String id) throws IOException, InterruptedException {
        Admin admin = databaseService.findAdmin();
        if (validateId(id) && admin.isRegistryPublished()) {
            LOGGER.info("Valid ID Entered: {}", id);
            List<User> possibleUsers = databaseService.findUsersWithNonPublishedTranscripts();
            for (User user : possibleUsers) {
                if (user.getId() == new Long(id) && user.isCompletedRegistration()) {
                    LOGGER.info("Activation code matches");
                    com.unilog.app.entity.Token token = new com.unilog.app.entity.Token();
                    user.setPublishedTranscript(true);

                    String contractAddress = networkInteractionService.publishContractToNetwork(true);
                    token.setContractAddress(contractAddress);
                    LOGGER.info("Contract address: {}", token.getContractAddress());
                    boolean emailSent = emailService.sendTokenMail(
                            new String[]{user.getEmailAddress()}, contractAddress, "0",
                            admin.getRegistryContractAddress());
                    if (emailSent) {
                        LOGGER.info("User contract published to the network.");
                        LOGGER.info("Email sent to User!");

                        Transcript transcript =
                                databaseService.findTranscriptByRecipientEmailAddress(user.getEmailAddress());
                        String jsonTranscript = prepareTranscript(transcript);
                        String contentAddress = distributedFileSystem.publishFileToNetwork(jsonTranscript);
                        LOGGER.info("Transcript published to IPFS distributed filesystem.");

                        //TODO  I know that the artifact ID will be 0. Hard code to reduce txs sent to network.
                        //TODO Should add functionality for unilog to add more artifacts for a user.
                        networkInteractionService.addTranscriptToUsersContract(contractAddress, contentAddress);
                        token.setArtifactId(0);
                        LOGGER.info("IPFS content address added to User's contract");

                        networkInteractionService.assignContractArtifactOwner(contractAddress,
                                user.getAccountAddress());
                        LOGGER.info("Owner added to the User's contract");

                        networkInteractionService.addUserContractToRegistry(admin.getRegistryContractAddress(),
                                contractAddress);
                        LOGGER.info("User's contract added to Registry");

                        user.setToken(token);
                        databaseService.saveUser(user);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String publishRegistry() {
        try {
            String registryAddress = networkInteractionService.publishContractToNetwork(false);
            Admin admin = databaseService.findAdmin();
            if (null != admin) {
                admin.setRegistryContractAddress(registryAddress);
                admin.setRegistryPublished(true);
                databaseService.saveAdmin(admin);
            }
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    @Override
    public boolean registerAccount(final AdminWindow request) {
        if (!ValidatorUtils.validEmailPatterns(request.getNewAccountEmail())) {
            return false;
        }
        Institution institution = new Institution();
        institution.setEmailAddress(request.getNewAccountEmail());
        institution.setRegistrationCode(SecureSessionCode.generateCode());
        boolean isEmailSent = emailService.sendSetupMail(
                new String[]{request.getNewAccountEmail()}, institution.getRegistrationCode(), SUBJECT, BODY);
        if (isEmailSent) {
            databaseService.saveInstitution(institution);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Formatting transcript data before publishing to IPFS
     *
     * @param transcript - transcript that will be published
     * @return JSON representation of the transcript as the data will be presented in a Javascript environment. i.e.
     * A Decentralised application
     */
    private String prepareTranscript(final Transcript transcript) {
        com.unilog.app.representation.Transcript transcriptToPublish
                = dozerBeanMapper.map(transcript, com.unilog.app.representation.Transcript.class);
        return gson.toJson(transcriptToPublish);
    }

    private boolean validateId(final String id) {
        try {
            int i = Integer.parseInt(id);
            return true;
        } catch (NumberFormatException er) {
            return false;
        }
    }
}

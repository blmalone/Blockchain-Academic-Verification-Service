package com.unilog.app.service;

import com.unilog.app.representation.Qualification;
import com.unilog.app.representation.RegistrationRequest;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public interface UserService {

    /**
     * A user, typically an issuer, is able to upload qualifications.
     * The qualifications are saved to the DB. Not yet published to the network
     * @param qualification - The qualification along with details of transcripts
     * @return -  Status of upload
     * @throws UnsupportedEncodingException
     */
    String uploadQualification(final Qualification qualification)
            throws UnsupportedEncodingException;

    /**
     * Allows for a transcript owner to complete their registration after they received their setup email.
     * @param request - Information to check if the request is valid. i.e. code, email address, new account address.
     * @return - Result of registration event. False is codes or email addresses don't match what is in the server.
     */
    boolean completeRegistration(final RegistrationRequest request);

    /**
     * Returns all the qualifications that a user has uploaded.
     * It will return both published and unpublished qualifications.
     * @param email - the issuer of the qualifications
     * @return - List of qualifications and their associated transcripts.
     */
    List<com.unilog.app.representation.Qualification> getQualifications(final String email);

    /**
     * Returns result of checking whether a transcript owner/user has already registered.
     * @param recipientEmailAddress - address to check
     * @return result of check
     */
    boolean isRegistered(final String recipientEmailAddress);

    /**
     * Sends a register request to admin of Unilog. If approved Admin will send email to institutionEmailAddress with
     * code. The institution can then register successfully with a code.
     * @param institutionEmailAddress - Email address
     * @param message - message which includes information for admin to verify e.g. telephone number.
     */
    boolean registrationApplication(final String institutionEmailAddress, final String message);

    /**
     * After an admin has accepted an application a code will be sent to the institutions email address.
     * For an institution to complete their registration, they will be asked to enter the code that was sent.
     * They'll also choose a password at this stage to be stored in the database.
     * @param request - contains email and code to be checked against value stored in DB by Admin.
     * @return - result of completed registration
     */
    boolean completeInstitutionRegistration(final RegistrationRequest request);

    /**
     * Generates all required data for the registration view.
     * @return Request object
     */
    RegistrationRequest generateRegistrationRequest(boolean isRegistryCreated);
}

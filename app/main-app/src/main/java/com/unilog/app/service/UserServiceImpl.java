package com.unilog.app.service;

import com.unilog.app.entity.Admin;
import com.unilog.app.entity.Institution;
import com.unilog.app.entity.Qualification;
import com.unilog.app.entity.User;
import com.unilog.app.representation.RegistrationRequest;
import com.unilog.app.security.SecureSessionCode;
import com.unilog.app.utils.ValidatorUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private MailService emailService;

    @Override
    public String uploadQualification(final com.unilog.app.representation.Qualification qualification)
            throws UnsupportedEncodingException {
        if (!checkQualificationIsComplete(qualification)) {
            return "qualificationError";
        }
        com.unilog.app.representation.Qualification updatedQualification = fillTranscripts(qualification);
        Qualification newQualification
                = dozerBeanMapper.map(updatedQualification, Qualification.class);
        if (!ValidatorUtils.validEmailPatterns(newQualification)) {
            return "emailError";
        }
        if (!createUsers(qualification)) {
            return "emailError";
        } else {
            databaseService.saveQualification(newQualification);
            return "success";
        }
    }

    @Override
    public boolean isRegistered(final String recipientEmailAddress) {
        User transcriptOwner = databaseService.findByEmailAddress(recipientEmailAddress);
        if (null != transcriptOwner && transcriptOwner.isCompletedRegistration()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean registrationApplication(final String institutionEmailAddress, final String message) {
        //Send email to Unilog containing information from application.
        if (ValidatorUtils.validEmailPatterns(institutionEmailAddress)) {
            return emailService.sendToAdmin(institutionEmailAddress, message);
        }
        return false;
    }

    @Override
    public boolean completeInstitutionRegistration(final RegistrationRequest request) {
        Institution institution = databaseService.findByInstitutionEmailAddress(
                request.getActivationInstitutionEmailAddress());
        if (!ValidatorUtils.validEmailPatterns(request.getActivationInstitutionEmailAddress())
                || request.getInstitutionCode().equals("") || null == institution) {
            return false;
        }
        if (!institution.isCompletedRegistration()) {
            if (institution.getRegistrationCode().equals(request.getInstitutionCode())) {
                institution.setCompletedRegistration(true);
                boolean emailSent = emailService.sendLoginDetailsMail(
                        new String[]{institution.getEmailAddress()},
                        institution.getEmailAddress(), institution.getPassword());
                if (emailSent) {
                    databaseService.saveInstitution(institution);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public RegistrationRequest generateRegistrationRequest(final boolean isRegistryCreated) {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        if (!isRegistryCreated) {
            Admin admin = databaseService.findAdmin();
            if (null != admin.getRegistryContractAddress()) {
                registrationRequest.setUnilogRegistryContractAddress(admin.getRegistryContractAddress());
                registrationRequest.setRegistryCreated(true);
            }
        }
        return registrationRequest;
    }

    @Override
    public List<com.unilog.app.representation.Qualification> getQualifications(final String email) {
        User user = databaseService.findByEmailAddress(email);
        List<com.unilog.app.representation.Qualification> qualifications = new ArrayList<>();
        if (user != null) {
            List<Qualification> qualificationEntities = databaseService.findAllQualifications(user);
            for (Qualification qualification : qualificationEntities) {
                com.unilog.app.representation.Qualification newQualification
                        = dozerBeanMapper.map(qualification, com.unilog.app.representation.Qualification.class);
                qualifications.add(newQualification);
            }
        }
        return qualifications;
    }

    @Override
    public boolean completeRegistration(final RegistrationRequest request) {
        User transcriptOwner = databaseService.findByEmailAddress(request.getRecipientEmailAddress());
        if (!checkRegistrationRequest(request)) {
            return false;
        }
        if (transcriptOwner != null) {
            if (transcriptOwner.getRegistrationCode().equals(request.getCode())) {
                transcriptOwner.setAccountAddress(request.getRecipientAccountAddress());
                transcriptOwner.setCompletedRegistration(true);
                databaseService.saveUser(transcriptOwner);
                LOGGER.info("Account completed, user details updated: {}", transcriptOwner.getEmailAddress());
                return true;
            }
        }
        return false;
    }

    private boolean checkQualificationIsComplete(final com.unilog.app.representation.Qualification qualification) {
        if (qualification.getQualificationCode().equals("")
                || qualification.getAccreditedInstitution().equals("")
                || checkIfTranscriptsAreInComplete(qualification.getTranscripts())) {
            return false;
        }
        return true;
    }

    private boolean checkIfTranscriptsAreInComplete(final List<com.unilog.app.representation.Transcript> transcripts) {
        for (com.unilog.app.representation.Transcript transcript : transcripts) {
            if (transcript.getClassification() == 0.0
                    || transcript.getRecipientEmailAddress().equals("")
                    || transcript.getTranscriptReference().equals("")) {
                return true;
            }
        }
        return false;
    }

    private com.unilog.app.representation.Qualification fillTranscripts(
            final com.unilog.app.representation.Qualification qualification) {
        String qualificationCode = qualification.getQualificationCode();
        String accreditedInstitution = qualification.getAccreditedInstitution();
        String qualificationTitle = qualification.getQualificationTitle();
        for (int i = 0; i < qualification.getTranscripts().size(); i++) {
            qualification.getTranscripts().get(i).setQualificationCode(qualificationCode);
            qualification.getTranscripts().get(i).setAccreditedInstitution(accreditedInstitution);
            qualification.getTranscripts().get(i).setQualificationTitle(qualificationTitle);
        }
        return qualification;
    }

    private boolean createUsers(final com.unilog.app.representation.Qualification qualification) {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < qualification.getTranscripts().size(); i++) {
            String code = SecureSessionCode.generateCode();
            users.add(new User(qualification.getTranscripts().get(i).getRecipientEmailAddress(), null, code));
            User user = users.get(i);
            String userEmail = user.getEmailAddress();
            LOGGER.info("Sending setup email to: {}", userEmail);
            boolean emailSent = emailService.sendSetupMail(new String[]{userEmail}, code, null, null);
            if (emailSent) {
                LOGGER.info("Setup email sent, now saving the user: {}", userEmail);
                databaseService.saveUser(user);
            } else {
                LOGGER.info("Setup email not sent, user will not be saved: {}", userEmail);
                return false;
            }
        }
        return true;
    }

    private boolean checkRegistrationRequest(final RegistrationRequest request) {
        if (request.getRecipientAccountAddress().equals("")
                || request.getCode().equals("")
                || !ValidatorUtils.validEmailPatterns(request.getRecipientEmailAddress())) {
            return false;
        }
        return true;
    }
}

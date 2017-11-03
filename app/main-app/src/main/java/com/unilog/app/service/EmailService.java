package com.unilog.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Using Gmail SMTP Server to send emails from a custom made Unilog gmail account.
 */
@Service
public class EmailService implements MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private static Properties properties;

    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    @Value("${email.host}")
    private String host;

    private boolean setup = false;

    private final String SETUP_SUBJECT = "Unilog - Complete Setup!";
    private String setupBody = "An accredited institution uploaded a transcript associated with you.\n"
            + "If you already have an Ethereum Account then you're ready to finish registration!\n\n"
            + "Otherwise, create an account and come back to us to complete your registration!\n\n"
            + "Bring the following activation code with you when you're completing registration:   \n\n";

    private final String TOKEN_SUBJECT = "Unilog - Your token is ready!";
    private String tokenBody = "We have successfully generated your Unilog token.\n\n"
            + "Contract Address: ";

    private final String APPLICATION_SUBJECT = "Unilog - Application Review";
    private String applicationBody = "Admin, \nThe following party has asked to be registered in the "
            + "Unilog application.\nTake care when investigating the legitimacy of their request. Bel"
            + "ow are the details of their application: \n\n";

    private final String DETAILS_SUBJECT = "Unilog - Your Login Details";
    private String detailsBody = "Below are your login details for Unilog. "
            + "\nMake sure to enjoy our features available to you!\n"
            + "Using block chain technology we can reduce the work load of your staff by"
            + " handling the academic verification of transcripts!\n\n";

    private String RECIPIENT = "bmalone05@qub.ac.uk";

    public EmailService() {
    }

    private void setupProperties() {
        if (!setup) {
            properties = System.getProperties();
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.user", username);
            properties.put("mail.smtp.password", password);
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
        }
    }

    @Override
    public boolean sendToAdmin(final String institutionEmailAddress, final String applicationMessage) {
        String[] to = new String[]{"yourunilog@gmail.com"};
        setupProperties();
        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
            message.setSubject(APPLICATION_SUBJECT);
            message.setText(applicationBody + "Email: " + institutionEmailAddress + "\nMessage: " + applicationMessage);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            LOGGER.info("Email sent to Admin");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sendSetupMail(final String[] to, final String code, final String subject, final String body) {
        setupProperties();
        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
            if (subject == null || body == null) {
                message.setSubject(SETUP_SUBJECT);
                message.setText(setupBody + code);
            } else {
                message.setSubject(subject);
                message.setText(body + code);
            }

            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            LOGGER.info("Setup email has been sent.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sendTokenMail(final String[] to, final String contractAddress, final String artifactId,
                                 final String registryContract) {
        setupProperties();
        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
            message.setSubject(TOKEN_SUBJECT);
            message.setText(tokenBody + contractAddress + "\nArtifact ID: " + artifactId
                    + "\n\nBelow is the Unilog Registry. Add this into the verifiers portal if you need to"
                    + " verify a token: \n" + registryContract);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            LOGGER.info("Token email has been sent");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sendLoginDetailsMail(final String[] to, final String email, final String accountPassword) {
        setupProperties();
        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
            message.setSubject(DETAILS_SUBJECT);
            message.setText(detailsBody + "username: " + email + "\npassword: " + accountPassword);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            LOGGER.info("Login details email has been sent.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

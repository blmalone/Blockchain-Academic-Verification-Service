package com.unilog.app.service;


public interface MailService {

    boolean sendToAdmin(final String institutionEmailAddress, final String applicationMessage);
    boolean sendSetupMail(final String[] to, final String code, final String subject, final String body);
    boolean sendTokenMail(final String[] to, final String contractAddress, final String artifactId,
                          final String registryContract);
    boolean sendLoginDetailsMail(final String[] to, final String email, final String password);
}

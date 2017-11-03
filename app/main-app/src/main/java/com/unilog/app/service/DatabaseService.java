package com.unilog.app.service;

import com.unilog.app.entity.Admin;
import com.unilog.app.entity.Institution;
import com.unilog.app.entity.Qualification;
import com.unilog.app.entity.Transcript;
import com.unilog.app.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DatabaseService {

    User findById(final String id);

    List<User> findUsersWithNonPublishedTranscripts();

    User findByEmailAddress(final String email);

    Institution findByInstitutionId(final String id);

    Institution findByInstitutionEmailAddress(final String email);

    void saveInstitution(final Institution institution);

    void saveUser(final User user);

    void saveQualification(final Qualification qualification);

    List<Qualification> findAllQualifications(final User user);

    Qualification findQualificationById(final Long qualificationId);

    Transcript findTranscriptByRecipientEmailAddress(final String email);

    void saveAdmin(final Admin admin);

    Admin findAdmin();
}

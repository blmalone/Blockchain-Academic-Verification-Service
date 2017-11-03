package com.unilog.app.service;

import com.unilog.app.db.AdminDao;
import com.unilog.app.db.InstitutionDao;
import com.unilog.app.db.QualificationDao;
import com.unilog.app.db.TranscriptDao;
import com.unilog.app.db.UserDao;
import com.unilog.app.entity.Admin;
import com.unilog.app.entity.Institution;
import com.unilog.app.entity.Qualification;
import com.unilog.app.entity.Transcript;
import com.unilog.app.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private QualificationDao qualificationDao;

    @Autowired
    private TranscriptDao transcriptDao;

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private InstitutionDao institutionDao;

    @Override
    public User findById(final String id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> findUsersWithNonPublishedTranscripts() {
        return userDao.findByPublishedTranscript(false);
    }

    @Override
    public User findByEmailAddress(final String email) {
        return userDao.findByEmailAddress(email);
    }

    @Override
    public Institution findByInstitutionId(final String id) {
        return institutionDao.findById(id);
    }

    @Override
    public Institution findByInstitutionEmailAddress(final String email) {
        return institutionDao.findByEmailAddress(email);
    }

    @Override
    public void saveInstitution(final Institution institution) {
        LOGGER.info("Saving User to the database :{}", institution.getEmailAddress());
        institutionDao.save(institution);
    }

    @Override
    public void saveUser(final User user) {
        LOGGER.info("Saving User to the database :{}", user.getEmailAddress());
        userDao.save(user);
    }

    @Override
    public void saveQualification(final Qualification qualification) {
        LOGGER.info("Saving qualification to the database for :{} ", qualification.getIssuer());
        qualificationDao.save(qualification);
    }

    @Override
    public List<Qualification> findAllQualifications(final User user) {
        LOGGER.info("Looking for all qualifications associated with :{}", user.getEmailAddress());
        return qualificationDao.findQualificationByUser(user);
    }

    @Override
    public Qualification findQualificationById(final Long qualificationId) {
        LOGGER.info("Looking for qualification wit id :{}", qualificationId);
        return qualificationDao.findQualificationById(qualificationId);
    }

    @Override
    public Transcript findTranscriptByRecipientEmailAddress(final String email) {
        return transcriptDao.findTranscriptByRecipientEmailAddress(email);
    }

    @Override
    public void saveAdmin(final Admin admin) {
        adminDao.save(admin);
    }

    @Override
    public Admin findAdmin() {
        return adminDao.findById(1L);
    }

}

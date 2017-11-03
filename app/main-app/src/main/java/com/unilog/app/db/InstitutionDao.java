package com.unilog.app.db;

import com.unilog.app.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface InstitutionDao extends JpaRepository<Institution, Long> {

    Institution findById(final String id);

    Institution findByEmailAddress(final String emailAddress);

}

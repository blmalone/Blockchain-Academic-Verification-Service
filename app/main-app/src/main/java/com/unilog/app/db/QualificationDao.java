package com.unilog.app.db;

import com.unilog.app.entity.Qualification;
import com.unilog.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface QualificationDao extends JpaRepository<Qualification, Long> {

    List<Qualification> findQualificationByUser(final User user);

    Qualification findQualificationById(final Long id);

}

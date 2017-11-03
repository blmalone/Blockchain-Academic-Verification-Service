package com.unilog.app.db;

import com.unilog.app.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface AdminDao extends JpaRepository<Admin, Long> {

    Admin findById(long id);

}

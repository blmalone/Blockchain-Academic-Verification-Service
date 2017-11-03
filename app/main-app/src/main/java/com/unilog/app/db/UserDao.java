package com.unilog.app.db;

import com.unilog.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface UserDao extends JpaRepository<User, Long> {

    User findById(String id);

    User findByEmailAddress(String emailAddress);

    List<User> findByPublishedTranscript(boolean publishedTranscript);

}

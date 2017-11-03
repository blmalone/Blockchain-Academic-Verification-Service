package com.unilog.app.db;

import com.unilog.app.entity.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface TranscriptDao extends JpaRepository<Transcript, Long> {

    Transcript findTranscriptByRecipientEmailAddress(final String recipientEmailAddress);

}

package com.example.fileStorage.repository;

import com.example.fileStorage.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findByToContainingOrSubjectContainingOrBodyContaining(String to, String subject, String body);
}

package com.example.fileStorage.controller;

import com.example.fileStorage.model.Email;

import com.example.fileStorage.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/emails")
public class EmailRestController {

    private final EmailService emailService;

    @Autowired
    public EmailRestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<Email>> getAllEmails() {
        List<Email> emails = emailService.getAllEmails();
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Email> getEmailById(@PathVariable Long id) {
        Optional<Email> email = emailService.getEmailById(id);
        return email.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Email>> searchEmails(@RequestParam String query) {
        List<Email> emails = emailService.searchEmails(query);
        return ResponseEntity.ok(emails);
    }

    @PostMapping("/send")
    public ResponseEntity<Email> sendEmail(@RequestBody Map<String, String> emailRequest) {
        String to = emailRequest.get("to");
        String subject = emailRequest.get("subject");
        String body = emailRequest.get("body");
        String folder = emailRequest.get("folder"); // Сонголттой параметр

        // Заавал байх ёстой талбаруудыг шалгах
        if (to == null || subject == null) {
            return ResponseEntity.badRequest().build();
        }

        // Хэрэв body байхгүй бол хоосон утгаар орлуулах
        body = (body != null) ? body : "";

        // EmailService-ийн sendEmail функцийг дуудах
        Email sentEmail = emailService.sendEmail(to, subject, body, folder);
        return ResponseEntity.status(HttpStatus.CREATED).body(sentEmail);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        try {
            emailService.deleteEmail(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
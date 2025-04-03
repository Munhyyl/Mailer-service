package com.example.fileStorage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient")
    private String to;

    private String subject;

    @Lob  // Large Object аннотаци нэмж байна
    @Column(columnDefinition = "TEXT")  // TEXT төрөл тохируулж байна
    private String body;

    private LocalDateTime sentAt;

    public Email(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.sentAt = LocalDateTime.now();
    }
}
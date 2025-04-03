package com.example.fileStorage.service;

import com.example.fileStorage.model.Email;
import com.example.fileStorage.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;
    private final RestTemplate restTemplate;
    private final String fileManagerUrl; // File Manager Service-ийн URL, жишээ нь "http://localhost:8082"


    @Autowired
    public EmailService(JavaMailSender mailSender, EmailRepository emailRepository,
                        RestTemplate restTemplate, @Value("${file.manager.url}") String fileManagerUrl) {
        this.mailSender = mailSender;
        this.emailRepository = emailRepository;
        this.restTemplate = restTemplate;
        this.fileManagerUrl = fileManagerUrl;
    }

    public Email sendEmail(String to, String subject, String body, String folder) {
        try {
            String htmlBody;
            if (folder != null) {
                // File Manager Service-ээс зургуудыг татаж авах
                String apiUrl = fileManagerUrl + "/api/files/list?folder=" + folder;
                List<Map<String, String>> photoInfoList = restTemplate.getForObject(apiUrl, List.class);

                // HTML агуулга үүсгэх
                StringBuilder sb = new StringBuilder();
                sb.append("<h1>").append(subject).append("</h1>");
                sb.append("<div style='display: flex; flex-wrap: wrap; gap: 10px;'>");
                for (Map<String, String> photoInfo : photoInfoList) {
                    String imageUrl = photoInfo.get("url");
                    String key = photoInfo.get("key");
                    sb.append("<div style='margin: 10px;'>");
                    sb.append("<img src='").append(imageUrl).append("' style='max-width: 300px;' />");
                    sb.append("<p>").append(key.substring(key.lastIndexOf('/') + 1)).append("</p>");
                    sb.append("</div>");
                }
                sb.append("</div>");
                htmlBody = sb.toString();
            } else {
                htmlBody = body; // folder байхгүй бол ердийн body-г ашиглана
            }

            // Имэйл илгээх
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);

            // Имэйлийг баазад хадгалах
            Email email = new Email(to, subject, htmlBody);
            return emailRepository.save(email);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch photos or send email", e);
        }
    }

    // Файлын хэмжээг хүн унших хэлбэрт хөрвүүлэх
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    public List<Email> getAllEmails() {
        return emailRepository.findAll();
    }

    public List<Email> searchEmails(String query) {
        return emailRepository.findByToContainingOrSubjectContainingOrBodyContaining(query, query, query);
    }

    public Optional<Email> getEmailById(Long id) {
        return emailRepository.findById(id);
    }

    public void deleteEmail(Long id) {
        emailRepository.deleteById(id);
    }

    public Map<String, Object> getEmailStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmails", emailRepository.count());
        // You can add more statistics as needed
        return stats;
    }


}
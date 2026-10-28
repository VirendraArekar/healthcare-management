package com.hospital.notification.scheduler;

import com.hospital.common.service.*;
import com.hospital.common.util.*;
import com.hospital.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MailWorker {
  private final NotificationRepository repository;
  private final JavaMailSender mail;
  private final String recipient;

  public MailWorker(
      NotificationRepository repository,
      JavaMailSender mail,
      @Value("${MAIL_RECIPIENT:workflow@demo.hospital}") String recipient) {
    this.repository = repository;
    this.mail = mail;
    this.recipient = recipient;
  }

  @Scheduled(fixedDelay = 10000)
  @Transactional
  public void send() {
    for (var r : repository.lockPendingEmails()) {
      try {
        var message = new SimpleMailMessage();
        message.setFrom("no-reply@demo.hospital");
        message.setTo(r.get("recipient") == null ? recipient : r.get("recipient").toString());
        message.setSubject(r.get("subject").toString());
        message.setText(r.get("body").toString());
        mail.send(message);
        repository.markEmailSent(r.get("id"));
      } catch (Exception e) {
        repository.incrementEmailAttempts(r.get("id"));
      }
    }
  }
}

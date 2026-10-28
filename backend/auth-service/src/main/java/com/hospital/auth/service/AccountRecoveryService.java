package com.hospital.auth.service;

import com.hospital.auth.repository.AuthRepository;
import com.hospital.common.util.Validation;
import java.security.SecureRandom;
import java.util.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountRecoveryService {
  private final AuthRepository repository;
  private final JavaMailSender mail;

  public AccountRecoveryService(AuthRepository repository, JavaMailSender mail) {
    this.repository = repository;
    this.mail = mail;
  }

  @Transactional
  public void forgot(String email) {
    var users = repository.findEnabledUserByEmail(email.toLowerCase(Locale.ROOT));
    if (users.isEmpty()) return;
    byte[] random = new byte[48];
    new SecureRandom().nextBytes(random);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    repository.resetToken(AuthService.hash(token), users.getFirst().get("id"));
    var message = new SimpleMailMessage();
    message.setFrom("no-reply@demo.hospital");
    message.setTo(email);
    message.setSubject("Hospital account recovery");
    message.setText(
        "Your single-use recovery token (expires in 15 minutes): "
            + token
            + "\nEnter it in the hospital password recovery form.");
    try {
      mail.send(message);
    } catch (org.springframework.mail.MailException ignored) {
      /* No account existence or SMTP details are disclosed. Operator must inspect mail availability. */
    }
  }

  @Transactional
  public void reset(String token, String password) {
    var rows = repository.lockResetToken(AuthService.hash(token));
    Validation.require(!rows.isEmpty(), "Recovery token is invalid or expired");
    repository.setPassword(
        rows.getFirst().get("user_id"), new BCryptPasswordEncoder(12).encode(password));
  }
}

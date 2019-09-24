package com.kverchi.diary.service.email;

import com.kverchi.diary.model.Email;

import javax.mail.internet.MimeMessage;
import java.util.List;

public interface EmailService {
    public void sendEmail(Email email);
}

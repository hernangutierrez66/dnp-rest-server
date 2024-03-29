package com.kverchi.diary.service.email.impl;

import com.kverchi.diary.model.Email;
import com.kverchi.diary.service.email.EmailMessagingConsumerListener;
import com.kverchi.diary.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class EmailMessagingConsumerListenerImpl implements EmailMessagingConsumerListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailMessagingConsumerListenerImpl.class);

    @Autowired
    EmailService emailService;

    @JmsListener(destination = "diary.email.queue")
    public void receiveEmail(Email email) {
        emailService.sendEmail(email);
    }
}

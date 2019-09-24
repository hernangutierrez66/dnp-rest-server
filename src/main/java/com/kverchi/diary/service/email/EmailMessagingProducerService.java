package com.kverchi.diary.service.email;

import com.kverchi.diary.model.Email;


public interface EmailMessagingProducerService {
    void sendEmail(Email email);

}

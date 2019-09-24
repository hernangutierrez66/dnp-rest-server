package com.kverchi.diary.service.email;

import com.kverchi.diary.model.Email;


public interface EmailMessagingConsumerListener {
    public void receiveEmail(Email email);
}

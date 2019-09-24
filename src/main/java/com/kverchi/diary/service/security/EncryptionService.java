package com.kverchi.diary.service.security;


public interface EncryptionService {
    String encryptText(String clearText, String key);
    String decryptText(String encryptedText, String key);
}

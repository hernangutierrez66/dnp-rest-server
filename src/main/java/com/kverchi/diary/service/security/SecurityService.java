package com.kverchi.diary.service.security;

import com.kverchi.diary.model.entity.User;


public interface SecurityService {
    String generateSecurityToken();
    Object getUserFromSession();
}

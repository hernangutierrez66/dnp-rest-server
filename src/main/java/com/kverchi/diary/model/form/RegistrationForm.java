package com.kverchi.diary.model.form;

import com.kverchi.diary.model.entity.User;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
@Data
public class RegistrationForm {
	private String username;
	private String password;
	private String matchingPassword;
	private String email;
	private int city;
	private int role;


	public RegistrationForm() {
	}

	public RegistrationForm(String username, String password, String matchingPassword, String email) {
		this.username = username;
		this.password = password;
		this.matchingPassword = matchingPassword;
		this.email = email;
	}

	public User toUser(PasswordEncoder passwordEncoder) {
		return new User(username, passwordEncoder.encode(password), email);
	}




}

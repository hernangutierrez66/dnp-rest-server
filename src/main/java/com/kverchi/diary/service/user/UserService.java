package com.kverchi.diary.service.user;


import com.kverchi.diary.model.entity.Role;
import com.kverchi.diary.model.entity.VerificationToken;
import com.kverchi.diary.service.user.impl.ServiceResponse;
import com.kverchi.diary.model.entity.User;
import com.kverchi.diary.model.form.RegistrationForm;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface UserService {
	ServiceResponse login(User requestUser);
	ServiceResponse logout();
	List<Role> getAllRoles();
	List<User> findAll();
	ServiceResponse register(Map<String, String> body);
	void activateAccount(User user);
	boolean updatePassword(User user);
	boolean createAndSendResetPasswordToken(String email);
	User getResetPasswordToken(String token);
	void createVerificationTokenForUser(User user, String token);
	void createPasswordResetTokenForUser(User user, String token);
	String validateVerificationToken(String token);
	String validatePasswordResetToken(long id, String token);
	String validateSavePasswordResetToken(String token);
	User findUserByEmail(String email);
	void changeUserPassword(User user, String password);
	User findUserByTokenPassword(final String token);



	User getUserFromSession();
	boolean isValuePresent(String key, Object value);
	void saveUserInfo(int userId, String info);
	VerificationToken generateNewVerificationToken(String token);
	//TODO is it correct place for these two methods? Or would it be better to use them in Sight layer?
	/*List getUserWishedSights(int userId);
	List getUserVisitedSights(int userId);*/
	boolean verifyPassword(String rawPass, String encodedPass);

	static String generateServerBaseUrl(HttpServletRequest request) {
		int port = request.getServerPort();
		StringBuilder baseUrl = new StringBuilder();
		baseUrl.append(request.getScheme())
				.append("://")
				.append(request.getServerName());
		if((request.getScheme().equals("http") && port != 80) || (request.getScheme().equals("https") && port != 443)) {
			baseUrl.append(":")
					.append(port);
		}
		return baseUrl.toString();
	}
}

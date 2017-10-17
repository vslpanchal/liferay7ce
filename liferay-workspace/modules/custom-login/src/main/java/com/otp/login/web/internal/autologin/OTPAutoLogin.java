package com.otp.login.web.internal.autologin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalService;

@Component(immediate = true, service = AutoLogin.class)
public class OTPAutoLogin extends BaseAutoLogin {

	@Override
	protected String[] doLogin(
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		HttpSession session = request.getSession();

		Long userId = (Long)session.getAttribute("otpUserId");

		if (userId == null) {
			return null;
		}

		session.removeAttribute("otpUserId");

		User user = _userLocalService.getUserById(userId);

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}

	@Reference
	private volatile UserLocalService _userLocalService;

}

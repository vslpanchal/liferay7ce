package com.otp.login.web.portlet.command;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.otp.login.web.auth.token.GenerateOTPUtil;



@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + PortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + PortletKeys.LOGIN,
		"mvc.command.name=/login/otp_login"
	},
	service = MVCActionCommand.class

)
public class OTPLoginMVCActionCommand extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			com.liferay.portal.kernel.util.WebKeys.THEME_DISPLAY);

		

		if (actionRequest.getRemoteUser() != null) {
			actionResponse.sendRedirect(themeDisplay.getPathMain());
			return;
		}
		
		HttpServletRequest request = PortalUtil.getHttpServletRequest(actionRequest);

		request = PortalUtil.getOriginalServletRequest(request);

		String otp = ParamUtil.getString(actionRequest, "otp");
		String email = ParamUtil.getString(actionRequest, "email");
		_log.debug("User email: "+ email + " otpEntered :"+otp);
		try{
		User user=_userLocalService.getUserByEmailAddress(themeDisplay.getCompanyId(), email);
		
		if (user == null || !user.isActive()) {
			SessionErrors.add(actionRequest, AuthException.class);
			return;
		} 
		
		int pin = 0;
		
		if(Validator.isNotNull(user.getExpandoBridge().getAttribute("Secret Key").toString())){
			pin = Integer.parseInt(user.getExpandoBridge().getAttribute("Secret Key").toString());
		} else {
			SessionErrors.add(actionRequest, "please-set-4-digit-token");
			throw new Exception("NoTokenFoundException");
		}
		
		
		String generatedOTP = GenerateOTPUtil.generateToken(email.trim(),pin);
		
		if(!generatedOTP.equals(otp.trim())){
			_log.debug("OTP did not matched for user : "+ email +", provided OTP :"+otp +" generated OTP :"+generatedOTP);
			SessionErrors.add(actionRequest, "invalid-token"); 
			throw new Exception("InvalidTokenException");
		}
		
		HttpSession session = request.getSession();
		session.setAttribute("otpUserId", user.getUserId());
		String redirect = ParamUtil.getString(actionRequest, "redirect");
		if (Validator.isNull(redirect)) {
			redirect = themeDisplay.getPathMain();
		}
		redirect = PortalUtil.escapeRedirect(redirect);
		actionResponse.sendRedirect(redirect);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug("OTP login failed for user : "+ email, e);
			}
		
			SessionErrors.add(actionRequest, e.getClass());
			actionResponse.setRenderParameter("mvcRenderCommandName", "/login/otp_login");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
			OTPLoginMVCActionCommand.class);


	@Reference
	private volatile UserLocalService _userLocalService;

}

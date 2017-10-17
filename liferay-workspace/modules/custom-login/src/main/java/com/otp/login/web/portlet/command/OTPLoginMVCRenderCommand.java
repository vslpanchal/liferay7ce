package com.otp.login.web.portlet.command;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		property = {
			"javax.portlet.name=" + PortletKeys.FAST_LOGIN,
			"javax.portlet.name=" + PortletKeys.LOGIN,
			"mvc.command.name=/login/otp_login",
			"service.ranking:Integer=100"
		},
		service = MVCRenderCommand.class

	)
public class OTPLoginMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(renderRequest);

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(renderResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isSignedIn()) {

			return "/login.jsp";
		}

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(_JSP_PATH);

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception e) {
			_log.error("Unable to include JSP " + _JSP_PATH, e);

			throw new PortletException("Unable to include JSP " + _JSP_PATH, e);
		}

		return "/navigation.jsp";
	}
	@Reference(
		target = "(osgi.web.symbolicname=com.otp.login.web)",
		unbind = "-"
	)
	protected void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private static final String _JSP_PATH = "/com.liferay.login.web/otp.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
			OTPLoginMVCRenderCommand.class);

	private ServletContext _servletContext;

}

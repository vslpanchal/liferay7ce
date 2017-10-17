/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.otp.login.web.portlet.create.account.command;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.otp.login.web.portlet.command.OTPLoginMVCRenderCommand;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Fellwock
 */
@Component(
	property = {
		"javax.portlet.name=com_liferay_login_web_portlet_FastLoginPortlet",
		"javax.portlet.name=com_liferay_login_web_portlet_LoginPortlet",
		"mvc.command.name=/login/create_account",
		"service.ranking:Integer=100"
	},
	service = MVCRenderCommand.class
)
public class OTPCreateAccountMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {
		
		HttpServletRequest httpServletRequest =
				PortalUtil.getHttpServletRequest(renderRequest);

		HttpServletResponse httpServletResponse =
				PortalUtil.getHttpServletResponse(renderResponse);
		
		System.out.println("My custom login render cmd called.");

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		renderResponse.setTitle(themeDisplay.translate("create-account"));
		
		RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(_JSP_PATH);

			try {
				requestDispatcher.include(httpServletRequest, httpServletResponse);
			}
			catch (Exception e) {
				_log.error("Unable to include JSP " + _JSP_PATH, e);

				throw new PortletException("Unable to include JSP " + _JSP_PATH, e);
			}

		//return "/create_account.jsp";
		//return "/com.liferay.login.web/create_account.jsp";
			return "/navigation.jsp";
	}
	
	@Reference(
			target = "(osgi.web.symbolicname=com.otp.login.web)",
			unbind = "-"
		)
		protected void setServletContext(ServletContext servletContext) {
			_servletContext = servletContext;
		}

		private static final String _JSP_PATH = "/com.liferay.login.web/create_account.jsp";

		private static final Log _log = LogFactoryUtil.getLog(
				OTPLoginMVCRenderCommand.class);

		private ServletContext _servletContext;

}
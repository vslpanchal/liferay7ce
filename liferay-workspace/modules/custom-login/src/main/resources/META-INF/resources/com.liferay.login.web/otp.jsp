<%@page import="com.liferay.portal.kernel.exception.NoSuchUserException"%>
<%@ include file="/com.liferay.login.web/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
%>
<portlet:actionURL var="otpLoginURL">
	<portlet:param name="<%= ActionRequest.ACTION_NAME %>" value="/login/otp_login" />
</portlet:actionURL>

<liferay-ui:error   key="please-set-4-digit-token" message="Please set 4 digit token first." />
	<liferay-ui:error key="invalid-token" message="Invalid token, try again." />
	<liferay-ui:error exception="<%= NoSuchUserException.class %>" message="No user found with this email." />

<aui:form action="<%= otpLoginURL %>" method="post" name="fm">
	<aui:input name="saveLastPath" type="hidden" value="<%= false %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:fieldset>
		<aui:input autoComplete="off" autoFocus="true" label="email" name="email" title="email" type="text" value="">
			<aui:validator  name="required "  errorMessage="Field is required" />
			<aui:validator name="email" />
		</aui:input>
		<aui:input autoComplete="off" autoFocus="false" label="otp" name="otp" title="otp" value="" type="password">
			<aui:validator  name="required "  errorMessage="Field is required" />
			<aui:validator name="digits" />
			<aui:validator name="maxLength">6</aui:validator>
			<aui:validator name="minLength">6</aui:validator>
		</aui:input>

		<aui:button-row>
			<aui:button cssClass="btn-lg" type="submit" value="sign-in" />
		</aui:button-row>
	</aui:fieldset>
</aui:form>
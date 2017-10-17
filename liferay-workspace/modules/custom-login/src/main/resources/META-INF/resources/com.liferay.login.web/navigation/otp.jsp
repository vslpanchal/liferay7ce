<%@ include file="/com.liferay.login.web/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
//String iconURL = "/o/otp-login-web/com.liferay.login.web/navigation/otp.png";
%>
<portlet:renderURL var="otpLoginURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/login/otp_login" />
	<portlet:param name="redirect" value="<%= redirect %>" />
</portlet:renderURL>

<liferay-ui:icon
	image="edit"
	message="otp"
	url="<%= otpLoginURL %>"
	
/>
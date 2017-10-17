package com.exportfile.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
//import com.liferay.portal.kernel.util.DocumentConversionUtil;
import com.liferay.portlet.documentlibrary.util.DocumentConversionUtil;
import com.liferay.wiki.engine.impl.WikiEngineRenderer;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.util.WikiUtil;
//import com.liferay.wiki.web.internal.portlet.action.ExportPageMVCActionCommand;

/**
 * @author vishal
 */
/*
 * @Component(immediate = true, property = {
 * "javax.portlet.name=com_liferay_wiki_web_portlet_WikiPortlet",
 * "mvc.command.name=/", "mvc.command.name=/wiki/export_page",
 * "service.ranking:Integer=100" }, service = MVCActionCommand.class)
 */

// public class ExportFileAction extends BaseMVCActionCommand {
public class ExportFileAction {

	/*
	 * @Override public String render(RenderRequest renderRequest,
	 * RenderResponse renderResponse) throws PortletException { // TODO
	 * Auto-generated method stub
	 * System.out.println("My /wiki/export_page called"); return null;
	 * 
	 * }
	 */

	// @Override
	public void exportFile(RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {
		// ExportPageMVCActionCommand
		System.out.println("My /wiki/export_page called");

		// PortletConfig portletConfig = getPortletConfig(actionRequest);

		try {
			long nodeId = ParamUtil.getLong(renderRequest, "nodeId");
			String nodeName = ParamUtil.getString(renderRequest, "nodeName");
			String title = ParamUtil.getString(renderRequest, "title");
			double version = ParamUtil.getDouble(renderRequest, "version");

			String targetExtension = ParamUtil.getString(renderRequest, "targetExtension");

			ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			PortletURL viewPageURL = PortletURLFactoryUtil.create(renderRequest,
					"com_liferay_wiki_web_portlet_WikiPortlet", themeDisplay.getLayout(), PortletRequest.RENDER_PHASE);

			viewPageURL.setParameter("mvcRenderCommandName", "/wiki/view");
			viewPageURL.setParameter("nodeName", nodeName);
			viewPageURL.setParameter("title", title);
			viewPageURL.setPortletMode(PortletMode.VIEW);
			viewPageURL.setWindowState(WindowState.MAXIMIZED);

			PortletURL editPageURL = PortletURLFactoryUtil.create(renderRequest,
					"com_liferay_wiki_web_portlet_WikiPortlet", themeDisplay.getLayout(), PortletRequest.RENDER_PHASE);

			editPageURL.setParameter("mvcRenderCommandName", "/wiki/edit_page");
			editPageURL.setParameter("nodeId", String.valueOf(nodeId));
			editPageURL.setParameter("title", title);
			editPageURL.setPortletMode(PortletMode.VIEW);
			editPageURL.setWindowState(WindowState.MAXIMIZED);

			HttpServletRequest request = PortalUtil.getHttpServletRequest(renderRequest);
			HttpServletResponse response = PortalUtil.getHttpServletResponse(renderResponse);

			getFile(nodeId, title, version, targetExtension, viewPageURL, editPageURL, themeDisplay, request, response);

			// actionResponse.setRenderParameter("mvcPath", "/null.jsp");
		} catch (Exception e) {
			String host = PrefsPropsUtil.getString(PropsKeys.OPENOFFICE_SERVER_HOST);

			if (Validator.isNotNull(host) && !host.equals(_LOCALHOST_IP) && !host.startsWith(_LOCALHOST)) {

				StringBundler sb = new StringBundler(3);

				sb.append("Conversion using a remote OpenOffice instance is ");
				sb.append("not fully supported. Please use a local instance ");
				sb.append("to prevent any limitations and problems.");

				// _log.error(sb.toString());
			}

			// _portal.sendError(e, actionRequest, actionResponse);
		}

	}

	protected void getFile(long nodeId, String title, double version, String targetExtension, PortletURL viewPageURL,
			PortletURL editPageURL, ThemeDisplay themeDisplay, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		WikiPage page = WikiPageLocalServiceUtil.getPage(nodeId, title, version);
		// WikiPage page1 = _wikiPageService.getPage(nodeId, title, version);

		String content = page.getContent();

		String attachmentURLPrefix = WikiUtil.getAttachmentURLPrefix(themeDisplay.getPathMain(), themeDisplay.getPlid(),
				nodeId, title);

		try {
			// content = _wikiEngineRenderer.convert(page, viewPageURL,
			// editPageURL, attachmentURLPrefix);
			WikiEngineRenderer wikiRenderew = new WikiEngineRenderer();
			content = wikiRenderew.convert(page, viewPageURL, editPageURL, attachmentURLPrefix);
		} catch (Exception e) {
			// _log.error("Error formatting the wiki page " + page.getPageId() +
			// " with the format " + page.getFormat(), e);
		}

		StringBundler sb = new StringBundler(17);

		sb.append("<!DOCTYPE html>");

		sb.append("<html>");

		sb.append("<head>");
		sb.append("<meta content=\"");
		sb.append(ContentTypes.TEXT_HTML_UTF8);
		sb.append("\" http-equiv=\"content-type\" />");
		sb.append("<base href=\"");
		sb.append(themeDisplay.getPortalURL());
		sb.append("\" />");
		sb.append("</head>");

		sb.append("<body>");

		sb.append("<h1>");
		sb.append(title);
		sb.append("</h1>");
		sb.append(content);

		sb.append("</body>");
		sb.append("</html>");

		InputStream is = new UnsyncByteArrayInputStream(sb.toString().getBytes(StringPool.UTF8));

		String sourceExtension = "html";

		String fileName = title.concat(StringPool.PERIOD).concat(sourceExtension);
		File convertedFile = null;
		if (Validator.isNotNull(targetExtension)) {
			String id = page.getUuid();

			convertedFile = DocumentConversionUtil.convert(id, is, sourceExtension, targetExtension);

			if (convertedFile != null) {
				fileName = title.concat(StringPool.PERIOD).concat(targetExtension);

				is = new FileInputStream(convertedFile);
			}
		}
		System.out.println(response.getCharacterEncoding());
		/*
		 * OutputStream os = response.getOutputStream();
		 * os.write(FileUtils.readFileToByteArray(convertedFile));
		 */

		String contentType = MimeTypesUtil.getContentType(fileName);
		ServletResponseUtil.sendFile(request, response, fileName, is,
				contentType);
		/*
		 * response.setContentType(contentType);
		 * response.setCharacterEncoding(StringPool.UTF8); response.setHeader(
		 * "Content-Disposition", String.format("attachment; filename=\"%s\"",
		 * fileName)); FileUtils.copyURLToFile(new
		 * URL(convertedFile.toString()), convertedFile);
		 */

		/*FileInputStream fis = new FileInputStream(new File(convertedFile.toString()));
		String encoding = "utf8";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding))) {
			StringBuilder sb1 = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb1.append(line);
				sb1.append('\n');
			}
			System.out.println(sb1.toString());
			final byte[] userAttributeValue = sb1.toString().getBytes("utf8");
			response.setContentType(contentType);
			response.setCharacterEncoding(StringPool.UTF8); response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
			OutputStream os = response.getOutputStream();
			os.write(userAttributeValue);
			
		}*/

		// ServletResponseUtil.sendFile(request, response, fileName, is,
		// contentType);
		// response.getWriter().write(title + "\n " + content);
		// response.getWriter().write(content);
		// String contentType = MimeTypesUtil.getContentType(fileName);
		// ServletResponseUtil.sendFile(request, response, fileName, is,
		// contentType);
		// ServletResponseUtil.sendFile(request, response, fileName,
		// content.getBytes());
		// ServletResponseUtil.sendFile(request, response, fileName,
		// FileUtils.readFileToByteArray(convertedFile),contentType);
		// ServletResponseUtil.sendFile
	}

	/*
	 * @Reference(unbind = "-") protected void
	 * setWikiEngineRenderer(WikiEngineRenderer wikiEngineRenderer) {
	 * 
	 * _wikiEngineRenderer = wikiEngineRenderer; }
	 * 
	 * //@Reference(unbind = "-") //@Reference(target =
	 * "(osgi.web.symbolicname=com.liferay.wiki.api)")
	 * 
	 * @Reference(unbind = "-") protected void
	 * setWikiPageService(WikiPageService wikiPageService) { _wikiPageService =
	 * wikiPageService; }
	 */

	private static final String _LOCALHOST = "localhost";

	private static final String _LOCALHOST_IP = "127.0.0.1";

	// private static final Log _log =
	// LogFactoryUtil.getLog(ExportPageMVCActionCommand.class);

	/*
	 * @Reference private Portal _portal;
	 * 
	 * private WikiEngineRenderer _wikiEngineRenderer; private WikiPageService
	 * _wikiPageService;
	 */

}
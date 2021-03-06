package com.wiki.display.render.mvc.command;

import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.engine.impl.WikiEngineRenderer;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.service.WikiNodeService;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.service.WikiPageService;
import com.liferay.wiki.web.util.WikiWebComponentProvider;

/**
 * @author V0066801
 */
@Component(immediate = true, property = { "javax.portlet.name=com_liferay_wiki_web_portlet_WikiDisplayPortlet",
		"mvc.command.name=/", "mvc.command.name=/wiki_display/view",
		"service.ranking:Integer=100" }, service = MVCRenderCommand.class)

public class WikiDisplayMvcRender implements MVCRenderCommand {

	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {

		/*
		 * System.out.println("My custom render cmd called...ucin->"+ParamUtil.
		 * getString(renderRequest, "ucin")); HttpServletRequest
		 * httpServletRequest =
		 * PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest
		 * (renderRequest));
		 * System.out.println("ucin->"+httpServletRequest.getParameter("ucin"));
		 * System.out.println("ucin1->"+httpServletRequest.getParameter("ucin1")
		 * ); String ucin = httpServletRequest.getParameter("ucin");
		 */

		try {
			// PortletPreferences portletPreferences
			// =renderRequest.getPreferences();

			ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			WikiWebComponentProvider wikiWebComponentProvider = WikiWebComponentProvider.getWikiWebComponentProvider();

			WikiGroupServiceConfiguration wikiGroupServiceConfiguration = wikiWebComponentProvider
					.getWikiGroupServiceConfiguration();

			WikiPage page = null;

			// To be implemented String ucinLength =
			// PropsUtil.get("ucon.number.length");
			String ucin = null;
			
			if(themeDisplay.getURLCurrent().contains("/ucin")){
					ucin = themeDisplay.getURLCurrent().substring(themeDisplay.getURLCurrent().indexOf("ucin/") + 5,
					themeDisplay.getURLCurrent().length());
			}
			
			//System.out.println("UCIN-->" + ucin); // to be removed
			
			WikiNode node = null;
			if(Validator.isNotNull(ucin)){
			List<WikiNode> nodes = WikiNodeLocalServiceUtil.getNodes(themeDisplay.getScopeGroupId(), -1, -1);
			
				for (WikiNode wikiNode : nodes) {
					List<WikiPage> wikiPages = WikiPageLocalServiceUtil.getPages(wikiNode.getNodeId(), -1, -1);
					for (WikiPage wikiPage : wikiPages) {
						if (wikiPage.getExpandoBridge().getAttribute("ucin").equals(ucin)) {
							page = WikiPageLocalServiceUtil.fetchLatestPage(wikiPage.getNodeId(), wikiPage.getTitle(),
									wikiPage.getStatus(), wikiPage.isApproved());
							node = wikiNode;
							break;
						}
					}
				}
			}
			if(Validator.isNull(node)){
				node = getNode(renderRequest);
			}
			
			if ((page == null) || page.isInTrash()) {
				page = _wikiPageService.getPage(node.getNodeId(), wikiGroupServiceConfiguration.frontPageName());
			}

			renderRequest.setAttribute(WikiWebKeys.WIKI_ENGINE_RENDERER, _wikiEngineRenderer);
			renderRequest.setAttribute(WikiWebKeys.WIKI_NODE, node);
			renderRequest.setAttribute(WikiWebKeys.WIKI_PAGE, page);

			return "/wiki_display/view.jsp";
		} catch (NoSuchNodeException nsne) {
			return "/wiki_display/portlet_not_setup.jsp";
		} catch (NoSuchPageException nspe) {
			return "/wiki_display/portlet_not_setup.jsp";
		} catch (PortalException pe) {
			SessionErrors.add(renderRequest, pe.getClass());

			return "/wiki/error.jsp";
		}
	}

	protected WikiNode getNode(RenderRequest renderRequest) throws PortalException {

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

		String nodeName = ParamUtil.getString(renderRequest, "nodeName");

		if (Validator.isNotNull(nodeName)) {
			return _wikiNodeService.getNode(themeDisplay.getScopeGroupId(), nodeName);
		} else {
			long nodeId = GetterUtil.getLong(portletPreferences.getValue("nodeId", StringPool.BLANK));

			return _wikiNodeService.getNode(nodeId);
		}
	}

	@Reference(unbind = "-")
	protected void setWikiEngineRenderer(WikiEngineRenderer wikiEngineRenderer) {

		_wikiEngineRenderer = wikiEngineRenderer;
	}

	@Reference(unbind = "-")
	protected void setWikiNodeService(WikiNodeService wikiNodeService) {
		_wikiNodeService = wikiNodeService;
	}

	@Reference(unbind = "-")
	protected void setWikiPageService(WikiPageService wikiPageService) {
		_wikiPageService = wikiPageService;
	}

	private WikiEngineRenderer _wikiEngineRenderer;
	private WikiNodeService _wikiNodeService;
	private WikiPageService _wikiPageService;

}
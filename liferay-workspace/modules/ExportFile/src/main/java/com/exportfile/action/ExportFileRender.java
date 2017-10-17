package com.exportfile.action;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

@Component(immediate = true, property = { "javax.portlet.name=com_liferay_wiki_web_portlet_WikiPortlet",
		"mvc.command.name=/", "mvc.command.name=/wiki/export_page",
		"service.ranking:Integer=100" }, service = MVCRenderCommand.class)

public class ExportFileRender implements MVCRenderCommand{

	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {
		
		System.out.println("My /wiki/export_page called");

		ExportFileAction exportFileActionObj = new ExportFileAction();
		
		try {
			exportFileActionObj.exportFile(renderRequest, renderResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	

}

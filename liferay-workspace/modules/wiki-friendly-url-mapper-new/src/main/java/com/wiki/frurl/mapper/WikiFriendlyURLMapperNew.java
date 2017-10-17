package com.wiki.frurl.mapper;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.Router;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.escape.WikiEscapeUtil;

/**
 * @author V0066801
 */
@Component(
	immediate = true,
	property = {
			"com.liferay.portlet.friendly-url-routes=META-INF/friendly-url-routes/routes.xml","javax.portlet.name=com_liferay_wiki_web_portlet_WikiPortlet",
			"service.ranking:Integer=-1"},
	service = FriendlyURLMapper.class
)
public class WikiFriendlyURLMapperNew extends DefaultFriendlyURLMapper {

	@Override
	public boolean isPortletInstanceable() {
		return false;
	}

	@Override
	public String buildPath(LiferayPortletURL liferayPortletURL) {
		Map<String, String> routeParameters = new HashMap<>();

		buildRouteParameters(liferayPortletURL, routeParameters);

		addParameter(routeParameters, "nodeName", true);
		addParameter(routeParameters, "title", true);
		addParameter(routeParameters, "ucin", true);

		String friendlyURLPath = router.parametersToUrl(routeParameters);

		if (Validator.isNull(friendlyURLPath)) {
			return null;
		}

		addParametersIncludedInPath(liferayPortletURL, routeParameters);

		friendlyURLPath = StringPool.SLASH.concat(getMapping()).concat(
			friendlyURLPath);

		return friendlyURLPath;
	}

	@Override
	public String getMapping() {
		return _MAPPING;
	}

	protected void addParameter(
		Map<String, String> routeParameters, String name, boolean escape) {

		if (!routeParameters.containsKey(name)) {
			return;
		}

		String value = routeParameters.get(name);

		if (escape) {
			value = WikiEscapeUtil.escapeName(value);
		}
		else {
			value = WikiEscapeUtil.unescapeName(value);
		}

		routeParameters.put(name, value);
	}

	@Override
	protected void populateParams(
		Map<String, String[]> parameterMap, String namespace,
		Map<String, String> routeParameters) {

		addParameter(routeParameters, "nodeName", false);
		addParameter(routeParameters, "title", false);
		addParameter(routeParameters, "ucin", false);

		super.populateParams(parameterMap, namespace, routeParameters);
	}

	private static final String _MAPPING = "wiki";

}
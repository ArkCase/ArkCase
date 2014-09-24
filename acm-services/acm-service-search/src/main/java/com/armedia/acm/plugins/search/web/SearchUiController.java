package com.armedia.acm.plugins.search.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.armedia.acm.web.AcmPageDescriptor;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Collection;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Map;

@RequestMapping("/plugin/search")
public class SearchUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPluginManager acmPluginManager;
    private AcmPageDescriptor pageDescriptor;
//    private Properties searchDefProperties;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView search()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("search");
        retval.addObject("pageDescriptor", getPageDescriptor());

        JSONArray arr = new JSONArray();
        Collection<AcmPlugin> plugins = getAcmPluginManager().getAcmPlugins();
        for (AcmPlugin plugin : plugins) {
            Map<String, Object> props = plugin.getPluginProperties();
            if (null != props) {
                Object prop = props.get("search.ex");
                if (null != prop) {
                    try {
                        JSONObject searchEx = new JSONObject(prop.toString());
                        arr.put(searchEx);
                    } catch (JSONException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        retval.addObject("searchEx", arr);

        return retval;
    }

    public AcmPageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }

    public void setPageDescriptor(AcmPageDescriptor pageDescriptor) {
        this.pageDescriptor = pageDescriptor;
    }

//    public Properties getSearchDefProperties() {
//        return searchDefProperties;
//    }
//
//    public void setSearchDefProperties(Properties searchDefProperties) {
//        this.searchDefProperties = searchDefProperties;
//    }

    public AcmPluginManager getAcmPluginManager() {
        return acmPluginManager;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager) {
        this.acmPluginManager = acmPluginManager;
    }
}

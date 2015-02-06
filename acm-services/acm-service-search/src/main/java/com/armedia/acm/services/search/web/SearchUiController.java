package com.armedia.acm.services.search.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Collection;
import java.util.Map;

@RequestMapping("/plugin/search")
public class SearchUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPlugin plugin;
    private AcmPluginManager pluginManager;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView search()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("search");

        JSONArray objectTypes = null;
        Map<String, Object> props = props = plugin.getPluginProperties();
        if (null != props) {
            Object prop = props.get("object.types");
            if (null != prop) {
                try {
                    objectTypes = new JSONArray(prop.toString());
                } catch (JSONException e) {
                    log.error(e.getMessage());
                }
            }
        }
        if (null != objectTypes) {
            retval.addObject("objectTypes", objectTypes);
        }


//        JSONArray arr = new JSONArray();
//        Collection<AcmPlugin> plugins = getPluginManager().getAcmPlugins();
//        for (AcmPlugin p : plugins) {
//            props = p.getPluginProperties();
//            if (null != props) {
//                Object prop = props.get("search.ex");
//                if (null != prop) {
//                    try {
//                        JSONObject searchEx = new JSONObject(prop.toString());
//                        arr.put(searchEx);
//                    } catch (JSONException e) {
//                        log.error(e.getMessage());
//                    }
//                }
//            }
//        }
//        retval.addObject("searchEx", arr);

        return retval;
    }

    public AcmPluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(AcmPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public AcmPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(AcmPlugin plugin) {
        this.plugin = plugin;
    }
}

package com.armedia.acm.services.subscription.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.Map;

@RequestMapping("/plugin/subscription")
public class SubscriptionUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPluginManager acmPluginManager;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView search()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("subscription");

//        JSONArray arr = new JSONArray();
//        Collection<AcmPlugin> plugins = getAcmPluginManager().getAcmPlugins();
//        for (AcmPlugin plugin : plugins) {
//            Map<String, Object> props = plugin.getPluginProperties();
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

    public AcmPluginManager getAcmPluginManager() {
        return acmPluginManager;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager) {
        this.acmPluginManager = acmPluginManager;
    }
}

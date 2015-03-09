package com.armedia.acm.services.notification.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/plugin/notification")
public class NotificationUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, Object> pluginProperties = new HashMap<>();

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showNotificationPage()
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("notification");

        Map<String, Object> props = getPluginProperties();
        if (null != props) {
            Object prop = props.get("search.name");
            if (null != prop) {
                mv.addObject("searchName", prop);
            }
            try {
                prop = props.get("search.filters");
                if (null != prop) {
                    JSONArray searchFilters = new JSONArray(prop.toString());
                    mv.addObject("searchFilters", searchFilters);
                }
            } catch (JSONException e) {
                log.error(e.getMessage());
            }
        }

        return mv;
    }

    public Map<String, Object> getPluginProperties() {
        return pluginProperties;
    }

    public void setPluginProperties(Map<String, Object> pluginProperties) {
        this.pluginProperties = pluginProperties;
    }
}

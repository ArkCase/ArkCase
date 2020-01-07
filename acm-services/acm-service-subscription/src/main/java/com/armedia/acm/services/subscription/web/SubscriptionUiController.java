package com.armedia.acm.services.subscription.web;

/*-
 * #%L
 * ACM Service: Subscription
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/plugin/subscription")
public class SubscriptionUiController
{
    private Logger log = LogManager.getLogger(getClass());
    private Map<String, Object> pluginProperties = new HashMap<>();

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView search()
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("subscription");

        Map<String, Object> props = getPluginProperties();
        if (null != props)
        {
            Object prop = props.get("search.name");
            if (null != prop)
            {
                mv.addObject("searchName", prop);
            }
            try
            {
                prop = props.get("search.filters");
                if (null != prop)
                {
                    JSONArray searchFilters = new JSONArray(prop.toString());
                    mv.addObject("searchFilters", searchFilters);
                }
            }
            catch (JSONException e)
            {
                log.error(e.getMessage());
            }
        }

        return mv;
    }

    public Map<String, Object> getPluginProperties()
    {
        return pluginProperties;
    }

    public void setPluginProperties(Map<String, Object> pluginProperties)
    {
        this.pluginProperties = pluginProperties;
    }
}

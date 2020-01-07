package com.armedia.acm.services.search.web;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequestMapping("/plugin/search")
public class SearchUiController
{
    private Logger log = LogManager.getLogger(getClass());
    private AcmPlugin plugin;
    private AcmPluginManager pluginManager;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView search()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("search");

        JSONArray objectTypes = null;
        Map<String, Object> props = props = plugin.getPluginProperties();
        if (null != props)
        {
            Object prop = props.get("object.types");
            if (null != prop)
            {
                try
                {
                    objectTypes = new JSONArray(prop.toString());
                }
                catch (JSONException e)
                {
                    log.error(e.getMessage());
                }
            }
        }
        if (null != objectTypes)
        {
            retval.addObject("objectTypes", objectTypes);
        }

        // JSONArray arr = new JSONArray();
        // Collection<AcmPlugin> plugins = getPluginManager().getAcmPlugins();
        // for (AcmPlugin p : plugins) {
        // props = p.getPluginProperties();
        // if (null != props) {
        // Object prop = props.get("search.ex");
        // if (null != prop) {
        // try {
        // JSONObject searchEx = new JSONObject(prop.toString());
        // arr.put(searchEx);
        // } catch (JSONException e) {
        // log.error(e.getMessage());
        // }
        // }
        // }
        // }
        // retval.addObject("searchEx", arr);

        return retval;
    }

    public AcmPluginManager getPluginManager()
    {
        return pluginManager;
    }

    public void setPluginManager(AcmPluginManager pluginManager)
    {
        this.pluginManager = pluginManager;
    }

    public AcmPlugin getPlugin()
    {
        return plugin;
    }

    public void setPlugin(AcmPlugin plugin)
    {
        this.plugin = plugin;
    }
}

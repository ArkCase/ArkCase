package com.armedia.acm.plugins.task.web;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.pluginmanager.model.AcmPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequestMapping("/plugin/task")
public class TaskUiController
{
    private Logger log = LogManager.getLogger(getClass());
    private AcmPlugin plugin;
    private FormUrl formUrl;
    private Map<String, Object> notificationProperties;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView openTaskList()
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("task");

        initModelAndView(mv);
        return mv;
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public ModelAndView openTaskDetail(@PathVariable(value = "taskId") Long taskId)
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("task");
        mv.addObject("objId", taskId);

        initModelAndView(mv);
        return mv;
    }

    private void addJsonArrayProp(ModelAndView mv, Map<String, Object> props, String propName, String attrName)
    {
        if (null != props)
        {
            try
            {
                Object prop = props.get(propName);
                if (null != prop)
                {
                    JSONArray ar = new JSONArray(prop.toString());
                    mv.addObject(attrName, ar);
                }

            }
            catch (JSONException e)
            {
                log.error(e.getMessage());
            }
        }
    }

    private ModelAndView initModelAndView(ModelAndView mv)
    {
        Map<String, Object> props = getPlugin().getPluginProperties();
        addJsonArrayProp(mv, props, "search.tree.filter", "treeFilter");
        addJsonArrayProp(mv, props, "search.tree.sort", "treeSort");
        addJsonArrayProp(mv, props, "fileTypes", "fileTypes");

        // frevvo form URLs
        mv.addObject("editCloseComplaintFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CLOSE_COMPLAINT, false));
        mv.addObject("changeCaseStatusFormUrl", formUrl.getNewFormUrl(FrevvoFormName.CHANGE_CASE_STATUS, false));
        mv.addObject("allowMailFilesAsAttachments", getNotificationProperties().get("notification.allowMailFilesAsAttachments"));
        mv.addObject("allowMailFilesToExternalAddresses",
                getNotificationProperties().get("notification.allowMailFilesToExternalAddresses"));

        return mv;
    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openTaskWizard(
            @RequestParam(value = "parentType", required = false) String parentType,
            @RequestParam(value = "reference", required = false) String reference)
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("taskWizard");
        mv.addObject("parentType", parentType);
        mv.addObject("reference", reference);
        return mv;

    }

    public FormUrl getFormUrl()
    {
        return formUrl;
    }

    public void setFormUrl(FormUrl formUrl)
    {
        this.formUrl = formUrl;
    }

    public AcmPlugin getPlugin()
    {
        return plugin;
    }

    public void setPlugin(AcmPlugin plugin)
    {
        this.plugin = plugin;
    }

    public Map<String, Object> getNotificationProperties()
    {
        return notificationProperties;
    }

    public void setNotificationProperties(Map<String, Object> notificationProperties)
    {
        this.notificationProperties = notificationProperties;
    }
}

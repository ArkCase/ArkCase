package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.services.labels.exception.AcmLabelManagementException;
import com.armedia.acm.services.labels.service.LabelManagementService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey on 3/2/16.
 */
@Controller
@RequestMapping({
        "/api/v1/plugin/admin",
        "/api/latest/plugin/admin" })
public class LabelManagementRefreshResource
{
    private Logger log = LogManager.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/admin-resource/refresh", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String updateResource(@RequestBody String resource) throws IOException, AcmLabelManagementException
    {

        List<String> ns = new ArrayList<>();
        List<String> langs = new ArrayList<>();

        try
        {
            JSONObject resourceObject = new JSONObject(resource);
            JSONArray nsJsonArray = resourceObject.getJSONArray("ns");
            for (int i = 0; i < nsJsonArray.length(); i++)
            {
                ns.add(nsJsonArray.getString(i));
            }
            JSONArray langJsonArray = resourceObject.getJSONArray("lng");
            for (int i = 0; i < langJsonArray.length(); i++)
            {
                langs.add(langJsonArray.getString(i));
            }
        }
        catch (Exception e)
        {
            log.error(String.format("Wrong refresh parameter '%s' ", resource));
            throw new AcmLabelManagementException("Reset resource error", e);
        }

        labelManagementService.refresh(ns, langs);
        return (new JSONObject()).toString();
    }

    public void setLabelManagementService(LabelManagementService labelManagementService)
    {
        this.labelManagementService = labelManagementService;
    }

}

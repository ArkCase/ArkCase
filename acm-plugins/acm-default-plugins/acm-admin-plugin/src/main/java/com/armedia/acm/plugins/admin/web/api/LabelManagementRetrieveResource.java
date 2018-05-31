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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by sergey on 2/14/16.
 */
@Controller
@RequestMapping({
        "/api/v1/plugin/admin",
        "/api/latest/plugin/admin" })
public class LabelManagementRetrieveResource
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/resource", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String retrieveResource(@RequestParam("lang") String lang, @RequestParam("ns") String ns, HttpServletResponse response)
            throws AcmLabelManagementException
    {
        return retrieveResourceJson(lang, ns).toString();
    }

    @RequestMapping(value = "/labelmanagement/resources", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveResources(
            @RequestParam("lang") String lang,
            @RequestParam("ns[]") String[] ns,
            HttpServletResponse response) throws AcmLabelManagementException
    {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < ns.length; i++)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lang", lang);
            jsonObject.put("ns", ns[i]);
            jsonObject.put("res", retrieveResourceJson(lang, ns[i]));
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    private JSONObject retrieveResourceJson(String lang, String ns) throws AcmLabelManagementException
    {
        try
        {
            // Retrieve resource, third parameter indicates that we have to try create resource it it is absent
            JSONObject jsonResource = labelManagementService.getResource(ns, lang, true);

            // Return empty JSON if resource is absent
            if (jsonResource == null)
            {
                jsonResource = new JSONObject();
            }
            return jsonResource;
        }
        catch (Exception e)
        {
            String msg = String.format("Can't retrieve resource %s:%s", lang, ns);
            log.error(msg, e);
            throw new AcmLabelManagementException(msg, e);
        }
    }

    public void setLabelManagementService(LabelManagementService labelManagementService)
    {
        this.labelManagementService = labelManagementService;
    }

}

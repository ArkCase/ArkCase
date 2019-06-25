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

import com.armedia.acm.plugins.admin.exception.AcmLabelConfigurationException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Created by sergey on 2/14/16.
 */
@Controller
@RequestMapping({
        "/api/v1/plugin/admin",
        "/api/latest/plugin/admin" })
public class LabelManagementUpdateResource
{
    private static final Set<String> ISO_LANGUAGES = new HashSet<String>(Arrays.asList(Locale.getISOLanguages()));
    private Logger log = LogManager.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/admin-resource", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String updateResource(@RequestParam("lang") String lang, @RequestParam("ns") String ns, @RequestBody String resource,
            HttpServletResponse response) throws IOException, AcmLabelConfigurationException, AcmLabelManagementException
    {
        if (ISO_LANGUAGES.contains(lang))
        {
            try
            {

                JSONObject value = new JSONObject(resource);
                JSONObject updatedRes = labelManagementService.updateCustomResource(ns, lang, value);
                JSONArray jsonResourceArray = new JSONArray();
                // Convert json object to the array
                Iterator<String> keys = updatedRes.keys();
                while (keys.hasNext())
                {
                    String key = keys.next();
                    JSONObject node = (JSONObject) updatedRes.get(key);
                    node.put("id", key);
                    jsonResourceArray.put(node);
                }
                return jsonResourceArray.toString();
            }
            catch (Exception e)
            {
                String msg = String.format("Can't update resource %s:%s", lang, ns);
                log.error(msg, e);
                throw new AcmLabelManagementException(msg, e);
            }
        }
        else
        {
            String message = String.format("Language parameter is not valid %s", lang);
            log.error(message);
            throw new AcmLabelManagementException(message);
        }

    }

    public void setLabelManagementService(LabelManagementService labelManagementService)
    {
        this.labelManagementService = labelManagementService;
    }
}

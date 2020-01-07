package com.armedia.acm.form.config;

/*-
 * #%L
 * ACM Service: Form Configuration
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

import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping({ "/api/v1/service/forms/type", "/api/latest/service/forms/type" })
public class FormsTypeConfigurationController
{
    private Logger log = LogManager.getLogger(getClass());
    private FormsTypeManagementService formsTypeManagementService;

    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveProperties(HttpServletResponse response) throws FormsTypeManagementException
    {
        try
        {
            return formsTypeManagementService.getProperties().toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application properties";
            log.error(msg, e);
            throw new FormsTypeManagementException(msg, e);
        }
    }

    @RequestMapping(value = "/{propertyName}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String retrieveProperty(@PathVariable("propertyName") String propertyName, HttpServletResponse response)
            throws FormsTypeManagementException
    {
        try
        {
            return formsTypeManagementService.getProperty(propertyName).toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application property";
            log.error(msg, e);
            throw new FormsTypeManagementException(msg, e);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String updateProperty(@RequestBody String resource, HttpServletResponse response)
            throws FormsTypeManagementException
    {
        try
        {
            JSONObject newProps = new JSONObject(resource);
            return formsTypeManagementService.updateProperties(newProps).toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application property";
            log.error(msg, e);
            throw new FormsTypeManagementException(msg, e);
        }
    }

    public void setFormsTypeManagementService(FormsTypeManagementService formsTypeManagementService)
    {
        this.formsTypeManagementService = formsTypeManagementService;
    }

}

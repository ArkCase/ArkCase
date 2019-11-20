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

import com.armedia.acm.configuration.core.LabelsConfiguration;
import com.armedia.acm.configuration.model.ModuleConfig;
import com.armedia.acm.services.labels.exception.AcmLabelManagementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by sergey on 2/14/16.
 */
@Controller
@RequestMapping({
        "/api/v1/plugin/admin",
        "/api/latest/plugin/admin" })
public class LabelManagementRetrieveNamespaces
{
    private Logger log = LogManager.getLogger(getClass());
    private LabelsConfiguration labelsConfiguration;

    @RequestMapping(value = "/labelmanagement/namespaces", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public List<ModuleConfig> retrieveNamespaces(HttpServletResponse response) throws IOException, AcmLabelManagementException
    {

        try
        {
            return labelsConfiguration.getModules();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve namespaces";
            log.error(msg, e);
            throw new AcmLabelManagementException(msg, e);
        }
    }

    public void setLabelsConfiguration(LabelsConfiguration labelsConfiguration)
    {
        this.labelsConfiguration = labelsConfiguration;
    }
}

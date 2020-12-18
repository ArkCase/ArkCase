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

import com.armedia.acm.core.LanguageSettingsConfig;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.labels.exception.AcmLabelManagementException;
import com.armedia.acm.services.labels.service.LabelManagementService;

import com.google.json.JsonSanitizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by sergey on 2/14/16.
 */
@Controller
@RequestMapping({
        "/api/v1/plugin/admin",
        "/api/latest/plugin/admin" })
public class LabelManagementUpdateSettings
{
    private Logger log = LogManager.getLogger(getClass());
    private LabelManagementService labelManagementService;
    private ObjectConverter objectConverter;

    @RequestMapping(value = "/labelmanagement/settings", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String updateSettings(@RequestBody String settings) throws AcmLabelManagementException
    {

        try
        {
            LanguageSettingsConfig languageSettings = getObjectConverter().getJsonUnmarshaller().unmarshall(settings,
                    LanguageSettingsConfig.class);
            LanguageSettingsConfig updatedSettingsObj = labelManagementService.updateLanguageSettings(languageSettings);
            String wellFormedJson = JsonSanitizer.sanitize(updatedSettingsObj.toString());
            return wellFormedJson;
        }
        catch (Exception e)
        {
            String msg = "Can't update setitngs";
            log.error(msg, e);
            throw new AcmLabelManagementException(msg, e);
        }
    }

    public void setLabelManagementService(LabelManagementService labelManagementService)
    {
        this.labelManagementService = labelManagementService;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}

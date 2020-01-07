package com.armedia.acm.services.labels.service;

/*-
 * #%L
 * ACM Service: Labels Service
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
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.web.api.MDCConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;

/**
 * Created by bojan.milenkoski on 07.9.2017
 */
public class TranslationService
{
    private Logger log = LogManager.getLogger(getClass());
    private UserDao userDao;
    private LabelManagementService labelManagementService;

    /**
     * Returns a translated value for the given labelKey in the requested language. If there is no translated value in
     * the requested
     * language, the translated value of the default language is returned. If the labelKey is not found we return the
     * labelKey as a
     * translated value.
     *
     * @param labelKey
     *            the label key for which we search the translated value
     * @param lang
     *            the ISO code of the language in which the value should be returned
     * @return the translated value for the given key in the given language
     */
    public String translate(String labelKey, String lang)
    {
        String translatedValue = labelKey;
        JSONObject jsonObject = null;

        if (!labelKey.contains("."))
        {
            return translatedValue;
        }

        try
        {
            jsonObject = labelManagementService.getCachedResource(labelKey.substring(0, labelKey.indexOf(".")), lang);
        }
        catch (AcmLabelManagementException e)
        {
            // no translation file for the requested label in the requested language
            // search in the default language
            try
            {
                jsonObject = labelManagementService.getCachedResource(labelKey.substring(0, labelKey.indexOf(".")),
                        labelManagementService.getDefaultLocale());
            }
            catch (AcmLabelManagementException e1)
            {
                return translatedValue;
            }
        }

        try
        {
            translatedValue = (String) jsonObject.get(labelKey);
        }
        catch (JSONException e)
        {
            // if the labelKey is not found we return the labelKey as translated value
        }

        return translatedValue;
    }

    /**
     * Returns a translated value for the given labelKey in the language set for the {@link AcmUser}. If there is no
     * translated value in the
     * user' language, the translated value of the default language is returned. If the labelKey is not found we return
     * the labelKey as a
     * translated value.
     *
     * @param labelKey
     *            the label key for which we search the translated value
     * @return the translated value for the given key
     */
    public String translate(String labelKey)
    {
        AcmUser acmUser = userDao.findByUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
        if (acmUser == null)
        {
            return labelKey;
        }
        return translate(labelKey, acmUser.getLang());
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public LabelManagementService getLabelManagementService()
    {
        return labelManagementService;
    }

    public void setLabelManagementService(LabelManagementService labelManagementService)
    {
        this.labelManagementService = labelManagementService;
    }
}

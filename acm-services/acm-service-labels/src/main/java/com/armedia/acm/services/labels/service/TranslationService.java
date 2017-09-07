package com.armedia.acm.services.labels.service;

import com.armedia.acm.services.labels.exception.AcmLabelManagementException;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.web.api.MDCConstants;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Created by bojan.milenkoski on 07.9.2017
 */
public class TranslationService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private UserDao userDao;
    private LabelManagementService labelManagementService;

    public String translate(String labelKey, String lang)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = labelManagementService.getCachedResource(labelKey.substring(0, labelKey.indexOf(".")), lang);
        }
        catch (AcmLabelManagementException e)
        {
            log.error(String.format("Error getting translation for label with key: {} in language: {}", labelKey, lang), e);
            return null;
        }

        return (String) jsonObject.get(labelKey);
    }

    public String translate(String labelKey)
    {
        AcmUser acmUser = userDao.findByUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
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

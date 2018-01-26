package com.armedia.acm.web.api.service;

import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dragan.simonovski on 05/11/2016.
 */
public class LoginWarningMessageServiceImpl implements LoginWarningMessageService
{
    private Boolean warningEnabled;
    private String warningMessage;
    private ApplicationMetaInfoService applicationMetaInfoService;

    @Override
    public boolean isEnabled()
    {
        return warningEnabled == null ? false : warningEnabled.booleanValue();
    }

    @Override
    public String getMessage()
    {
        return warningMessage == null ? "" : warningMessage;
    }

    @Override
    public Map<String, Object> getWarning()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("enabled", warningEnabled);
        map.put("message", warningMessage);
        return map;
    }

    @Override
    public void buildModel(Model model)
    {
        model.addAttribute("warningEnabled", isEnabled());
        model.addAttribute("warningMessage", getMessage());
        model.addAttribute("version", applicationMetaInfoService.getVersion());
    }

    public void setWarningEnabled(String warningEnabled)
    {
        this.warningEnabled = Boolean.parseBoolean(warningEnabled == null ? "false" : warningEnabled);
    }

    public void setWarningMessage(String warningMessage)
    {
        this.warningMessage = warningMessage;
    }

    public ApplicationMetaInfoService getApplicationMetaInfoService()
    {
        return applicationMetaInfoService;
    }

    public void setApplicationMetaInfoService(ApplicationMetaInfoService applicationMetaInfoService)
    {
        this.applicationMetaInfoService = applicationMetaInfoService;
    }
}

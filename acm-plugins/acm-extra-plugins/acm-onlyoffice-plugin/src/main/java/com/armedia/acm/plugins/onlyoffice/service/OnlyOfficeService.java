package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.onlyoffice.model.CallBackData;
import com.armedia.acm.plugins.onlyoffice.model.CallbackResponse;
import com.armedia.acm.plugins.onlyoffice.model.config.Config;

import org.springframework.security.core.Authentication;

public interface OnlyOfficeService
{
    CallbackResponse processCallback(CallBackData callBackInfo);

    Config getConfig(Long fileId, Authentication auth);
}

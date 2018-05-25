package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.onlyoffice.model.CallBackData;
import com.armedia.acm.plugins.onlyoffice.model.CallbackResponse;

import org.springframework.security.core.Authentication;

public interface CallbackService
{
    CallbackResponse handleCallback(CallBackData callBackData, Authentication authentication);
}

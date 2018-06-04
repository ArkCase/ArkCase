package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.onlyoffice.model.CallbackResponse;
import com.armedia.acm.plugins.onlyoffice.model.callback.CallBackData;
import org.springframework.security.core.Authentication;

public interface CallbackService
{
    CallbackResponse handleCallback(CallBackData callBackData, Authentication authentication);
}

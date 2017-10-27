package com.armedia.acm.services.email.service;

import com.armedia.acm.auth.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

public class OnLoginCheckPasswordExpiration implements ApplicationListener<LoginEvent>
{
    private ResetPasswordService resetPasswordService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(LoginEvent loginEvent)
    {
        if (!loginEvent.isSucceeded())
        {
            log.debug("On successful login check password expiration");
            Authentication authentication = loginEvent.getAuthentication();
            if (authentication != null)
            {
                if (resetPasswordService.isUserPasswordExpired(authentication.getName()))
                {
                    log.debug("Password for user [{}] is expired", authentication.getName());
                    resetPasswordService.sendPasswordResetEmail(authentication.getName());
                }
            }
        }
    }

    public void setResetPasswordService(ResetPasswordService resetPasswordService)
    {
        this.resetPasswordService = resetPasswordService;
    }
}

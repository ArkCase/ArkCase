package com.armedia.acm.services.email.service;

import com.armedia.acm.auth.web.ForgotPasswordEvent;
import com.armedia.acm.services.users.model.AcmUser;
import org.springframework.context.ApplicationListener;

public class OnForgotPassword implements ApplicationListener<ForgotPasswordEvent>
{
    private ResetPasswordService resetPasswordService;

    @Override
    public void onApplicationEvent(ForgotPasswordEvent forgotPasswordEvent)
    {
        if (forgotPasswordEvent.isSucceeded())
        {
            AcmUser user = forgotPasswordEvent.getAcmUser();
            resetPasswordService.sendPasswordResetEmail(user);
        }
    }

    public void setResetPasswordService(ResetPasswordService resetPasswordService)
    {
        this.resetPasswordService = resetPasswordService;
    }
}
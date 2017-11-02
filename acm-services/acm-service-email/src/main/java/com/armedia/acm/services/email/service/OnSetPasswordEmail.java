package com.armedia.acm.services.email.service;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.event.SetPasswordEmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class OnSetPasswordEmail implements ApplicationListener<SetPasswordEmailEvent>
{
    private ResetPasswordService resetPasswordService;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(SetPasswordEmailEvent setPasswordEmailEvent)
    {
        if (setPasswordEmailEvent.isSucceeded())
        {
            log.debug("On set password mail event...");
            AcmUser user = setPasswordEmailEvent.getAcmUser();
            resetPasswordService.sendPasswordResetEmail(user);
        }
    }

    public void setResetPasswordService(ResetPasswordService resetPasswordService)
    {
        this.resetPasswordService = resetPasswordService;
    }
}
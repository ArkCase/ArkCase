package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
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

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.MessageBodyFactory;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.PasswordResetToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.stream.Stream;

public class ResetPasswordService
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private UserDao userDao;
    private AcmApplication acmAppConfiguration;
    private AcmEmailSenderService emailSenderService;
    /**
     * Formatting string to be used for producing text to inserted as a body in the password reset email. The formatting
     * string accepts the password reset link string twice.
     * e.g: "You can change your password on the following link: <a href='%s'>%s</a>"
     */
    private String passwordResetEmailBodyTemplate;
    private String passwordResetEmailSubject;
    private String passwordResetTemplateContent;
    /**
     * Formatting string to be used for constructing the password reset link. The formatting
     * string accepts two parameters: base url and reset password token.
     * e.g: "/reset-password?token=%s"
     */
    private String passwordResetLink;
    private EmailBuilder<AcmUser> emailBuilder = (acmUser, messageProps) -> {
        messageProps.put("to", acmUser.getMail());
        messageProps.put("subject", passwordResetEmailSubject);
    };
    private EmailBodyBuilder<AcmUser> emailBodyBuilder = (user) -> {
        String link = String.format(passwordResetLink, acmAppConfiguration.getBaseUrl(), user.getPasswordResetToken().getToken());
        String messageBody = String.format(passwordResetEmailBodyTemplate, user.getUserId(), link);
        return new MessageBodyFactory(passwordResetTemplateContent).buildMessageBodyFromTemplate(messageBody, "", "");
    };

    @Async
    public void sendPasswordResetEmail(AcmUser user)
    {
        try
        {
            log.debug("Sending password reset email for user: [{}]", user.getUserId());
            user.setPasswordResetToken(new PasswordResetToken());
            userDao.save(user);
            emailSenderService.sendPlainEmail(Stream.of(user), emailBuilder, emailBodyBuilder);
            log.info("Set password reset token to user: [{}]", user.getUserId());
        }
        catch (Exception e)
        {
            log.error("Password reset email was not sent for user: [{}]", user.getUserId());
        }
    }

    public void sendPasswordResetEmail(String userId)
    {
        AcmUser user = userDao.findByUserId(userId);
        sendPasswordResetEmail(user);
    }

    public boolean isUserPasswordExpired(String userId)
    {
        return userDao.isUserPasswordExpired(userId);
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    public void setPasswordResetEmailBodyTemplate(String passwordResetEmailBodyTemplate)
    {
        this.passwordResetEmailBodyTemplate = passwordResetEmailBodyTemplate;
    }

    public String getPasswordResetTemplateContent()
    {

        return passwordResetTemplateContent;
    }

    public void setPasswordResetTemplateContent(String passwordResetTemplateContent)
    {
        this.passwordResetTemplateContent = passwordResetTemplateContent;
    }

    public void setPasswordResetEmailSubject(String passwordResetEmailSubject)
    {
        this.passwordResetEmailSubject = passwordResetEmailSubject;
    }

    public void setPasswordResetLink(String passwordResetLink)
    {
        this.passwordResetLink = passwordResetLink;
    }
}

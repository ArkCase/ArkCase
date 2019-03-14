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

import com.armedia.acm.core.AcmSpringActiveProfile;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.UsernamePasswordNotifierConfig;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.PasswordResetToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ResetPasswordService
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private UserDao userDao;
    private AcmEmailSenderService emailSenderService;
    private AcmSpringActiveProfile acmSpringActiveProfile;
    private UsernamePasswordNotifierConfig usernamePasswordNotifierConfig;
    private String passwordResetEmailBodyTemplate;
    private TemplatingEngine templatingEngine;

    @Async
    public void sendPasswordResetEmail(AcmUser user)
    {
        if (acmSpringActiveProfile.isSAMLEnabledEnvironment())
        {
            log.info("Won't send password reset email when SSO environment");
            return;
        }
        try
        {
            log.debug("Sending password reset email for user: [{}]", user.getUserId());
            user.setPasswordResetToken(new PasswordResetToken());
            userDao.save(user);
            EmailWithEmbeddedLinksDTO emailDTO = new EmailWithEmbeddedLinksDTO();
            emailDTO.setSubject(usernamePasswordNotifierConfig.getPasswordResetEmailSubject());
            String body = getTemplatingEngine().process(passwordResetEmailBodyTemplate, "changePassword", user);
            emailDTO.setBody(body);
            emailDTO.setTemplate(body);
            emailDTO.setEmailAddresses(Arrays.asList(user.getMail()));
            emailSenderService.sendEmailWithEmbeddedLinks(emailDTO, null, user);
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

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    public AcmSpringActiveProfile getAcmSpringActiveProfile()
    {
        return acmSpringActiveProfile;
    }

    public void setAcmSpringActiveProfile(AcmSpringActiveProfile acmSpringActiveProfile)
    {
        this.acmSpringActiveProfile = acmSpringActiveProfile;
    }

    public UsernamePasswordNotifierConfig getUsernamePasswordNotifierConfig()
    {
        return usernamePasswordNotifierConfig;
    }

    public void setUsernamePasswordNotifierConfig(UsernamePasswordNotifierConfig usernamePasswordNotifierConfig)
    {
        this.usernamePasswordNotifierConfig = usernamePasswordNotifierConfig;
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public void setPasswordResetEmailBodyTemplate(Resource passwordResetEmailBodyTemplate) throws IOException
    {
        try (DataInputStream resourceStream = new DataInputStream(passwordResetEmailBodyTemplate.getInputStream()))
        {
            byte[] bytes = new byte[resourceStream.available()];
            resourceStream.readFully(bytes);
            this.passwordResetEmailBodyTemplate = new String(bytes, Charset.forName("UTF-8"));
        }
    }
}

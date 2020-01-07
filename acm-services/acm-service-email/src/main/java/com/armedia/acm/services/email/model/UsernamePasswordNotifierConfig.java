package com.armedia.acm.services.email.model;

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

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

public class UsernamePasswordNotifierConfig
{
    private static final Logger log = LogManager.getLogger(UsernamePasswordNotifierConfig.class);

    @Value("${upcoming.password.notification.subject}")
    private String passwordResetEmailSubject;

    @Value("file:${upcoming.password.notification.template.path}")
    private Resource passwordResetTemplate;

    /**
     * Formatting string to be used for constructing the password reset link. The formatting
     * string accepts two parameters: base url and reset password token.
     * e.g: "/reset-password?token=%s"
     */
    @Value("${upcoming.password.notification.resetPasswordLink}")
    private String passwordResetLink;

    @Value("${upcoming.forgotUsername.notification.subject}")
    private String forgotUsernameEmailSubject;

    /**
     * Formatting string to be used for producing text to inserted as a body in the forgot username email. The
     * formatting
     * string accepts the login link string twice.
     * e.g: "Proceed to login <a href='%s'>%s</a>"
     */
    @Value("${upcoming.forgotUsername.notification.body.template}")
    private String forgotUsernameEmailBodyTemplate;

    public String getPasswordResetEmailSubject()
    {
        return passwordResetEmailSubject;
    }

    @Bean
    public String passwordResetTemplate()
    {
        try (InputStream is = passwordResetTemplate.getInputStream())
        {
            return IOUtils.toString(is);
        }
        catch (IOException e)
        {
            log.warn("Failed to read reset password template content!");
            return null;
        }
    }

    public String getPasswordResetLink()
    {
        return passwordResetLink;
    }

    public String getForgotUsernameEmailSubject()
    {
        return forgotUsernameEmailSubject;
    }

    public String getForgotUsernameEmailBodyTemplate()
    {
        return forgotUsernameEmailBodyTemplate;
    }
}

package com.armedia.acm.services.email.service;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.PasswordResetToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class ResetPasswordService
{
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
    /**
     * Formatting string to be used for constructing the password reset link. The formatting
     * string accepts two parameters: base url and reset password token.
     * e.g: "/reset-password?token=%s"
     */
    private String passwordResetLink;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void sendPasswordResetEmail(AcmUser user)
    {
        try
        {
            log.debug("Sending password reset email...");
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

    private EmailBuilder<AcmUser> emailBuilder = (acmUser, messageProps) ->
    {
        messageProps.put("to", "nadica.cuculova@gmail.com");
        messageProps.put("subject", passwordResetEmailSubject);
    };

    private EmailBodyBuilder<AcmUser> emailBodyBuilder = (user) ->
    {
        String link = String.format(passwordResetLink, acmAppConfiguration.getBaseUrl(), user.getPasswordResetToken().getToken());
        return String.format(passwordResetEmailBodyTemplate, link, link);
    };

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

    public void setPasswordResetEmailSubject(String passwordResetEmailSubject)
    {
        this.passwordResetEmailSubject = passwordResetEmailSubject;
    }

    public void setPasswordResetLink(String passwordResetLink)
    {
        this.passwordResetLink = passwordResetLink;
    }
}

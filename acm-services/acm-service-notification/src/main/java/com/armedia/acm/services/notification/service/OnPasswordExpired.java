package com.armedia.acm.services.notification.service;

import com.armedia.acm.auth.LoginEvent;
import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.PasswordResetToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

import java.util.stream.Stream;

public class OnPasswordExpired implements ApplicationListener<LoginEvent>
{
    private UserDao userDao;
    private SmtpNotificationSender smtpNotificationSender;
    private AcmApplication acmAppConfiguration;
    /**
     * Formatting string to be used for producing text to inserted as a body in the password reset email. The formatting
     * string accepts the password reset link string twice.
     * e.g: "You can change your password on the following link: <a href='%s'>%s<\\/a>"
     */
    private String passwordResetEmailBodyTemplate;
    private String passwordResetEmailSubject;
    /**
     * Formatting string to be used for constructing the password reset link. The formatting
     * string accepts two parameters: base url and reset password token.
     * e.g: "\\/reset-password?token=%s"
     */
    private String passwordResetLink;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(LoginEvent loginEvent)
    {
        if (!loginEvent.isSucceeded())
        {
            Authentication authentication = loginEvent.getAuthentication();
            if (authentication != null)
            {
                if (userDao.isUserPasswordExpired(authentication.getName()))
                {
                    log.debug("Password for user [{}] is expired", authentication.getName());
                    AcmUser user = setUserPasswordToken(authentication.getName());
                    sendPasswordResetEmail(user, authentication);
                }
            }
        }
    }

    private AcmUser setUserPasswordToken(String userId)
    {
        AcmUser user = userDao.findByUserId(userId);
        user.setPasswordResetToken(new PasswordResetToken());
        log.info("Set password reset token to user");
        return userDao.save(user);
    }

    private void sendPasswordResetEmail(AcmUser user, Authentication auth)
    {
        try
        {
            log.debug("Sending password reset email...");
            smtpNotificationSender.sendPlainEmail(Stream.of(user), emailBuilder, emailBodyBuilder);
        } catch (Exception e)
        {
            log.error("Password reset email was not sent for user: {}", auth.getName());
        }
    }

    private EmailBuilder<AcmUser> emailBuilder = (acmUser, messageProps) ->
    {
        messageProps.put("to", acmUser.getMail());
        messageProps.put("subject", passwordResetEmailSubject);
    };

    private EmailBodyBuilder<AcmUser> emailBodyBuilder = (user) ->
    {
        String link = String.format(passwordResetLink, acmAppConfiguration.getBaseUrl(), user.getPasswordResetToken().getToken());
        return String.format(passwordResetEmailBodyTemplate, link, link);
    };

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setSmtpNotificationSender(SmtpNotificationSender smtpNotificationSender)
    {
        this.smtpNotificationSender = smtpNotificationSender;
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
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

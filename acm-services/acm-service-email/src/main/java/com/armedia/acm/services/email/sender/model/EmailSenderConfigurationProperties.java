package com.armedia.acm.services.email.sender.model;

/**
 * @author sasko.tanaskoski
 *
 */
public interface EmailSenderConfigurationProperties
{

    String SENDER_HOST = "notification.user.email.host";
    String SENDER_PORT = "notification.user.email.port";
    String SENDER_TYPE = "notification.user.email.flow.type";
    String SENDER_ENCRYPTION = "email.sender.encryption";

    String USERNAME = "notification.user.email.user";
    String PASSWORD = "notification.user.email.password";
    String USER_FROM = "notification.user.email.from";

    String ALLOW_SENDING = "email.sender.allow";
    String ALLOW_ATTACHMENTS = "notification.allowMailFilesAsAttachments";
    String ALLOW_HYPERLINKS = "notification.allowMailFilesToExternalAddresses";

}
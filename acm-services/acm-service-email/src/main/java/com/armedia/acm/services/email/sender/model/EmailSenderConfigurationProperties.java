package com.armedia.acm.services.email.sender.model;

/**
 * @author sasko.tanaskoski
 *
 */
public interface EmailSenderConfigurationProperties
{

    String HOST = "email.sender.host";
    String PORT = "email.sender.port";
    String TYPE = "email.sender.type";
    String ENCRYPTION = "email.sender.encryption";

    String USERNAME = "email.sender.username";
    String PASSWORD = "email.sender.password";
    String USER_FROM = "email.sender.userFrom";

    String ALLOW_DOCUMENTS = "email.sender.allowDocuments";
    String ALLOW_ATTACHMENTS = "email.sender.allowAttachments";
    String ALLOW_HYPERLINKS = "email.sender.allowHyperlinks";

}
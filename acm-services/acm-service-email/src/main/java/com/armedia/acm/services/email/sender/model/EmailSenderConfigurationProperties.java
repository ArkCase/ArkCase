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

    /*
     * String EMAIL_HOST_KEY = "notification.user.email.host";
     * 
     *//**
       * The property key to use in the properties file that keeps email port
       */
    /*
     * String EMAIL_PORT_KEY = "notification.user.email.port";
     * 
     *//**
       * The property key to use in the properties file that keeps email user
       */
    /*
     * String EMAIL_USER_KEY = "notification.user.email.user";
     * 
     *//**
       * The property key to use in the properties file that keeps email password
       */
    /*
     * String EMAIL_PASSWORD_KEY = "notification.user.email.password";
     * 
     *//**
       * The property key to use in the properties file that keeps email from
       */
    /*
     * String EMAIL_FROM_KEY = "notification.user.email.from";
     * 
     *//**
       * The property key to use in the properties file that keeps flow type
       *//*
         * String EMAIL_FLOW_TYPE = "notification.user.email.flow.type";
         */

}
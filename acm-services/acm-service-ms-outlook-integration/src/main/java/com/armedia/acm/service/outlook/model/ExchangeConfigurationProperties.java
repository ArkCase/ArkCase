package com.armedia.acm.service.outlook.model;

/**
 * @author sasko.tanaskoski
 *
 */
public interface ExchangeConfigurationProperties
{

    String INTEGRATION_ENABLED = "outlook.integration.enabled";
    String SERVER_VERSION = "outlook.exchange.server.version";
    String ENABLE_AUTODISCOVERY = "outlook.exchange.enable.autodiscovery";
    String CLIENT_ACCESS_SERVER = "outlook.exchange.client-access-server";
    String DEFAULT_ACCESS = "outlook.exchange.default_access";
    String SYSTEM_USER_EMAIL = "outlook.exchange.system_user_email";
    String SYSTEM_USER_EMAIL_PASSWORD = "outlook.exchange.system_user_email_password";
    String SYSTEM_USER_ID = "outlook.exchange.system_user_id";
}

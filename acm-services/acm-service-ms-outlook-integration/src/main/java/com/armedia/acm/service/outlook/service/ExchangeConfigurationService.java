package com.armedia.acm.service.outlook.service;

import com.armedia.acm.service.outlook.model.ExchangeConfiguration;

import org.springframework.security.core.Authentication;

/**
 * @author sasko.tanaskoski
 *
 */
public interface ExchangeConfigurationService
{
    void writeConfiguration(ExchangeConfiguration configuration, Authentication auth);

    ExchangeConfiguration readConfiguration();
}

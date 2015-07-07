package com.armedia.acm.service.outlook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class OutlookSpringConfiguration
{
    // there's no apparent way to enable processing of @Retryable annotations via XML configuration
    // so we need this empty Spring configuration class just to tell Spring to look for and honor @Retryable :-(
}

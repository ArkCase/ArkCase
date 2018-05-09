package com.armedia.acm.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Spring Java configuration. For now, we only need "@EnableRetry" since the retry library has no XML configuration.
 * 
 * @author dmiller
 *
 */
@Configuration
@EnableRetry
public class ArkCaseSpringConfiguration
{

}

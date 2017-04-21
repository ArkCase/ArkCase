package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.service.outlook.model.ExchangeConfiguration;
import com.armedia.acm.service.outlook.service.impl.ExchangeConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author sasko.tanaskoski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class ExchangeConfigurationAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private ExchangeConfigurationService exchangeConfigurationService;

    @RequestMapping(value = "/exchange/configuration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ExchangeConfiguration getConfiguration()
    {
        return exchangeConfigurationService.readConfiguration();

    }

    @RequestMapping(value = "/exchange/configuration", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateConfiguration(@RequestBody ExchangeConfiguration configuration)
    {
        exchangeConfigurationService.writeConfiguration(configuration);
    }

    /**
     * @param exchangeConfigurationService
     *            the exchangeConfigurationService to set
     */
    public void setExchangeConfigurationService(ExchangeConfigurationService exchangeConfigurationService)
    {
        this.exchangeConfigurationService = exchangeConfigurationService;
    }
}

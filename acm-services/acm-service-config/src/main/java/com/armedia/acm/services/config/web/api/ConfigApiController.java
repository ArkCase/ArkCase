package com.armedia.acm.services.config.web.api;

import com.armedia.acm.services.config.service.ConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({
        "/api/v1/service/config",
        "/api/latest/service/config" })
public class ConfigApiController
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ConfigService configService;

    /**
     * This method return json stucture when /api/v1/service/config is called.
     *
     * @return - Returns json structure, list of config names and their descriptions
     */
    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE })
    @ResponseBody
    public List<Map<String, String>> getInfo()
    {
        return configService.getInfo();
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public Object getConfig(@PathVariable("name") String name, Authentication authentication, HttpSession session)
    {
        return configService.getConfigAsJson(name);
    }

    public ConfigService getConfigService()
    {
        return configService;
    }

    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
}

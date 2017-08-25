package com.armedia.acm.services.config.lookups.web.api;

import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.config.model.AcmConfig;
import com.armedia.acm.services.config.model.JsonConfig;
import com.armedia.acm.services.config.web.api.ConfigApiController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
@Controller
@RequestMapping({
        "/api/v1/service/config/lookups",
        "/api/latest/service/config/lookups" })
public class LookupsApiController
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ConfigApiController configApiController;
    private LookupDao lookupDao;

    /**
     * Rest API method to update the server side lookups. Returns all the lookups as json, with the updated lookup.
     *
     * @param lookupDefinition
     *            the {@link LookupDefinition} for the lookup to update
     * @param lookupAsJson
     *            the lookup to update as json. The json should be an array with the lookup entries.
     *            <p>
     *            Example: [{"key":"someKey", "value":"someValue"}, {"key":"someOtherKey", "value":"someOtherValue"}, ...]
     * @return all the updated lookups as json
     * @throws InvalidLookupException
     *             when the json is invalid or when null or duplicate keys or values exist
     * @throws IOException
     *             when the underlying store cannot be accessed
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String updateLookup(LookupDefinition lookupDefinition, String lookupAsJson) throws InvalidLookupException, IOException
    {
        // validate lookup
        String lookupsAsJson = lookupDao.updateLookup(lookupDefinition, lookupAsJson);

        // replace the lookups config value in ConfigApiController
        List<AcmConfig> configList = configApiController.getConfigList();
        configList.stream().filter(config -> config.getConfigName().equals("lookups"))
                .forEach(config -> ((JsonConfig) config).setJson(lookupsAsJson));

        return lookupAsJson;
    }

    public ConfigApiController getConfigApiController()
    {
        return configApiController;
    }

    public void setConfigApiController(ConfigApiController configApiController)
    {
        this.configApiController = configApiController;
    }

    public LookupDao getLookupService()
    {
        return lookupDao;
    }

    public void setLookupService(LookupDao lookupService)
    {
        this.lookupDao = lookupService;
    }
}

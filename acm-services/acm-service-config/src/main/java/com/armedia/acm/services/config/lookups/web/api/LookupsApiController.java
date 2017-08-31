package com.armedia.acm.services.config.lookups.web.api;

import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.service.LookupDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

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

    private LookupDao lookupDao;

    /**
     * Rest API method to update the server side lookups. Returns all the lookups as json, with the updated lookup.
     *
     * @param lookupDefinition
     *            the {@link LookupDefinition} for the lookup to update
     * @return all the updated lookups as json
     * @throws InvalidLookupException
     *             when the json is invalid or when null or duplicate keys or values exist
     * @throws IOException
     *             when the underlying store cannot be accessed
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public String updateLookup(@RequestBody LookupDefinition lookupDefinition) throws InvalidLookupException, IOException
    {
        log.debug("Update lookup definition for lookupType: {}, lookupName: {}, lookupAsJson: {}", lookupDefinition.getLookupType(),
                lookupDefinition.getName(), lookupDefinition.getLookupEntriesAsJson());

        String lookupsAsJson = lookupDao.updateLookup(lookupDefinition);

        return lookupsAsJson;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }
}

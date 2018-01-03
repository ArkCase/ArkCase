package com.armedia.acm.services.config.lookups.web.api;

import com.armedia.acm.core.exceptions.AcmResourceNotFoundException;
import com.armedia.acm.core.exceptions.AcmResourceNotModifiableException;
import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
     * Rest API method returns all the lookups as json.
     */
    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_XML_VALUE})
    @ResponseBody
    public String getLookups() {
        return lookupDao.getMergedLookups();
    }


    /**
     * Rest API method to delete a lookup.
     * @param name
     *            the {@link name} for the lookup to be deleted
     * @return all the updated lookups as json
     * @throws AcmResourceNotFoundException
     *             when the lookup cannot be found
     * @throws AcmResourceNotModifiableException
     *             when the lookup cannot be modified
     * @throws IOException
     *             when the underlying store cannot be accessed
     */
    @RequestMapping(value="/{name:.+}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_HTML_VALUE})
    @ResponseBody
    public String deleteLookup(@PathVariable String name)
            throws AcmResourceNotFoundException, AcmResourceNotModifiableException, IOException
    {
        log.debug("Deleting lookup:" + name);
        return lookupDao.deleteLookup(name);
    }

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
    @RequestMapping(method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public String saveLookup(@RequestBody LookupDefinition lookupDefinition) throws InvalidLookupException, IOException
    {
        log.debug("Update lookup definition for lookupType: {}, lookupName: {}, lookupAsJson: {}, readonly: {}", lookupDefinition.getLookupType(),
                lookupDefinition.getName(), lookupDefinition.getReadonly(), lookupDefinition.getLookupEntriesAsJson(), lookupDefinition.getReadonly());
        return lookupDao.saveLookup(lookupDefinition);
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

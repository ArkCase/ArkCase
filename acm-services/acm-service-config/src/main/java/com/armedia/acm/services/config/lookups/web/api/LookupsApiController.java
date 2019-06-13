package com.armedia.acm.services.config.lookups.web.api;

/*-
 * #%L
 * ACM Service: Config
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmResourceNotFoundException;
import com.armedia.acm.core.exceptions.AcmResourceNotModifiableException;
import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.service.LookupDao;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private transient final Logger log = LogManager.getLogger(getClass());

    private LookupDao lookupDao;

    /**
     * Rest API method returns all the lookups as json.
     */
    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public String getLookups()
    {
        return lookupDao.getMergedLookups();
    }

    /**
     * Rest API method to delete a lookup.
     * 
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
    @RequestMapping(value = "/{name:.+}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_HTML_VALUE })
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
    public String saveLookup(@RequestBody LookupDefinition lookupDefinition)
            throws InvalidLookupException, IOException, AcmResourceNotModifiableException
    {
        log.debug("Update lookup definition for lookupType: {}, lookupName: {}, lookupAsJson: {}, readonly: {}",
                lookupDefinition.getLookupType(),
                lookupDefinition.getName(), lookupDefinition.getReadonly(), lookupDefinition.getLookupEntriesAsJson(),
                lookupDefinition.getReadonly());
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

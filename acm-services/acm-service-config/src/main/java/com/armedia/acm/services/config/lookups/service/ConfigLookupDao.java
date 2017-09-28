package com.armedia.acm.services.config.lookups.service;

import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.model.LookupType;
import com.armedia.acm.services.config.lookups.model.LookupValidationResult;
import com.armedia.acm.services.config.service.ConfigService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class ConfigLookupDao implements LookupDao
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ConfigService configService;
    private ObjectConverter converter = ObjectConverter.createJSONConverter();

    private static final Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();

    private static final Configuration configurationWithSuppressedExceptions = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS)
            .jsonProvider(new JacksonJsonNodeJsonProvider()).mappingProvider(new JacksonMappingProvider()).build();

    @Override
    public synchronized String updateLookup(LookupDefinition lookupDefinition) throws InvalidLookupException, IOException
    {
        // validate lookup
        AcmLookup<?> lookup = converter.getUnmarshaller().unmarshall(
                "{\"name\" : \"" + lookupDefinition.getName() + "\", \"entries\" : " + lookupDefinition.getLookupEntriesAsJson() + "}",
                lookupDefinition.getLookupType().getLookupClass());
        if (lookup == null)
        {
            log.error("Unmarshalling lookup entries failed. Lookup name: '{}', lookupAsJson: '{}'", lookupDefinition.getName(),
                    lookupDefinition.getLookupEntriesAsJson());
            throw new InvalidLookupException("Invalid lookup Json: " + lookupDefinition.getLookupEntriesAsJson());
        }

        LookupValidationResult lookupValidationResult = lookup.validate();
        if (!lookupValidationResult.isValid())
        {
            log.error("Lookup validation failed with error: '{}'. Lookup name: '{}', lookupAsJson: '{}'",
                    lookupValidationResult.getErrorMessage(), lookupDefinition.getName(), lookupDefinition.getLookupEntriesAsJson());
            throw new InvalidLookupException(lookupValidationResult.getErrorMessage());
        }

        // load lookups json from file
        String lookupsAsJson = configService.getLookupsAsJson();

        // replace the json content of the lookup to update
        String updatedLookupsAsJson = null;
        try
        {
            updatedLookupsAsJson = JsonPath.using(configuration).parse(lookupsAsJson)
                    .set("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@." + lookupDefinition.getName() + ")]."
                            + lookupDefinition.getName(), lookup.getEntries())
                    .jsonString();
        }
        catch (RuntimeException e)
        {
            log.error("Updating lookups failed with error: '{}'! Lookup name: '{}', lookupAsJson: '{}'", e.getMessage(),
                    lookupDefinition.getName(), lookupDefinition.getLookupEntriesAsJson());
            throw new InvalidLookupException("Invalid lookup Json: " + lookupDefinition.getLookupEntriesAsJson(), e);
        }

        // save updated lookups to file
        configService.saveLookups(updatedLookupsAsJson);

        return updatedLookupsAsJson;
    }

    @Override
    public AcmLookup<?> getLookupByName(String name)
    {
        String lookups = configService.getLookupsAsJson();

        for (LookupType lookupType : LookupType.values())
        {
            ArrayNode jsonArray = JsonPath.using(configurationWithSuppressedExceptions).parse(lookups)
                    .read("$." + lookupType.getTypeName() + "..[?(@." + name + ")]." + name);

            if (jsonArray.size() == 0)
            {
                continue;
            }

            String entriesAsJson = jsonArray.get(0).toString();

            AcmLookup<?> acmLookup = converter.getUnmarshaller()
                    .unmarshall("{\"name\" : \"" + name + "\", \"entries\" : " + entriesAsJson + "}", lookupType.getLookupClass());

            if (acmLookup != null)
            {
                return acmLookup;
            }
        }

        return null;
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

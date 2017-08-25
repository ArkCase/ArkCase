package com.armedia.acm.services.config.lookups.service;

import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.model.LookupValidationResult;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class ConfigFileLookupDao implements LookupDao
{
    private String lookupsFileLocation;

    private ObjectConverter converter = ObjectConverter.createJSONConverter();

    private static final Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();

    @Override
    public String updateLookup(LookupDefinition lookupDefinition, String lookupAsJson) throws InvalidLookupException, IOException
    {
        // validate lookup
        AcmLookup<?> lookup = converter.getUnmarshaller().unmarshall("{\"entries\" : " + lookupAsJson + "}",
                lookupDefinition.getLookupType().getLookupClass());
        if (lookup == null)
        {
            throw new InvalidLookupException("Invalid lookup Json: " + lookupAsJson);
        }

        LookupValidationResult lookupValidationResult = lookup.validate();
        if (!lookupValidationResult.isValid())
        {
            throw new InvalidLookupException(lookupValidationResult.getErrorMessage());
        }

        // load lookups json from file
        String lookupsAsJson = new String(Files.readAllBytes(Paths.get(lookupsFileLocation)));

        // replace the json content of the lookup to update ..[?(@.id==7)]
        String updatedLookupsAsJson = JsonPath.using(configuration).parse(lookupsAsJson)
                .set("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@." + lookupDefinition.getName() + ")]."
                        + lookupDefinition.getName(), lookupAsJson)
                .jsonString();

        // save updated lookups to file
        Files.write(Paths.get(lookupsFileLocation), updatedLookupsAsJson.getBytes());

        return updatedLookupsAsJson;
    }

    public String getLookupsFileLocation()
    {
        return lookupsFileLocation;
    }

    public void setLookupsFileLocation(String lookupsFileLocation)
    {
        this.lookupsFileLocation = lookupsFileLocation;
    }

}

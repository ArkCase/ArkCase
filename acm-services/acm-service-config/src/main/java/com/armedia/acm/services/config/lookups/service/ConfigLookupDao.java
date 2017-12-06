package com.armedia.acm.services.config.lookups.service;

import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.model.LookupType;
import com.armedia.acm.services.config.lookups.model.LookupValidationResult;
import com.armedia.acm.services.config.service.ConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.sun.mail.handlers.message_rfc822;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class ConfigLookupDao implements LookupDao {
    private static final Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();
    private static final Configuration configurationWithSuppressedExceptions = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS)
            .jsonProvider(new JacksonJsonNodeJsonProvider()).mappingProvider(new JacksonMappingProvider()).build();
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ConfigService configService;
    private ObjectConverter objectConverter;

    @Override
    public AcmLookup<?> getLookupByName(String name) {
        String lookups = getMergedLookups();

        for (LookupType lookupType : LookupType.values()) {
            ArrayNode jsonArray = JsonPath.using(configurationWithSuppressedExceptions).parse(lookups)
                    .read("$." + lookupType.getTypeName() + "..[?(@.name=='" + name + "')]");

            if (jsonArray.size() == 0) {
                continue;
            }

            String lookupAsJson = jsonArray.get(0).toString();

            AcmLookup<?> acmLookup = getObjectConverter().getJsonUnmarshaller()
                    .unmarshall(lookupAsJson, lookupType.getLookupClass());

            if (acmLookup != null) {
                return acmLookup;
            }
        }

        return null;
    }

    @Override
    public String getMergedLookups() {
        String lookups = configService.getLookupsAsJson();
        String lookupsExt = configService.getLookupsExtAsJson();

        //merge both json files
        for (LookupType lookupType : LookupType.values())  //get lookupType
        {
            ArrayNode jsonArrayExt = JsonPath.using(configurationWithSuppressedExceptions).parse(lookupsExt)
                    .read("$." + lookupType.getTypeName());

            ArrayNode jsonArray = JsonPath.using(configurationWithSuppressedExceptions).parse(lookups)
                    .read("$." + lookupType.getTypeName());
            if (jsonArrayExt != null) {
                for (JsonNode lookupExt : jsonArrayExt) {
                    // find lookup entries from existing lookup
                    ArrayNode existingLookup = JsonPath.using(configurationWithSuppressedExceptions).parse(lookups)
                            .read("$." + lookupType.getTypeName() + "..[?(@." + lookupExt.get("name") + ")]." + lookupExt.get("name"));

                    // find lookup entries from lookupExt
                    ArrayNode newLookupEntries = JsonPath.using(configurationWithSuppressedExceptions).parse(lookupsExt)
                            .read("$." + lookupType.getTypeName() + "..[?(@." + lookupExt.get("name") + ")]." + lookupExt.get("name"));

                    if (existingLookup.size() == 0) {
                        // add lookupExt object to jsonArray
                        jsonArray.add(lookupExt);
                        lookups = JsonPath.using(configuration).parse(lookups)
                                .set("$." + lookupType.getTypeName(), jsonArray).jsonString();
                    } else {
                        // replace lookup object in jsonArray with lookupExt
                        // replace the json content of the lookup to update
                        lookups = JsonPath.using(configuration).parse(lookups)
                                .set("$." + lookupType.getTypeName() + "..[?(@." + lookupExt.get("name") + ")]."
                                        + lookupExt.get("name"), newLookupEntries)
                                .jsonString();

                    }
                }
            }
        }

        return lookups;
    }

    @Override
    public synchronized String saveLookup(LookupDefinition lookupDefinition) throws InvalidLookupException, IOException {
        // find lookup by name in lookups-ext.json
        String lookups = configService.getLookupsExtAsJson();

        boolean found = false;
        ArrayNode jsonArray = JsonPath.using(configurationWithSuppressedExceptions).parse(lookups)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@." + lookupDefinition.getName() + ")]." + lookupDefinition.getName());

        if (jsonArray.size() > 0) {
            // if found update the lookup entries and save lookups-ext.json file
            updateLookup(lookupDefinition);
            found = true;
        }

        if (!found) {
            // if not found add new lookup in lookups-ext.json and save
            addNewLookup(lookupDefinition);
        }

        return getMergedLookups();
    }

    public synchronized String updateLookup(LookupDefinition lookupDefinition) throws InvalidLookupException, IOException {
        // validate lookup
        AcmLookup<?> lookup = getObjectConverter().getJsonUnmarshaller().unmarshall(
                "{\"name\" : \"" + lookupDefinition.getName() + "\", \"entries\" : " + lookupDefinition.getLookupEntriesAsJson() + "}",
                lookupDefinition.getLookupType().getLookupClass());
        if (lookup == null) {
            log.error("Unmarshalling lookup entries failed. Lookup name: '{}', lookupAsJson: '{}'", lookupDefinition.getName(),
                    lookupDefinition.getLookupEntriesAsJson());
            throw new InvalidLookupException("Invalid lookup Json: " + lookupDefinition.getLookupEntriesAsJson());
        }

        LookupValidationResult lookupValidationResult = lookup.validate();
        if (!lookupValidationResult.isValid()) {
            log.error("Lookup validation failed with error: '{}'. Lookup name: '{}', lookupAsJson: '{}'",
                    lookupValidationResult.getErrorMessage(), lookupDefinition.getName(), lookupDefinition.getLookupEntriesAsJson());
            throw new InvalidLookupException(lookupValidationResult.getErrorMessage());
        }

        // load lookups json from file
        String lookupsAsJson = configService.getLookupsExtAsJson();

        // replace the json content of the lookup to update
        String updatedLookupsAsJson = null;
        try {
            updatedLookupsAsJson = JsonPath.using(configuration).parse(lookupsAsJson)
                    .set("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@." + lookupDefinition.getName() + ")]."
                            + lookupDefinition.getName(), lookup.getEntries())
                    .jsonString();
        } catch (RuntimeException e) {
            log.error("Updating lookups failed with error: '{}'! Lookup name: '{}', lookupAsJson: '{}'", e.getMessage(),
                    lookupDefinition.getName(), lookupDefinition.getLookupEntriesAsJson());
            throw new InvalidLookupException("Invalid lookup Json: " + lookupDefinition.getLookupEntriesAsJson(), e);
        }

        // save updated lookups to file
        configService.saveLookupsExt(updatedLookupsAsJson);

        return updatedLookupsAsJson;
    }

    private String addNewLookup(LookupDefinition lookupDefinition) throws IOException {
        // add lookupExt object to jsonArrayExt
        String lookupsExt = configService.getLookupsExtAsJson();

        ArrayNode jsonArrayExt = JsonPath.using(configurationWithSuppressedExceptions).parse(lookupsExt)
                .read("$." + lookupDefinition.getLookupType().getTypeName());

        ObjectNode lookupExt = new ObjectNode(new JsonNodeFactory(false));

        lookupExt.put("readonly", lookupDefinition.getReadonly());
        lookupExt.putArray(lookupDefinition.getName());
        // lookupExt.put("readonly", lookupDefinition.getReadonly());

        //jsonArrayExt = null, create new object
        if (jsonArrayExt == null) {
            jsonArrayExt = new ArrayNode(new JsonNodeFactory(false));
        }
        jsonArrayExt.add(lookupExt);
        String updatedLookupsAsJson = JsonPath.using(configuration).parse(lookupsExt)
                .set("$." + lookupDefinition.getLookupType().getTypeName(), jsonArrayExt).jsonString();

        configService.saveLookupsExt(updatedLookupsAsJson);
        return updatedLookupsAsJson;
    }


    public ConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public ObjectConverter getObjectConverter() {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter converter) {
        this.objectConverter = converter;
    }
}

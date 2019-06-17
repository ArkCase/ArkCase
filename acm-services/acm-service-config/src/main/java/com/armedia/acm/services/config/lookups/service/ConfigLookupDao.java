package com.armedia.acm.services.config.lookups.service;

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
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.AcmLookupEntry;
import com.armedia.acm.services.config.lookups.model.InverseValuesLookup;
import com.armedia.acm.services.config.lookups.model.InverseValuesLookupEntry;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.model.LookupType;
import com.armedia.acm.services.config.lookups.model.LookupValidationResult;
import com.armedia.acm.services.config.lookups.model.NestedLookup;
import com.armedia.acm.services.config.lookups.model.NestedLookupEntry;
import com.armedia.acm.services.config.lookups.model.StandardLookup;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class ConfigLookupDao implements LookupDao
{
    private static final Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();
    private static final Configuration configurationWithSuppressedExceptions = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS)
            .jsonProvider(new JacksonJsonNodeJsonProvider()).mappingProvider(new JacksonMappingProvider()).build();
    private transient final Logger log = LogManager.getLogger(getClass());
    private ObjectConverter objectConverter;

    private String lookups;
    private String lookupsExt;
    private String mergedLookups;
    private String lookupsExtFileLocation;

    @Override
    public AcmLookup<?> getLookupByName(String name)
    {
        String mergedLookups = getMergedLookups();

        for (LookupType lookupType : LookupType.values())
        {
            ArrayNode jsonArray = JsonPath.using(configurationWithSuppressedExceptions).parse(mergedLookups)
                    .read("$." + lookupType.getTypeName() + "..[?(@.name=='" + name + "')]");

            if (jsonArray.size() == 0)
            {
                continue;
            }

            String lookupAsJson = jsonArray.get(0).toString();

            return getObjectConverter().getJsonUnmarshaller().unmarshall(lookupAsJson, lookupType.getLookupClass());
        }

        return null;
    }

    @Override
    public String getMergedLookups()
    {
        if (mergedLookups != null)
        {
            return mergedLookups;
        }

        mergeLookups();

        return mergedLookups;
    }

    private void mergeLookups()
    {
        mergedLookups = lookups;

        // merge both json files
        for (LookupType lookupType : LookupType.values()) // get lookupType
        {
            ArrayNode jsonArrayExt = JsonPath.using(configurationWithSuppressedExceptions).parse(lookupsExt)
                    .read("$." + lookupType.getTypeName());

            ArrayNode jsonArray = JsonPath.using(configurationWithSuppressedExceptions).parse(lookups)
                    .read("$." + lookupType.getTypeName());

            if (jsonArrayExt != null)
            {
                for (JsonNode lookupExt : jsonArrayExt)
                {
                    // find lookup from core lookups
                    ArrayNode coreLookup = JsonPath.using(configurationWithSuppressedExceptions).parse(lookups)
                            .read("$." + lookupType.getTypeName() + "..[?(@.name=='" + lookupExt.get("name").textValue() + "')]");

                    if (coreLookup.size() == 0)
                    {
                        // add lookupExt object to jsonArray
                        jsonArray.add(lookupExt);
                    }
                    else
                    {
                        // replace coreLookup object in jsonArray with extLookup
                        for (int i = 0; i < jsonArray.size(); i++)
                        {
                            JsonNode lookup = jsonArray.get(i);
                            if (lookup.has("name") && lookup.get("name").equals(lookupExt.get("name")))
                            {
                                jsonArray.set(i, lookupExt);
                                break;
                            }
                        }
                    }
                }
            }

            mergedLookups = JsonPath.using(configuration).parse(mergedLookups).set("$." + lookupType.getTypeName(), jsonArray).jsonString();
        }
    }

    @Override
    public synchronized String saveLookup(LookupDefinition lookupDefinition)
            throws InvalidLookupException, IOException, AcmResourceNotModifiableException
    {
        // find lookup by name in lookups-ext.json
        boolean found = false;
        ArrayNode jsonArray = JsonPath.using(configurationWithSuppressedExceptions).parse(lookupsExt)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName() + "')]");

        if (jsonArray.size() > 0)
        {
            // if found update the lookup entries and save lookups-ext.json file
            updateLookup(lookupDefinition);
            found = true;
        }

        if (!found)
        {
            // if not found add new lookup in lookups-ext.json and save
            addNewLookup(lookupDefinition);
        }

        return getMergedLookups();
    }

    @Override
    public String deleteLookup(String name) throws AcmResourceNotFoundException, AcmResourceNotModifiableException, IOException
    {
        AcmLookup<?> lookup = getLookupByName(name);

        if (lookup == null)
        {
            throw new AcmResourceNotFoundException("Lookup with name: '" + name + "' does not exist!");
        }

        if (lookup.isReadonly())
        {
            throw new AcmResourceNotModifiableException("Lookup with name: '" + name + "' is readonly!");
        }

        deleteLookupFromExtLookups(name);

        return getMergedLookups();
    }

    private synchronized void updateLookup(LookupDefinition lookupDefinition)
            throws InvalidLookupException, IOException, AcmResourceNotModifiableException
    {
        // validate lookup
        AcmLookup<?> lookup = getObjectConverter().getJsonUnmarshaller().unmarshall(
                "{\"name\" : \"" + lookupDefinition.getName() + "\", \"entries\" : " + lookupDefinition.getLookupEntriesAsJson()
                        + ", \"readonly\": " + lookupDefinition.getReadonly() + "}",
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

        checkReadOnlyEntries(lookupDefinition);

        // replace the json content of the lookup to update
        String updatedLookupsAsJson;
        try
        {
            updatedLookupsAsJson = JsonPath.using(configuration).parse(lookupsExt)
                    .set("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                            + "')].entries", lookup.getEntries())
                    .jsonString();
        }
        catch (RuntimeException e)
        {
            log.error("Updating lookups failed with error: '{}'! Lookup name: '{}', lookupAsJson: '{}'", e.getMessage(),
                    lookupDefinition.getName(), lookupDefinition.getLookupEntriesAsJson());
            throw new InvalidLookupException("Invalid lookup Json: " + lookupDefinition.getLookupEntriesAsJson(), e);
        }

        // save updated lookups to file
        saveLookupsExt(updatedLookupsAsJson);
    }

    private void checkStandardLookupReadOnlyEntries(LookupDefinition lookupDefinition) throws AcmResourceNotModifiableException
    {

        List<StandardLookupEntry> protectedEntries = ((StandardLookup) getLookupByName(lookupDefinition.getName()))
                .getEntries().stream()
                .filter(AcmLookupEntry::isReadonly)
                .collect(Collectors.toList());

        List<StandardLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, StandardLookupEntry.class);

        for (StandardLookupEntry protectedEntry : protectedEntries)
        {
            if (entries.stream().noneMatch(entry -> entry.getKey().equals(protectedEntry.getKey())))
            {
                throw new AcmResourceNotModifiableException("Entry with key: " + protectedEntry.getKey() + " cannot be deleted");
            }
        }

    }

    private void checkInverseLookupReadOnlyEntries(LookupDefinition lookupDefinition) throws AcmResourceNotModifiableException
    {

        List<InverseValuesLookupEntry> protectedEntries = ((InverseValuesLookup) getLookupByName(lookupDefinition.getName()))
                .getEntries().stream()
                .filter(AcmLookupEntry::isReadonly)
                .collect(Collectors.toList());

        List<InverseValuesLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, InverseValuesLookupEntry.class);
        for (InverseValuesLookupEntry protectedEntry : protectedEntries)
        {
            if (entries.stream().noneMatch(entry -> entry.getKey().equals(protectedEntry.getKey())))
            {
                throw new AcmResourceNotModifiableException("Entry with key: " + protectedEntry.getKey() +
                        " cannot be deleted");
            }
        }

    }

    private void checkNestedLookupReadOnlyEntries(LookupDefinition lookupDefinition) throws AcmResourceNotModifiableException
    {

        List<NestedLookupEntry> protectedMainEntries = ((NestedLookup) getLookupByName(lookupDefinition.getName()))
                .getEntries().stream()
                .filter(AcmLookupEntry::isReadonly)
                .collect(Collectors.toList());

        List<NestedLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, NestedLookupEntry.class);

        // we expect that a protected entry in sublookup must have a protected main entry
        for (NestedLookupEntry protectedMainEntry : protectedMainEntries)
        {
            if (entries.stream().noneMatch(entry -> entry.getKey().equals(protectedMainEntry.getKey())))
            {
                throw new AcmResourceNotModifiableException("Entry with key: " + protectedMainEntry.getKey() +
                        " cannot be deleted");
            }

            List<StandardLookupEntry> protectedSubEntries = protectedMainEntry.getSubLookup().stream()
                    .filter(AcmLookupEntry::isReadonly)
                    .collect(Collectors.toList());

            Optional<NestedLookupEntry> optionalNestedLookupEntry = entries.stream()
                    .filter(entry -> entry.getKey().equals(protectedMainEntry.getKey()))
                    .findFirst();
            List<StandardLookupEntry> subEntries = null;
            if (optionalNestedLookupEntry.isPresent())
            {
                subEntries = optionalNestedLookupEntry.get().getSubLookup();
            }
            else
            {
                throw new AcmResourceNotModifiableException(
                        String.format("Entries can't be reached for key %s", protectedMainEntry.getKey()));
            }
            for (StandardLookupEntry protectedSubEntry : protectedSubEntries)
            {
                if (subEntries.stream().noneMatch(entry -> entry.getKey().equals(protectedSubEntry.getKey())))
                {
                    throw new AcmResourceNotModifiableException("Entry with key: " + protectedSubEntry.getKey() +
                            " cannot be deleted");
                }
            }
        }

    }

    private void checkReadOnlyEntries(LookupDefinition lookupDefinition) throws AcmResourceNotModifiableException
    {
        if (LookupType.STANDARD_LOOKUP.equals(lookupDefinition.getLookupType()))
        {
            checkStandardLookupReadOnlyEntries(lookupDefinition);
        }
        else if (LookupType.INVERSE_VALUES_LOOKUP.equals(lookupDefinition.getLookupType()))
        {
            checkInverseLookupReadOnlyEntries(lookupDefinition);
        }
        else if (LookupType.NESTED_LOOKUP.equals(lookupDefinition.getLookupType()))
        {
            checkNestedLookupReadOnlyEntries(lookupDefinition);
        }

    }

    private void addNewLookup(LookupDefinition lookupDefinition) throws IOException, AcmResourceNotModifiableException
    {
        // entriesArr is empty when adding new lookup
        JSONArray entriesArr = new JSONArray(lookupDefinition.getLookupEntriesAsJson());
        if (entriesArr.length() > 0)
        {
            checkReadOnlyEntries(lookupDefinition);
        }

        // add lookupExt object to jsonArrayExt
        ArrayNode jsonArrayExt = JsonPath.using(configurationWithSuppressedExceptions).parse(lookupsExt)
                .read("$." + lookupDefinition.getLookupType().getTypeName());

        ObjectNode lookupExt = new ObjectNode(new JsonNodeFactory(false));

        lookupExt.put("name", lookupDefinition.getName());
        lookupExt.put("readonly", lookupDefinition.getReadonly());

        ArrayNode entriesNode = lookupExt.putArray("entries");
        if (LookupType.INVERSE_VALUES_LOOKUP.equals(lookupDefinition.getLookupType()))
        {
            List<InverseValuesLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                    .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, InverseValuesLookupEntry.class);

            entries.forEach(entry -> {
                ObjectNode entryNode = new ObjectNode(new JsonNodeFactory(false));
                entryNode.put("key", entry.getKey());
                entryNode.put("value", entry.getValue());
                entryNode.put("inverseKey", entry.getInverseKey());
                entryNode.put("inverseValue", entry.getInverseValue());
                entryNode.put("readonly", entry.isReadonly());
                entriesNode.add(entryNode);
            });
        }
        else if (lookupDefinition.getLookupType().equals(LookupType.NESTED_LOOKUP))
        {
            List<NestedLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                    .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, NestedLookupEntry.class);

            entries.forEach(entry -> {
                ObjectNode entryNode = new ObjectNode(new JsonNodeFactory(false));
                entryNode.put("key", entry.getKey());
                entryNode.put("value", entry.getValue());
                entryNode.put("readonly", entry.isReadonly());
                ArrayNode sublookupNode = entryNode.putArray("subLookup");

                entry.getSubLookup().forEach(sublookupEntry -> {
                    ObjectNode subEntryNode = new ObjectNode(new JsonNodeFactory(false));
                    subEntryNode.put("key", sublookupEntry.getKey());
                    subEntryNode.put("value", sublookupEntry.getValue());
                    subEntryNode.put("readonly", sublookupEntry.isReadonly());

                    sublookupNode.add(subEntryNode);
                });

                entryNode.set("subLookup", sublookupNode);

                entriesNode.add(entryNode);
            });
        }
        else if (lookupDefinition.getLookupType().equals(LookupType.STANDARD_LOOKUP))
        {
            List<StandardLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                    .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, StandardLookupEntry.class);

            entries.forEach(entry -> {
                ObjectNode entryNode = new ObjectNode(new JsonNodeFactory(false));
                entryNode.put("key", entry.getKey());
                entryNode.put("value", entry.getValue());
                entryNode.put("readonly", entry.isReadonly());
                entriesNode.add(entryNode);
            });
        }
        jsonArrayExt.add(lookupExt);

        String updatedLookupsAsJson = JsonPath.using(configuration).parse(lookupsExt)
                .set("$." + lookupDefinition.getLookupType().getTypeName(), jsonArrayExt).jsonString();

        saveLookupsExt(updatedLookupsAsJson);
    }

    private void deleteLookupFromExtLookups(String lookupName) throws IOException
    {
        String updatedLookupsAsJson = null;
        for (LookupType lookupType : LookupType.values())
        {
            ArrayNode jsonArrayExt = JsonPath.using(configurationWithSuppressedExceptions).parse(lookupsExt)
                    .read("$." + lookupType.getTypeName());

            if (jsonArrayExt != null)
            {
                for (int i = 0; i < jsonArrayExt.size(); i++)
                {
                    JsonNode node = jsonArrayExt.get(i);
                    if (lookupName.equals(node.get("name").asText()))
                    {
                        jsonArrayExt.remove(i);
                        updatedLookupsAsJson = JsonPath.using(configuration).parse(lookupsExt)
                                .set("$." + lookupType.getTypeName(), jsonArrayExt).jsonString();
                        break;
                    }
                }
            }
        }
        saveLookupsExt(updatedLookupsAsJson);
    }

    private void saveLookupsExt(String updatedLookupsAsJson) throws JSONException, IOException
    {
        Files.write(Paths.get(getLookupsExtFileLocation()), new JSONObject(updatedLookupsAsJson).toString(2).getBytes());
        lookupsExt = updatedLookupsAsJson;
        mergeLookups();
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter converter)
    {
        this.objectConverter = converter;
    }

    public String getLookups()
    {
        return lookups;
    }

    public void setLookups(String lookups)
    {
        this.lookups = lookups;
    }

    public String getLookupsExt()
    {
        return lookupsExt;
    }

    public void setLookupsExt(String lookupsExt)
    {
        this.lookupsExt = lookupsExt;
    }

    public String getLookupsExtFileLocation()
    {
        return lookupsExtFileLocation;
    }

    public void setLookupsExtFileLocation(String lookupsExtFileLocation)
    {
        this.lookupsExtFileLocation = lookupsExtFileLocation;
    }
}

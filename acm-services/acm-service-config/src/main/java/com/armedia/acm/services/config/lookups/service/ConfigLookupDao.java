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

import com.armedia.acm.configuration.service.CollectionPropertiesConfigurationService;
import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.configuration.util.MergeFlags;
import com.armedia.acm.core.exceptions.AcmResourceNotFoundException;
import com.armedia.acm.core.exceptions.AcmResourceNotModifiableException;
import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.AcmLookupConfig;
import com.armedia.acm.services.config.lookups.model.AcmLookupEntry;
import com.armedia.acm.services.config.lookups.model.AcmLookupTransformer;
import com.armedia.acm.services.config.lookups.model.InverseValuesLookup;
import com.armedia.acm.services.config.lookups.model.InverseValuesLookupEntry;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.model.LookupType;
import com.armedia.acm.services.config.lookups.model.LookupValidationResult;
import com.armedia.acm.services.config.lookups.model.NestedLookup;
import com.armedia.acm.services.config.lookups.model.NestedLookupEntry;
import com.armedia.acm.services.config.lookups.model.StandardLookup;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class ConfigLookupDao implements LookupDao
{

    private static final Configuration configurationWithSuppressedExceptions = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS)
            .jsonProvider(new JacksonJsonNodeJsonProvider()).mappingProvider(new JacksonMappingProvider()).build();
    public static final String ENTRIES_CONFIG_KEY = "entries";
    private static final String SUBLOOKUP_CONFIG_KEY = "subLookup";
    private transient final Logger log = LogManager.getLogger(getClass());
    private ObjectConverter objectConverter;
    private AcmLookupConfig lookupConfig;
    private ConfigurationPropertyService configurationPropertyService;
    private CollectionPropertiesConfigurationService propertiesConfigurationService;

    @Override
    public AcmLookup<?> getLookupByName(String name)
    {

        String lookup = getLookupsFromConfiguration();

        for (LookupType lookupType : LookupType.values())
        {

            JsonNode jsonLookupNode = JsonPath.using(configurationWithSuppressedExceptions).parse(lookup.trim())
                    .read("$." + lookupType.getTypeName() + "." + name);

            if (jsonLookupNode == null || jsonLookupNode.size() == 0)
            {
                continue;
            }

            // avoid empty arrays during deserealization
            getObjectConverter().getJsonMarshaller().getMapper().enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

            AcmLookup<?> obj = getObjectConverter().getJsonUnmarshaller().unmarshall(jsonLookupNode.toString(),
                    lookupType.getLookupClass());

            if (obj != null)
            {
                obj.setName(name);
                if (obj.getEntries() == null)
                {
                    obj.setEntries(new ArrayList<>());
                }
                return obj;
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    private String getLookupsFromConfiguration()
    {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try
        {
            Map<String, Object> lookups = convertInAcmLookupMap(lookupConfig.getLookups());
            return ow.writeValueAsString(lookups);
        }
        catch (JsonProcessingException e)
        {

            log.error("Converting of merged lookups to json format failed: ", e.getMessage());
            return null;
        }
    }

    @Override
    public String getMergedLookups()
    {
        return getLookupsFromConfiguration();
    }

    @Override
    public synchronized String saveLookup(LookupDefinition lookupDefinition)
            throws InvalidLookupException, AcmResourceNotModifiableException
    {
        boolean found = false;
        JsonNode jsonArray = JsonPath.using(configurationWithSuppressedExceptions).parse(getLookupsFromConfiguration())
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "." + lookupDefinition.getName());

        if (jsonArray != null)
        {
            // if found update the lookup entries in yaml configuration
            updateLookup(lookupDefinition);
            found = true;
        }

        if (!found)
        {
            // if not found add new empty lookup
            addNewLookup(lookupDefinition);
        }

        return getMergedLookups();
    }

    private void addNewLookup(LookupDefinition lookupDefinition) throws AcmResourceNotModifiableException
    {
        // entriesArr is empty when adding new lookup
        JSONArray entriesArr = new JSONArray(lookupDefinition.getLookupEntriesAsJson());

        if (entriesArr.length() > 0)
        {
            checkReadOnlyEntries(lookupDefinition);
        }
        else
        {
            addEmptyLookup(lookupDefinition);
        }
    }

    @Override
    public String deleteLookup(String name, String lookupType) throws AcmResourceNotFoundException, AcmResourceNotModifiableException
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

        deleteLookupDefinition(lookup.getName(), lookupType);

        return getMergedLookups();
    }

    private synchronized void updateLookup(LookupDefinition lookupDefinition)
            throws InvalidLookupException, AcmResourceNotModifiableException
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

        try
        {
            AcmLookupTransformer configurationTransformedLookup;

            AcmLookup<?> lookupsFromConfiguration = getLookupByName(lookupDefinition.getName());

            if (lookupDefinition.getLookupType().equals(LookupType.NESTED_LOOKUP))
            {
                List<NestedLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                        .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, NestedLookupEntry.class);

                configurationTransformedLookup = lookupsFromConfiguration.transformToConfigurationEntries(entries);

                updateSubLookupConfiguration(lookupDefinition, configurationTransformedLookup, lookupsFromConfiguration, entries.size());
            }
            else if (lookupDefinition.getLookupType().equals(LookupType.INVERSE_VALUES_LOOKUP))
            {
                List<InverseValuesLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                        .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, InverseValuesLookupEntry.class);

                configurationTransformedLookup = lookupsFromConfiguration.transformToConfigurationEntries(entries);

                updateSubLookupConfiguration(lookupDefinition, configurationTransformedLookup, lookupsFromConfiguration, entries.size());
            }
            else
            {
                List<StandardLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                        .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, StandardLookupEntry.class);

                configurationTransformedLookup = lookupsFromConfiguration.transformToConfigurationEntries(entries);

                updateSubLookupConfiguration(lookupDefinition, configurationTransformedLookup, lookupsFromConfiguration, entries.size());
            }
        }
        catch (RuntimeException e)
        {
            log.error("Updating lookups failed with error: '{}'! Lookup name: '{}', lookupAsJson: '{}'", e.getMessage(),
                    lookupDefinition.getName(), lookupDefinition.getLookupEntriesAsJson());
            throw new InvalidLookupException("Invalid lookup Json: " + lookupDefinition.getLookupEntriesAsJson(), e);
        }
    }

    private void updateSubLookupConfiguration(LookupDefinition lookupDefinition, AcmLookupTransformer configurationTransformedLookup,
            AcmLookup<?> lookupsFromConfiguration, int size)
    {
        if (lookupsFromConfiguration.getEntries().size() > size)
        {
            deleteLookupEntry(configurationTransformedLookup.getTransformedEntries(), lookupDefinition);
        }
        else
        {
            addNewLookupEntry(lookupDefinition, configurationTransformedLookup.getTransformedEntries());
        }
    }

    private void checkStandardLookupReadOnlyEntries(LookupDefinition lookupDefinition)
            throws AcmResourceNotModifiableException
    {

        List<StandardLookupEntry> protectedEntries = ((StandardLookup) getLookupByName(lookupDefinition.getName()))
                .getEntries().stream()
                .filter(AcmLookupEntry::isReadonly)
                .collect(Collectors.toList());

        List<StandardLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, StandardLookupEntry.class);

        for (StandardLookupEntry protectedEntry : protectedEntries)
        {
            if (entries.stream().anyMatch(entry -> entry.getKey().equals(protectedEntry.getKey())))
            {
                throw new AcmResourceNotModifiableException("Entry with key: " + protectedEntry.getKey() + " cannot be deleted");
            }
        }

    }

    private void checkInverseLookupReadOnlyEntries(LookupDefinition lookupDefinition)
            throws AcmResourceNotModifiableException
    {

        List<InverseValuesLookupEntry> protectedEntries = ((InverseValuesLookup) getLookupByName(lookupDefinition.getName()))
                .getEntries().stream()
                .filter(AcmLookupEntry::isReadonly)
                .collect(Collectors.toList());

        List<InverseValuesLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, InverseValuesLookupEntry.class);
        for (InverseValuesLookupEntry protectedEntry : protectedEntries)
        {
            if (entries.stream().anyMatch(entry -> entry.getKey().equals(protectedEntry.getKey())))
            {
                throw new AcmResourceNotModifiableException("Entry with key: " + protectedEntry.getKey() +
                        " cannot be deleted");
            }
        }

    }

    private void checkNestedLookupReadOnlyEntries(LookupDefinition lookupDefinition)
            throws AcmResourceNotModifiableException
    {

        List<NestedLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, NestedLookupEntry.class);

        List<NestedLookupEntry> protectedMainEntries = ((NestedLookup) getLookupByName(lookupDefinition.getName()))
                .getEntries().stream()
                .filter(entry -> entry.getKey().equals(entries.get(0).getKey()))
                .collect(Collectors.toList());

        // we expect that a protected entry in sublookup must have a protected main entry
        for (NestedLookupEntry protectedMainEntry : protectedMainEntries)
        {
            if (entries.stream().anyMatch(entry -> entry.getKey().equals(protectedMainEntry.getKey())))
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
            List<StandardLookupEntry> subEntries;
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

    private void checkStandardLookupPrimaryEntries(LookupDefinition lookupDefinition) throws AcmResourceNotModifiableException
    {
        List<StandardLookupEntry> entries = getObjectConverter().getJsonUnmarshaller()
                .unmarshallCollection(lookupDefinition.getLookupEntriesAsJson(), List.class, StandardLookupEntry.class);

        List<StandardLookupEntry> primaryEntry = entries.stream().filter(AcmLookupEntry::isPrimary).collect(Collectors.toList());

        if(primaryEntry.size() > 1)
        {
            throw new AcmResourceNotModifiableException("There is already primary entry in " + lookupDefinition.getName() + " lookup");
        }

    }

    private void checkReadOnlyEntries(LookupDefinition lookupDefinition) throws AcmResourceNotModifiableException
    {
        if (LookupType.STANDARD_LOOKUP.equals(lookupDefinition.getLookupType()))
        {
            checkStandardLookupReadOnlyEntries(lookupDefinition);
            checkStandardLookupPrimaryEntries(lookupDefinition);
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

    private void deleteLookupDefinition(String lookupName, String lookupType)
    {
        Map<String, Object> lookupDefinitionForDelete = new HashMap<>();

        Map<String, Object> configurationLookupMap = propertiesConfigurationService.getLookupConfiguration(
                AcmLookupConfig.LOOKUPS_PROP_KEY + "." + lookupType,
                lookupName + "." + ENTRIES_CONFIG_KEY);

        if (configurationLookupMap.isEmpty())
        {
            lookupName = MergeFlags.MERGE.getSymbol() + lookupName;
        }

        lookupDefinitionForDelete.put(AcmLookupConfig.LOOKUPS_PROP_KEY + "." + lookupType + "." + lookupName + "." + ENTRIES_CONFIG_KEY,
                MergeFlags.REMOVE.getSymbol());

        updateLookupConfiguration(lookupDefinitionForDelete);
    }

    private void deleteLookupEntry(List<Map<String, Object>> lookupEntries,
            LookupDefinition lookupDefinition)
    {
        Map<String, Object> lookupEntryForDelete = new HashMap<>();
        Map<String, Object> lookupEntriesUpdatedFromConfiguration = new HashMap<>();

        Map<String, Object> configurationLookupMap = getLookupEntryFromConfiguration(lookupDefinition);

        if (configurationLookupMap.get(ENTRIES_CONFIG_KEY) != null)
        {
            lookupEntriesUpdatedFromConfiguration = (Map<String, Object>) configurationLookupMap.get(ENTRIES_CONFIG_KEY);
        }

        for (Map<String, Object> lookupEntry : lookupEntries)
        {
            String lookupEntryKey = (String) lookupEntry.get("key");

            if (!lookupEntriesUpdatedFromConfiguration.isEmpty())
            {

                if (lookupEntriesUpdatedFromConfiguration.get(lookupEntryKey) != null)
                {
                    lookupEntriesUpdatedFromConfiguration.remove(lookupEntryKey);
                }
                lookupEntriesUpdatedFromConfiguration
                        .putAll(convertIntoConfigurationEntry(lookupEntry, MergeFlags.REMOVE.getSymbol()));

            }
            else
            {
                lookupEntriesUpdatedFromConfiguration
                        .putAll(convertIntoConfigurationEntry(lookupEntry, MergeFlags.REMOVE.getSymbol()));

            }
        }

        lookupEntryForDelete.put(AcmLookupConfig.LOOKUPS_PROP_KEY + "." + lookupDefinition.getLookupType().getTypeName() + "."
                + lookupDefinition.getName() + "." + ENTRIES_CONFIG_KEY,
                lookupEntriesUpdatedFromConfiguration);

        updateLookupConfiguration(lookupEntryForDelete);
    }

    @Override
    public String deleteSubLookup(String subLookupName, String parentName, LookupDefinition lookupDefinition)
            throws AcmResourceNotFoundException, AcmResourceNotModifiableException
    {

        AcmLookup<?> lookup = getLookupByName(lookupDefinition.getName());

        if (lookup == null)
        {
            throw new AcmResourceNotFoundException("Lookup with name: '" + parentName + "' does not exist!");
        }

        if (lookup.isReadonly())
        {
            throw new AcmResourceNotModifiableException("Lookup with name: '" + parentName + "' is readonly!");
        }

        deleteSubLookupDefinition(subLookupName, parentName, lookupDefinition);

        return getMergedLookups();
    }

    private void deleteSubLookupDefinition(String subLookupName, String parentName, LookupDefinition lookupDefinition)
    {
        Map<String, Object> configurationLookupMap = getLookupEntryFromConfiguration(lookupDefinition);
        Map<String, Object> updatedConfigurationMap = new HashMap<>();
        Map<String, Object> subLookupEntries = new HashMap<>();
        Map<String, Object> nestedEntrySublookups = new HashMap<>();
        Map<String, Object> nestedConfigurationMap = new HashMap<>();

        if (configurationLookupMap.isEmpty())
        {
            getRemoveSublookupEntryKey(subLookupName, parentName, lookupDefinition, configurationLookupMap);
            updateLookupConfiguration(configurationLookupMap);
        }
        else
        {
            Map<String, Object> parentEntries = (Map<String, Object>) configurationLookupMap.get(ENTRIES_CONFIG_KEY);

            if (parentEntries != null)
            {
                Map<String, Object> parentMergedEntries = (Map<String, Object>) parentEntries.get(parentName);
                Map<String, Object> parentSubLookups = (Map<String, Object>) parentMergedEntries.get(SUBLOOKUP_CONFIG_KEY);
                parentSubLookups.remove(subLookupName);

                subLookupEntries.putAll(parentSubLookups);
            }

            if (nestedEntrySublookups.get(parentName) == null)
            {
                subLookupEntries.put(MergeFlags.REMOVE.getSymbol() + subLookupName, "");
            }
            else
            {
                subLookupEntries = (Map<String, Object>) nestedEntrySublookups.get(SUBLOOKUP_CONFIG_KEY);

                if (subLookupEntries.get(subLookupName) == null)
                {
                    subLookupEntries.put(MergeFlags.REMOVE.getSymbol() + subLookupName, "");

                }
                else
                {
                    subLookupEntries.remove(subLookupName);
                    subLookupEntries.put(MergeFlags.REMOVE.getSymbol() + subLookupName, "");
                }
            }

            nestedEntrySublookups.put(SUBLOOKUP_CONFIG_KEY, subLookupEntries);
            updatedConfigurationMap.put(parentName, nestedEntrySublookups);

            nestedConfigurationMap.put(AcmLookupConfig.LOOKUPS_PROP_KEY + "." + lookupDefinition.getLookupType().getTypeName() + "."
                    + lookupDefinition.getName() + "." + ENTRIES_CONFIG_KEY, updatedConfigurationMap);

            updateLookupConfiguration(nestedConfigurationMap);

        }
    }

    private void getRemoveSublookupEntryKey(String subLookupName, String parentName, LookupDefinition lookupDefinition,
            Map<String, Object> configurationLookupMap)
    {
        configurationLookupMap.put(AcmLookupConfig.LOOKUPS_PROP_KEY + "." + lookupDefinition.getLookupType().getTypeName() + "."
                + lookupDefinition.getName() + "." + ENTRIES_CONFIG_KEY + "." + parentName + "." + SUBLOOKUP_CONFIG_KEY + "."
                + MergeFlags.REMOVE.getSymbol()
                + subLookupName,
                "");
    }

    private void updateLookupConfiguration(Map<String, Object> configurationMap)
    {
        configurationPropertyService.updateProperties(configurationMap, "lookups");
        log.info("Configuration updated successfully");
    }

    private void addEmptyLookup(LookupDefinition lookupDefinition)
    {

        Map<String, Object> lookupEntry = new HashMap<>();

        lookupEntry.put(AcmLookupConfig.LOOKUPS_PROP_KEY + "." + lookupDefinition.getLookupType().getTypeName() + "."
                + lookupDefinition.getName()
                + "." + ENTRIES_CONFIG_KEY, "");

        updateLookupConfiguration(lookupEntry);
    }

    private void addNewLookupEntry(LookupDefinition lookupDefinition, List<Map<String, Object>> lookupEntries)
    {

        Map<String, Object> lookupEntry = addNewLookupEntryToConfiguration(lookupDefinition, lookupEntries);

        updateLookupConfiguration(lookupEntry);
    }

    private Map<String, Object> addNewLookupEntryToConfiguration(LookupDefinition lookupDefinition, List<Map<String, Object>> lookupEntries)
    {
        Map<String, Object> lookupEntriesUpdated = new HashMap<>();
        Map<String, Object> lookupEntry = new HashMap<>();

        Map<String, Object> configurationLookupMap = getLookupEntryFromConfiguration(lookupDefinition);

        if (configurationLookupMap.size() > 0)
        {
            if (configurationLookupMap.get(ENTRIES_CONFIG_KEY) == "")
            {
                configurationLookupMap.remove(ENTRIES_CONFIG_KEY);
            }
            else
            {
                lookupEntriesUpdated = (Map<String, Object>) configurationLookupMap.get(ENTRIES_CONFIG_KEY);
            }
        }

        for (Map<String, Object> entries : lookupEntries)
        {
            lookupEntriesUpdated.putAll(convertIntoConfigurationEntry(entries, null));
        }

        lookupEntry.put(AcmLookupConfig.LOOKUPS_PROP_KEY + "." + lookupDefinition.getLookupType().getTypeName() + "."
                + lookupDefinition.getName()
                + "." + ENTRIES_CONFIG_KEY,
                lookupEntriesUpdated);

        return lookupEntry;
    }

    private Map<String, Object> convertIntoConfigurationEntry(Map<String, Object> lookupEntries, String operation)
    {
        Map<String, Object> confEntry = new HashMap<>();
        String uniqueKey = (String) lookupEntries.get("key");

        lookupEntries.remove("key");

        if (operation != null)
        {
            confEntry.put(operation + uniqueKey, "");
            confEntry.put(operation + uniqueKey, lookupEntries);
        }
        else
        {
            confEntry.put(uniqueKey, "");
            confEntry.put(uniqueKey, lookupEntries);
        }
        return confEntry;
    }

    private Map<String, Object> getLookupEntryFromConfiguration(LookupDefinition lookupDefinition)
    {
        Map<String, Object> configurationLookupMapp = propertiesConfigurationService.getLookupConfiguration(
                AcmLookupConfig.LOOKUPS_PROP_KEY + "." + lookupDefinition.getLookupType().getTypeName(),
                lookupDefinition.getName() + "." + ENTRIES_CONFIG_KEY);
        configurationLookupMapp = (Map<String, Object>) configurationLookupMapp.get(lookupDefinition.getLookupType().getTypeName());

        if (configurationLookupMapp != null)
        {
            return (Map<String, Object>) configurationLookupMapp.get(lookupDefinition.getName());
        }
        else
        {
            configurationLookupMapp = propertiesConfigurationService.getLookupConfiguration(
                    AcmLookupConfig.LOOKUPS_PROP_KEY + "." + lookupDefinition.getLookupType().getTypeName(),
                    MergeFlags.MERGE.getSymbol() + lookupDefinition.getName() + "." + ENTRIES_CONFIG_KEY);
            configurationLookupMapp = (Map<String, Object>) configurationLookupMapp.get(lookupDefinition.getLookupType().getTypeName());

            if (configurationLookupMapp != null)
            {
                return (Map<String, Object>) configurationLookupMapp.get(MergeFlags.MERGE.getSymbol() + lookupDefinition.getName());
            }
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> convertInAcmLookupMap(Map<String, Object> lookups)
    {

        Map<String, Object> acmLookupMap = new HashMap<>();

        for (String lookupType : lookups.keySet())
        {

            Map<String, Object> lookupEntries = (Map<String, Object>) lookups.get(lookupType);

            Map<String, Object> updatedLookupEntries = new HashMap<>();

            for (String entryTypeKey : lookupEntries.keySet())
            {
                Map<String, Object> updatedLookup = new HashMap<>();
                List<Map<String, Object>> updatedEntries = new ArrayList<>();

                Map<String, Object> specifiedLookupEntries = (Map<String, Object>) lookupEntries.get(entryTypeKey);

                if (specifiedLookupEntries.get(ENTRIES_CONFIG_KEY) != "")
                {
                    Map<String, Object> entries = (Map<String, Object>) specifiedLookupEntries.get(ENTRIES_CONFIG_KEY);

                    for (String standardEntryKey : entries.keySet())
                    {
                        Map<String, Object> entry = (Map<String, Object>) entries.get(standardEntryKey);

                        if (lookupType.equals(LookupType.INVERSE_VALUES_LOOKUP.getTypeName()))
                        {
                            updatedEntries.add(AcmLookupTransformer.transformIntoInverseLookupEntry(standardEntryKey, entry));
                        }
                        else if (lookupType.equals(LookupType.STANDARD_LOOKUP.getTypeName()))
                        {
                            updatedEntries.add(AcmLookupTransformer.transformIntoStandardLookupEntry(standardEntryKey, entry));
                        }
                        else if (lookupType.equals(LookupType.NESTED_LOOKUP.getTypeName()))
                        {
                            updatedEntries.add(AcmLookupTransformer.transformIntoNestedLookupEntry(standardEntryKey, entry));
                        }
                    }

                    if (!updatedEntries.isEmpty())
                    {
                        updatedEntries.sort(Comparator.comparing(entry -> (Integer) entry.get("order")));
                    }

                    updatedLookup.put(ENTRIES_CONFIG_KEY, updatedEntries);
                }
                else
                {
                    updatedLookup.put(ENTRIES_CONFIG_KEY, "");
                }
                updatedLookup.put("readonly", specifiedLookupEntries.get("readonly"));

                updatedLookupEntries.put(entryTypeKey, updatedLookup);
            }
            acmLookupMap.put(lookupType, updatedLookupEntries);
        }

        return acmLookupMap;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter converter)
    {
        this.objectConverter = converter;
    }

    public void setLookupConfig(AcmLookupConfig lookupConfig)
    {
        this.lookupConfig = lookupConfig;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public void setPropertiesConfigurationService(CollectionPropertiesConfigurationService propertiesConfigurationService)
    {
        this.propertiesConfigurationService = propertiesConfigurationService;
    }
}

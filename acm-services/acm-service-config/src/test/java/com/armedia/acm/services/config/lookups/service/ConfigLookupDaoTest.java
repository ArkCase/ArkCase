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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.armedia.acm.core.exceptions.AcmResourceNotFoundException;
import com.armedia.acm.core.exceptions.AcmResourceNotModifiableException;
import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.model.LookupType;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class ConfigLookupDaoTest extends EasyMockSupport
{
    private static final Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();
    private final String lookupsExtFileLocation = "lookups-ext.json";
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private ConfigLookupDao configLookupDao;

    @Before
    public void setUp() throws Exception
    {
        configLookupDao = new ConfigLookupDao();
        configLookupDao.setObjectConverter(ObjectConverter.createObjectConverterForTests());
        configLookupDao.setLookupsExtFileLocation(folder.getRoot().getAbsolutePath() + "/" + lookupsExtFileLocation);
    }

    @Test
    public void testSaveLookupAddEntryToCoreLookupSuccess() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[{\"name\":\"colors\", \"entries\":[], \"readonly\":true}]}");
        configLookupDao.setLookupsExt("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(entriesAsJson, updatedValue.get(0).toString(), false);
    }

    @Test
    public void testSaveLookupUpdateInverseLookupEntriesToExt() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.INVERSE_VALUES_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"inverseKey\":\"someKey\",\"inverseValue\":\"someValue\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [{\"name\":\"colors\", \"entries\":[], \"readonly\":true}], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(entriesAsJson, updatedValue.get(0).toString(), false);
    }

    @Test
    public void testSaveLookupAddInverseEntriesToExtLookupSuccess() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.INVERSE_VALUES_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"inverseKey\":\"someKey\",\"inverseValue\":\"someValue\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [{\"name\":\"colors\", \"entries\":[], \"readonly\":true}], \"nestedLookup\": [],\"standardLookup\":[]}");
        configLookupDao.setLookupsExt("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(entriesAsJson, updatedValue.get(0).toString(), false);
    }

    @Test
    public void testSaveLookupAddEntryToCoreAndExtLookupSuccess() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\"},{\"key\":\"someKey1\",\"value\":\"someValue1\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[{\"name\":\"colors\", \"entries\":[], \"readonly\":true}]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": [{\"name\":\"colors\", \"entries\":[{\"key\":\"someKey\",\"value\":\"someValue\"}], \"readonly\":true}]}");

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(entriesAsJson, updatedValue.get(0).toString(), false);
    }

    @Test
    public void testSaveLookupToExtLookupSuccess() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[]}");
        configLookupDao.setLookups("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(entriesAsJson, updatedValue.get(0).toString(), true);
    }

    @Test
    public void testSaveLookupNestedLookupSuccess() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.NESTED_LOOKUP);
        lookupDefinition.setName("contactMethodTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"phone\",\"value\":\"lookups.contactMethodTypes.phone\",\"subLookup\":[{\"key\":\"Home1\",\"value\":\"lookups.common.home\"},{\"key\":\"Work\",\"value\":\"lookups.contactMethodTypes.work\"},{\"key\":\"Mobile\",\"value\":\"lookups.contactMethodTypes.mobile\"}]},{\"key\":\"Fax\",\"value\":\"lookups.contactMethodTypes.fax\",\"subLookup\":[]},{\"key\":\"email\",\"value\":\"lookups.contactMethodTypes.email\",\"subLookup\":[{\"key\":\"Personal\",\"value\":\"Personal\"},{\"key\":\"Business\",\"value\":\"Business\"}]},{\"key\":\"url\",\"value\":\"Url\",\"subLookup\":[{\"key\":\"Web Site\",\"value\":\"Web Site\"},{\"key\":\"Facebook\",\"value\":\"Facebook\"},{\"key\":\"LinkedIn\",\"value\":\"LinkedIn\"},{\"key\":\"Twitter\",\"value\":\"Twitter\"},{\"key\":\"Other\",\"value\":\"Other\"}]}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [{\"name\":\"contactMethodTypes\", \"entries\":[], \"readonly\":true}],\"standardLookup\":[]}");
        configLookupDao.setLookupsExt("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(entriesAsJson, updatedValue.get(0).toString(), false);
    }

    @Test(expected = InvalidLookupException.class)
    public void testSaveLookupThrowsExceptionOnInvalidLookupJson() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        String lookupAsJson = "[\"key\":\"someKey\", \"value\":\"someValue\"}]";
        lookupDefinition.setLookupEntriesAsJson(lookupAsJson);

        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[{\"name\":\"colors\", \"entries\":[], \"readonly\":true}]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\":[{\"name\":\"colors\", \"entries\":[], \"readonly\":true}]}");

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("Should have thrown InvalidLookupException!");
    }

    @Test(expected = InvalidLookupException.class)
    public void testSaveLookupThrowsExceptionOnDuplicateKeys() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        String lookupAsJson = "[{\"key\":\"someKey\", \"value\":\"someValue\"}, {\"key\":\"someKey\", \"value\":\"someValue1\"}]";
        lookupDefinition.setLookupEntriesAsJson(lookupAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[{\"name\":\"colors\", \"entries\":[], \"readonly\":true}]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\":[{\"name\":\"colors\", \"entries\":[], \"readonly\":true}]}");

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("Should have thrown InvalidLookupException!");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetLookupByNameReturnsCorrectAcmLookupFromCoreLookups()
    {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\""
                + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";
        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName(lookupName);

        // then
        assertEquals(lookupName, acmLookup.getName());
        assertTrue(acmLookup.getEntries().size() == 2);
        assertEquals(key1, acmLookup.getEntries().get(0).getKey());
        assertEquals(value1, acmLookup.getEntries().get(0).getValue());
        assertEquals(key2, acmLookup.getEntries().get(1).getKey());
        assertEquals(value2, acmLookup.getEntries().get(1).getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetLookupByNameReturnsCorrectAcmLookupFromExtLookups()
    {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\""
                + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";
        configLookupDao.setLookups("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");
        configLookupDao.setLookupsExt(lookups);

        // when
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName(lookupName);

        // then
        assertEquals(lookupName, acmLookup.getName());
        assertTrue(acmLookup.getEntries().size() == 2);
        assertEquals(key1, acmLookup.getEntries().get(0).getKey());
        assertEquals(value1, acmLookup.getEntries().get(0).getValue());
        assertEquals(key2, acmLookup.getEntries().get(1).getKey());
        assertEquals(value2, acmLookup.getEntries().get(1).getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetLookupByNameReturnsCorrectAcmLookupFromMergedLookups()
    {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\""
                + value1 +
                "\"}],\"readonly\":\"" + readonly + "\"}]}";
        String extLookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\""
                + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt(extLookups);

        // when
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName(lookupName);

        // then
        assertEquals(lookupName, acmLookup.getName());
        assertTrue(acmLookup.getEntries().size() == 2);
        assertEquals(key1, acmLookup.getEntries().get(0).getKey());
        assertEquals(value1, acmLookup.getEntries().get(0).getValue());
        assertEquals(key2, acmLookup.getEntries().get(1).getKey());
        assertEquals(value2, acmLookup.getEntries().get(1).getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetLookupByNameReturnsNullForUnknownLookup()
    {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\""
                + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName("unknown");

        // then
        assertNull(acmLookup);
    }

    @Test
    public void testMergeLookupsUpdateLookup()
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\""
                + value1 + "\"}, {\"key\":\"" + key2
                + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";

        lookupDefinition.setName(lookupName);
        lookupDefinition.setReadonly(readonly);

        String lookupNameExt = "lookupName";
        String key1Ext = "someKey1Ext";
        String value1Ext = "someValue1Ext";
        String key2Ext = "someKey2Ext";
        String value2Ext = "someValue2Ext";
        Boolean readonlyExt = false;
        String updatedEntries = "[{\"key\":\"" + key1Ext + "\",\"value\":\"" + value1Ext
                + "\"}, {\"key\":\"" + key2Ext + "\",\"value\":\"" + value2Ext + "\"}]";
        String lookupsExt = "{\"standardLookup\":[{\"name\":\"" + lookupNameExt + "\",\"entries\":" + updatedEntries + ",\"readonly\":\""
                + readonlyExt + "\"}]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt(lookupsExt);

        // when
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(mergedLookups)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(updatedEntries, updatedValue.get(0).toString(), true);
    }

    @Test
    public void testMergeLookupsUpdateLookupWithEmptyEntries()
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\", \"entries\":[{\"key\":\"" + key1 + "\",\"value\":\""
                + value1 + "\"}, {\"key\":\"" + key2
                + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";
        lookupDefinition.setName(lookupName);
        lookupDefinition.setReadonly(readonly);

        String lookupNameExt = "lookupName";
        Boolean readonlyExt = false;
        String updatedEntries = "[]";
        String lookupsExt = "{\"standardLookup\":[{\"name\":\"" + lookupNameExt + "\", \"entries\":" + updatedEntries + ",\"readonly\":\""
                + readonlyExt + "\"}]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt(lookupsExt);

        // when
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(mergedLookups)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(updatedEntries, updatedValue.get(0).toString(), true);
    }

    @Test
    public void testMergeLookupsUpdateInverseLookup()
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.INVERSE_VALUES_LOOKUP);
        String inverseLookupName = "inverseLookupName";
        String inverseKey1 = "someInverseKey1";
        String inverseValue1 = "someInverseValue1";
        String keyInv2 = "someKey2";
        String valueInv2 = "someValue2";
        Boolean readonlyInv = true;
        lookupDefinition.setName(inverseLookupName);
        lookupDefinition.setReadonly(readonlyInv);

        String lookups = "{\"standardLookup\":[],\"nestedLookup\":[],\"inverseValuesLookup\":[{\"name\":\""
                + inverseLookupName + "\", \"entries\":[{\"inverseKey\":\"" + inverseKey1 + "\",\"inverseValue\":\"" + inverseValue1
                + "\",\"key\":\"" + keyInv2 + "\",\"value\":\"" + valueInv2 + "\"}],\"readonly\":\"" + readonlyInv + "\"}]}";

        String inverseLookupNameExt = "inverseLookupName";
        String inverseKey1Ext = "someInverseKey1Ext";
        String inverseValue1Ext = "someInverseValue1Ext";
        String keyInv2Ext = "someKey2Ext";
        String valueInv2Ext = "someValue2Ext";
        Boolean readonlyInvExt = false;
        String updatedEntries = "[{\"inverseKey\":\"" + inverseKey1Ext
                + "\",\"inverseValue\":\"" + inverseValue1Ext + "\",\"key\":\"" + keyInv2Ext + "\",\"value\":\"" + valueInv2Ext
                + "\"}]";

        String lookupsExt = "{\"standardLookup\":[], \"nestedLookup\":[] ,\"inverseValuesLookup\":[{\"name\":\"" + inverseLookupNameExt
                + "\", \"entries\":[{\"inverseKey\":\"" + inverseKey1Ext
                + "\",\"inverseValue\":\"" + inverseValue1Ext + "\",\"key\":\"" + keyInv2Ext + "\",\"value\":\"" + valueInv2Ext
                + "\"}],\"readonly\":\"" + readonlyInvExt + "\"}]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt(lookupsExt);

        // when
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(mergedLookups)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(updatedEntries, updatedValue.get(0).toString(), true);

    }

    @Test
    public void testMergeLookupsAddInverseLookup()
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.INVERSE_VALUES_LOOKUP);
        String inverseLookupName = "inverseLookupName";
        String inverseKey1 = "someInverseKey1";
        String inverseValue1 = "someInverseValue1";
        String keyInv2 = "someKey2";
        String valueInv2 = "someValue2";
        Boolean readonlyInv = true;
        lookupDefinition.setName(inverseLookupName);
        lookupDefinition.setReadonly(readonlyInv);

        String lookups = "{\"standardLookup\":[],\"nestedLookup\":[],\"inverseValuesLookup\":[{\"name\":\""
                + inverseLookupName + "\", \"entries\":[{\"inverseKey\":\"" + inverseKey1 + "\",\"inverseValue\":\"" + inverseValue1
                + "\",\"key\":\"" + keyInv2 + "\",\"value\":\"" + valueInv2 + "\"}],\"readonly\":\"" + readonlyInv + "\"}]}";

        String updatedEntries = "[{\"inverseKey\":\"" + inverseKey1
                + "\",\"inverseValue\":\"" + inverseValue1 + "\",\"key\":\"" + keyInv2 + "\",\"value\":\"" + valueInv2
                + "\"}]";

        String lookupsExt = "{\"standardLookup\":[], \"nestedLookup\":[] ,\"inverseValuesLookup\":[]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt(lookupsExt);

        // when
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(mergedLookups)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(updatedEntries, updatedValue.get(0).toString(), true);

    }

    @Test
    public void testDeleteLookupFromExtLookups() throws Exception
    {
        // given
        String lookupName = "lookupName";
        Boolean readonlyExt = false;
        String lookups = "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}";
        String lookupNameTmp = "lookupNameTmp";
        Boolean readonlyExTmp = false;
        String extLookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[],\"readonly\":\"" + readonlyExt
                + "\"},{\"name\":\"" + lookupNameTmp + "\",\"entries\":[],\"readonly\":\"" + readonlyExTmp + "\"}]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt(extLookups);

        // when
        String returnedLookups = configLookupDao.deleteLookup(lookupName);

        // then
        ArrayNode returnedLookup = JsonPath.using(configuration).parse(returnedLookups)
                .read("$.standardLookup..[?(@.name=='" + lookupName
                        + "')]");

        assertEquals(0, returnedLookup.size());
    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testDeleteLookupThatCantBeDeletedReturnNotModifiableException() throws Exception
    {
        // given
        String lookupName = "lookupName";
        Boolean readonlyExt = true;
        String lookups = "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}";
        String extLookups = "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[{\"name\":\"" + lookupName
                + "\",\"entries\":[],\"readonly\":\"" + readonlyExt + "\"}]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt(extLookups);

        // when
        configLookupDao.deleteLookup(lookupName);

        // then
        fail("Should have thrown AcmResourceNotModifiableException");
    }

    @Test(expected = AcmResourceNotFoundException.class)
    public void testDeleteLookupReturnsNotFoundExceptionForUnknownLookup() throws Exception
    {
        // given
        String lookupName = "lookupName";
        String unknownLookupName = "unknown";
        Boolean readonlyExt = true;
        String lookups = "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}";
        String extLookups = "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[{\"name\":\"" + lookupName
                + "\",\"entries\":[],\"readonly\":\"" + readonlyExt + "\"}]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt(extLookups);

        // when
        configLookupDao.deleteLookup(unknownLookupName);

        fail("Should have thrown AcmResourceNotFoundException");
    }

    @Test
    public void testOrderOfLookupEntries()
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        String lookupName = "someLookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\""
                + value1 + "\"}, {\"key\":\"" + key2
                + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";

        lookupDefinition.setName(lookupName);
        lookupDefinition.setReadonly(readonly);

        String lookupNameExt = "someLookupName";
        String key1Ext = "someKey2";
        String value1Ext = "someValue2";
        String key2Ext = "someKey1";
        String value2Ext = "someValue1";
        Boolean readonlyExt = false;
        String updatedEntries = "[{\"key\":\"" + key1Ext + "\",\"value\":\"" + value1Ext
                + "\"}, {\"key\":\"" + key2Ext + "\",\"value\":\"" + value2Ext + "\"}]";
        String lookupsExt = "{\"standardLookup\":[{\"name\":\"" + lookupNameExt + "\",\"entries\":" + updatedEntries + ",\"readonly\":\""
                + readonlyExt + "\"}]}";

        configLookupDao.setLookups(lookups);
        configLookupDao.setLookupsExt(lookupsExt);

        // when
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(mergedLookups)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(updatedValue.get(0).toString(), updatedEntries, true);
    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testCheckDeleteProtectedEntry() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"readonly\":true}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": [{\"name\":\"colors\", \"entries\":[{\"key\":\"someKey\",\"value\":\"someValue\",\"readonly\":true},{\"key\":\"someKey2\",\"value\":\"someValue2\",\"readonly\":true}], \"readonly\":true}]}");

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("AcmResourceNotModifiableException should have been thrown");
    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testCheckReadOnlyEntriesOnUpdate() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("deviceTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"email\",\"value\":\"Email\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": [{\"name\":\"deviceTypes\", \"entries\":[{\"key\":\"email\",\"value\":\"Email\"},{\"key\":\"mobile\",\"value\":\"Mobile\",\"readonly\":true}], \"readonly\":true}]}");

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("AcmResourceNotModifiableException should have been thrown");
    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testCheckDeleteProtectedEntryOnAdding() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("deviceTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"email\",\"value\":\"Email\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": [{\"name\":\"deviceTypes\", \"entries\":[{\"key\":\"email\",\"value\":\"Email\"},{\"key\":\"mobile\",\"value\":\"Mobile\",\"readonly\":true}], \"readonly\":true}]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[]}");

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("AcmResourceNotModifiableException should have been thrown");
    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testCheckDeleteProtectedEntryInInverseLookup() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.INVERSE_VALUES_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"inverseKey\":\"someKey\",\"inverseValue\":\"someValue\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [{\"name\":\"colors\", \"entries\":[{\"key\":\"someKey\",\"value\":\"someValue\",\"inverseKey\":\"someKey\",\"inverseValue\":\"someValue\",\"readonly\":true},{\"key\":\"someKey2\",\"value\":\"someValue2\",\"inverseKey\":\"someInvKey2\",\"inverseValue\":\"someInvValue2\",\"readonly\":true}], \"readonly\":true}], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("AcmResourceNotModifiableException should have been thrown");
    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testCheckReadonlyEntriesInNestedLookup() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.NESTED_LOOKUP);
        lookupDefinition.setName("contactMethodTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"subLookup\":[{\"value\":\"core.lookups.common.home\",\"key\":\"Home\",\"readonly\": true},{\"key\":\"Work\",\"value\":\"work\"},{\"value\":\"core.lookups.contactMethodTypes.mobile\",\"key\":\"Mobile\",\"readonly\":true}],\"value\":\"core.lookups.contactMethodTypes.phone\",\"key\":\"phone\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        configLookupDao.setLookups("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [{\"name\":\"contactMethodTypes\",\"readonly\":true,\"entries\":[{\"subLookup\":[{\"value\":\"core.lookups.common.home\",\"key\":\"Home\",\"readonly\": true},{\"key\":\"Work\",\"value\":\"work\"},{\"value\":\"core.lookups.contactMethodTypes.mobile\",\"key\":\"Mobile\",\"readonly\":true}],\"value\":\"core.lookups.contactMethodTypes.phone\",\"key\":\"phone\"},{\"subLookup\":[{\"value\":\"core.lookups.contactMethodTypes.fax\",\"key\":\"Fax\"}],\"value\":\"core.lookups.contactMethodTypes.fax\",\"key\":\"fax\",\"readonly\":true}]}], \"standardLookup\": []}");

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("AcmResourceNotModifiableException should have been thrown");

    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testCheckReadonlyEntriesInSubLookupInNestedLookup() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.NESTED_LOOKUP);
        lookupDefinition.setName("contactMethodTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"subLookup\":[{\"value\":\"core.lookups.contactMethodTypes.mobile\",\"key\":\"Mobile\",\"readonly\":true}],\"value\":\"core.lookups.contactMethodTypes.phone\",\"key\":\"phone\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        configLookupDao.setLookups("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [{\"name\":\"contactMethodTypes\",\"readonly\":true,\"entries\":[{\"subLookup\":[{\"value\":\"core.lookups.common.home\",\"key\":\"Home\",\"readonly\": true},{\"value\":\"core.lookups.contactMethodTypes.mobile\",\"key\":\"Mobile\",\"readonly\":true}],\"value\":\"core.lookups.contactMethodTypes.phone\",\"key\":\"phone\",\"readonly\":true}]}], \"standardLookup\": []}");

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("AcmResourceNotModifiableException should have been thrown");

    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testPrimaryEntriesOnUpdate() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("componentsAgencies");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"FOIA\",\"value\":\"FOIA\",\"readonly\":false,\"primary\":true}, {\"key\":\"sales\",\"value\":\"Sales\",\"readonly\":false,\"primary\":true}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": [{\"name\":\"componentsAgencies\", \"entries\":[{\"key\":\"FOIA\",\"value\":\"FOIA\",\"readonly\":false,\"primary\":true}, {\"key\":\"sales\",\"value\":\"Sales\",\"readonly\":false,\"primary\":false}], \"readonly\":true}]}");

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("AcmResourceNotModifiableException should have been thrown");
    }

    @Test
    public void testAddLookupWithDescriptionEntry() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"readonly\":false,\"description\":\"someDescription\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[{\"name\":\"colors\", \"entries\":[], \"readonly\":true}]}");
        configLookupDao.setLookupsExt("{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(entriesAsJson, updatedValue.get(0).toString(), false);
    }

    @Test
    public void testUpdateLookupWithDescriptionEntry() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(false);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"readonly\":false,\"description\":\"someDescription2\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [], \"standardLookup\": [{\"name\":\"colors\", \"entries\":[{\"key\":\"someKey\",\"value\":\"someValue\",\"readonly\":false,\"description\":\"someDescription\"}], \"readonly\":true}]}");

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(entriesAsJson, updatedValue.get(0).toString(), false);
    }

    @Test
    public void testUpdateInverseLookupEntriesWithDescription() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.INVERSE_VALUES_LOOKUP);
        lookupDefinition.setName("colors");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"inverseKey\":\"someKey\",\"inverseValue\":\"someValue\",\"description\":\"someDescription\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);
        configLookupDao.setLookups(
                "{\"inverseValuesLookup\": [], \"nestedLookup\": [],\"standardLookup\":[]}");
        configLookupDao.setLookupsExt(
                "{\"inverseValuesLookup\": [{\"name\":\"colors\", \"entries\":[], \"readonly\":true}], \"nestedLookup\": [], \"standardLookup\": []}");

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertEquals(entriesAsJson, updatedValue.get(0).toString(), false);
    }

}

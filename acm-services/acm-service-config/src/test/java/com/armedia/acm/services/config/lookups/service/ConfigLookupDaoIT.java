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
import static org.junit.Assert.fail;

import com.armedia.acm.core.exceptions.AcmResourceNotFoundException;
import com.armedia.acm.core.exceptions.AcmResourceNotModifiableException;
import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.model.LookupType;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "spring", locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-config-plugin-test.xml",
})
public class ConfigLookupDaoIT
{

    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    private static final Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();

    @Autowired
    private ConfigLookupDao configLookupDao;

    @Test
    public void testSaveLookupAddEntryToCoreLookupSuccess() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("timesheetTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(entriesAsJson, updatedValue.toString(), true);

    }

    @Test
    public void testSaveLookupUpdateInverseLookupEntries() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.INVERSE_VALUES_LOOKUP);
        lookupDefinition.setName("personOrganizationRelationTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"inverseKey\":\"someKey\",\"inverseValue\":\"someValue\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(entriesAsJson, updatedValue.toString(), true);
    }

    @Test
    public void testSaveLookupLookupSuccess() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("locationTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"Business\", \"value\":\"core.lookups.addressTypes.business\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(entriesAsJson, updatedValue.toString(), true);
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

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(entriesAsJson, updatedValue.toString(), true);
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
        String lookupName = "timesheetTypes";
        String key1 = "CASE_FILE";
        String value1 = "core.lookups.timesheet.types.case_file";
        String key2 = "CONSULTATION";
        String value2 = "core.lookups.timesheet.types.consultation";
        // when
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName(lookupName);

        // then
        assertEquals(lookupName, acmLookup.getName());
        assertEquals(4, acmLookup.getEntries().size());
        assertEquals(key1, acmLookup.getEntries().get(0).getKey());
        assertEquals(value1, acmLookup.getEntries().get(0).getValue());
        assertEquals(key2, acmLookup.getEntries().get(1).getKey());
        assertEquals(value2, acmLookup.getEntries().get(1).getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetLookupByNameReturnsCorrectAcmLookupFromConfiguration()
    {
        // given
        String lookupName = "dbasTypes";
        String key1 = "DBA";
        String value1 = "core.lookups.dbasTypes.dba";

        // when
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName(lookupName);

        // then
        assertEquals(lookupName, acmLookup.getName());
        assertEquals(1, acmLookup.getEntries().size());
        assertEquals(key1, acmLookup.getEntries().get(0).getKey());
        assertEquals(value1, acmLookup.getEntries().get(0).getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetLookupByNameReturnsCorrectAcmLookupFromMergedLookups()
    {
        // given
        String lookupName = "caseFilePersonInitiatorTypes";
        String key1 = "Initiator";
        String value1 = "core.lookups.complaintPersonTypes.initiator";

        // when
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName(lookupName);

        // then
        assertEquals(lookupName, acmLookup.getName());
        assertEquals(1, acmLookup.getEntries().size());
        assertEquals(key1, acmLookup.getEntries().get(0).getKey());
        assertEquals(value1, acmLookup.getEntries().get(0).getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetLookupByNameReturnsNullForUnknownLookup()
    {
        // when
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName("unknown");

        // then
        assertNull(acmLookup);
    }

    @Test
    public void testMergeLookupsUpdateLookupWithEmptyEntries()
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        String lookupName = "locationTypes";
        Boolean readonly = true;

        lookupDefinition.setName(lookupName);
        lookupDefinition.setReadonly(readonly);

        String updatedEntries = "{}";

        // when
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(mergedLookups)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(updatedEntries, updatedValue.toString(), true);
    }

    @Test
    public void testMergeLookupsUpdateInverseLookup()
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.INVERSE_VALUES_LOOKUP);
        String inverseLookupName = "organizationRelationTypes";
        Boolean readonlyInv = true;
        lookupDefinition.setName(inverseLookupName);
        lookupDefinition.setReadonly(readonlyInv);

        String inverseKey1Ext = "someInverseKey1Ext";
        String inverseValue1Ext = "someInverseValue1Ext";
        String keyInv2Ext = "someKey2Ext";
        String valueInv2Ext = "someValue2Ext";
        String updatedEntries = "[{\"inverseKey\":\"" + inverseKey1Ext
                + "\",\"inverseValue\":\"" + inverseValue1Ext + "\",\"key\":\"" + keyInv2Ext + "\",\"value\":\"" + valueInv2Ext
                + "\"}]";

        // when
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(mergedLookups)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(updatedEntries, updatedValue.toString(), true);

    }

    @Test
    public void testDeleteLookupFromConfiguration() throws Exception
    {
        // given
        String lookupName = "costsheetTypes";

        // when
        String returnedLookups = configLookupDao.deleteLookup(lookupName, "standardLookup");

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
        String lookupName = "locationTypes";

        // when
        configLookupDao.deleteLookup(lookupName, "standardLookup");

        // then
        fail("Should have thrown AcmResourceNotModifiableException");
    }

    @Test(expected = AcmResourceNotFoundException.class)
    public void testDeleteLookupReturnsNotFoundExceptionForUnknownLookup() throws Exception
    {
        // given
        String unknownLookupName = "unknown";

        // when
        configLookupDao.deleteLookup(unknownLookupName, "standardLookup");

        fail("Should have thrown AcmResourceNotFoundException");
    }

    @Test
    public void testOrderOfLookupEntries()
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        String lookupName = "timesheetTypes";
        Boolean readonly = true;

        lookupDefinition.setName(lookupName);
        lookupDefinition.setReadonly(readonly);

        String key1Ext = "someKey2";
        String value1Ext = "someValue2";
        String key2Ext = "someKey1";
        String value2Ext = "someValue1";
        String updatedEntries = "[{\"key\":\"" + key1Ext + "\",\"value\":\"" + value1Ext
                + "\"}, {\"key\":\"" + key2Ext + "\",\"value\":\"" + value2Ext + "\"}]";

        // when
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(mergedLookups)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(updatedEntries, updatedValue.toString(), true);
    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testCheckDeleteProtectedEntry() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("caseFileTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"readonly\":true}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        configLookupDao.deleteLookup("caseFileTypes", lookupDefinition.getLookupType().toString());

        // then
        fail("AcmResourceNotModifiableException should have been thrown");
    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testCheckReadOnlyEntriesOnUpdate() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("caseFileTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"email\",\"value\":\"Email\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        configLookupDao.deleteLookup("caseFileTypes", lookupDefinition.getLookupType().toString());

        // then
        fail("AcmResourceNotModifiableException should have been thrown");
    }

    @Test(expected = AssertionError.class)
    public void testCheckReadonlyEntriesInNestedLookup() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.NESTED_LOOKUP);
        lookupDefinition.setName("contactMethodTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"subLookup\":[{\"value\":\"core.lookups.common.home\",\"key\":\"Home\",\"readonly\": true},{\"key\":\"Work\",\"value\":\"work\"},{\"value\":\"core.lookups.contactMethodTypes.mobile\",\"key\":\"Mobile\",\"readonly\":true}],\"value\":\"core.lookups.contactMethodTypes.phone\",\"key\":\"phone\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("AssertionError should have been thrown");

    }

    @Test(expected = AssertionError.class)
    public void testCheckReadonlyEntriesInSubLookupInNestedLookup() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.NESTED_LOOKUP);
        lookupDefinition.setName("contactMethodTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"subLookup\":[{\"value\":\"core.lookups.contactMethodTypes.mobile2\",\"key\":\"Mobile\",\"readonly\":true}],\"value\":\"core.lookups.contactMethodTypes.phone\",\"key\":\"phone\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        configLookupDao.saveLookup(lookupDefinition);

        // then
        fail("AssertionError should have been thrown");

    }

    @Test(expected = AcmResourceNotModifiableException.class)
    public void testPrimaryEntriesOnUpdate() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("costsheetTitles");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"FOIA\",\"value\":\"FOIA\",\"readonly\":false,\"primary\":true}, {\"key\":\"sales\",\"value\":\"Sales\",\"readonly\":false,\"primary\":true}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        JSONAssert.assertNotEquals(entriesAsJson, ret, true);
    }

    @Test
    public void testAddLookupWithDescriptionEntry() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("timesheetTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"readonly\":false,\"description\":\"someDescription\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(entriesAsJson, updatedValue.toString(), true);
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

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(entriesAsJson, updatedValue.toString(), true);
    }

    @Test
    public void testUpdateInverseLookupEntriesWithDescription() throws Exception
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.INVERSE_VALUES_LOOKUP);
        lookupDefinition.setName("personOrganizationRelationTypes");
        lookupDefinition.setReadonly(true);
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\",\"inverseKey\":\"someKey\",\"inverseValue\":\"someValue\",\"description\":\"someDescription\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        // when
        String ret = configLookupDao.saveLookup(lookupDefinition);

        // then
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret)
                .read("$." + lookupDefinition.getLookupType().getTypeName() + "..[?(@.name=='" + lookupDefinition.getName()
                        + "')].entries");

        JSONAssert.assertNotEquals(entriesAsJson, updatedValue.toString(), true);
    }

}

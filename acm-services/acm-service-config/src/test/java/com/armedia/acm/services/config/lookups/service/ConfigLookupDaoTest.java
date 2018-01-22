package com.armedia.acm.services.config.lookups.service;

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

import java.io.IOException;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class ConfigLookupDaoTest extends EasyMockSupport
{
    private static final Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ConfigLookupDao configLookupDao;
    private final String lookupsExtFileLocation = "lookups-ext.json";

    @Before
    public void setUp() throws Exception
    {
        configLookupDao = new ConfigLookupDao();
        configLookupDao.setObjectConverter(ObjectConverter.createObjectConverterForTests());
        configLookupDao.setLookupsExtFileLocation(folder.getRoot().getAbsolutePath() + "/" + lookupsExtFileLocation);
    }

    @Test
    public void testSaveLookupAddEntryToCoreLookupSuccess() throws InvalidLookupException, IOException
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

        JSONAssert.assertEquals(updatedValue.get(0).toString(), entriesAsJson, false);
    }

    @Test
    public void testSaveLookupAddEntryToCoreAndExtLookupSuccess() throws InvalidLookupException, IOException
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

        JSONAssert.assertEquals(updatedValue.get(0).toString(), entriesAsJson, false);
    }

    @Test
    public void testSaveLookupToExtLookupSuccess() throws InvalidLookupException, IOException
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

        JSONAssert.assertEquals(updatedValue.get(0).toString(), entriesAsJson, false);
    }

    @Test
    public void testSaveLookupNestedLookupSuccess() throws InvalidLookupException, IOException
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.NESTED_LOOKUP);
        lookupDefinition.setName("contactMethodTypes");
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

        JSONAssert.assertEquals(updatedValue.get(0).toString(), entriesAsJson, false);
    }

    @Test(expected = InvalidLookupException.class)
    public void testSaveLookupThrowsExceptionOnInvalidLookupJson() throws InvalidLookupException, IOException
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
    public void testSaveLookupThrowsExceptionOnDuplicateKeys() throws InvalidLookupException, IOException
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

        JSONAssert.assertEquals(updatedValue.get(0).toString(), updatedEntries, false);
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

        JSONAssert.assertEquals(updatedValue.get(0).toString(), updatedEntries, false);
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

        JSONAssert.assertEquals(updatedValue.get(0).toString(), updatedEntries, false);

    }

    @Test
    public void testDeleteLookupFromExtLookups() throws AcmResourceNotModifiableException, AcmResourceNotFoundException, IOException
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
    public void testDeleteLookupThatCantBeDeletedReturnNotModifiableException()
            throws AcmResourceNotModifiableException, AcmResourceNotFoundException, IOException
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
    public void testDeleteLookupReturnsNotFoundExceptionForUnknownLookup()
            throws AcmResourceNotModifiableException, IOException, AcmResourceNotFoundException
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
}

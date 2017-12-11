package com.armedia.acm.services.config.lookups.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.model.LookupType;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.service.ConfigService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public class ConfigLookupDaoTest extends EasyMockSupport
{
    private static final Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();

    private ConfigLookupDao configLookupDao;
    private ConfigService mockConfigService;

    @Before
    public void setUp() throws Exception
    {
        configLookupDao = new ConfigLookupDao();
        mockConfigService = createMock(ConfigService.class);
        //configLookupDao.setConfigService(mockConfigService);
        configLookupDao.setObjectConverter(ObjectConverter.createObjectConverterForTests());
    }

    @Test
    public void testUpdateLookupSuccess() throws InvalidLookupException, IOException
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        String entriesAsJson = "[{\"key\":\"someKey\",\"value\":\"someValue\"}]";
        lookupDefinition.setLookupEntriesAsJson(entriesAsJson);

        //expect(mockConfigService.getLookupsAsJson()).andReturn("{\"standardLookup\" : [{\"colors\" : []}]}");
        // mockConfigService.saveLookupsExt("{\"standardLookup\":[{\"colors\":" + entriesAsJson + "}]}");
        expectLastCall().once();

        // when
        replayAll();
        String ret = configLookupDao.updateLookup(lookupDefinition);

        // then
        verifyAll();
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret).read("$." + lookupDefinition.getLookupType().getTypeName()
                + "..[?(@.name=='" + lookupDefinition.getName() + "')].entries");

        JSONAssert.assertEquals(updatedValue.get(0).toString(), entriesAsJson, false);
    }

    @Test
    public void testUpdateNestedLookupSuccess() throws InvalidLookupException, IOException
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.NESTED_LOOKUP);
        lookupDefinition.setName("contactMethodTypes");
        String lookupAsJson = "[{\"key\":\"phone\",\"value\":\"lookups.contactMethodTypes.phone\",\"subLookup\":[{\"key\":\"Home1\",\"value\":\"lookups.common.home\"},{\"key\":\"Work\",\"value\":\"lookups.contactMethodTypes.work\"},{\"key\":\"Mobile\",\"value\":\"lookups.contactMethodTypes.mobile\"}]},{\"key\":\"Fax\",\"value\":\"lookups.contactMethodTypes.fax\",\"subLookup\":[]},{\"key\":\"email\",\"value\":\"lookups.contactMethodTypes.email\",\"subLookup\":[{\"key\":\"Personal\",\"value\":\"Personal\"},{\"key\":\"Business\",\"value\":\"Business\"}]},{\"key\":\"url\",\"value\":\"Url\",\"subLookup\":[{\"key\":\"Web Site\",\"value\":\"Web Site\"},{\"key\":\"Facebook\",\"value\":\"Facebook\"},{\"key\":\"LinkedIn\",\"value\":\"LinkedIn\"},{\"key\":\"Twitter\",\"value\":\"Twitter\"},{\"key\":\"Other\",\"value\":\"Other\"}]}]";
        lookupDefinition.setLookupEntriesAsJson(lookupAsJson);

        //expect(mockConfigService.getLookupsAsJson()).andReturn("{\"nestedLookup\" : [{\"contactMethodTypes\" : []}]}");
        // mockConfigService.saveLookupsExt("{\"nestedLookup\":[{\"contactMethodTypes\":" + lookupAsJson + "}]}");
        expectLastCall().once();

        // when
        replayAll();
        String ret = configLookupDao.updateLookup(lookupDefinition);

        // then
        verifyAll();
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret).read("$." + lookupDefinition.getLookupType().getTypeName()
                + "..[?(@." + lookupDefinition.getName() + ")]." + lookupDefinition.getName());

        JSONAssert.assertEquals(updatedValue.get(0).toString(), lookupAsJson, false);
    }

    @Test(expected = InvalidLookupException.class)
    public void testUpdateLookupThrowsExceptionOnInvalidLookupJson() throws InvalidLookupException, IOException
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        String lookupAsJson = "[\"key\":\"someKey\", \"value\":\"someValue\"}]";
        lookupDefinition.setLookupEntriesAsJson(lookupAsJson);

        // when
        configLookupDao.updateLookup(lookupDefinition);

        // then
        fail("Should have thrown InvalidLookupException!");
    }

    @Test(expected = InvalidLookupException.class)
    public void testUpdateLookupThrowsExceptionOnDuplicateKeys() throws InvalidLookupException, IOException
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");
        String lookupAsJson = "[{\"key\":\"someKey\", \"value\":\"someValue\"}, {\"key\":\"someKey\", \"value\":\"someValue1\"}]";
        lookupDefinition.setLookupEntriesAsJson(lookupAsJson);

        // when
        configLookupDao.updateLookup(lookupDefinition);

        // then
        fail("Should have thrown InvalidLookupException!");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetLookupByNameReturnsCorrectAcmLookup()
    {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\"" + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";

        //expect(mockConfigService.getLookupsAsJson()).andReturn(lookups);
        //expect(mockConfigService.getLookupsExtAsJson()).andReturn("{\"standardLookup\":[],\"inverseValuesLookup\":[],\"nestedLookup\":[]}");

        // when
        replayAll();
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName(lookupName);

        // then
        verifyAll();
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
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\"" + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";

        //expect(mockConfigService.getLookupsAsJson()).andReturn(lookups);
        //expect(mockConfigService.getLookupsExtAsJson()).andReturn("{\"standardLookup\":[],\"inverseValuesLookup\":[],\"nestedLookup\":[]}");

        // when
        replayAll();
        AcmLookup<StandardLookupEntry> acmLookup = (AcmLookup<StandardLookupEntry>) configLookupDao.getLookupByName("unknown");

        // then
        verifyAll();
        assertNull(acmLookup);
    }

    @Test
    public void testMergeLookupsAddingNewLookup() {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"" + lookupName + "\":[{\"key\":\"" + key1 + "\",\"value\":\"" + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";

        String lookupNameExt = "lookupNameExt";
        String key1Ext = "someKey1Ext";
        String value1Ext = "someValue1Ext";
        String key2Ext = "someKey2Ext";
        String value2Ext = "someValue2Ext";
        Boolean readonlyExt = false;
        String lookupsExt = "{\"standardLookup\":[{\"" + lookupNameExt + "\":[{\"key\":\"" + key1Ext + "\",\"value\":\"" + value1Ext
                + "\"}, {\"key\":\"" + key2Ext + "\",\"value\":\"" + value2Ext + "\"}],\"readonly\":\"" + readonlyExt + "\"}]}";

        //expect(mockConfigService.getLookupsAsJson()).andReturn(lookups);
        //expect(mockConfigService.getLookupsExtAsJson()).andReturn(lookupsExt);

        // when
        replayAll();
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        verifyAll();
    }

    @Test
    public void testMergeLookupsUpdateLookup() {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"" + lookupName + "\":[{\"key\":\"" + key1 + "\",\"value\":\"" + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";
        String lookupNameExt = "lookupName";
        String key1Ext = "someKey1Ext";
        String value1Ext = "someValue1Ext";
        String key2Ext = "someKey2Ext";
        String value2Ext = "someValue2Ext";
        Boolean readonlyExt = false;
        String lookupsExt = "{\"standardLookup\":[{\"" + lookupNameExt + "\":[{\"key\":\"" + key1Ext + "\",\"value\":\"" + value1Ext
                + "\"}, {\"key\":\"" + key2Ext + "\",\"value\":\"" + value2Ext + "\"}],\"readonly\":\"" + readonlyExt + "\"}]}";

        //expect(mockConfigService.getLookupsAsJson()).andReturn(lookups);
        //expect(mockConfigService.getLookupsExtAsJson()).andReturn(lookupsExt);

        // when
        replayAll();
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        verifyAll();
    }

    @Test
    public void testMergeLookupsUpdateLookupNew() {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"name\":\"" + lookupName + "\",\"entries\":[{\"key\":\"" + key1 + "\",\"value\":\"" + value1 + "\"},{\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";
        String lookupNameExt = "lookupName";
        String key1Ext = "someKey1Ext";
        String value1Ext = "someValue1Ext";
        String key2Ext = "someKey2Ext";
        String value2Ext = "someValue2Ext";
        Boolean readonlyExt = false;
        String lookupsExt = "{\"standardLookup\":[{\"name\":\"" + lookupNameExt + "\",\"entries\":[{\"key\":\"" + key1Ext + "\",\"value\":\"" + value1Ext
                + "\"}, {\"key\":\"" + key2Ext + "\",\"value\":\"" + value2Ext + "\"}],\"readonly\":\"" + readonlyExt + "\"}]}";

        //expect(mockConfigService.getLookupsAsJson()).andReturn(lookups);
        //expect(mockConfigService.getLookupsExtAsJson()).andReturn(lookupsExt);

        // when
        replayAll();
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        verifyAll();
    }

    @Test
    public void testMergeLookupsAddingNewLookupWithEmptyEntries() {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"" + lookupName + "\":[{\"key\":\"" + key1 + "\",\"value\":\"" + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";

        String lookupNameExt = "lookupNameExt";
        Boolean readonlyExt = false;
        String lookupsExt = "{\"standardLookup\":[{\"" + lookupNameExt + "\":[{}],\"readonly\":\"" + readonlyExt + "\"}]}";

        //expect(mockConfigService.getLookupsAsJson()).andReturn(lookups);
        //expect(mockConfigService.getLookupsExtAsJson()).andReturn(lookupsExt);

        // when
        replayAll();
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        verifyAll();
    }

    @Test
    public void testMergeLookupsUpdateLookupWithEmptyEntries() {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;
        String lookups = "{\"standardLookup\":[{\"" + lookupName + "\":[{\"key\":\"" + key1 + "\",\"value\":\"" + value1
                + "\"}, {\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}]}";

        String lookupNameExt = "lookupName";
        Boolean readonlyExt = false;
        String lookupsExt = "{\"standardLookup\":[{\"" + lookupNameExt + "\":[{}],\"readonly\":\"" + readonlyExt + "\"}]}";

        //expect(mockConfigService.getLookupsAsJson()).andReturn(lookups);
        //expect(mockConfigService.getLookupsExtAsJson()).andReturn(lookupsExt);

        // when
        replayAll();
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        verifyAll();
    }

    @Test
    public void testMergeLookupsUpdateLookupOfDifferentTypes() {
        // given
        String lookupName = "lookupName";
        String key1 = "someKey1";
        String value1 = "someValue1";
        String key2 = "someKey2";
        String value2 = "someValue2";
        Boolean readonly = true;

        String inverseLookupName = "inverseLookupName";
        String inverseKey1 = "someInverseKey1";
        String inverseValue1 = "someInverseValue1";
        String keyInv2 = "someKey2";
        String valueInv2 = "someValue2";
        Boolean readonlyInv = true;


        String lookups = "{\"standardLookup\":[{\"" + lookupName + "\":[{\"key\":\"" + key1 + "\",\"value\":\"" + value1 + "\"},{\"key\":\"" + key2 + "\",\"value\":\"" + value2 + "\"}],\"readonly\":\"" + readonly + "\"}],\"inverseValuesLookup\":[{\"" + inverseLookupName + "\":[{\"inverseKey\":\"" + inverseKey1 + "\",\"inverseValue\":\"" + inverseValue1 + "\",\"key\":\"" + keyInv2 + "\",\"value\":\"" + valueInv2 + "\"}],\"readonly\":\"" + readonlyInv + "\"}]}";

        String lookupNameExt = "lookupName";
        String key1Ext = "someKey1Ext";
        String value1Ext = "someValue1Ext";
        String key2Ext = "someKey2Ext";
        String value2Ext = "someValue2Ext";
        Boolean readonlyExt = false;

        String inverseLookupNameExt = "inverseLookupName";
        String inverseKey1Ext = "someInverseKey1Ext";
        String inverseValue1Ext = "someInverseValue1Ext";
        String keyInv2Ext = "someKey2Ext";
        String valueInv2Ext = "someValue2Ext";
        Boolean readonlyInvExt = false;

        String lookupsExt = "{\"standardLookup\":[{\"" + lookupNameExt + "\":[{\"key\":\"" + key1Ext + "\",\"value\":\"" + value1Ext + "\"},{\"key\":\"" + key2Ext + "\",\"value\":\"" + value2Ext + "\"}],\"readonly\":\"" + readonlyExt + "\"}],\"inverseValuesLookup\":[{\"" + inverseLookupNameExt + "\":[{\"inverseKey\":\"" + inverseKey1Ext + "\",\"inverseValue\":\"" + inverseValue1Ext + "\",\"key\":\"" + keyInv2Ext + "\",\"value\":\"" + valueInv2Ext + "\"}],\"readonly\":\"" + readonlyInvExt + "\"}]}";

        //expect(mockConfigService.getLookupsAsJson()).andReturn(lookups);
        //expect(mockConfigService.getLookupsExtAsJson()).andReturn(lookupsExt);

        // when
        replayAll();
        String mergedLookups = configLookupDao.getMergedLookups();

        // then
        verifyAll();

    }
}

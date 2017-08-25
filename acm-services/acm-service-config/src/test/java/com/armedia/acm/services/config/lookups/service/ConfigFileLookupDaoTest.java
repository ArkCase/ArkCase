package com.armedia.acm.services.config.lookups.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.mockStaticPartial;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import com.armedia.acm.services.config.lookups.model.LookupType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Files.class,
        ConfigFileLookupDao.class })
public class ConfigFileLookupDaoTest
{
    private static final Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();

    private ConfigFileLookupDao configFileLookupDao;

    @Before
    public void setUp() throws Exception
    {
        configFileLookupDao = new ConfigFileLookupDao();
        Resource lookupsFolder = new ClassPathResource("/lookups");
        String lookupsFolderPath = lookupsFolder.getFile().getCanonicalPath();
        configFileLookupDao.setLookupsFileLocation(lookupsFolderPath + "/lookups.json");
    }

    @Test
    public void testUpdateLookupSuccess() throws InvalidLookupException, IOException
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");

        String lookupAsJson = "[{\"key\":\"someKey\", \"value\":\"someValue\"}]";

        mockStaticPartial(Files.class, "write");
        expect(Files.write(anyObject(Path.class), (byte[]) anyObject())).andReturn(null);

        // when
        replayAll();
        String ret = configFileLookupDao.updateLookup(lookupDefinition, lookupAsJson);

        // then
        verifyAll();
        ArrayNode updatedValue = JsonPath.using(configuration).parse(ret).read("$." + lookupDefinition.getLookupType().getTypeName()
                + "..[?(@." + lookupDefinition.getName() + ")]." + lookupDefinition.getName());

        JSONAssert.assertEquals(updatedValue.get(0).asText(), lookupAsJson, false);
    }

    @Test(expected = InvalidLookupException.class)
    public void testUpdateLookupThrowsExceptionOnInvalidLookupJson() throws InvalidLookupException, IOException
    {
        // given
        LookupDefinition lookupDefinition = new LookupDefinition();
        lookupDefinition.setLookupType(LookupType.STANDARD_LOOKUP);
        lookupDefinition.setName("colors");

        String lookupAsJson = "[\"key\":\"someKey\", \"value\":\"someValue\"}]";

        // when
        configFileLookupDao.updateLookup(lookupDefinition, lookupAsJson);

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

        // when
        configFileLookupDao.updateLookup(lookupDefinition, lookupAsJson);

        // then
        fail("Should have thrown InvalidLookupException!");
    }
}

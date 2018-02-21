package com.armedia.acm.services.dataupdate.web;

import static org.junit.Assert.assertEquals;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.dataupdate.service.SolarReindexExecutor;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SolarReindexExecutorTests extends EasyMockSupport
{
    private static final String SOLR_LAST_RUN_DATE_PROPERTY_KEY = "solr.last.run.date";
    private PropertyFileManager propertyFileManager;
    private final String filePath = getClass().getClassLoader().getResource("properties/solrBatchUpdate.properties").getPath();
    private List<String> solrList;
    private Map<String, String> solrMap;
    private SolarReindexExecutor solarReindexExecutor;

    @Before
    public void setUp() throws Exception
    {
        propertyFileManager = new PropertyFileManager();

        solarReindexExecutor = new SolarReindexExecutor();
        solarReindexExecutor.setLastBatchUpdatePropertyFileLocation(filePath);
        solarReindexExecutor.setPropertyFileManager(propertyFileManager);

        solrList = Arrays.asList(SOLR_LAST_RUN_DATE_PROPERTY_KEY + "." + AcmUser.class.getName(),
                SOLR_LAST_RUN_DATE_PROPERTY_KEY + "." + AcmGroup.class.getName());

        solrMap = new HashMap<>();
        solrMap.put(SOLR_LAST_RUN_DATE_PROPERTY_KEY + "." + AcmUser.class.getName(), "testValueUser");
        solrMap.put(SOLR_LAST_RUN_DATE_PROPERTY_KEY + "." + AcmGroup.class.getName(), "testValueGroup");

        fillFile(solrMap);
    }

    private void fillFile(Map<String, String> solrMap) throws IOException
    {
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : solrMap.entrySet())
        {
            properties.put(entry.getKey(), entry.getValue());
        }
        properties.store(new FileOutputStream(filePath), null);
    }

    private String takePropertiesValueByKey(String key) throws IOException
    {
        String retval = "";
        Properties properties = new Properties();
        try (InputStream fis = new FileInputStream(filePath))
        {
            properties.load(fis);
            retval = properties.getProperty(key);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
        return retval;
    }

    @Test
    public void validateRemovedLines() throws Exception
    {
        solarReindexExecutor.reindex(Arrays.asList(AcmUser.class, AcmGroup.class));

        for (String key : solrList)
        {
            assertEquals(null, takePropertiesValueByKey(key));
        }
    }

}

package com.armedia.acm.services.dataupdate.web;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.dataupdate.service.SolrReindexService;
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

public class SolrReindexServiceTests extends EasyMockSupport
{
    private static final String SOLR_LAST_RUN_DATE_PROPERTY_KEY = "solr.last.run.date";
    private final String filePath = getClass().getClassLoader().getResource("properties/solrBatchUpdate.properties").getPath();
    private PropertyFileManager propertyFileManager;
    private List<String> solrList;
    private Map<String, String> solrMap;
    private SolrReindexService solrReindexService;

    @Before
    public void setUp() throws Exception
    {
        propertyFileManager = new PropertyFileManager();

        solrReindexService = new SolrReindexService();
        solrReindexService.setLastBatchUpdatePropertyFileLocation(filePath);
        solrReindexService.setPropertyFileManager(propertyFileManager);

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
        solrReindexService.reindex(Arrays.asList(AcmUser.class, AcmGroup.class));

        for (String key : solrList)
        {
            assertEquals(null, takePropertiesValueByKey(key));
        }
    }

}

package com.armedia.acm.files.propertymanager;

/*-
 * #%L
 * Tool Integrations: Property File Manager
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

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertyFileManagerIT
{

    private static final String FILE_NAME = "test.properties";
    Map<String, String> propertiesMap = new HashMap<>();
    Properties properties = new Properties();
    PropertyFileManager propertyFileManager = new PropertyFileManager();
    String tmpFolder = System.getProperty("java.io.tmpdir");
    String fullPath = tmpFolder + File.separator + FILE_NAME;

    @Before
    public void setUp() throws Exception
    {
        propertiesMap.put("x", "y");
        propertiesMap.put("a", "b");
    }

    @Test
    public void testStoreMultipleDoesFileExists()
    {
        propertyFileManager.storeMultiple(propertiesMap, fullPath, true);
        File mockFile = FileUtils.getFile(fullPath);

        Assert.assertTrue(mockFile.exists());
    }

    @Test
    public void testStoreMultipleCleanTrueWhenFileDoesnotExist() throws Exception
    {
        boolean clean = true;

        propertyFileManager.storeMultiple(propertiesMap, fullPath, clean);

        File mockPropertiesFile = FileUtils.getFile(fullPath);

        Assert.assertTrue(mockPropertiesFile.exists());

        try (InputStream is = FileUtils.openInputStream(mockPropertiesFile))
        {
            properties.load(is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Set<String> keys = properties.stringPropertyNames();
        Assert.assertTrue(keys.containsAll(propertiesMap.keySet()));

        deleteFile(fullPath);

    }

    @Test
    public void testStoreMultipleCleanFalseWithUpdatedMap() throws Exception
    {
        boolean clean = false;
        boolean fileCreated = createFile(fullPath);

        writeToFile(fullPath, propertiesMap);
        Map<String, String> updatedMapOfStrings = Collections.singletonMap("another", "row");

        propertyFileManager.storeMultiple(updatedMapOfStrings, fullPath, clean);

        File mockPropertiesFile = FileUtils.getFile(fullPath);

        Assert.assertTrue(mockPropertiesFile.exists());

        try (InputStream is = FileUtils.openInputStream(mockPropertiesFile))
        {
            properties.load(is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Set<String> keys = properties.stringPropertyNames();

        Assert.assertTrue(keys.containsAll(propertiesMap.keySet()));
        Assert.assertTrue(keys.containsAll(updatedMapOfStrings.keySet()));

        if (fileCreated)
        {
            deleteFile(fullPath);
        }
    }

    private boolean createFile(String name) throws IOException
    {
        File tmpFile = new File(name);
        return tmpFile.createNewFile();
    }

    private void deleteFile(String name)
    {
        File tmpFile = new File(name);
        tmpFile.delete();
    }

    private void writeToFile(String fileName, Map<String, String> propertiesMap)
    {
        Properties p = new Properties();

        propertiesMap.forEach((key, value) -> p.setProperty(key, value));

        try (OutputStream out = new FileOutputStream(fileName))
        {
            p.store(out, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}

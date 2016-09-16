package com.armedia.acm.files.propertymanager;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertyFileManagerTest
{

    private static final String FILE_NAME = "test.properties";
    Map<String, String> propertiesMap = new HashMap<>();
    Properties properties = new Properties();
    PropertyFileManager propertyFileManager = new PropertyFileManager();

    @Before
    public void setUp() throws Exception
    {
        propertiesMap.put("x", "y");
        propertiesMap.put("a", "b");
    }

    @Test
    public void testStoreMultipleCleanTrue() throws Exception
    {
        String tmpFolder = System.getProperty("java.io.tmpdir");
        String fullPath = tmpFolder + FILE_NAME;
        boolean clean = true;
        boolean fileCreated = createFile(fullPath);

        propertyFileManager.storeMultiple(propertiesMap, tmpFolder + FILE_NAME, clean);

        File mockPropertiesFile = FileUtils.getFile(tmpFolder + FILE_NAME);

        Assert.assertTrue(mockPropertiesFile.exists());

        InputStream is = null;
        try
        {
            is = FileUtils.openInputStream(mockPropertiesFile);
            properties.load(is);
        } catch (Exception e)
        {
            if (is != null)
            {
                is.close();
            }
        }

        Set<String> keys = properties.stringPropertyNames();
        Assert.assertTrue(keys.containsAll(propertiesMap.keySet()));

        if (is != null)
        {
            is.close();
        }

        fileCreated = true;

        if (fileCreated)
        {
            deleteFile(fullPath);
        }

    }

    @Test
    public void testStoreMultipleCleanFalse() throws Exception
    {
        String tmpFolder = System.getProperty("java.io.tmpdir");
        String fullPath = tmpFolder + FILE_NAME;
        boolean clean = false;
        boolean fileCreated = createFile(fullPath);

        propertyFileManager.storeMultiple(propertiesMap, tmpFolder + FILE_NAME, clean);

        File mockPropertiesFile = FileUtils.getFile(tmpFolder + FILE_NAME);

        Assert.assertTrue(mockPropertiesFile.exists());

        InputStream is = null;
        try
        {
            is = FileUtils.openInputStream(mockPropertiesFile);
            properties.load(is);
        } catch (Exception e)
        {
            if (is != null)
            {
                is.close();
            }
        }

        Set<String> keys = properties.stringPropertyNames();
        Assert.assertTrue(keys.containsAll(propertiesMap.keySet()));

        if (is != null)
        {
            is.close();
        }

        fileCreated = true;

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
        System.gc();
        tmpFile.delete();
    }

}


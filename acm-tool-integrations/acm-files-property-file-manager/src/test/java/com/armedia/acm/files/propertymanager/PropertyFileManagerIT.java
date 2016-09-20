package com.armedia.acm.files.propertymanager;

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
    String fullPath = tmpFolder + FILE_NAME;

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
        } catch (Exception e)
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
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Set<String> keys = properties.stringPropertyNames();

        Assert.assertTrue(keys.containsAll(propertiesMap.keySet()));

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
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}


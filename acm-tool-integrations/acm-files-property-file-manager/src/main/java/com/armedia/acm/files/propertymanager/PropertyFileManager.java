package com.armedia.acm.files.propertymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Created by armdev on 6/18/14.
 */
public class PropertyFileManager
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Properties readFromFile(File propertiesFile) throws IOException
    {
        try (FileReader fr = new FileReader(propertiesFile))
        {
            Properties p = new Properties();
            p.load(fr);

            log.debug("Properties loaded from [{}]", propertiesFile.getName());

            return p;
        } catch (IOException e)
        {
            log.error("Could not reload properties from [" + propertiesFile.getName() + "]: " + e.getMessage(), e);
            throw e;
        }
    }

    public void store(String key, String value, String filename)
    {
        Properties p = new Properties();
        p.setProperty(key, value);

        try (OutputStream fos = new FileOutputStream(filename))
        {
            p.store(fos, "last updated");
        } catch (IOException e)
        {
            log.debug("could not create properties file: " + e.getMessage(), e);
        }
    }

    public void storeMultiple(Map<String, String> propertiesMap, String fileName, boolean clean)
    {
        if (propertiesMap != null && propertiesMap.size() > 0)
        {

            try (FileInputStream in = new FileInputStream(fileName);
                 FileOutputStream out = new FileOutputStream(fileName))
            {
                Properties p = new Properties();

                if (!clean)
                {
                    p.load(in);
                }

                for (Entry<String, String> entry : propertiesMap.entrySet())
                {
                    p.setProperty(entry.getKey(), entry.getValue());
                }

                p.store(out, null);
            } catch (IOException e)
            {
                log.debug("Could not update properties file: " + e.getMessage(), e);
            }
        }
    }

    public void removeMultiple(List<String> properties, String fileName)
    {
        if (properties != null && properties.size() > 0)
        {

            try (FileInputStream in = new FileInputStream(fileName);
                 FileOutputStream out = new FileOutputStream(fileName))
            {

                Properties p = new Properties();
                p.load(in);

                for (String key : properties)
                {
                    p.remove(key);
                }

                p.store(out, null);
            } catch (IOException e)
            {
                log.debug("Could not remove properties file: " + e.getMessage(), e);
            }
        }
    }

    public String load(String filename, String key, String defaultValue)
    {


        Properties p = new Properties();
        String retval = defaultValue;

        try (InputStream fis = new FileInputStream(filename))
        {
            p.load(fis);

            retval = p.getProperty(key, defaultValue);

        } catch (IOException e)
        {
            log.warn("file [{}] not found, using default last update time.", filename);
        }

        return retval;
    }
}

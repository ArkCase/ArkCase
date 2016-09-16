package com.armedia.acm.files.propertymanager;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
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

    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

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

            try (InputStream in = new FileInputStream(fileName))
            {
                Properties p = new Properties();
                if (!clean)
                {
                    p.load(in);
                }

                propertiesMap.forEach((key, value) -> p.setProperty(key, value));

                try (OutputStream out = new FileOutputStream(fileName))
                {
                    p.store(out, null);
                }
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

            try (OutputStream out = new FileOutputStream(fileName))
            {

                Properties p = new Properties();
                InputStream in = new FileInputStream(fileName);
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

    public String load(String filename, String key, String defaultValue) throws AcmEncryptionException
    {

        Properties p = new Properties();
        String retval = defaultValue;

        try (InputStream fis = new FileInputStream(filename))
        {
            p.load(fis);

            retval = encryptablePropertyUtils.decryptPropertyValue(p.getProperty(key, defaultValue));

        } catch (IOException e)
        {
            log.warn("file [{}] not found, using default last update time.", filename);
        }

        return retval;
    }

    /**
     * @return the encryptablePropertyUtils
     */
    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    /**
     * @param encryptablePropertyUtils the encryptablePropertyUtils to set
     */
    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }
}
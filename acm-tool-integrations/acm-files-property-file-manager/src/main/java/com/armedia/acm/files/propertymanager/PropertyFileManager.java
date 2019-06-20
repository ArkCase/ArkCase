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

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by armdev on 6/18/14.
 */
public class PropertyFileManager
{
    private final Logger log = LogManager.getLogger(getClass());
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    public Properties readFromFile(File propertiesFile) throws IOException
    {
        try (FileReader fr = new FileReader(propertiesFile))
        {
            Properties p = new Properties();
            p.load(fr);

            log.debug("Properties loaded from [{}]", propertiesFile.getName());

            return p;
        }
        catch (IOException e)
        {
            log.error("Could not reload properties from [{}] ", propertiesFile.getName(), e);
            throw e;
        }
    }

    public Map<String, String> readFromFileAsMap(File propertiesFile) throws IOException
    {
        Map<String, String> propertiesMap = new HashMap<>();

        Properties properties =  readFromFile(propertiesFile);
        properties.forEach((o, o2) -> propertiesMap.put((String)o, (String)o2));

        return propertiesMap;
    }

    public void store(String key, String value, String fileName)
    {
        Properties p = new Properties();
        p.setProperty(key, value);

        try (OutputStream fos = new FileOutputStream(fileName))
        {
            log.info("Saving property file [{}]", fileName);
            p.store(fos, "last updated");
        }
        catch (IOException e)
        {
            log.debug("could not create properties file: [{}] ", e.getMessage(), e);
        }
    }

    public void store(String key, String value, String fileName, boolean clean)
    {
        Properties p = new Properties();

        if (!clean)
        {
            try (InputStream in = new FileInputStream(fileName))
            {
                p.load(in);
            }
            catch (IOException e)
            {
                log.warn("Could not open properties file: {}", e.getMessage());
            }
        }

        try (OutputStream out = new FileOutputStream(fileName))
        {
            p.setProperty(key, value);
            log.info("Saving property file [{}]", fileName);
            p.store(out, null);
        }
        catch (IOException e)
        {
            log.warn("Could not update properties file: {}", e.getMessage());
        }
    }

    public void storeMultiple(Map<String, String> propertiesMap, String fileName, boolean clean)
    {
        if (propertiesMap == null)
            return;

        Properties p = new Properties();

        if (!clean)
        {
            try (InputStream in = new FileInputStream(fileName))
            {
                p.load(in);
            }
            catch (IOException e)
            {
                log.warn("Could not open properties file: {}", e.getMessage(), e);
            }
        }

        try (OutputStream out = new FileOutputStream(fileName))
        {
            propertiesMap.forEach(p::setProperty);
            log.info("Saving property file [{}]", fileName);
            p.store(out, null);
        }
        catch (IOException e)
        {
            log.warn("Could not update properties file: {}", e.getMessage(), e);
        }
    }

    public void removeMultiple(List<String> properties, String fileName)
    {
        if (properties != null && properties.size() > 0)
        {
            Properties p = new Properties();

            try (FileInputStream in = new FileInputStream(fileName))
            {
                p.load(in);

                for (String key : properties)
                {
                    p.remove(key);
                }
            }
            catch (IOException e)
            {
                log.debug("Could not update properties file: " + e.getMessage(), e);
            }

            try (FileOutputStream out = new FileOutputStream(fileName))
            {
                log.info("Saving property file [{}]", fileName);
                p.store(out, null);
            }
            catch (IOException e)
            {
                log.debug("Could not update properties file: " + e.getMessage(), e);
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

        }
        catch (IOException e)
        {
            log.warn("file [{}] not found, using default last update time.", filename);
        }

        return retval;
    }

    public Map<String, Object> loadMultiple(String filename, String... keys) throws AcmEncryptionException
    {

        Properties p = new Properties();
        Map<String, Object> retval = new HashMap<>();

        try (InputStream fis = new FileInputStream(filename))
        {
            p.load(fis);

            for (String key : keys)
            {
                retval.put(key, encryptablePropertyUtils.decryptPropertyValue(p.getProperty(key)));
            }

        }
        catch (IOException e)
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
     * @param encryptablePropertyUtils
     *            the encryptablePropertyUtils to set
     */
    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }
}

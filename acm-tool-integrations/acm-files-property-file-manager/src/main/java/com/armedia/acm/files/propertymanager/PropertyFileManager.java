package com.armedia.acm.files.propertymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by armdev on 6/18/14.
 */
public class PropertyFileManager
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void store(String key, String value, String filename)
    {
        Properties p = new Properties();
        p.setProperty(key, value);

        OutputStream fos = null;
        try
        {
            fos = new FileOutputStream(filename);
            p.store(fos, "last updated");
        }
        catch (IOException e)
        {
            log.debug("could not create properties file: " + e.getMessage(), e);
        }
        finally
        {
            if ( fos != null )
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    log.warn("could not close properties file: " + e.getMessage(), e);
                }
            }
        }
    }

    public String load(String filename, String key, String defaultValue)
    {

        InputStream fis = null;
        Properties p = new Properties();
        String retval = defaultValue;

        try
        {
            fis = new FileInputStream(filename);
            p.load(fis);

            retval = p.getProperty(key, defaultValue);

        }
        catch (IOException e)
        {
            log.warn("file not found, using default last update time.");
        }
        finally
        {
            if ( fis != null )
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                    log.warn("Could not close properties file: " + e.getMessage(), e);
                }
            }
        }

        return retval;
    }
}

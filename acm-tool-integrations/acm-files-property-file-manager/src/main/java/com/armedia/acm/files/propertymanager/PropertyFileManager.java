package com.armedia.acm.files.propertymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    
    public void storeMultiple(Map<String, String> propertiesMap, String fileName, boolean clean)
    {
    	if (propertiesMap != null && propertiesMap.size() > 0)
    	{
    		FileInputStream in = null;
    		FileOutputStream out = null;
    		try
    		{    			
    			Properties p = new Properties();
    		
    			if (!clean)
    			{
	    			in = new FileInputStream(fileName);
	    			p.load(in);
    			}
    			
    			out = new FileOutputStream(fileName);
    			
    			for (Entry<String, String> entry : propertiesMap.entrySet())
        		{
    				p.setProperty(entry.getKey(), entry.getValue());
        		}
    			
    			p.store(out, null);
    		}
    		catch(IOException e)
    		{
    			log.debug("Could not update properties file: " + e.getMessage(), e);
    		}
    		finally
            {
                if ( in != null )
                {
                    try
                    {
                        in.close();
                    }
                    catch (IOException e)
                    {
                        log.warn("Could not close input stream: " + e.getMessage(), e);
                    }
                }
                
                if ( out != null )
                {
                    try
                    {
                        out.close();
                    }
                    catch (IOException e)
                    {
                        log.warn("Could not close output stream: " + e.getMessage(), e);
                    }
                }
            }
    	}
    }
    
    public void removeMultiple(List<String> properties, String fileName)
    {
    	if (properties != null && properties.size() > 0)
    	{
    		FileInputStream in = null;
    		FileOutputStream out = null;
    		try
    		{
    			in = new FileInputStream(fileName);
    			
    			Properties p = new Properties();
    			p.load(in);
    			
    			out = new FileOutputStream(fileName);
    			
    			for (String key : properties)
        		{
    				p.remove(key);
        		}
    			
    			p.store(out, null);
    		}
    		catch(IOException e)
    		{
    			log.debug("Could not remove properties file: " + e.getMessage(), e);
    		}
    		finally
            {
                if ( in != null )
                {
                    try
                    {
                        in.close();
                    }
                    catch (IOException e)
                    {
                        log.warn("Could not close input stream: " + e.getMessage(), e);
                    }
                }
                
                if ( out != null )
                {
                    try
                    {
                        out.close();
                    }
                    catch (IOException e)
                    {
                        log.warn("Could not close output stream: " + e.getMessage(), e);
                    }
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

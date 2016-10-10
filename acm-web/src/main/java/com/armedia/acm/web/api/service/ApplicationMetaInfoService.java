package com.armedia.acm.web.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * Created by jovan.ivanovski on 10/7/2016.
 */
public class ApplicationMetaInfoService implements InitializingBean
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String version;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public void findVersion()
    {
        Properties prop = new Properties();
        String className = getClass().getSimpleName() + ".class";
        String classPath = getClass().getResource(className).toString();

        if (classPath != null && classPath.startsWith("jar"))
        {
            String manifestPath = classPath.substring(10, classPath.lastIndexOf("/WEB-INF/")) + "/" + JarFile.MANIFEST_NAME;
            try (InputStream inputStream = new FileInputStream(manifestPath))
            {
                if (inputStream != null)
                {
                    prop.load(inputStream);
                    version = prop.getProperty("Implementation-Version", "");
                }
            } catch (IOException e)
            {
                log.warn("Could not open manifest file: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        findVersion();
    }
}

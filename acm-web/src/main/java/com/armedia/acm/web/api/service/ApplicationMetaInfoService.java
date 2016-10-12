package com.armedia.acm.web.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jovan.ivanovski on 10/7/2016.
 */
public class ApplicationMetaInfoService implements InitializingBean, ServletContextAware
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String version;

    @Override
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    ServletContext servletContext;

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

        try (InputStream manifestStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"))
        {
            prop.load(manifestStream);
            version = prop.getProperty("Implementation-Version", "");
        } catch (IOException e)
        {
            log.warn("Could not open manifest file: {}", e.getMessage(), e);
        }

    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        findVersion();
    }


}

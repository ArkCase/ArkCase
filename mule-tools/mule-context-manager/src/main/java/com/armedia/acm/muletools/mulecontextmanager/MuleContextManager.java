package com.armedia.acm.muletools.mulecontextmanager;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.mule.api.context.MuleContextFactory;
import org.mule.config.ConfigResource;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

public class MuleContextManager implements ApplicationContextAware
{

    private MuleContext muleContext;
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private String muleConfigFilePattern;

    public MuleClient getMuleClient()
    {
        return getMuleContext().getClient();
    }

    private void startMuleContext(ApplicationContext applicationContext) throws MuleException, IOException
    {
        if ( getMuleContext() != null )
        {
            return;
        }

        if ( log.isDebugEnabled() )
        {
            log.debug("Creating spring config builder.");
        }

        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        Resource[] muleConfigs = pathResolver.getResources(getMuleConfigFilePattern());
        ConfigResource[] configs = new ConfigResource[muleConfigs.length];

        if ( log.isDebugEnabled() )
        {
            log.debug(muleConfigs.length + " mule configs found.");
        }

        for ( int a = 0; a < muleConfigs.length; a++ )
        {
            Resource muleConfig = muleConfigs[a];
            if ( log.isDebugEnabled() )
            {
                log.debug("Processing mule config " + muleConfig.getFilename());
            }
            configs[a] = new ConfigResource(muleConfig.getURL());
        }

        SpringXmlConfigurationBuilder builder = new SpringXmlConfigurationBuilder(configs);

        builder.setParentContext(applicationContext);
        MuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
        MuleContext muleContext = muleContextFactory.createMuleContext(builder);

        if ( log.isDebugEnabled() )
        {
            log.debug("Starting mule context");
        }

        muleContext.start();
        setMuleContext(muleContext);

        if ( log.isDebugEnabled() )
        {
            log.debug("Done.");
        }
    }

    public void shutdownBean()
    {
        try
        {
            if ( getMuleContext() != null )
            {
                log.debug("Stopping Mule context");
                getMuleContext().stop();
            }
        }
        catch (MuleException e)
        {
            log.error("Could not stop Mule context: " + e.getMessage(), e);
        }
    }


    public MuleContext getMuleContext()
    {
        return muleContext;
    }

    public void setMuleContext(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        if ( getMuleContext() == null )
        {
            try
            {
                startMuleContext(applicationContext);
            }
            catch (MuleException | IOException e)
            {
                log.error("Could not start Mule context: " + e.getMessage(), e);
                throw new IllegalStateException(e);
            }
        }
    }

    public String getMuleConfigFilePattern()
    {
        return muleConfigFilePattern;
    }

    public void setMuleConfigFilePattern(String muleConfigFilePattern)
    {
        this.muleConfigFilePattern = muleConfigFilePattern;
    }
}

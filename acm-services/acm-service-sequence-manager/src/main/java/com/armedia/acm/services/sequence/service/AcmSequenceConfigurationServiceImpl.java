package com.armedia.acm.services.sequence.service;

/*-
 * #%L
 * ACM Service: Sequence Manager
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
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.sequence.event.AcmSequenceEventPublisher;
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceConfigurationServiceImpl
        implements AcmSequenceConfigurationService, ApplicationListener<AbstractConfigurationFileEvent>, InitializingBean
{

    private final Logger log = LogManager.getLogger(getClass());

    private ObjectConverter objectConverter;

    private File sequenceConfigurationFile;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private AcmSequenceEventPublisher sequenceEventPublisher;

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.sequence.service.AcmSequenceConfigurationService#saveSequenceConfiguration(java.util.
     * List)
     */
    @Override
    public List<AcmSequenceConfiguration> saveSequenceConfiguration(List<AcmSequenceConfiguration> sequenceConfigurationList)
            throws AcmSequenceException
    {
        log.debug("Updating sequence configuration [{}]", getSequenceConfigurationFile().getAbsolutePath());
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try (OutputStream os = Files.newOutputStream(getSequenceConfigurationFile().toPath(), StandardOpenOption.TRUNCATE_EXISTING))
        {
            String sequenceConfigurationString = getObjectConverter().getJsonMarshaller().marshal(sequenceConfigurationList);
            os.write(sequenceConfigurationString.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Error while updating sequence configuration [%s]", getSequenceConfigurationFile().toPath()), e);
        }
        finally
        {
            writeLock.unlock();
        }
        return getSequenceConfiguration();
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceConfigurationService#getSequenceConfiguration()
     */
    @Override
    public List<AcmSequenceConfiguration> getSequenceConfiguration() throws AcmSequenceException
    {
        try
        {
            log.debug("Retrieving sequence configuration from [{}]", getSequenceConfigurationFile().getAbsolutePath());
            List<AcmSequenceConfiguration> sequenceConfigurationList = getObjectConverter().getJsonUnmarshaller().unmarshallCollection(
                    FileUtils.readFileToString(getSequenceConfigurationFile()),
                    List.class, AcmSequenceConfiguration.class);
            return sequenceConfigurationList;
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to retrieve sequence configuration from [%s]", getSequenceConfigurationFile().getAbsolutePath()),
                    e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {
        if (event instanceof ConfigurationFileChangedEvent && event.getConfigFile().getName().equals("acmSequenceConfiguration.json"))
        {
            try
            {
                getSequenceEventPublisher().publishSequenceConfigurationUpdatedEvent(getSequenceConfiguration());
            }
            catch (Exception e)
            {
                log.error("Reason [{}]", e.getMessage(), e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        getSequenceEventPublisher().publishSequenceConfigurationUpdatedEvent(getSequenceConfiguration());
    }

    /**
     * @return the objectConverter
     */
    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    /**
     * @param objectConverter
     *            the objectConverter to set
     */
    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    /**
     * @return the sequenceConfigurationFile
     */
    public File getSequenceConfigurationFile()
    {
        return sequenceConfigurationFile;
    }

    /**
     * @param sequenceConfigurationFile
     *            the sequenceConfigurationFile to set
     */
    public void setSequenceConfigurationFile(File sequenceConfigurationFile)
    {
        this.sequenceConfigurationFile = sequenceConfigurationFile;
    }

    /**
     * @return the sequenceEventPublisher
     */
    public AcmSequenceEventPublisher getSequenceEventPublisher()
    {
        return sequenceEventPublisher;
    }

    /**
     * @param sequenceEventPublisher
     *            the sequenceEventPublisher to set
     */
    public void setSequenceEventPublisher(AcmSequenceEventPublisher sequenceEventPublisher)
    {
        this.sequenceEventPublisher = sequenceEventPublisher;
    }

}

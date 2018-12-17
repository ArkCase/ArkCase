package com.armedia.acm.services.sequence.generator;

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
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.service.AcmSequenceService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceGeneratorManager implements ApplicationListener<AbstractConfigurationFileEvent>, InitializingBean
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, AcmSequenceConfiguration> sequenceMap = new HashMap<>();

    private Map<String, AcmSequenceGenerator> generatorsMap = new HashMap<>();

    private ObjectConverter objectConverter;

    private File sequenceConfiguration;

    private AcmSequenceService sequenceService;

    public void initSequenceMap()
    {
        log.debug("Creating sequence configuration from [{}]", getSequenceConfiguration().getAbsolutePath());
        try
        {
            Map<String, AcmSequenceConfiguration> sequenceMap = new HashMap<>();
            List<AcmSequenceConfiguration> acmSequenceConfigurations = getObjectConverter().getJsonUnmarshaller().unmarshallCollection(
                    FileUtils.readFileToString(getSequenceConfiguration()),
                    List.class, AcmSequenceConfiguration.class);

            for (AcmSequenceConfiguration sequenceConfiguration : acmSequenceConfigurations)
            {
                sequenceMap.put(sequenceConfiguration.getSequenceName(), sequenceConfiguration);
            }
            this.sequenceMap = sequenceMap;
        }
        catch (IOException e)
        {
            log.error("Unable to create sequence configuration from [{}]", getSequenceConfiguration().getAbsolutePath(), e);
        }
    }

    public void register(String partType, AcmSequenceGenerator generator)
    {
        generatorsMap.put(partType, generator);
    }

    public Boolean getSequenceEnabled(String sequenceName)
    {
        if (sequenceMap.get(sequenceName).getSequenceEnabled() != null)
        {
            return sequenceMap.get(sequenceName).getSequenceEnabled();
        }
        return Boolean.FALSE;
    }

    public String generateValue(String sequenceName, Object object) throws AcmSequenceException
    {

        String sequenceValue = "";
        List<AcmSequencePart> sequenceParts = sequenceMap.get(sequenceName).getSequenceParts();
        Map<String, Long> autoincrementPartNameToValue = new HashMap();
        for (AcmSequencePart sequencePart : sequenceParts)
        {
            sequenceValue += generatorsMap.get(sequencePart.getSequencePartType()).generatePartValue(sequenceName, sequencePart, object,
                    autoincrementPartNameToValue);
        }

        registerSequence(sequenceValue, sequenceName, autoincrementPartNameToValue);

        return sequenceValue;
    }

    private void registerSequence(String sequenceValue, String sequenceName, Map<String, Long> autoincrementPartNameToValue)
            throws AcmSequenceException
    {
        // Not using Lambda i.e. can not re-throw exception
        for (String partName : autoincrementPartNameToValue.keySet())
        {
            AcmSequenceRegistry sequenceRegistry = new AcmSequenceRegistry();
            sequenceRegistry.setSequenceValue(sequenceValue);
            sequenceRegistry.setSequencePartName(partName);
            sequenceRegistry.setSequencePartValue(autoincrementPartNameToValue.get(partName));
            sequenceRegistry.setSequenceName(sequenceName);
            getSequenceService().saveSequenceRegistry(sequenceRegistry);
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
                initSequenceMap();
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
        initSequenceMap();
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
     * @return the sequenceConfiguration
     */
    public File getSequenceConfiguration()
    {
        return sequenceConfiguration;
    }

    /**
     * @param sequenceConfiguration
     *            the sequenceConfiguration to set
     */
    public void setSequenceConfiguration(File sequenceConfiguration)
    {
        this.sequenceConfiguration = sequenceConfiguration;
    }

    /**
     * @return the sequenceService
     */
    public AcmSequenceService getSequenceService()
    {
        return sequenceService;
    }

    /**
     * @param sequenceService
     *            the sequenceService to set
     */
    public void setSequenceService(AcmSequenceService sequenceService)
    {
        this.sequenceService = sequenceService;
    }

}

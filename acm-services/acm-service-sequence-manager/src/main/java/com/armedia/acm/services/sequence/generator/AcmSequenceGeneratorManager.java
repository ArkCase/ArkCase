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
import com.armedia.acm.services.sequence.event.AcmSequenceConfigurationUpdatedEvent;
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.service.AcmSequenceConfigurationService;
import com.armedia.acm.services.sequence.service.AcmSequenceService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceGeneratorManager implements ApplicationListener<AcmSequenceConfigurationUpdatedEvent>
{

    private final Logger log = LogManager.getLogger(getClass());

    private Map<String, AcmSequenceConfiguration> sequenceMap = new HashMap<>();

    private Map<String, AcmSequenceGenerator> generatorsMap = new HashMap<>();

    private AcmSequenceService sequenceService;

    private AcmSequenceConfigurationService sequenceConfigurationService;

    public void initSequenceMap() throws AcmSequenceException
    {
        setSequenceConfiguration(getSequenceConfigurationService().getSequenceConfiguration());
    }

    public void register(String partType, AcmSequenceGenerator generator)
    {
        generatorsMap.put(partType, generator);
    }

    public Boolean isSequenceEnabled(String sequenceName)
    {
        return sequenceMap.get(sequenceName) != null && sequenceMap.get(sequenceName).getSequenceEnabled() != null
                && sequenceMap.get(sequenceName).getSequenceEnabled() && sequenceMap.get(sequenceName).getSequenceParts() != null
                && !sequenceMap.get(sequenceName).getSequenceParts().isEmpty();
    }

    public String generateValue(String sequenceName, Object object) throws AcmSequenceException
    {

        String sequenceValue = "";
        List<AcmSequencePart> sequenceParts = sequenceMap.get(sequenceName).getSequenceParts();
        Map<String, Long> autoincrementPartNameToValue = new HashMap<>();
        for (AcmSequencePart sequencePart : sequenceParts)
        {
            if (sequencePart.getSequencePartType() != null)
            {
                sequenceValue += generatorsMap.get(sequencePart.getSequencePartType()).generatePartValue(sequenceName, sequencePart, object,
                        autoincrementPartNameToValue);
            }
        }

        if (!sequenceValue.isEmpty())
        {
            registerSequence(sequenceValue, sequenceName, autoincrementPartNameToValue);
        }

        return sequenceValue;
    }

    private void registerSequence(String sequenceValue, String sequenceName, Map<String, Long> autoincrementPartNameToValue)
            throws AcmSequenceException
    {
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

    /**
     * @return the sequenceConfigurationService
     */
    public AcmSequenceConfigurationService getSequenceConfigurationService()
    {
        return sequenceConfigurationService;
    }

    /**
     * @param sequenceConfigurationService
     *            the sequenceConfigurationService to set
     */
    public void setSequenceConfigurationService(AcmSequenceConfigurationService sequenceConfigurationService)
    {
        this.sequenceConfigurationService = sequenceConfigurationService;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(AcmSequenceConfigurationUpdatedEvent event)
    {
        setSequenceConfiguration(event.getSource());
    }

    private void setSequenceConfiguration(List<AcmSequenceConfiguration> sequenceConfigurationList)
    {
        Map<String, AcmSequenceConfiguration> sequenceMap = new HashMap<>();

        for (AcmSequenceConfiguration sequenceConfiguration : sequenceConfigurationList)
        {
            sequenceMap.put(sequenceConfiguration.getSequenceName(), sequenceConfiguration);
        }
        this.sequenceMap = sequenceMap;
    }

}

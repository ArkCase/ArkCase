package com.armedia.acm.services.sequence.generator;

import com.armedia.acm.services.sequence.event.AcmSequenceConfigurationChangedEvent;
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.service.AcmSequenceConfigurationService;
import com.armedia.acm.services.sequence.service.AcmSequenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceGeneratorManager implements ApplicationListener<AcmSequenceConfigurationChangedEvent>
{

    private final Logger log = LoggerFactory.getLogger(getClass());

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
    public void onApplicationEvent(AcmSequenceConfigurationChangedEvent event)
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

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
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;
import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequencePartType;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;
import com.armedia.acm.services.sequence.service.AcmSequenceService;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceGenerator implements ApplicationListener<AbstractConfigurationFileEvent>, InitializingBean
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, AcmSequenceConfiguration> sequenceMap = new HashMap<>();

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

    public Boolean getSequenceEnabled(String sequenceName)
    {
        return sequenceMap.get(sequenceName).getSequenceEnabled();
    }

    public String generateValue(String sequenceName, Object object)
    {

        String sequenceValue = "";
        List<AcmSequencePart> sequenceParts = sequenceMap.get(sequenceName).getSequenceParts();
        Map<String, Integer> autoincrementPartNameToValue = new HashMap();
        for (AcmSequencePart sequencePart : sequenceParts)
        {
            AcmSequencePartType sequencePartType = AcmSequencePartType.valueOf(sequencePart.getSequencePartType());
            switch (sequencePartType)
            {
            case ARBITRARY_TEXT:
                sequenceValue += getArbitraryTextPart(sequencePart);
                break;
            case UUID:
                sequenceValue += getUUIDPart(sequencePart);
                break;
            case DATE:
                sequenceValue += getDatePart(sequencePart);
                break;
            case OBJECT_PROPERTY:
                sequenceValue += getObjectPropertyPart(sequencePart, object);
                break;
            case AUTOINCREMENT:
                sequenceValue += getAutoincrementPart(sequenceName, sequencePart, autoincrementPartNameToValue);
            }
        }

        registerSequence(sequenceValue, sequenceName, autoincrementPartNameToValue);

        return sequenceValue;
    }

    private String getArbitraryTextPart(AcmSequencePart sequencePart)
    {
        return sequencePart.getSequenceArbitraryText();
    }

    private String getUUIDPart(AcmSequencePart sequencePart)
    {
        return UUID.randomUUID().toString();
    }

    private String getDatePart(AcmSequencePart sequencePart)
    {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(sequencePart.getSequenceDateFormat()));
    }

    private String getObjectPropertyPart(AcmSequencePart sequencePart, Object object)
    {
        String objectPropertyValue = "";
        try
        {
            objectPropertyValue = PropertyUtils.getProperty(object, sequencePart.getSequenceObjectPropertyName()).toString();
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            log.error("Error getting property [{}], reason [{}]", sequencePart.getSequenceObjectPropertyName(),
                    e.getMessage(), e);
        }
        return objectPropertyValue;
    }

    private String getAutoincrementPart(String sequenceName, AcmSequencePart sequencePart,
            Map<String, Integer> autoincrementPartNameToValue)
    {
        String autoIncrementPartValue = "";

        Integer nextValue = 0;
        AcmSequenceEntity sequenceEntity = getSequenceService().getSequenceEntity(sequenceName, sequencePart.getSequencePartName());
        // Create and use new sequence if not exists
        if (sequenceEntity == null)
        {
            nextValue = createSequence(sequenceName, sequencePart, autoincrementPartNameToValue);
        }
        else
        {
            // Reset and use start sequence value if reset conditions are met
            List<AcmSequenceReset> sequenceResetList = getSequenceService().getSequenceResetList(sequenceName,
                    sequencePart.getSequencePartName(), Boolean.FALSE);

            if (sequenceResetList != null && !sequenceResetList.isEmpty())
            {
                nextValue = resetSequence(sequenceResetList, sequenceEntity, sequencePart, autoincrementPartNameToValue);
            }
            else
            {
                // Check for unused sequences
                if (sequencePart.getSequenceFillBlanks() != null && sequencePart.getSequenceFillBlanks())
                {
                    List<AcmSequenceRegistry> sequenceRegistryList = getSequenceService()
                            .getSequenceRegistryList(sequenceName, sequencePart.getSequencePartName(), Boolean.FALSE);
                    if (sequenceRegistryList != null && !sequenceRegistryList.isEmpty())
                    {
                        nextValue = getSequenceFromRegistry(sequenceRegistryList, sequencePart, autoincrementPartNameToValue);
                    }
                    else
                    {
                        nextValue = updateSequence(sequenceEntity, sequenceName, sequencePart, autoincrementPartNameToValue);
                    }
                }
                else
                {
                    nextValue = updateSequence(sequenceEntity, sequenceName, sequencePart, autoincrementPartNameToValue);
                }
            }
        }

        if (sequencePart.getSequenceNumberLength() != null && sequencePart.getSequenceNumberLength() > 0)
        {
            autoIncrementPartValue += String.format("%0" + sequencePart.getSequenceNumberLength() + "d", nextValue);
        }
        else
        {
            autoIncrementPartValue += nextValue;
        }

        return autoIncrementPartValue;
    }

    private void registerSequence(String sequenceValue, String sequenceName, Map<String, Integer> autoincrementPartNameToValue)
    {
        autoincrementPartNameToValue.forEach((k, v) -> {
            AcmSequenceRegistry sequenceRegistry = new AcmSequenceRegistry();
            sequenceRegistry.setSequenceValue(sequenceValue);
            sequenceRegistry.setSequencePartName(k);
            sequenceRegistry.setSequencePartValue(v);
            sequenceRegistry.setSequenceName(sequenceName);
            getSequenceService().saveSequenceRegistry(sequenceRegistry);
        });
    }

    private Integer createSequence(String sequenceName, AcmSequencePart sequencePart, Map<String, Integer> autoincrementPartNameToValue)
    {
        AcmSequenceEntity sequenceEntity = new AcmSequenceEntity();
        sequenceEntity.setSequenceName(sequenceName);
        sequenceEntity.setSequencePartName(sequencePart.getSequencePartName());
        sequenceEntity.setSequencePartValue(sequencePart.getSequenceStartNumber() + sequencePart.getSequenceIncrementSize());
        // getSequenceService().insertSequenceEntity(sequenceEntity);
        // Can not make it work EclipsLink-7311
        AcmSequenceEntity saved = getSequenceService().saveSequenceEntity(sequenceEntity);
        Integer partValue = saved.getSequencePartValue();
        autoincrementPartNameToValue.put(sequencePart.getSequencePartName(), partValue);
        return partValue;
    }

    private Integer updateSequence(AcmSequenceEntity sequenceEntity, String sequenceName, AcmSequencePart sequencePart,
            Map<String, Integer> autoincrementPartNameToValue)
    {
        /*
         * sequenceEntity.setSequencePartValue(sequenceEntity.getSequencePartValue() +
         * sequencePart.getSequenceIncrementSize());
         * try
         * {
         * getSequenceDao().save(sequenceEntity);
         * }
         * catch (OptimisticLockException ole)
         * {
         * sequenceEntity = getSequenceDao().getAcmSequence(sequenceName, sequencePart.getSequencePartName());
         * sequenceEntity.setSequencePartValue(sequenceEntity.getSequencePartValue() +
         * sequencePart.getSequenceIncrementSize());
         * getSequenceDao().save(sequenceEntity);
         * }
         */
        AcmSequenceEntity updated = getSequenceService().updateSequenceEntity(sequenceEntity, sequencePart, false);
        Integer partValue = updated.getSequencePartValue();
        autoincrementPartNameToValue.put(sequencePart.getSequencePartName(), partValue);
        return partValue;
    }

    private Integer getSequenceFromRegistry(List<AcmSequenceRegistry> sequenceRegistryList, AcmSequencePart sequencePart,
            Map<String, Integer> autoincrementPartNameToValue)
    {
        Integer partValue = sequenceRegistryList.get(0).getSequencePartValue();
        autoincrementPartNameToValue.put(sequencePart.getSequencePartName(), partValue);
        getSequenceService().removeSequenceRegistry(sequenceRegistryList.get(0).getSequenceValue());
        return partValue;
    }

    private Integer resetSequence(List<AcmSequenceReset> sequenceResetList, AcmSequenceEntity sequenceEntity, AcmSequencePart sequencePart,
            Map<String, Integer> autoincrementPartNameToValue)
    {

        for (AcmSequenceReset sequenceReset : sequenceResetList)
        {
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (currentDateTime.isAfter(sequenceReset.getResetDate()))
            {

                // Remove unused sequences from registry, not needed anymore since we reset the respective sequence
                getSequenceService().removeSequenceRegistry(sequenceReset.getSequenceName(), sequenceReset.getSequencePartName());

                // Update sequence reset configuration with executed time and executed flag
                sequenceReset.setResetExecutedDate(currentDateTime);
                sequenceReset.setResetExecutedFlag(Boolean.TRUE);
                getSequenceService().updateSequenceReset(sequenceReset);

                // Create new sequence reset configuration for repeatable reset
                if (sequenceReset.getResetRepeatableFlag().equals(Boolean.TRUE))
                {
                    AcmSequenceReset newSequenceReset = new AcmSequenceReset();
                    newSequenceReset.setSequenceName(sequenceReset.getSequenceName());
                    newSequenceReset.setSequencePartName(sequenceReset.getSequencePartName());
                    newSequenceReset.setResetRepeatableFlag(sequenceReset.getResetRepeatableFlag());
                    newSequenceReset.setResetRepeatablePeriod(sequenceReset.getResetRepeatablePeriod());
                    newSequenceReset.setResetDate(sequenceReset.getResetDate().plusDays(sequenceReset.getResetRepeatablePeriod()));
                    getSequenceService().saveSequenceReset(newSequenceReset);
                }
            }
        }

        // Save sequence entity with reset value
        AcmSequenceEntity savedSequenceEntity = getSequenceService().updateSequenceEntity(sequenceEntity, sequencePart, true);

        autoincrementPartNameToValue.put(sequencePart.getSequencePartName(), savedSequenceEntity.getSequencePartValue());
        return savedSequenceEntity.getSequencePartValue();
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

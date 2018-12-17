package com.armedia.acm.services.sequence.generator;

import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequencePart;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmObjectPropertySequenceGenerator implements AcmSequenceGenerator
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AcmSequenceGeneratorManager sequenceGeneratorManager;

    public void init()
    {
        getSequenceGeneratorManager().register("OBJECT_PROPERTY", this);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.generator.AcmSequenceGenerator#generatePartValue(java.lang.String,
     * com.armedia.acm.services.sequence.model.AcmSequencePart, java.lang.Object, java.util.Map)
     */
    @Override
    public String generatePartValue(String sequenceName, AcmSequencePart sequencePart, Object object,
            Map<String, Long> autoincrementPartNameToValue) throws AcmSequenceException
    {
        String objectPropertyValue = "";
        try
        {
            objectPropertyValue = PropertyUtils.getProperty(object, sequencePart.getSequenceObjectPropertyName()).toString();
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new AcmSequenceException("Error getting property " + sequencePart.getSequenceObjectPropertyName(), e);
        }
        return objectPropertyValue;

    }

    /**
     * @return the sequenceGeneratorManager
     */
    public AcmSequenceGeneratorManager getSequenceGeneratorManager()
    {
        return sequenceGeneratorManager;
    }

    /**
     * @param sequenceGeneratorManager
     *            the sequenceGeneratorManager to set
     */
    public void setSequenceGeneratorManager(AcmSequenceGeneratorManager sequenceGeneratorManager)
    {
        this.sequenceGeneratorManager = sequenceGeneratorManager;
    }

}

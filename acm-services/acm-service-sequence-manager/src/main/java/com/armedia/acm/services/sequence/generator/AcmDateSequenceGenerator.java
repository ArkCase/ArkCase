package com.armedia.acm.services.sequence.generator;

import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequencePart;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmDateSequenceGenerator implements AcmSequenceGenerator
{
    private AcmSequenceGeneratorManager sequenceGeneratorManager;

    public void init()
    {
        getSequenceGeneratorManager().register("DATE", this);
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
        try
        {
            DateTimeFormatter.ofPattern(sequencePart.getSequenceDateFormat());
        }
        catch (IllegalArgumentException e)
        {
            throw new AcmSequenceException(String.format("Illegal Date Format in sequence name [%s]", sequenceName), e);
        }
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(sequencePart.getSequenceDateFormat()));
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

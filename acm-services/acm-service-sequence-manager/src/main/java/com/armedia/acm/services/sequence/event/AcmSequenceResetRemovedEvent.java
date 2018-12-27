package com.armedia.acm.services.sequence.event;

import com.armedia.acm.services.sequence.model.AcmSequenceConstants;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceResetRemovedEvent extends AcmSequenceResetEvent
{

    private static final long serialVersionUID = 4367485019555899193L;

    public AcmSequenceResetRemovedEvent(AcmSequenceReset source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return AcmSequenceConstants.SEQUENCE_RESET_REMOVED_EVENT;
    }

}

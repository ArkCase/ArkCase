package com.armedia.acm.services.sequence.event;

import com.armedia.acm.services.sequence.model.AcmSequenceConstants;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceResetUpdatedEvent extends AcmSequenceResetEvent
{

    private static final long serialVersionUID = 6966180619696977334L;

    public AcmSequenceResetUpdatedEvent(AcmSequenceReset source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return AcmSequenceConstants.SEQUENCE_RESET_UPDATED_EVENT;
    }

}

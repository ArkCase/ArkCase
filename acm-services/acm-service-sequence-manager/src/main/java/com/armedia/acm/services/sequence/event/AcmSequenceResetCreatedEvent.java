/**
 * 
 */
package com.armedia.acm.services.sequence.event;

import com.armedia.acm.services.sequence.model.AcmSequenceConstants;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceResetCreatedEvent extends AcmSequenceResetEvent
{

    private static final long serialVersionUID = -104456135842858229L;

    public AcmSequenceResetCreatedEvent(AcmSequenceReset source)
    {
        super(source);
    }

    @Override
    public String getEventType()
    {
        return AcmSequenceConstants.SEQUENCE_RESET_CREATED_EVENT;
    }

}

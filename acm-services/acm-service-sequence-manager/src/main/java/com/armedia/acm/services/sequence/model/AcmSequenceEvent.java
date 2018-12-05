package com.armedia.acm.services.sequence.model;

import com.armedia.acm.core.model.AcmEvent;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceEvent extends AcmEvent
{

    private static final long serialVersionUID = -8724629643697739675L;

    /**
     * @param source
     */
    public AcmSequenceEvent(Object source)
    {
        super(source);
    }

}

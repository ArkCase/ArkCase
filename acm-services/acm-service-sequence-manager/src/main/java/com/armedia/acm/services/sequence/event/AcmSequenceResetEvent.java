/**
 * 
 */
package com.armedia.acm.services.sequence.event;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

import java.util.Date;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceResetEvent extends AcmEvent
{

    private static final long serialVersionUID = -1703979861499148278L;

    public AcmSequenceResetEvent(AcmSequenceReset source)
    {
        super(source);
        setEventDate(new Date());
    }

    @Override
    public AcmSequenceReset getSource()
    {
        return (AcmSequenceReset) super.getSource();
    }

}

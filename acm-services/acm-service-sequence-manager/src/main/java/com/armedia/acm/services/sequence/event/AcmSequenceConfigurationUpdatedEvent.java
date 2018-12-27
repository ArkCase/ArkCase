/**
 * 
 */
package com.armedia.acm.services.sequence.event;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;
import com.armedia.acm.services.sequence.model.AcmSequenceConstants;

import java.util.Date;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceConfigurationUpdatedEvent extends AcmEvent
{

    private static final long serialVersionUID = -467443790265934307L;

    public AcmSequenceConfigurationUpdatedEvent(List<AcmSequenceConfiguration> source)
    {
        super(source);
        setEventDate(new Date());
    }

    @Override
    public List<AcmSequenceConfiguration> getSource()
    {
        return (List<AcmSequenceConfiguration>) super.getSource();
    }

    @Override
    public String getEventType()
    {
        return AcmSequenceConstants.SEQUENCE_CONFIGURATION_UPDATED_EVENT;
    }

}

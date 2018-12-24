/**
 * 
 */
package com.armedia.acm.services.sequence.event;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceConfigurationEvent extends AcmEvent
{

    private static final long serialVersionUID = -467443790265934307L;

    public AcmSequenceConfigurationEvent(List<AcmSequenceConfiguration> source)
    {
        super(source);
    }

    @Override
    public List<AcmSequenceConfiguration> getSource()
    {
        return (List<AcmSequenceConfiguration>) super.getSource();
    }

}

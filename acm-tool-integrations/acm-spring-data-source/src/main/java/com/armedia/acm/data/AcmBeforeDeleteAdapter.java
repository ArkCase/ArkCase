package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

/**
 * Created by armdev on 11/24/14.
 */
class AcmBeforeDeleteAdapter extends DescriptorEventAdapter
{
    private final AcmBeforeDeleteListener beforeDeleteListener;

    public AcmBeforeDeleteAdapter(AcmBeforeDeleteListener beforeDeleteListener)
    {
        this.beforeDeleteListener = beforeDeleteListener;
    }

    @Override
    public void preDelete(DescriptorEvent event)
    {
        super.preDelete(event);

        getBeforeDeleteListener().beforeDelete(event.getObject());
    }

    public AcmBeforeDeleteListener getBeforeDeleteListener()
    {
        return beforeDeleteListener;
    }
}

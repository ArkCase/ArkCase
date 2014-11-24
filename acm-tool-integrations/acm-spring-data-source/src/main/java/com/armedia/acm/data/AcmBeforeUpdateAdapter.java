package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

/**
 * Created by armdev on 11/24/14.
 */
public class AcmBeforeUpdateAdapter extends DescriptorEventAdapter
{
    private final AcmBeforeUpdateListener beforeUpdateListener;

    public AcmBeforeUpdateAdapter(AcmBeforeUpdateListener beforeUpdateListener)
    {
        this.beforeUpdateListener = beforeUpdateListener;
    }

    @Override
    public void preUpdate(DescriptorEvent event)
    {
        super.preUpdate(event);

        getBeforeUpdateListener().beforeUpdate(event.getObject());
    }

    public AcmBeforeUpdateListener getBeforeUpdateListener()
    {
        return beforeUpdateListener;
    }
}

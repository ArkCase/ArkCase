package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

/**
 * Created by armdev on 11/24/14.
 */
class AcmBeforeInsertAdapter extends DescriptorEventAdapter
{
    private final AcmBeforeInsertListener beforeInsertListener;

    AcmBeforeInsertAdapter(AcmBeforeInsertListener beforeInsertListener)
    {
        this.beforeInsertListener = beforeInsertListener;
    }

    @Override
    public void preInsert(DescriptorEvent event)
    {
        super.preInsert(event);
        getBeforeInsertListener().beforeInsert(event.getObject());
    }

    public AcmBeforeInsertListener getBeforeInsertListener()
    {
        return beforeInsertListener;
    }
}

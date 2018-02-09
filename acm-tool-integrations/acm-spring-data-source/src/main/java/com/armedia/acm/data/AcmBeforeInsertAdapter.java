package com.armedia.acm.data;

import com.armedia.acm.core.exceptions.AcmAccessControlException;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

import javax.persistence.PersistenceException;

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
        try
        {
            getBeforeInsertListener().beforeInsert(event.getObject());
        }
        catch (AcmAccessControlException e)
        {
            throw new PersistenceException(e);
        }
    }

    public AcmBeforeInsertListener getBeforeInsertListener()
    {
        return beforeInsertListener;
    }
}

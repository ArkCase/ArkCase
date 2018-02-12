package com.armedia.acm.data;

import com.armedia.acm.core.exceptions.AcmAccessControlException;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

import javax.persistence.PersistenceException;

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
        try
        {
            getBeforeUpdateListener().beforeUpdate(event.getObject());
        }
        catch (AcmAccessControlException e)
        {
            throw new PersistenceException(e);
        }
    }

    public AcmBeforeUpdateListener getBeforeUpdateListener()
    {
        return beforeUpdateListener;
    }
}

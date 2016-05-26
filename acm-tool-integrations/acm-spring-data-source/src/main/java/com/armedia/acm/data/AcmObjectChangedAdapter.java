package com.armedia.acm.data;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmParentObjectInfo;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static com.armedia.acm.data.AcmObjectEventConstants.*;

/**
 * Listens for object changes and sends them to the acmObjectNotifier
 * <p>
 * Created by nebojsha on 10.05.2016.
 */
public class AcmObjectChangedAdapter extends DescriptorEventAdapter
{
    private AcmObjectChangedNotifier acmObjectChangedNotifier;

    @Override
    public void postDelete(DescriptorEvent event)
    {
        acmObjectChangedNotifier.notifyChange(ACTION_DELETE, event.getSource());
    }

    @Override
    public void postInsert(DescriptorEvent event)
    {
        acmObjectChangedNotifier.notifyChange(ACTION_INSERT, event.getSource());
    }

    @Override
    public void postUpdate(DescriptorEvent event)
    {
        acmObjectChangedNotifier.notifyChange(ACTION_UPDATE, event.getSource());
    }

    public void setAcmObjectChangedNotifier(AcmObjectChangedNotifier acmObjectChangedNotifier)
    {
        this.acmObjectChangedNotifier = acmObjectChangedNotifier;
    }
}

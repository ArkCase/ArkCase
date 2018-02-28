package com.armedia.acm.services.transcribe.listener;

import com.armedia.acm.plugins.ecm.model.event.EcmFileCopiedEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCreatedEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFilePersistenceEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileReplacedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public class FileAddedListener implements ApplicationListener<EcmFilePersistenceEvent>
{
    @Override
    public void onApplicationEvent(EcmFilePersistenceEvent event)
    {
        if (allow(event))
        {

        }
    }

    private boolean allow(EcmFilePersistenceEvent event)
    {
        // TODO: Restrict only for Case/Complaints?
        return event != null && (event instanceof EcmFileCreatedEvent || event instanceof EcmFileReplacedEvent || event instanceof EcmFileCopiedEvent);
    }
}

package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.event.EcmFileMovedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class EcmFIleMovedToRecycleBinDuplicationListener implements ApplicationListener<EcmFileMovedEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private EcmFileService ecmFileService;

    @Override
    public void onApplicationEvent(EcmFileMovedEvent event)
    {

        EcmFile ecmFile = (EcmFile) event.getSource();
        getEcmFileService().checkAndSetDuplicatesByHash(ecmFile);
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }
}


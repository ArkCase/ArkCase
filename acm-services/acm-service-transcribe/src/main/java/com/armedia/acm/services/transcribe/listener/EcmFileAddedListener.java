package com.armedia.acm.services.transcribe.listener;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCopiedEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCreatedEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFilePersistenceEvent;
import com.armedia.acm.plugins.ecm.model.event.EcmFileReplacedEvent;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeConfigurationException;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeConstants;
import com.armedia.acm.services.transcribe.model.TranscribeType;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public class EcmFileAddedListener implements ApplicationListener<EcmFileAddedEvent>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ArkCaseTranscribeService arkCaseTranscribeService;

    @Override
    public void onApplicationEvent(EcmFileAddedEvent event)
    {
        if (event != null && event.getSource() != null)
        {
            EcmFileVersion ecmFileVersion = event.getSource().getVersions().stream().filter(item -> event.getSource().getActiveVersionTag().equals(item.getVersionTag())).findFirst().orElse(null);
            try
            {
                getArkCaseTranscribeService().create(ecmFileVersion, TranscribeType.AUTOMATIC);
            }
            catch (CreateTranscribeException e)
            {
                LOG.error("Error while creating Transcription in automatic way.", e);
            }
        }
    }

    public ArkCaseTranscribeService getArkCaseTranscribeService()
    {
        return arkCaseTranscribeService;
    }

    public void setArkCaseTranscribeService(ArkCaseTranscribeService arkCaseTranscribeService)
    {
        this.arkCaseTranscribeService = arkCaseTranscribeService;
    }
}

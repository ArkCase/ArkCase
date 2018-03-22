package com.armedia.acm.services.transcribe.listener;

import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
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

            if (getArkCaseTranscribeService().isFileVersionTranscribable(ecmFileVersion))
            {
                try
                {
                    getArkCaseTranscribeService().create(ecmFileVersion, TranscribeType.AUTOMATIC);
                }
                catch (CreateTranscribeException e)
                {
                    LOG.warn("Creating Transcription in automatic way was not executed.");
                }
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

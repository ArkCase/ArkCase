package com.armedia.acm.services.transcribe.listener;

import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
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
    private FolderAndFilesUtils folderAndFilesUtils;

    @Override
    public void onApplicationEvent(EcmFileAddedEvent event)
    {
        if (event != null && event.isSucceeded())
        {
            EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(event.getSource(), event.getSource().getActiveVersionTag());

            if (getArkCaseTranscribeService().isFileVersionTranscribable(ecmFileVersion)
                    && getArkCaseTranscribeService().isAutomaticTranscribeOn())
            {
                try
                {
                    getArkCaseTranscribeService().create(ecmFileVersion, TranscribeType.AUTOMATIC);
                }
                catch (CreateTranscribeException e)
                {
                    LOG.warn("Creating Transcription in automatic way for MEDIA_FILE_VERSION_ID=[{}] was not executed. REASON=[{}]",
                            ecmFileVersion.getId(), e.getMessage());
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

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}

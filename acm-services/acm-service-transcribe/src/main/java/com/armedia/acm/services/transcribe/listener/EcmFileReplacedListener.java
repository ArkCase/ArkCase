package com.armedia.acm.services.transcribe.listener;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.event.EcmFileReplacedEvent;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeType;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/20/2018
 */
public class EcmFileReplacedListener implements ApplicationListener<EcmFileReplacedEvent>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ArkCaseTranscribeService arkCaseTranscribeService;
    private FolderAndFilesUtils folderAndFilesUtils;

    @Override
    public void onApplicationEvent(EcmFileReplacedEvent event)
    {
        if (event != null && event.isSucceeded())
        {
            EcmFile ecmFile = (EcmFile) event.getSource();
            EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(ecmFile, ecmFile.getActiveVersionTag());
            if (getArkCaseTranscribeService().isFileVersionTranscribable(ecmFileVersion) && getArkCaseTranscribeService().isTranscribeOn())
            {
                try
                {
                    TranscribeConfiguration configuration = getArkCaseTranscribeService().getConfiguration();
                    if (configuration.isCopyTranscriptionForNewVersion())
                    {
                        LOG.debug("Copy Transcription for replaced file with ID=[{}] and VERSION_ID=[{}]", ecmFile.getId(),
                                ecmFileVersion.getId());
                        Transcribe transcribe = getArkCaseTranscribeService()
                                .getByMediaVersionId(event.getPreviousActiveFileVersion().getId());

                        if (transcribe != null)
                        {
                            getArkCaseTranscribeService().copy(transcribe, ecmFileVersion);
                        }
                    }
                    else if (configuration.isNewTranscriptionForNewVersion() && configuration.isAutomaticEnabled())
                    {
                        LOG.debug("New Transcription for replaced file with ID=[{}] and VERSION_ID=[{}]", ecmFile.getId(),
                                ecmFileVersion.getId());
                        getArkCaseTranscribeService().create(ecmFileVersion, TranscribeType.AUTOMATIC);
                    }
                }
                catch (GetConfigurationException | GetTranscribeException | CreateTranscribeException e)
                {
                    LOG.warn("Creating Transcription for replaced file with ID=[{}] and VERSION_ID=[{}] is not executed. REASON=[{}]",
                            ecmFile.getId(), ecmFileVersion.getId(), e.getMessage());
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

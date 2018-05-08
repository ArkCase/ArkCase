package com.armedia.acm.services.transcribe.listener;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCopiedEvent;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/22/2018
 */
public class EcmFileCopiedListener implements ApplicationListener<EcmFileCopiedEvent>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ArkCaseTranscribeService arkCaseTranscribeService;
    private FolderAndFilesUtils folderAndFilesUtils;

    @Override
    public void onApplicationEvent(EcmFileCopiedEvent event)
    {
        if (event != null && event.isSucceeded() && event.getOriginal() != null)
        {
            EcmFile copy = (EcmFile) event.getSource();
            EcmFile original = event.getOriginal();

            // I've saw that we are coping only active version, no other versions for the file, so copy Transcribe
            // object only for active version
            EcmFileVersion copyActiveVersion = getFolderAndFilesUtils().getVersion(copy, copy.getActiveVersionTag());
            EcmFileVersion originalActiveVersion = getFolderAndFilesUtils().getVersion(original, original.getActiveVersionTag());
            if (originalActiveVersion != null)
            {
                try
                {
                    Transcribe transcribe = getArkCaseTranscribeService().getByMediaVersionId(originalActiveVersion.getId());
                    if (transcribe != null)
                    {
                        getArkCaseTranscribeService().copy(transcribe, copyActiveVersion);
                    }
                }
                catch (GetTranscribeException | CreateTranscribeException e)
                {
                    LOG.warn("Could not copy Transcription for EcmFile ID=[{}]. REASON=[{}]", copy != null ? copy.getId() : null,
                            e.getMessage());
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

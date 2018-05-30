package com.armedia.acm.services.transcribe.listener;

/*-
 * #%L
 * ACM Service: Transcribe
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

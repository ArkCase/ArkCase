package com.armedia.acm.services.mediaengine.listener;

/*-
 * #%L
 * ACM Service: Media engine
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.plugins.ecm.model.event.EcmFileCopiedEvent;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.service.MediaEngineService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class EcmFileCopiedListener implements ApplicationListener<EcmFileCopiedEvent>
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private MediaEngineServiceFactory mediaEngineServiceFactory;
    private FolderAndFilesUtils folderAndFilesUtils;

    @Override
    public void onApplicationEvent(EcmFileCopiedEvent event)
    {
        if (event != null && event.isSucceeded() && event.getOriginal() != null)
        {
            EcmFile copy = (EcmFile) event.getSource();
            EcmFile original = event.getOriginal();

            EcmFileVersion copyActiveVersion = getFolderAndFilesUtils().getVersion(copy, copy.getActiveVersionTag());
            EcmFileVersion originalActiveVersion = getFolderAndFilesUtils().getVersion(original, original.getActiveVersionTag());

            Map<String, MediaEngineService> list = getMediaEngineServiceFactory().getServices();
            for (Map.Entry<String, MediaEngineService> entry : list.entrySet())
            {
                MediaEngineService service = entry.getValue();
                try
                {
                    createCopy(copyActiveVersion, originalActiveVersion, service);
                }

                catch (CreateMediaEngineException | GetMediaEngineException e)
                {
                    LOG.error("Creating [{}] for file with ID=[{}] and VERSION_ID=[{}] is not executed. REASON=[{}]",
                            service.getServiceName(), copyActiveVersion.getId(), copy.getId(), e.getMessage(), e);
                }

            }
        }
    }

    private void createCopy(EcmFileVersion copyActiveVersion, EcmFileVersion originalActiveVersion,
            MediaEngineService service) throws GetMediaEngineException, CreateMediaEngineException
    {
        if (originalActiveVersion != null && service.allow(originalActiveVersion))
        {
            MediaEngine mediaEngine = service.getForCopiedFile(originalActiveVersion.getFile().getId(), originalActiveVersion.getId());

            if (mediaEngine != null)
            {
                service.copy(mediaEngine, copyActiveVersion);
            }
        }
    }

    public MediaEngineServiceFactory getMediaEngineServiceFactory()
    {
        return mediaEngineServiceFactory;
    }

    public void setMediaEngineServiceFactory(MediaEngineServiceFactory mediaEngineServiceFactory)
    {
        this.mediaEngineServiceFactory = mediaEngineServiceFactory;
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

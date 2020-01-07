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
import com.armedia.acm.plugins.ecm.model.event.EcmFileReplacedEvent;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.GetConfigurationException;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.mediaengine.service.MediaEngineService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class EcmFileReplacedListener implements ApplicationListener<EcmFileReplacedEvent>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private FolderAndFilesUtils folderAndFilesUtils;
    private MediaEngineServiceFactory mediaEngineServiceFactory;

    @Override
    public void onApplicationEvent(EcmFileReplacedEvent event)
    {
        if (event != null && event.isSucceeded())
        {
            EcmFile ecmFile = (EcmFile) event.getSource();
            EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(ecmFile, ecmFile.getActiveVersionTag());

            Map<String, MediaEngineService> list = getMediaEngineServiceFactory().getServices();
            for (Map.Entry<String, MediaEngineService> entry : list.entrySet())
            {
                MediaEngineService service = entry.getValue();

                if (service.allow(ecmFileVersion))
                {
                    try
                    {
                        replace(event, ecmFileVersion, service);
                    }
                    catch (GetConfigurationException | GetMediaEngineException | CreateMediaEngineException e)
                    {
                        LOG.warn("Creating [{}] for replaced file with ID=[{}] and VERSION_ID=[{}] is not executed. REASON=[{}]",
                                service.getServiceName(), ecmFile.getId(), ecmFileVersion.getId(), e.getMessage());
                    }
                }
            }
        }
    }

    private void replace(EcmFileReplacedEvent event, EcmFileVersion ecmFileVersion, MediaEngineService service)
            throws GetConfigurationException, GetMediaEngineException, CreateMediaEngineException
    {
        MediaEngineConfiguration configuration = service.getConfiguration();
        if (configuration.isCopyMediaEngineForNewVersion())
        {
            MediaEngine mediaEngine = service.getByMediaVersionId(event.getPreviousActiveFileVersion().getId());

            if (mediaEngine != null)
            {
                service.copy(mediaEngine, ecmFileVersion);
            }
        }
        else if (configuration.isNewMediaEngineForNewVersion() && configuration.isAutomaticEnabled())
        {
            service.create(ecmFileVersion, MediaEngineType.AUTOMATIC);
        }
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public MediaEngineServiceFactory getMediaEngineServiceFactory()
    {
        return mediaEngineServiceFactory;
    }

    public void setMediaEngineServiceFactory(MediaEngineServiceFactory mediaEngineServiceFactory)
    {
        this.mediaEngineServiceFactory = mediaEngineServiceFactory;
    }
}

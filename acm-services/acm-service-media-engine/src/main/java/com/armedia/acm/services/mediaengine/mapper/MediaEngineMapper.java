package com.armedia.acm.services.mediaengine.mapper;

/*-
 * #%L
 * ACM Service: Media Engine
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

import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineMapper
{
    private final Logger LOG = LogManager.getLogger(getClass());

    public MediaEngineDTO mediaEngineToDTO(MediaEngine mediaEngine, String tempPath)
    {
        MediaEngineDTO mediaEngineDTO = null;
        try
        {
            List<String> excludeFields = new ArrayList<>();
            excludeFields.add("mediaEcmFileVersion");
            excludeFields.add("className");

            ArkCaseBeanUtils utils = new ArkCaseBeanUtils();
            utils.setExcludeFields(excludeFields);

            mediaEngineDTO = new MediaEngineDTO();

            utils.copyProperties(mediaEngineDTO, mediaEngine);

            mediaEngineDTO.setTempPath(tempPath);

            Integer numberOfPages = mediaEngine.getMediaEcmFileVersion().getFile().getPageCount();

            Map<String, String> props = new HashMap<>();
            props.put("extension", mediaEngine.getMediaEcmFileVersion().getFile().getFileExtension());
            props.put("mimeType", mediaEngine.getMediaEcmFileVersion().getVersionMimeType());
            props.put("fileSize", mediaEngine.getMediaEcmFileVersion().getFileSizeBytes().toString());
            props.put("activeVersionMimeType", mediaEngine.getMediaEcmFileVersion().getFile().getFileActiveVersionMimeType());

            props.put("fileId", mediaEngine.getMediaEcmFileVersion().getFile().getId().toString());
            props.put("fileObjectType", mediaEngine.getMediaEcmFileVersion().getFile().getObjectType());
            props.put("fileName", mediaEngine.getMediaEcmFileVersion().getFile().getObjectType());

            props.put("containerObjectId", mediaEngine.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId().toString());
            props.put("containerObjectType",
                    mediaEngine.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId().toString());
            props.put("containerObjectTitle",
                    mediaEngine.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId().toString());
            props.put("numberOfPages", numberOfPages.toString());
            mediaEngineDTO.setProperties(props);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            LOG.warn("Could not copy properties for object with ID=[{}]. REASON=[{}]",
                    mediaEngine != null ? mediaEngine.getId() : null, e.getMessage());
        }

        return mediaEngineDTO;
    }

    public MediaEngine dtoToMediaEngine(MediaEngineDTO mediaEngineDTO)
    {
        MediaEngine mediaEngine = null;
        try
        {
            List<String> excludeFields = new ArrayList<>();
            excludeFields.add("mediaEcmFileVersion");
            excludeFields.add("className");

            ArkCaseBeanUtils utils = new ArkCaseBeanUtils();
            utils.setExcludeFields(excludeFields);

            mediaEngine = new MediaEngine();

            utils.copyProperties(mediaEngine, mediaEngineDTO);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            LOG.warn("Could not copy properties for object with ID=[{}]. REASON=[{}]",
                    mediaEngine != null ? mediaEngine.getId() : null, e.getMessage());
        }

        return mediaEngine;
    }
}

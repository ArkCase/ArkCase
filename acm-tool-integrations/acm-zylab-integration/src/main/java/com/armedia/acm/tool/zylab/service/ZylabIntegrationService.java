package com.armedia.acm.tool.zylab.service;

/*-
 * #%L
 * Tool Integrations: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.tool.zylab.exception.ZylabProductionSyncException;
import com.armedia.acm.tool.zylab.model.CreateMatterRequest;
import com.armedia.acm.tool.zylab.model.ZylabMatterCreationStatus;

import java.io.File;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
public interface ZylabIntegrationService
{
    /**
     *
     * Creates a new Matter or finds an existing one with the same name in ZyLAB with the default template
     *
     * @param matterName
     *            The name of the matter as it should be called in ZyLAB
     * @return Matter data
     */
    ZylabMatterCreationStatus createMatter(String matterName);

    /**
     * 
     * Creates a new Matter or finds an existing one with the same name in ZyLAB with a specified template
     * 
     * @param matterName
     *            The name of the matter as it should be called in ZyLAB
     * @param matterTemplateId
     *            The ZyLAB ID of the template that needs to be used for creation of the new matter
     * @return Matter data
     */
    ZylabMatterCreationStatus createMatter(String matterName, long matterTemplateId);

    /**
     *
     * Creates a new Matter or finds an existing one with the same name in ZyLAB with a specified template
     *
     * @param createMatterRequest
     *            object that holds the needed info to create a new ZyLAB Matter
     * @return Matter data
     */
    ZylabMatterCreationStatus createMatter(CreateMatterRequest createMatterRequest);

    /**
     * 
     * Downloads the production files from ZyLAB and decompresses the file into a temporary folder
     * 
     * @param matterId
     *            The ZyLAB ID of the matter that needs to be accessed
     * @param productionKey
     *            The key of the production in ZyLAB that needs to be downloaded
     * @return temporary folder with the uncompressed contents of the production
     * @throws ZylabProductionSyncException
     */
    File getZylabProductionFolder(long matterId, String productionKey) throws ZylabProductionSyncException;

    /**
     * 
     * Deletes the temporary folder that holds uncompressed production files after processing and the original zip file
     * 
     * @param tempFolder
     *            folder to delete
     */
    void cleanupTemporaryProductionFiles(File tempFolder);
}

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

import java.io.InputStream;
import java.util.List;

import com.armedia.acm.tool.zylab.model.CreateMatterRequest;
import com.armedia.acm.tool.zylab.model.MatterDTO;
import com.armedia.acm.tool.zylab.model.MatterTemplateDTO;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
public interface ZylabRestClient
{

    /**
     *
     * Sends a POST request to create a new Matter in ZyLAB with the specified info
     * 
     * @param createMatterRequest
     *            object that holds the needed info to create a new ZyLAB Matter
     * @return Matter data
     */
    MatterDTO createMatter(CreateMatterRequest createMatterRequest);

    /**
     * 
     * Sends a POST request to download the production files from ZyLAB and decompresses the file into a temporary
     * folder
     * 
     * @param matterId
     *            The ZyLAB ID of the matter that needs to be accessed
     * @param productionKey
     *            The key of the production in ZyLAB that needs to be downloaded
     * @return input stream of compressed production
     */
    InputStream getProductionFiles(long matterId, String productionKey);

    /**
     * 
     * Sends a GET request to return all matter templates in ZyLAB
     * 
     * @return a list of all matter templates
     */
    List<MatterTemplateDTO> getMatterTemplates();
}

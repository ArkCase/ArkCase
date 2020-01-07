package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
/**putFileIntoRecycleBin
 * @author darko.dimitrievski
 */
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.RecycleBinDTO;
import com.armedia.acm.plugins.ecm.model.RecycleBinItem;
import com.armedia.acm.plugins.ecm.model.RecycleBinItemDTO;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;

/**
 * @author darko.dimitrievski
 */

public interface RecycleBinItemService
{

    /**
     * Save item into Recycle Bin
     *
     * @param recycleBinItem
     *
     * @return the file contents as RecycleBinItem
     *
     */
    RecycleBinItem save(RecycleBinItem recycleBinItem);

    /**
     * Put item into Recycle Bin
     *
     * @param ecmFile, authentication, session
     *
     * @return the file contents as EcmFile
     *
     * @throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
     */
    RecycleBinItem putFileIntoRecycleBin(EcmFile ecmFile, Authentication authentication, HttpSession session) throws AcmUserActionFailedException, AcmObjectNotFoundException,
            AcmCreateObjectFailedException;

    /**
     * List all items from Recycle Bin
     *
     * @param authentication
     *
     * @return the file contents as EcmFile
     *
     * @throws  MuleException, ParseException
     */
    RecycleBinDTO findRecycleBinItems(Authentication authentication, String sortBy, String sortDir, int pageNumber, int pageSize) throws MuleException, ParseException;

    /**
     *
     * Restore all items from Recycle Bin
     *
     * @param itemsToBeRestored
     *
     * @return the file contents as EcmFile
     *
     * @throws  AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
     */
    List<RecycleBinItemDTO> restoreItemsFromRecycleBin(List<RecycleBinItemDTO> itemsToBeRestored, Authentication authentication)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException;

    /**
     *
     * Create new container or get it if it's already crated for the wanted cmis
     *
     * @param objectType, cmisRepositoryId, authentication
     *
     * @return instance of AcmContainer
     *
     * @throws  AcmCreateObjectFailedException
     */
    @Transactional
    AcmContainer getOrCreateContainerForRecycleBin(String objectType, String cmisRepositoryId, Authentication authentication) throws AcmCreateObjectFailedException;

    /**
     *
     * Remove an item from Recycle Bin
     *
     * @param fileId
     */
    void removeItemFromRecycleBin(Long fileId);
}

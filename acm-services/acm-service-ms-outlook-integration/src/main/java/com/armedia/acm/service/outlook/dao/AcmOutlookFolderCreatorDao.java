package com.armedia.acm.service.outlook.dao;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookObjectReference;

import java.util.List;
import java.util.Set;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 8, 2017
 *
 */
public interface AcmOutlookFolderCreatorDao
{

    AcmOutlookObjectReference getOutlookObjectReference(Long objectId, String objectType) throws AcmOutlookFolderCreatorDaoException;

    void deleteObjectReference(Long objectId, String objectType);

    AcmOutlookFolderCreator getFolderCreator(String systemEmailAddress, String systemPassword) throws AcmOutlookFolderCreatorDaoException;

    AcmOutlookFolderCreator getFolderCreator(Long creatorId) throws AcmOutlookFolderCreatorDaoException;

    AcmOutlookFolderCreator getFolderCreatorForObject(Long objectId, String objectType) throws AcmOutlookFolderCreatorDaoException;

    void recordFolderCreator(AcmOutlookFolderCreator creator, Long objectId, String objectType) throws AcmOutlookFolderCreatorDaoException;

    List<AcmOutlookFolderCreator> getFolderCreators();

    /**
     *
     * @param existing
     * @param folderCreator
     * @throws AcmOutlookFolderCreatorDaoException
     */
    void updateFolderCreator(AcmOutlookFolderCreator existing, AcmOutlookFolderCreator folderCreator)
            throws AcmOutlookFolderCreatorDaoException;

    Set<AcmOutlookObjectReference> getObjectReferences(AcmOutlookFolderCreator folderCreator) throws AcmOutlookFolderCreatorDaoException;

}

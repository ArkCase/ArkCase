package com.armedia.acm.service.outlook.dao;

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

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

    AcmOutlookFolderCreator getFolderCreatorForObject(Long objectId, String objectType) throws AcmOutlookFolderCreatorDaoException;

    void recordFolderCreator(AcmOutlookFolderCreator creator, Long objectId, String objectType) throws AcmOutlookFolderCreatorDaoException;

    List<AcmOutlookFolderCreator> getFolderCreatorsWithInvalidCredentials();

    /**
     *
     * @param updatedCreator
     * @return {@code true} if folders should be recreated, {@code false} otherwise. Folder should be recreated if the
     *         email address of the {@code AcmOutlookFolderCreator} is updated.
     * @see {@code AcmOutlookFolderCreator#getSystemEmailAddress()}
     * @throws AcmOutlookFolderCreatorDaoException
     */
    boolean updateFolderCreator(AcmOutlookFolderCreator updatedCreator) throws AcmOutlookFolderCreatorDaoException;

    Set<AcmOutlookObjectReference> getObjectReferences(AcmOutlookFolderCreator folderCreator);

}

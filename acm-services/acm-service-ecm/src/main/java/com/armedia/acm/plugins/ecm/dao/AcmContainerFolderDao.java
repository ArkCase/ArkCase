package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmContainerFolder;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Created by armdev on 3/11/15.
 */
@Repository
public class AcmContainerFolderDao extends AcmAbstractDao<AcmContainerFolder>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public AcmContainerFolder findFolderByObjectTypeAndId(String objectType, Long objectId) throws AcmObjectNotFoundException
    {
        TypedQuery<AcmContainerFolder> query =
                getEm().createQuery(EcmFileConstants.FIND_CONTAINER_FOLDER_QUERY, getPersistenceClass());

        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);

        try
        {
            AcmContainerFolder found = query.getSingleResult();
            log.info("Found existing folder " + found.getCmisFolderId() + "for object " + objectType + " id " + objectId);
            return found;
        }
        catch ( NoResultException e )
        {
            throw new AcmObjectNotFoundException(objectType, objectId, e.getMessage(), e);
        }
    }

    @Override
    protected Class<AcmContainerFolder> getPersistenceClass()
    {
        return AcmContainerFolder.class;
    }







}

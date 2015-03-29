package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Created by armdev on 3/11/15.
 */
@Repository
public class AcmContainerDao extends AcmAbstractDao<AcmContainer>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public AcmContainer findFolderByObjectTypeAndId(String objectType, Long objectId) throws AcmObjectNotFoundException
    {
        TypedQuery<AcmContainer> query =
                getEm().createQuery(EcmFileConstants.FIND_CONTAINER_QUERY, getPersistenceClass());

        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);

        try
        {
            AcmContainer found = query.getSingleResult();
            log.info("Found existing folder " + found.getId() + "for object " + objectType + " id " + objectId);
            return found;
        }
        catch ( NoResultException e )
        {
            throw new AcmObjectNotFoundException(objectType, objectId, e.getMessage(), e);
        }
    }
    
    public AcmContainer findByObjectTypeAndIdOrCreate(String objectType, Long objectId, String name, String title)
    {
    	AcmContainer container = null;
    	try
    	{
    		container = findFolderByObjectTypeAndId(objectType, objectId);
    		log.info("Found existing folder " + container.getId() + " for object " + objectType + " id " + objectId);
    	}
    	catch (AcmObjectNotFoundException e)
    	{
    		log.debug("Container for object " + objectType + " id " + objectId + " is not found. The new object will be created.");
    		
    		container = new AcmContainer();
    		container.setContainerObjectType(objectType);
    		container.setContainerObjectTitle(title);
    	}
    	
    	if (container.getFolder() == null)
    	{
    		name = name != null ? name : EcmFileConstants.CONTAINER_FOLDER_NAME;
    		AcmFolder folder = new AcmFolder();    		
    		folder.setName(name);
    		
    		container.setFolder(folder);
    	}
    	
    	return container;
    }

    @Override
    protected Class<AcmContainer> getPersistenceClass()
    {
        return AcmContainer.class;
    }







}

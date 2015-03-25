package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileServiceImpl implements ApplicationEventPublisherAware, EcmFileService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileTransaction ecmFileTransaction;
    
    private EcmFileDao ecmFileDao;

    private AcmContainerDao containerFolderDao;

    private ApplicationEventPublisher applicationEventPublisher;

    private Map<String, String> sortParameterNameToCmisFieldName;

    private Map<BaseTypeId, String> cmisBaseTypeToAcmType;

    private Properties ecmFileServiceProperties;

    private MuleClient muleClient;

    @Override
    public EcmFile upload(
            String fileType,
            String fileCategory,
            InputStream fileContents,
            String fileContentType,
            String fileName,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("The user '" + authentication.getName() + "' uploaded file: '" + fileName + "'");
        }

        AcmContainer container = getOrCreateContainerFolder(parentObjectType, parentObjectId);
        try
        {
            EcmFile uploaded = getEcmFileTransaction().addFileTransaction(
                    authentication,
                    fileType,
                    fileCategory,
                    fileContents,
                    fileContentType,
                    fileName,
                    targetCmisFolderId,
                    container);

            return uploaded;
        }
        catch (MuleException e)
        {
            log.error("Could not upload file: " + e.getMessage(), e);
            throw new AcmCreateObjectFailedException(fileName, e.getMessage(), e);
        }
    }

    @Override
    public EcmFile upload(
            String fileType,
            MultipartFile file,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("The user '" + authentication.getName() + "' uploaded file: '" + file.getOriginalFilename() + "'");
            log.info("File size: " + file.getSize() + "; content type: " + file.getContentType());
        }

        AcmContainer container = getOrCreateContainerFolder(parentObjectType, parentObjectId);

        try
        {
            EcmFile uploaded = getEcmFileTransaction().addFileTransaction(
                    authentication,
                    fileType,
                    file.getInputStream(),
                    file.getContentType(),
                    file.getOriginalFilename(),
                    targetCmisFolderId,
                    container);

            return uploaded;
        } catch (IOException | MuleException e)
        {
            log.error("Could not upload file: " + e.getMessage(), e);
            throw new AcmCreateObjectFailedException(file.getOriginalFilename(), e.getMessage(), e);
        }
    }


    @Override
	public EcmFile update(EcmFile ecmFile, MultipartFile file,
			Authentication authentication) throws AcmCreateObjectFailedException 
    {
    	if ( log.isInfoEnabled() )
        {
            log.info("The user '" + authentication.getName() + "' updating file: '" + file.getOriginalFilename() + "'");
        }

        EcmFileUpdatedEvent event = null;

        try
        {
            EcmFile updated = getEcmFileTransaction().updateFileTransaction(
                    authentication,
                    ecmFile,
                    file.getInputStream());

            event = new EcmFileUpdatedEvent(updated, authentication);

            event.setSucceeded(true);
            applicationEventPublisher.publishEvent(event);

            return updated;
        } catch (IOException | MuleException e)
        {
            if ( event != null )
            {
                event.setSucceeded(false);
                applicationEventPublisher.publishEvent(event);
            }
            log.error("Could not update file: " + e.getMessage(), e);
            throw new AcmCreateObjectFailedException(file.getOriginalFilename(), e.getMessage(), e);
        }
	}
    
    @Override
	public String download(Long id) throws MuleException 
    {    
    	try
    	{
    		EcmFile ecmFile = getEcmFileDao().find(id);
	    	String content = getEcmFileTransaction().downloadFileTransaction(ecmFile);
	    	
	    	return content;
	    } 
		catch (MuleException e) 
		{
			throw e;
		}
	}

    @Override
    public String createFolder(String folderPath) throws AcmCreateObjectFailedException
    {
        try
        {
            MuleMessage message = getMuleClient().send(EcmFileConstants.MULE_ENDPOINT_CREATE_FOLDER, folderPath, null);
            CmisObject cmisObject = message.getPayload(CmisObject.class);
            String cmisId = cmisObject.getId();
            return cmisId;
        }
        catch (MuleException e)
        {
            log.error("Could not create folder: " + e.getMessage(), e);
            throw new AcmCreateObjectFailedException("Folder", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AcmContainer getOrCreateContainerFolder(String objectType, Long objectId) throws
            AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        log.info("Finding folder for object " + objectType + " id " + objectId);

        try
        {
            AcmContainer retval = getContainerFolderDao().findFolderByObjectTypeAndId(objectType, objectId);
            return retval;
        }
        catch ( AcmObjectNotFoundException e)
        {
            return createContainerFolder(objectType, objectId);
        }
        catch ( PersistenceException pe )
        {
            throw new AcmUserActionFailedException("Find container folder", objectType, objectId, pe.getMessage(), pe);
        }
    }

    /**
     * Objects should really have a folder already.  Since we got here the object does not actually have one.
     * The application doesn't really care where the folder is, so we'll just create a folder in a sensible
     * location.
     * @param objectType
     * @param objectId
     * @return
     */
    private AcmContainer createContainerFolder(String objectType, Long objectId) throws AcmCreateObjectFailedException
    {
        log.debug("Creating new folder for object " + objectType + " id " + objectId);

        String path = getEcmFileServiceProperties().getProperty(EcmFileConstants.PROPERTY_KEY_DEFAULT_FOLDER_BASE_PATH);
        path += getEcmFileServiceProperties().getProperty(EcmFileConstants.PROPERTY_PREFIX_FOLDER_PATH_BY_TYPE + objectType);
        path += "/" + objectId;

        String cmisFolderId = createFolder(path);

        log.info("Created new folder " + cmisFolderId + "for object " + objectType + " id " + objectId);

        AcmContainer newContainer = new AcmContainer();
        newContainer.setContainerObjectId(objectId);
        newContainer.setContainerObjectType(objectType);
        AcmFolder newFolder = new AcmFolder();
        newFolder.setCmisFolderId(cmisFolderId);
        newFolder.setName(EcmFileConstants.CONTAINER_FOLDER_NAME);
        newContainer.setFolder(newFolder);

        newContainer = getContainerFolderDao().save(newContainer);

        return newContainer;
    }


    @Override
    public List<AcmCmisObject> listFolderContents(String folderId, String sortBy, String sortDirection)
            throws AcmListObjectsFailedException
    {
        try
        {
            String sortParam = listFolderContents_getSortSpec(sortBy, sortDirection);
            Map<String, Object> messageProperties = new HashMap<>();
            messageProperties.put("orderBy", sortParam);

            MuleMessage message = getMuleClient().send(
                    EcmFileConstants.MULE_ENDPOINT_LIST_FOLDER_CONTENTS,
                    folderId,
                    messageProperties);
            Object children = message.getPayload();
            log.debug("children type: '" + children.getClass().getName());

            ItemIterable<CmisObject> cmisChildren = (ItemIterable<CmisObject>) children;

            log.debug(cmisChildren.getTotalNumItems() + " items found");

            List<AcmCmisObject> retval = new ArrayList<>();
            for ( CmisObject cmisObject : cmisChildren )
            {
                AcmCmisObject acmCmisObject = fromCmisObject(cmisObject);
                if (acmCmisObject != null)
                {
                    retval.add(acmCmisObject);
                }
            }

            return retval;
        }
        catch (MuleException e)
        {
            log.error("Could not list folder contents: " + e.getMessage(), e);
            throw new AcmListObjectsFailedException("Folder Contents", e.getMessage(), e);
        }
    }

    private AcmCmisObject fromCmisObject(CmisObject cmisObject)
    {
        if ( ! getCmisBaseTypeToAcmType().containsKey(cmisObject.getBaseTypeId()) )
        {
            log.info("Child object is not a document or a folder, skipping");
            return null;
        }

        AcmCmisObject acmCmisObject = new AcmCmisObject();
        acmCmisObject.setCmisObjectId(cmisObject.getId());
        acmCmisObject.setName(cmisObject.getName());
        acmCmisObject.setObjectType(getCmisBaseTypeToAcmType().get(cmisObject.getBaseTypeId()));

        return acmCmisObject;
    }

    private String listFolderContents_getSortSpec(String sortBy, String sortDirection)
    {
        String sortParam = EcmFileConstants.FOLDER_LIST_DEFAULT_SORT_PARAM;
        if ( getSortParameterNameToCmisFieldName().containsKey(sortBy) )
        {
            sortParam = getSortParameterNameToCmisFieldName().get(sortBy);
        }
        sortParam = sortParam + " " + sortDirection;
        return sortParam;
    }
    
    @Override
    public String buildSafeFolderName(String folderName)
	{    	
		if (folderName != null)
		{
			String regex = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REGEX;
			String replacement = EcmFileConstants.INVALID_CHARACTERS_IN_FOLDER_NAME_REPLACEMENT;
			
			folderName = folderName.replaceAll(regex, replacement);
		}
		
		return folderName;
	}

    public EcmFileTransaction getEcmFileTransaction()
    {
        return ecmFileTransaction;
    }

    public void setEcmFileTransaction(EcmFileTransaction ecmFileTransaction)
    {
        this.ecmFileTransaction = ecmFileTransaction;
    }

    public EcmFileDao getEcmFileDao() {
		return ecmFileDao;
	}

	public void setEcmFileDao(EcmFileDao ecmFileDao) {
		this.ecmFileDao = ecmFileDao;
	}

	@Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }

    public Map<String, String> getSortParameterNameToCmisFieldName()
    {
        return sortParameterNameToCmisFieldName;
    }

    public void setSortParameterNameToCmisFieldName(Map<String, String> sortParameterNameToCmisFieldName)
    {
        this.sortParameterNameToCmisFieldName = sortParameterNameToCmisFieldName;
    }

    public Map<BaseTypeId, String> getCmisBaseTypeToAcmType()
    {
        return cmisBaseTypeToAcmType;
    }

    public void setCmisBaseTypeToAcmType(Map<BaseTypeId, String> cmisBaseTypeToAcmType)
    {
        this.cmisBaseTypeToAcmType = cmisBaseTypeToAcmType;
    }

    public AcmContainerDao getContainerFolderDao()
    {
        return containerFolderDao;
    }

    public void setContainerFolderDao(AcmContainerDao containerFolderDao)
    {
        this.containerFolderDao = containerFolderDao;
    }

    public Properties getEcmFileServiceProperties()
    {
        return ecmFileServiceProperties;
    }

    public void setEcmFileServiceProperties(Properties ecmFileServiceProperties)
    {
        this.ecmFileServiceProperties = ecmFileServiceProperties;
    }
}

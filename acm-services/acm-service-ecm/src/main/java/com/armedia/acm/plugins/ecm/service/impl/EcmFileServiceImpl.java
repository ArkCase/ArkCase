package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmContainerFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.model.FileUpload;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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

    private AcmContainerFolderDao containerFolderDao;

    private ApplicationEventPublisher applicationEventPublisher;

    private Map<String, String> sortParameterNameToCmisFieldName;

    private Map<BaseTypeId, String> cmisBaseTypeToAcmType;

    private Properties ecmFileServiceProperties;

    private MuleClient muleClient;

    @Override
    public EcmFile upload(
            String fileType,
            InputStream fileContents,
            String fileContentType,
            String fileName,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("The user '" + authentication.getName() + "' uploaded file: '" + fileName + "'");
        }

        try
        {
            EcmFile uploaded = getEcmFileTransaction().addFileTransaction(
                    authentication,
                    fileType,
                    fileContents,
                    fileContentType,
                    fileName,
                    targetCmisFolderId,
                    parentObjectType,
                    parentObjectId);

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
            String fileCategory,
            InputStream fileContents,
            String fileContentType,
            String fileName,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("The user '" + authentication.getName() + "' uploaded file: '" + fileName + "'");
        }

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
                    parentObjectType,
                    parentObjectId);

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
            Long parentObjectId) throws AcmCreateObjectFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("The user '" + authentication.getName() + "' uploaded file: '" + file.getOriginalFilename() + "'");
            log.info("File size: " + file.getSize() + "; content type: " + file.getContentType());
        }

        try
        {
            EcmFile uploaded = getEcmFileTransaction().addFileTransaction(
                    authentication,
                    fileType,
                    file.getInputStream(),
                    file.getContentType(),
                    file.getOriginalFilename(),
                    targetCmisFolderId,
                    parentObjectType,
                    parentObjectId);

            return uploaded;
        } catch (IOException | MuleException e)
        {
            log.error("Could not upload file: " + e.getMessage(), e);
            throw new AcmCreateObjectFailedException(file.getOriginalFilename(), e.getMessage(), e);
        }
    }


    @Override
    public ResponseEntity<? extends Object> upload(
            String fileType,
            MultipartFile file,
            String acceptHeader,
            String contextPath,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException
    {

        HttpHeaders responseHeaders = contentTypeFromAcceptHeader(acceptHeader);

        try
        {
            EcmFile uploaded = upload(
                    fileType,
                    file,
                    authentication,
                    targetCmisFolderId,
                    parentObjectType,
                    parentObjectId);

            FileUpload fileUpload = fileUploadFromEcmFile(file, contextPath, uploaded);

            Object retval;

            if ( responseHeaders.getContentType().equals(MediaType.TEXT_PLAIN) )
            {
                // sending a string with text/plain for IE.
                String json = constructJqueryFileUploadJson(fileUpload);
                retval = json;
            }
            else
            {
                // Jackson will convert this map into proper JSON for non-IE browsers.
                Map<String, List<FileUpload>> jsonMap = makeFileUploadMap(fileUpload);
                retval = jsonMap;
            }

            return new ResponseEntity<>(retval, responseHeaders, HttpStatus.OK);
        } catch (IOException  e)
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
    public AcmContainerFolder getOrCreateContainerFolder(String objectType, Long objectId) throws
            AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        log.info("Finding folder for object " + objectType + " id " + objectId);

        try
        {
            AcmContainerFolder retval = getContainerFolderDao().findFolderByObjectTypeAndId(objectType, objectId);
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
    private AcmContainerFolder createContainerFolder(String objectType, Long objectId) throws AcmCreateObjectFailedException
    {
        log.debug("Creating new folder for object " + objectType + " id " + objectId);

        String path = getEcmFileServiceProperties().getProperty(EcmFileConstants.PROPERTY_KEY_DEFAULT_FOLDER_BASE_PATH);
        path += getEcmFileServiceProperties().getProperty(EcmFileConstants.PROPERTY_PREFIX_FOLDER_PATH_BY_TYPE + objectType);
        path += "/" + objectId;

        String cmisFolderId = createFolder(path);

        log.info("Created new folder " + cmisFolderId + "for object " + objectType + " id " + objectId);

        AcmContainerFolder newFolder = new AcmContainerFolder();
        newFolder.setContainerObjectId(objectId);
        newFolder.setContainerObjectType(objectType);
        newFolder.setCmisFolderId(cmisFolderId);

        newFolder = getContainerFolderDao().save(newFolder);

        return newFolder;
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

    public String constructJqueryFileUploadJson(FileUpload fileUpload) throws IOException
    {
        Map<String, List<FileUpload>> retMap = makeFileUploadMap(fileUpload);

        ObjectMapper om = new ObjectMapper();

        return om.writeValueAsString(retMap);
    }

    public Map<String, List<FileUpload>> makeFileUploadMap(FileUpload fileUpload)
    {
        // construct JSON suitable for jQuery File Upload Plugin
        List<FileUpload> retval = Collections.singletonList(fileUpload);
        return Collections.singletonMap("files", retval);
    }

    public FileUpload fileUploadFromEcmFile(MultipartFile file, String contextPath, EcmFile uploaded)
    {
        FileUpload fileUpload = new FileUpload();

        String baseUrl = contextPath + "/file/" + uploaded.getFileId();
        fileUpload.setDeleteUrl(baseUrl);
        fileUpload.setName(file.getOriginalFilename());
        fileUpload.setSize(file.getSize());
        fileUpload.setUrl(baseUrl);
        fileUpload.setCreator(uploaded.getCreator());
        fileUpload.setId(uploaded.getFileId());
        fileUpload.setStatus(uploaded.getStatus());
        fileUpload.setCreated(uploaded.getCreated());
        fileUpload.setUploadFileType(uploaded.getFileType());
        return fileUpload;
    }

    public String determineResponseContentType(String acceptHeader)
    {
        // since IE is broken we have to conditionally set the response content type
        if ( acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE) || acceptHeader.contains("text/javascript"))
        {
            // good browser, it can send files via AJAX, so it can get the answer as JSON
            return MediaType.APPLICATION_JSON_VALUE;
        }
        else
        {
            // bad browser, must send the files via normal HTML file upload, so must get the answer as a string,
            // since if we send JSON response type, it will ask the user to download a JSON file.
            return MediaType.TEXT_PLAIN_VALUE;
        }
    }

    public HttpHeaders contentTypeFromAcceptHeader(String acceptType)
    {
        HttpHeaders responseHeaders = new HttpHeaders();

        String responseMimeType = determineResponseContentType(acceptType);

        responseHeaders.add("Content-Type", responseMimeType);

        return responseHeaders;
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

    public AcmContainerFolderDao getContainerFolderDao()
    {
        return containerFolderDao;
    }

    public void setContainerFolderDao(AcmContainerFolderDao containerFolderDao)
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

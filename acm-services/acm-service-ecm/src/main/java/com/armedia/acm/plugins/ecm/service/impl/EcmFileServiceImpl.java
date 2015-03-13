package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileServiceImpl implements ApplicationEventPublisherAware, EcmFileService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileTransaction ecmFileTransaction;
    
    private EcmFileDao ecmFileDao;

    private ApplicationEventPublisher applicationEventPublisher;

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
            Long parentObjectId,
            String parentObjectName) throws AcmCreateObjectFailedException
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
                    parentObjectId,
                    parentObjectName);

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
            Long parentObjectId,
            String parentObjectName) throws AcmCreateObjectFailedException
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
                    parentObjectId,
                    parentObjectName);

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
            Long parentObjectId,
            String parentObjectName) throws AcmCreateObjectFailedException
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
                    parentObjectId,
                    parentObjectName);

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
            Long parentObjectId,
            String parentObjectName) throws AcmCreateObjectFailedException
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
                    parentObjectId,
                    parentObjectName);

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
    public List<AcmCmisObject> listFolderContents(String folderId, String sortBy, String sortDirection)
            throws AcmListObjectsFailedException
    {
        try
        {
            String sortParam = "cmis:name";
            if ( "created".equals(sortBy) )
            {
                sortParam = "cmis:creationDate";
            }
            else if ( "modified".equals(sortBy) )
            {
                sortParam = "cmis:lastModificationDate";
            }
            sortParam = sortParam + " " + sortDirection;
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
                AcmCmisObject acmCmisObject = new AcmCmisObject();
                acmCmisObject.setCmisObjectId(cmisObject.getId());
                acmCmisObject.setName(cmisObject.getName());

                log.debug("child object CMIS type: " + cmisObject.getType().getDisplayName());
                log.debug("child object Java type: " + cmisObject.getClass().getName());



                if ( cmisObject.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT) )
                {
                    acmCmisObject.setObjectType("file");
                }
                else if ( cmisObject.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER) )
                {
                    acmCmisObject.setObjectType("folder");
                }
                else
                {
                    log.info("Child object is not a document or a folder, skipping");
                    continue;
                }

                retval.add(acmCmisObject);
            }


            return retval;
        }
        catch (MuleException e)
        {
            log.error("Could not list folder contents: " + e.getMessage(), e);
            throw new AcmListObjectsFailedException("Folder Contents", e.getMessage(), e);
        }
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
}

package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileServiceImpl implements ApplicationEventPublisherAware, EcmFileService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileTransaction ecmFileTransaction;
    
    private EcmFileDao ecmFileDao;

    private AcmContainerDao containerFolderDao;

    private AcmFolderDao folderDao;

    private ApplicationEventPublisher applicationEventPublisher;

    private Map<String, String> sortParameterNameToCmisFieldName;

    private Map<String, String> solrObjectTypeToAcmType;

    private Properties ecmFileServiceProperties;

    private MuleClient muleClient;

    private ExecuteSolrQuery solrQuery;
    private Map<String, String> categoryMap;

    private SearchResults searchResults;

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

        AcmContainer container = getOrCreateContainer(parentObjectType, parentObjectId);
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

        AcmContainer container = getOrCreateContainer(parentObjectType, parentObjectId);

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
    public AcmContainer getOrCreateContainer(String objectType, Long objectId) throws
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

    public AcmFolder addNewFolder(String parentFolderPath, String folderName) throws AcmCreateObjectFailedException {

        if( log.isInfoEnabled() ) {
            log.info("Creating new folder into  " + parentFolderPath + " with name " + folderName);
        }
        String path = parentFolderPath + "/" + folderName;

        String cmisFolderId = createFolder(path);

        if( log.isInfoEnabled() ){
            log.info("Created new folder " + cmisFolderId + "with name: " + folderName);
        }

        AcmFolder newFolder = new AcmFolder();
        newFolder.setCmisFolderId(cmisFolderId);
        newFolder.setName(folderName);

        return getFolderDao().save(newFolder);
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

        // the container needs a container name, so we'll make one up here, just like we made up a CMIS folder path
        String containerName = objectType + "-" + objectId;
        newContainer.setContainerObjectTitle(containerName);

        AcmFolder newFolder = new AcmFolder();
        newFolder.setCmisFolderId(cmisFolderId);
        newFolder.setName(EcmFileConstants.CONTAINER_FOLDER_NAME);
        newContainer.setFolder(newFolder);

        newContainer = getContainerFolderDao().save(newContainer);

        return newContainer;
    }

    @Override
    public AcmCmisObjectList allFilesForContainer(Authentication auth,
                                                  AcmContainer container)
            throws AcmListObjectsFailedException
    {

        log.debug("All files for container " + container.getContainerObjectType() + " " + container.getContainerObjectId());
        // This method is to search for all files that belong to a container, no matter where they are in the
        // folder hierarchy.
        String query = "{!join from=parent_object_id_i to=parent_object_id_i}object_type_s:" +
                "CONTAINER AND parent_object_id_i:" +
                container.getContainerObjectId() + " AND parent_object_type_s:" +
                container.getContainerObjectType();

        String filterQuery = "fq=object_type_s:FILE";

        // search for 50 records at a time until we find them all
        int start = 0;
        int max = 50;
        String sortBy = "created";
        String sortDirection = "ASC";

        AcmCmisObjectList retval = findObjects(auth, container, EcmFileConstants.CATEGORY_ALL, query, filterQuery,
                start, max, sortBy, sortDirection);

        int totalFiles = retval.getTotalChildren();
        int foundSoFar = retval.getChildren().size();

        log.debug("Got files " + start + " to " + foundSoFar + " of a total of " + totalFiles);

        while ( foundSoFar < totalFiles )
        {
            start += max;

            AcmCmisObjectList more = findObjects(auth, container, EcmFileConstants.CATEGORY_ALL, query, filterQuery,
                    start, max, sortBy, sortDirection);
            retval.getChildren().addAll(more.getChildren());

            foundSoFar += more.getChildren().size();

            log.debug("Got files " + start + " to " + foundSoFar + " of a total of " + totalFiles);
        }

        retval.setMaxRows(totalFiles);

        return retval;
    }


    @Override
    public AcmCmisObjectList listFolderContents(Authentication auth,
                                                AcmContainer container,
                                                String category,
                                                String sortBy,
                                                String sortDirection,
                                                int startRow,
                                                int maxRows)
            throws AcmListObjectsFailedException
    {

        // This method is to search for objects in the root of a container.  So we restrict the return list
        // to those items whose parent folder ID is the container folder id.... Note, this query assumes
        // only files and folders will have a "folder_id_i" attribute with a value that matches a container
        // folder id
        String query = "{!join from=folder_id_i to=parent_folder_id_i}object_type_s:" +
                "CONTAINER AND parent_object_id_i:" +
                container.getContainerObjectId() + " AND parent_object_type_s:" +
                container.getContainerObjectType();

        String filterQuery =
                category == null ? "fq=hidden_b:false" :
                        "fq=(category_s:" + category + " OR category_s:" + category.toUpperCase() + ") AND hidden_b:false"; // in case some bad data gets through

        return findObjects(auth, container, category, query, filterQuery, startRow, maxRows, sortBy, sortDirection);


    }

    private AcmCmisObjectList findObjects(Authentication auth, AcmContainer container, String category, String query,
                                          String filterQuery, int startRow, int maxRows, String sortBy,
                                          String sortDirection)
            throws AcmListObjectsFailedException
    {
        try
        {
            String sortParam = listFolderContents_getSortSpec(sortBy, sortDirection);

            String results = getSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.QUICK_SEARCH, query,
                    startRow, maxRows, sortParam, filterQuery);
            JSONArray docs = getSearchResults().getDocuments(results);
            int numFound = getSearchResults().getNumFound(results);

            AcmCmisObjectList retval = buildAcmCmisObjectList(container, category, numFound, sortBy, sortDirection,
                    startRow, maxRows);

            buildChildren(docs, retval);

            return retval;
        }
        catch (Exception e)
        {
            log.error("Could not list folder contents: " + e.getMessage(), e);
            throw new AcmListObjectsFailedException("Folder Contents", e.getMessage(), e);
        }
    }

    private void buildChildren(JSONArray docs, AcmCmisObjectList retval) throws ParseException
    {
        List<AcmCmisObject> cmisObjects = new ArrayList<>();
        retval.setChildren(cmisObjects);

        int count = docs.length();
        SimpleDateFormat solrFormat = new SimpleDateFormat(SearchConstants.SOLR_DATE_FORMAT);
        for ( int a = 0; a < count; a++ )
        {
            JSONObject doc = docs.getJSONObject(a);

        	AcmCmisObject object = buildAcmCmisObject(solrFormat, doc);

        	cmisObjects.add(object);
        }
    }

    private AcmCmisObjectList buildAcmCmisObjectList(AcmContainer container, String category, int numFound,
                                                     String sortBy, String sortDirection, int startRow, int maxRows)
    {
        AcmCmisObjectList retval = new AcmCmisObjectList();
        retval.setContainerObjectId(container.getContainerObjectId());
        retval.setContainerObjectType(container.getContainerObjectType());
        retval.setFolderId(container.getFolder().getId());
        retval.setTotalChildren(numFound);
        retval.setCategory(category == null ? "all" : category);
        retval.setSortBy(sortBy);
        retval.setSortDirection(sortDirection);
        retval.setStartRow(startRow);
        retval.setMaxRows(maxRows);
        return retval;
    }

    private AcmCmisObject buildAcmCmisObject(SimpleDateFormat solrFormat, JSONObject doc) throws ParseException
    {
        AcmCmisObject object = new AcmCmisObject();

        String categoryText = getSearchResults().extractString(doc, SearchConstants.PROPERTY_FILE_CATEGORY);
        if ( categoryText != null && getCategoryMap().containsKey(categoryText.toLowerCase()) )
        {
            object.setCategory(getCategoryMap().get(categoryText.toLowerCase()));
        }

        Date created = getSearchResults().extractDate(solrFormat, doc, SearchConstants.PROPERTY_CREATED);
        object.setCreated(created);

        String solrType = getSearchResults().extractString(doc, SearchConstants.PROPERTY_OBJECT_TYPE);
        String objectType = getSolrObjectTypeToAcmType().get(solrType);
        object.setObjectType(objectType);

        object.setCreator(getSearchResults().extractString(doc, SearchConstants.PROPERTY_CREATOR));

        object.setModified(getSearchResults().extractDate(solrFormat, doc, SearchConstants.PROPERTY_MODIFIED));

        object.setName(getSearchResults().extractString(doc, SearchConstants.PROPERTY_NAME));

        object.setObjectId(getSearchResults().extractLong(doc, SearchConstants.PROPERTY_OBJECT_ID_S));

        object.setType(getSearchResults().extractString(doc, SearchConstants.PROPERTY_FILE_TYPE));

        object.setVersion(getSearchResults().extractString(doc, SearchConstants.PROPERTY_VERSION));

        object.setModifier(getSearchResults().extractString(doc, SearchConstants.PROPERTY_MODIFIER));

        object.setCmisObjectId(getSearchResults().extractString(doc, SearchConstants.PROPERTY_CMIS_VERSION_SERIES_ID));
        
        object.setMimeType(getSearchResults().extractString(doc, SearchConstants.PROPERTY_MIME_TYPE));
        
        object.setStatus(getSearchResults().extractString(doc, SearchConstants.PROPERTY_STATUS));
        
        return object;
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


    @Override
    public EcmFile copyFile(Long fileId, String pathForTheNewCopy) throws AcmUserActionFailedException, AcmObjectNotFoundException {

        EcmFile file = getEcmFileDao().find(fileId);

        if( file == null ) {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE,fileId,"File not found",null);
        }

        Map<String,Object> props = new HashMap<>();
        props.put("ecmFileId",file.getVersionSeriesId());
        props.put("dstFolderPath",pathForTheNewCopy);

        EcmFile fileCopy;

        try {
            MuleMessage message = getMuleClient().send(EcmFileConstants.MULE_ENDPOINT_COPY_FILE, file, props);
            CmisObject cmisObject = message.getPayload(CmisObject.class);
            String newFileId = cmisObject.getId();
            file.setVersionSeriesId(newFileId);
            fileCopy = getEcmFileDao().save(file);
            return fileCopy;
        } catch ( MuleException e ) {
            if(log.isErrorEnabled()){
                log.error("Could not copy file "+e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_COPY_FILE,EcmFileConstants.OBJECT_FILE_TYPE,file.getId(),"Could not copy file",e);
        }
    }

    @Override
    public EcmFile moveFile(Long fileId, String pathForTheNewFileLocation) throws AcmUserActionFailedException, AcmObjectNotFoundException {


        EcmFile file = getEcmFileDao().find(fileId);


        if( file == null ) {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE,fileId,"File not found",null);
        }

        Map<String,Object> props = new HashMap<>();
        props.put("cmisObjectId", file.getVersionSeriesId());
        props.put("dstFolderId", pathForTheNewFileLocation);
        props.put("srcFolderId", file.getFolder().getCmisFolderId());

        EcmFile movedFile;

        try {
            MuleMessage message = getMuleClient().send(EcmFileConstants.MULE_ENDPOINT_MOVE_FILE, file, props);
            CmisObject cmisObject = message.getPayload(CmisObject.class);
            String cmisObjectId = cmisObject.getId();

            file.setVersionSeriesId(cmisObjectId);

            //TODO find a way to return newFolderId from mule flow
//            file.getFolder().setCmisFolderId("");
            movedFile = getEcmFileDao().save(file);
            return movedFile;
        } catch (MuleException e) {
            if(log.isErrorEnabled()){
                log.error("Could not move file "+e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_MOVE_FILE,EcmFileConstants.OBJECT_FILE_TYPE,file.getId(),"Could not move file",e);
        }
    }

    @Override
    public void deleteFile(Long objectId) throws AcmUserActionFailedException, AcmObjectNotFoundException {

        EcmFile file = getEcmFileDao().find(objectId);

        if( file == null ) {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE,objectId,"File not found",null);
        }

        Map<String,Object> props = new HashMap<>();
        props.put("ecmFileId", file.getVersionSeriesId());

        try {
            MuleMessage message = getMuleClient().send(EcmFileConstants.MULE_ENDPOINT_DELETE_FILE, file, props);

            getEcmFileDao().deleteFile(file);
        } catch ( MuleException e ) {
            if(log.isErrorEnabled()){
                log.error("Could not delete file "+e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_DELETE_FILE,EcmFileConstants.OBJECT_FILE_TYPE,file.getId(),"Could not delete file",e);

        }
    }

    @Override
    public EcmFile renameFile(Long fileId, String newFileName) throws AcmUserActionFailedException {
        EcmFile file = getEcmFileDao().find(fileId);

        Map<String,Object> props = new HashMap<>();
        props.put("ecmFileId", file.getVersionSeriesId());
        props.put("newFileName",newFileName);


        EcmFile renamedFile;
        try {
            MuleMessage message = getMuleClient().send(EcmFileConstants.MULE_ENDPOINT_RENAME_FILE, file, props);
            file.setFileName(newFileName);
            renamedFile = getEcmFileDao().save(file);
            return renamedFile;
        } catch ( MuleException e ) {
            if(log.isErrorEnabled()){
                log.error("Could not rename file "+e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_RENAME_FILE,EcmFileConstants.OBJECT_FILE_TYPE,file.getId(),"Could not rename file",e);

        }
    }

    @Override
    public EcmFile findById(Long fileId) {
        return getEcmFileDao().find(fileId);
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

    public Map<String, String> getSolrObjectTypeToAcmType()
    {
        return solrObjectTypeToAcmType;
    }

    public void setSolrObjectTypeToAcmType(Map<String, String> solrObjectTypeToAcmType)
    {
        this.solrObjectTypeToAcmType = solrObjectTypeToAcmType;
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

    public ExecuteSolrQuery getSolrQuery()
    {
        return solrQuery;
    }

    public void setSolrQuery(ExecuteSolrQuery solrQuery)
    {
        this.solrQuery = solrQuery;
    }

    public void setCategoryMap(Map<String, String> categoryMap)
    {
        this.categoryMap = categoryMap;
    }

    public Map<String, String> getCategoryMap()
    {
        return categoryMap;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public AcmFolderDao getFolderDao() {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao) {
        this.folderDao = folderDao;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }
}

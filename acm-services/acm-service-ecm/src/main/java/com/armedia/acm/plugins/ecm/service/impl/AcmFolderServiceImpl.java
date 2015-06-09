package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */

public class AcmFolderServiceImpl implements AcmFolderService, ApplicationEventPublisherAware {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;
    private AcmFolderDao folderDao;
    private AcmContainerDao containerDao;
    private EcmFileDao fileDao;
    private MuleClient muleClient;
    private EcmFileService fileService;
    private FolderAndFilesUtils folderAndFilesUtils;
    private Properties ecmFileServiceProperties;


    @Override
    public AcmFolder addNewFolder(Long parentFolderId, String newFolderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException {

        AcmFolder folder = getFolderDao().find(parentFolderId);

        return addNewFolder(folder, newFolderName);
    }
    
    @Override
    public AcmFolder addNewFolder(AcmFolder parentFolder, String newFolderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException {

        if ( parentFolder == null ){
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,null,"Folder not found",null);
        }
        String safeName = getFolderAndFilesUtils().buildSafeFolderName(newFolderName);
        String uniqueFolderName = getFolderAndFilesUtils().createUniqueFolderName(safeName);
        Map<String,Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.PARENT_FOLDER_ID,parentFolder.getCmisFolderId());
        properties.put(AcmFolderConstants.NEW_FOLDER_NAME, uniqueFolderName);
        String cmisFolderId = null;
        try {

            cmisFolderId = createNewFolderAndReturnCmisID(parentFolder, properties);
            if ( log.isDebugEnabled() ) {
                log.debug("Folder with name: " + newFolderName +"  exists inside the folder: "+ parentFolder.getName());
            }
            return prepareFolder(parentFolder, cmisFolderId, newFolderName);
        } catch ( NoResultException e ) {
            AcmFolder newFolder = new AcmFolder();
            if(cmisFolderId!=null) {
                newFolder.setCmisFolderId(cmisFolderId);
            } else {
                if ( log.isErrorEnabled() ){
                    log.error("Folder not added under "+parentFolder.getName()+" successfully" + e.getMessage(),e);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,parentFolder.getId(),"Folder was no added under "+parentFolder.getName()+" successfully",null);
            }
            newFolder.setName(newFolderName);
            newFolder.setParentFolderId(parentFolder.getId());

            AcmFolder result = getFolderDao().save(newFolder);

            return result;
        } catch ( PersistenceException | AcmFolderException | MuleException e ) {
            if ( log.isErrorEnabled() ){
                log.error("Folder not added under "+parentFolder.getName()+" successfully" + e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,parentFolder.getId(),"Folder was no added under "+parentFolder.getName()+" successfully",e);
        }
    }

    @Override
    public AcmFolder addNewFolderByPath(String targetObjectType, Long targetObjectId, String newPath) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException
    {
        log.info("New folder path: " + newPath);

        AcmContainer container = getContainerDao().findFolderByObjectTypeAndId(targetObjectType, targetObjectId);
        if (container == null)
        {
            throw new AcmObjectNotFoundException(targetObjectType, targetObjectId, "Container object not found", null);
        }

        // the new path could start with /, which will make
        // the first element in a split return null.  It could end with / too.
        if ( newPath.startsWith("/") )
        {
            newPath = newPath.substring(1);
        }
        if ( newPath.endsWith("/") )
        {
            newPath = newPath.substring(0, newPath.length() - 1);
        }

        log.info("Trimmed new path: " + newPath);

        String[] targetPathComponents = newPath.split("/");
        AcmFolder parent = container.getFolder();

        for ( String targetPathComponent : targetPathComponents )
        {
            log.info("Checking for folder named " + targetPathComponent);
            try
            {
                AcmFolder folder = getFolderDao().findFolderByNameInTheGivenParentFolder(targetPathComponent, parent.getId());
                parent = folder;
            }
            catch ( NoResultException nre )
            {
                // theoretically the folder could be created between when we check for it, and when we insert it
                // so we'll catch the key violation here
                try
                {
                    AcmFolder newFolder = addNewFolder(parent, targetPathComponent);
                    parent = newFolder;
                }
                catch (Exception e)
                {
                    AcmFolder foundFolder = getFolderDao().findFolderByNameInTheGivenParentFolder(targetPathComponent, parent.getId());
                    parent = foundFolder;
                }

            }
        }

        // by now "parent" should be the last folder found/created
        return parent;
    }

    @Override
    public String findFolderPath(String cmisFolderObjectId) throws MuleException, AcmUserActionFailedException
    {

        Map<String, Object> findFolderProperties = new HashMap<>();
        findFolderProperties.put("parentFolderId", cmisFolderObjectId);
        MuleMessage findFolderMessage = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_GET_FOLDER, null, findFolderProperties);
        if ( findFolderMessage.getInboundPropertyNames().contains(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
            MuleException muleException = findFolderMessage.getInboundProperty(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY);
            if (log.isErrorEnabled())
            {
                log.error("Folder can not be found " + muleException.getMessage(), muleException);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_CREATE_FOLDER_BY_PATH, AcmFolderConstants.OBJECT_FOLDER_TYPE, null,
                    "Folder not found", muleException);
        }

        CmisObject containerFolderObject = findFolderMessage.getPayload(CmisObject.class);
        Folder containerFolder = (Folder) containerFolderObject;

        return containerFolder.getPath();
    }


    @Override
    public AcmFolder renameFolder( Long folderId, String newFolderName ) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException {

        AcmFolder folder = getFolderDao().find(folderId);
        if ( folder == null ){
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,folderId,"Folder not found",null);
        }
        AcmFolder renamedFolder;
        try {
            folder.setName(newFolderName);
            renamedFolder = getFolderDao().save(folder);
            if ( log.isDebugEnabled() ) {
               log.debug("Folder name is changed to "+ newFolderName);
            }
            return renamedFolder;
        }  catch ( Exception e ) {
            Throwable t =  ExceptionUtils.getRootCause(e);
            if ( t instanceof  SQLIntegrityConstraintViolationException ) {
                if ( log.isErrorEnabled() ){
                    log.error("Folder " + folder.getName() + " was not renamed successfully" + e.getMessage(), e);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_RENAME_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),"Folder "+folder.getName()+" was not renamed successfully",e);
            } else {
                if(log.isErrorEnabled()){
                    log.error("Folder with name " + newFolderName + " already exists " + e.getMessage());
                }
                throw new AcmFolderException(e);
            }
        }
    }

    @Override
    public List<AcmObject> getFolderChildren(Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException {
        List<AcmObject> objectList = new ArrayList<>();

        List<AcmFolder> subfolders = getFolderDao().findSubFolders(folderId);
        if (subfolders != null && !subfolders.isEmpty())
            objectList.addAll(subfolders);

        List<EcmFile> files = getFileDao().findByFolderId(folderId);
        if (files != null && !files.isEmpty())
            objectList.addAll(files);

        return objectList;
    }

    @Override
    public AcmFolder moveFolder(AcmFolder folderForMoving, AcmFolder dstFolder) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException {

        AcmFolder movedFolder;

        if( folderForMoving == null ) {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,null, "Folder that need to be moved not found",null);
        }
        if( dstFolder == null ) {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,null, "Destination folder not found",null);
        }
        if ( folderForMoving.getParentFolderId() == null ) {
            if( log.isInfoEnabled())
                log.info("The folder: "+folderForMoving.getName()+" is a root folder, can not be moved!");
            throw  new AcmFolderException("The folder: "+folderForMoving.getName()+" is a root folder, can not be moved!");
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.ACM_FOLDER_ID, folderForMoving.getCmisFolderId());
        properties.put(AcmFolderConstants.DESTINATION_FOLDER_ID, dstFolder.getCmisFolderId());
        try {
            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_MOVE_FOLDER, folderForMoving, properties);

            if ( message.getInboundPropertyNames().contains(AcmFolderConstants.MOVE_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.MOVE_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if (log.isErrorEnabled()) {
                    log.error("Folder can not be moved successfully " + muleException.getMessage(), muleException);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folderForMoving.getId(),
                        "Folder " + folderForMoving.getName() + " can not be moved successfully", muleException);
            }

            CmisObject cmisObject = message.getPayload(CmisObject.class);
            String newFolderId = cmisObject.getId();

            folderForMoving.setCmisFolderId(newFolderId);
            folderForMoving.setParentFolderId(dstFolder.getId());
            movedFolder = getFolderDao().save(folderForMoving);
        } catch ( PersistenceException | MuleException e ) {
            if ( log.isErrorEnabled() ){
                log.error("Folder  "+folderForMoving.getName()+"not moved successfully" + e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folderForMoving.getId(),"Folder was not moved under "+dstFolder.getName()+" successfully",e);
        }
        return movedFolder;
    }

    @Override
    public AcmFolder moveRootFolder(AcmFolder folderForMoving, AcmFolder dstFolder) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException {

        AcmFolder movedFolder;

        if( folderForMoving == null ) {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,null, "Folder that need to be moved not found",null);
        }
        if( dstFolder == null ) {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,null, "Destination folder not found",null);
        }

        if ( folderForMoving.getParentFolderId() != null ) {
            if( log.isInfoEnabled())
                log.info("The folder: "+folderForMoving.getName()+" is not root folder, can not be moved!");
            throw  new AcmFolderException("The folder: "+folderForMoving.getName()+" is not root folder, can not be moved!");
        }



        Map<String, Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.ACM_FOLDER_ID, folderForMoving.getCmisFolderId());
        properties.put(AcmFolderConstants.DESTINATION_FOLDER_ID, dstFolder.getCmisFolderId());
        try {
            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_MOVE_FOLDER, folderForMoving, properties);

            if ( message.getInboundPropertyNames().contains(AcmFolderConstants.MOVE_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.MOVE_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if (log.isErrorEnabled()) {
                    log.error("Folder can not be moved successfully " + muleException.getMessage(), muleException);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folderForMoving.getId(),
                        "Folder " + folderForMoving.getName() + " can not be moved successfully", muleException);
            }

            CmisObject cmisObject = message.getPayload(CmisObject.class);
            String newFolderId = cmisObject.getId();

            folderForMoving.setCmisFolderId(newFolderId);
            folderForMoving.setParentFolderId(dstFolder.getId());
            movedFolder = getFolderDao().save(folderForMoving);
        } catch ( PersistenceException | MuleException e ) {
            if ( log.isErrorEnabled() ){
                log.error("Folder  "+folderForMoving.getName()+"not moved successfully" + e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folderForMoving.getId(),"Folder was not moved under "+dstFolder.getName()+" successfully",e);
        }
        return movedFolder;
    }

    @Override
    public AcmFolder copyFolder(Long folderToBeCopiedId, Long copyDstFolderId, Long targetObjectId,String targetObjectType) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmFolderException {

        AcmFolder toBeCopied = getFolderDao().find(folderToBeCopiedId);
        AcmFolder dstFolder = getFolderDao().find(copyDstFolderId);

        if ( toBeCopied == null ){
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,folderToBeCopiedId,"Folder not found",null);
        }
        if ( dstFolder == null ){
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,folderToBeCopiedId,"Parent folder not found",null);
        }

        if ( toBeCopied.getParentFolderId() == null ) {
            if( log.isInfoEnabled())
                log.info("The folder: "+toBeCopied.getName()+" is a root folder, can not be moved!");
            throw  new AcmFolderException("The folder: "+toBeCopied.getName()+" is a root folder, can not be moved!");
        }

        Map<String,Object> toBeCopiedFolderProperties = new HashMap<>();
        toBeCopiedFolderProperties.put(AcmFolderConstants.PARENT_FOLDER_ID, toBeCopied.getCmisFolderId());

        Map<String,Object> parentFolderProperties = new HashMap<>();
        parentFolderProperties.put(AcmFolderConstants.PARENT_FOLDER_ID, dstFolder.getCmisFolderId());

        Folder folderToBeCopied;
        Folder parentFolder;
        AcmFolder copiedFolder = null;
        boolean isFirstFolderFetched = false;
        try {
            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_GET_FOLDER, toBeCopied, toBeCopiedFolderProperties);

            if (message.getInboundPropertyNames().contains(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if (log.isErrorEnabled()) {
                    log.error("Folder not fetched successfully " + muleException.getMessage(), muleException);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, toBeCopied.getId(),
                        "Folder  " + toBeCopied.getName() + "was not fetched successfully", muleException);
            }
            CmisObject cmisObjectNewFolder= message.getPayload(CmisObject.class);
            folderToBeCopied =(Folder) cmisObjectNewFolder;

            isFirstFolderFetched = true;

            MuleMessage msg = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_GET_FOLDER, dstFolder, parentFolderProperties);

            if (message.getInboundPropertyNames().contains(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if (log.isErrorEnabled()) {
                    log.error("Folder not fetched successfully " + muleException.getMessage(), muleException);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, dstFolder.getId(),
                        "Folder  " + dstFolder.getName() + "was not fetched successfully", muleException);
            }
            CmisObject cmisObjectParentFolder = msg.getPayload(CmisObject.class);
            parentFolder =  ( Folder ) cmisObjectParentFolder;
        } catch ( MuleException e ){
            if (isFirstFolderFetched) {
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, dstFolder.getId(),
                        "Folder  " + dstFolder.getName() + "was not fetched successfully", e);
            } else {
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, toBeCopied.getId(),
                        "Folder  " + toBeCopied.getName() + "was not fetched successfully", e);
            }
        }
        copiedFolder = copyDir( parentFolder, folderToBeCopied, targetObjectId, targetObjectType);
        return copiedFolder;
    }

    private AcmFolder copyDir(Folder parentFolder,Folder toBeCopiedFolder,Long targetObjectId,String targetObjectType) throws AcmUserActionFailedException, AcmObjectNotFoundException {

        Map<String,Object> newFolderProperties = new HashMap<>();
        newFolderProperties.put(AcmFolderConstants.PARENT_FOLDER_ID, parentFolder.getId());
        newFolderProperties.put(AcmFolderConstants.NEW_FOLDER_NAME, toBeCopiedFolder.getName());

        Folder newFolder;
        AcmFolder copiedFolder = null;
        AcmFolder acmNewFolder = new AcmFolder();
        try {
            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_ADD_NEW_FOLDER, null, newFolderProperties);

            if (message.getInboundPropertyNames().contains(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if (log.isErrorEnabled()) {
                    log.error("Folder not added successfully " + muleException.getMessage(), muleException);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, null,
                        "Folder was not created under " + toBeCopiedFolder.getName() + " successfully", muleException);
            }
            CmisObject cmisObjectNewFolder = message.getPayload(CmisObject.class);
            newFolder =(Folder) cmisObjectNewFolder;

            acmNewFolder.setCmisFolderId(newFolder.getId());
            AcmFolder pFolder = getFolderDao().findByCmisFolderId(parentFolder.getId());
            acmNewFolder.setParentFolderId(pFolder.getId());
            acmNewFolder.setName(newFolder.getName());
            copiedFolder =  getFolderDao().save(acmNewFolder);

        } catch ( PersistenceException | MuleException e ){
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, null,
                    "Folder was not created under " + toBeCopiedFolder.getName() + " successfully", e);
        }
        copyChildren(newFolder, toBeCopiedFolder, targetObjectId, targetObjectType);

        return copiedFolder;
    }

    private void copyChildren(Folder parentFolder, Folder toCopyFolder, Long targetObjectId,String targetObjectType) throws AcmObjectNotFoundException, AcmUserActionFailedException {
        ItemIterable<CmisObject> immediateChildren = toCopyFolder.getChildren();
        for ( CmisObject child : immediateChildren ) {
            if ( child instanceof Document ) {
                AcmFolder acmParent = getFolderDao().findByCmisFolderId(toCopyFolder.getId());
                AcmFolder dstFolder = getFolderDao().findByCmisFolderId(parentFolder.getId());
                EcmFile ecmFile = null;
                try {
                     ecmFile = getFileDao().findByCmisFileIdAndFolderId(child.getId(), acmParent.getId());
                } catch ( NoResultException e ) {
                    if(log.isDebugEnabled()) {
                        log.debug("File with cmisId: "+child.getId() + " not found in the DB, but returned from Alfresco!",e);
                    }
                    continue;
                }
                if(ecmFile!=null) {
                 try {
                     getFileService().copyFile(ecmFile.getFileId(), targetObjectId, targetObjectType, dstFolder.getId());
                 } catch (AcmUserActionFailedException | AcmObjectNotFoundException e ){
                     throw e;
                 }
                }
            } else if ( child instanceof Folder ) {
                copyDir(parentFolder, (Folder) child, targetObjectId, targetObjectType);
            }
        }
    }

    @Override
    public void deleteFolderIfEmpty(Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException {

        AcmFolder folder = getFolderDao().find(folderId);
        if( folder == null ) {
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,folderId,"Folder not found",null);
        }

        Map<String,Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.ACM_FOLDER_ID, folder.getCmisFolderId());
        try {

            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_DELETE_EMPTY_FOLDER, folder, properties);

                if (message.getInboundPropertyNames().contains(AcmFolderConstants.DELETE_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                    MuleException muleException = message.getInboundProperty(AcmFolderConstants.DELETE_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                    if (log.isErrorEnabled()) {
                        log.error("Folder not deleted successfully " + muleException.getMessage(), muleException);
                    }
                    throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_DELETE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folder.getId(),
                            "Folder " + folder.getName() + "not deleted successfully", muleException);
            } else if (message.getInboundPropertyNames().contains(AcmFolderConstants.IS_FOLDER_NOT_EMPTY_INBOUND_PROPERTY)) {
                if (log.isErrorEnabled()) {
                    log.error("Folder "+folder.getName()+" is not empty and is not deleted!");
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_DELETE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folder.getId(),
                        "Folder " + folder.getName() + " not deleted successfully", null);
            }
            getFolderDao().deleteFolder(folderId);
        }
        catch ( PersistenceException | MuleException e ) {
            if ( log.isErrorEnabled() ){
                log.error("Folder  "+folder.getName()+"not deleted successfully" + e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),"Folder was no added under "+folder.getName()+" successfully",e);
        }
    }

    private AcmFolder prepareFolder (AcmFolder folder, String cmisFolderId, String folderName) throws AcmUserActionFailedException, PersistenceException, AcmFolderException {
        AcmFolder newFolder = new AcmFolder();
        if( cmisFolderId!=null ) {
            newFolder.setCmisFolderId(cmisFolderId);
        } else {
            if ( log.isErrorEnabled() ){
                log.error("Folder not added under "+folder.getName()+" successfully");
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),"Folder was no added under "+folder.getName()+" successfully",null);
        }
        newFolder.setName(folderName);
        newFolder.setParentFolderId(folder.getId());
        AcmFolder result;
        try {
             result = getFolderDao().save(newFolder);
        } catch (Exception e) {
            Throwable t =  ExceptionUtils.getRootCause(e);
            if ( t instanceof  SQLIntegrityConstraintViolationException ) {
                if(log.isDebugEnabled()){
                    log.debug("Folder with name "+folderName+" already exists "+e.getMessage());
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),
                        "Folder with name "+folderName+" already exists",e);
            } else {
                if(log.isErrorEnabled()){
                    log.error("Folder with name " + folderName + " already exists " + e.getMessage());
                }
                throw new AcmFolderException(e);
            }
        }
        return result;
    }

    private String createNewFolderAndReturnCmisID(AcmFolder folder, Map<String,Object> properties) throws MuleException, AcmUserActionFailedException {

        String cmisFolderId = null;

        MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_ADD_NEW_FOLDER,folder,properties);

        if ( message.getInboundPropertyNames().contains(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY)){
            MuleException muleException = message.getInboundProperty(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY);
            if( log.isErrorEnabled() ) {
                log.error("Folder not added successfully " + muleException.getMessage(),muleException);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),
                    "Folder was not created under "+folder.getName()+" successfully",muleException);
        }

        CmisObject cmisObject = message.getPayload(CmisObject.class);
        cmisFolderId = cmisObject.getId();
        return cmisFolderId;
    }

    @Override
    public AcmFolder findById(Long folderId) {
        return getFolderDao().find(folderId);
    }

    @Override
    public AcmFolder findByNameAndParent(String name, AcmFolder parent) {
        return getFolderDao().findFolderByNameInTheGivenParentFolder(name,parent.getId());
    }

    @Override
	public void addFolderStructure(AcmContainer container, AcmFolder parentFolder, JSONArray folderStructure) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException {
		if (folderStructure != null)
		{
			for (int i = 0; i < folderStructure.length(); i++)
			{
				if (isJSONObject(folderStructure, i))
				{
					JSONObject folderJSONObject = folderStructure.getJSONObject(i);
					
					String name = folderJSONObject.getString(AcmFolderConstants.FOLDER_STRUCTURE_KEY_NAME);
					Boolean attachments = folderJSONObject.getBoolean(AcmFolderConstants.FOLDER_STRUCTURE_KEY_ATTACHMENT);
					
					AcmFolder folder = addNewFolder(parentFolder, name);
					
					if (attachments != null && attachments)
					{
						container.setAttachmentFolder(folder);
						getContainerDao().save(container);
					}
					
					if (isJSONArray(folderJSONObject, AcmFolderConstants.FOLDER_STRUCTURE_KEY_CHILDREN))
					{
						addFolderStructure(container, folder, folderJSONObject.getJSONArray(AcmFolderConstants.FOLDER_STRUCTURE_KEY_CHILDREN));
					}
				}
			}
		}
	}
    
    private boolean isJSONObject(Object json, Object key)
    {
    	if (json != null)
    	{
	    	try
			{
	    		if (json instanceof JSONObject && key instanceof String)
	    		{
	    			((JSONObject) json).getJSONObject((String) key);
	    			return true;
	    		}
	    		
	    		if (json instanceof JSONArray && key instanceof Integer)
	    		{
	    			((JSONArray) json).getJSONObject((Integer) key);
	    			return true;
	    		}
			}
			catch (Exception e)
			{
				log.debug("Element with key=" + key + " in the json=" + json.toString() + " is not JSONObject.");
			}
    	}
    	
    	return false;
    }
    
    private boolean isJSONArray(Object json, Object key)
    {
    	if (json != null)
    	{
	    	try
			{
	    		if (json instanceof JSONObject && key instanceof String)
	    		{
	    			((JSONObject) json).getJSONArray((String) key);
					return true;
	    		}
	    		
	    		if (json instanceof JSONArray && key instanceof Integer)
	    		{
	    			((JSONArray) json).getJSONArray((Integer) key);
	    			return true;
	    		}
			}
			catch (Exception e)
			{
				log.debug("Element with key=" + key + " in the json=" + json.toString() + " is not JSONArray.");
			}
    	}
    	
    	return false;
    }

    @Override
    public String getFolderPath(AcmFolder folder) throws AcmObjectNotFoundException {
        if (folder.getParentFolderId() != null) {
            AcmFolder parent = findById(folder.getParentFolderId());
            if (parent == null)
                throw new AcmObjectNotFoundException(folder.getObjectType(), folder.getParentFolderId(), "Folder not found in database");
            return getFolderPath(parent) + "/" + folder.getName();
        } else
            return "";
    }

    @Override
    public boolean folderPathExists(String folderPath, AcmContainer container) throws AcmFolderException {
        log.info("Checking existence of path {} in container id = ", folderPath, container);

        Objects.requireNonNull(container, "Container should not be null");
        if (folderPath != null)
            folderPath = folderPath.trim();
        if (StringUtils.isEmpty(folderPath))
            throw new AcmFolderException("Folder path should not be null/empty");

        // format path and prepare for split
        if (folderPath.startsWith("/")) {
            folderPath = folderPath.substring(1);
        }
        if (folderPath.endsWith("/")) {
            folderPath = folderPath.substring(0, folderPath.length() - 1);
        }

        log.info("Formatted path: " + folderPath);

        String[] targetPathComponents = folderPath.split("/");
        AcmFolder parent = container.getFolder();

        for (String targetPathComponent : targetPathComponents) {
            log.info("Checking for folder named " + targetPathComponent);
            try {
                AcmFolder folder = getFolderDao().findFolderByNameInTheGivenParentFolder(targetPathComponent, parent.getId());
                parent = folder;
            } catch (NoResultException nre) {
                //this folder doesn't exists in the path
                return false;
            }
        }

        return true;
    }

    public EcmFileDao getFileDao() {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao) {
        this.fileDao = fileDao;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    public AcmFolderDao getFolderDao() {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao) {
        this.folderDao = folderDao;
    }

    public AcmContainerDao getContainerDao() {
		return containerDao;
	}

	public void setContainerDao(AcmContainerDao containerDao) {
		this.containerDao = containerDao;
	}

	public MuleClient getMuleClient() {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient) {
        this.muleClient = muleClient;
    }

    public EcmFileService getFileService() {
        return fileService;
    }

    public void setFileService(EcmFileService fileService) {
        this.fileService = fileService;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils() {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils) {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public Properties getEcmFileServiceProperties() {
        return ecmFileServiceProperties;
    }

    public void setEcmFileServiceProperties(Properties ecmFileServiceProperties) {
        this.ecmFileServiceProperties = ecmFileServiceProperties;
    }
}

package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.*;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    private AcmFolder copiedFolder;
    private boolean isFirstFolder = true;


    @Override
    public AcmFolder addNewFolder(Long parentFolderId, String newFolderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException {

        AcmFolder folder = getFolderDao().find(parentFolderId);

        return addNewFolder(folder, newFolderName);
    }
    
    @Override
    public AcmFolder addNewFolder(AcmFolder parentFolder, String newFolderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException {

        Map<String,Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.PARENT_FOLDER_ID,parentFolder.getCmisFolderId());
        properties.put(AcmFolderConstants.NEW_FOLDER_NAME, getFolderAndFilesUtils().buildSafeFolderName(newFolderName));
        String cmisFolderId = null;
        try {

            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_ADD_NEW_FOLDER,parentFolder,properties);

            if ( message.getInboundPropertyNames().contains(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY)){
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if( log.isErrorEnabled() ) {
                    log.error("Folder not added successfully " + muleException.getMessage(),muleException);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,parentFolder.getId(),
                        "Folder was not created under "+parentFolder.getName()+" successfully",muleException);
            }

            CmisObject cmisObject = message.getPayload(CmisObject.class);
            cmisFolderId = cmisObject.getId();

            //if folder already exists mule will return existing object, we will do the same
            AcmFolder existingFolder = getFolderDao().findByCmisFolderId(cmisFolderId);

            if ( log.isDebugEnabled() ) {
                log.debug("Folder with name: " + newFolderName +"  exists inside the folder: "+ parentFolder.getName());
            }
            return existingFolder;
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
        } catch ( PersistenceException | MuleException e ) {
            if ( log.isErrorEnabled() ){
                log.error("Folder not added under "+parentFolder.getName()+" successfully" + e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,parentFolder.getId(),"Folder was no added under "+parentFolder.getName()+" successfully",e);
        }
    }

    @Override
    public AcmFolder renameFolder( Long folderId, String newFolderName ) throws AcmUserActionFailedException {

        AcmFolder folder = getFolderDao().find(folderId);
        AcmFolder renamedFolder;
        try{
            folder.setName(newFolderName);
            renamedFolder = getFolderDao().save(folder);
            if ( log.isDebugEnabled() ) {
               log.debug("Folder name is changed to "+ newFolderName);
            }
            return renamedFolder;
        }  catch ( Exception e ) {
            if ( log.isErrorEnabled() ){
                log.error("Folder "+folder.getName()+" was not renamed successfully" + e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_RENAME_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),"Folder "+folder.getName()+" was not renamed successfully",e);
        }
    }

    @Override
    public AcmCmisObjectList getFolderChildren(String objectType, Long objectId, Long folderId) throws AcmUserActionFailedException, AcmObjectNotFoundException {

        AcmFolder folder = getFolderDao().find(folderId);
        if( folder == null )
            throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE,folderId,"Folder not found",null);
        Map<String,Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.ACM_FOLDER_ID,folder.getCmisFolderId());
        AcmCmisObjectList objectList;
        try {
            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_LIST_FOLDER, folder, properties);
            if ( message.getInboundPropertyNames().contains(AcmFolderConstants.LIST_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.LIST_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if (log.isErrorEnabled()) {
                    log.error("Folder children can not fetched successfully " + muleException.getMessage(), muleException);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_LIST_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folder.getId(),
                        "Folder " + folder.getName() + "can not be listed successfully", muleException);
            }
            objectList = prepareAcmCmisObjectList(objectType, objectId, folderId, message);
        } catch ( PersistenceException | MuleException e ) {
            if (log.isErrorEnabled()) {
                log.error("Folder  " + folder.getName() + "can not be listed successfully" + e.getMessage(), e);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_LIST_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folder.getId(), "Folder " + folder.getName() + "can not be listed successfully", e);
        }
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
        boolean isFirstFolderFetched = false;
        try {
            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_GET_FOLDER, toBeCopied, toBeCopiedFolderProperties);

            if (message.getInboundPropertyNames().contains(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.GET_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if (log.isErrorEnabled()) {
                    log.error("Folder not fetched successfully " + muleException.getMessage(), muleException);
                }
                copiedFolder = null;
                isFirstFolder = true;
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
                copiedFolder = null;
                isFirstFolder = true;
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, dstFolder.getId(),
                        "Folder  " + dstFolder.getName() + "was not fetched successfully", muleException);
            }
            CmisObject cmisObjectParentFolder = msg.getPayload(CmisObject.class);
            parentFolder =  ( Folder ) cmisObjectParentFolder;
        } catch ( MuleException e ){
            copiedFolder = null;
            isFirstFolder = true;
            if (isFirstFolderFetched) {
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, dstFolder.getId(),
                        "Folder  " + dstFolder.getName() + "was not fetched successfully", e);
            } else {
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, toBeCopied.getId(),
                        "Folder  " + toBeCopied.getName() + "was not fetched successfully", e);
            }
        }
        copyDir( parentFolder, folderToBeCopied, targetObjectId, targetObjectType);
        AcmFolder result = copiedFolder;
        copiedFolder = null;
        isFirstFolder = true;
        return result;
    }

    private void copyDir(Folder parentFolder,Folder toBeCopiedFolder,Long targetObjectId,String targetObjectType) throws AcmUserActionFailedException, AcmObjectNotFoundException {

        Map<String,Object> newFolderProperties = new HashMap<>();
        newFolderProperties.put(AcmFolderConstants.PARENT_FOLDER_ID, parentFolder.getId());
        newFolderProperties.put(AcmFolderConstants.NEW_FOLDER_NAME, toBeCopiedFolder.getName());

        Folder newFolder;
        AcmFolder acmNewFolder = new AcmFolder();
        try {
            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_ADD_NEW_FOLDER, null, newFolderProperties);

            if (message.getInboundPropertyNames().contains(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if (log.isErrorEnabled()) {
                    log.error("Folder not added successfully " + muleException.getMessage(), muleException);
                }
                copiedFolder = null;
                isFirstFolder = true;
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, null,
                        "Folder was not created under " + toBeCopiedFolder.getName() + " successfully", muleException);
            }
            CmisObject cmisObjectNewFolder = message.getPayload(CmisObject.class);
            newFolder =(Folder) cmisObjectNewFolder;

            acmNewFolder.setCmisFolderId(newFolder.getId());
            AcmFolder pFolder = getFolderDao().findByCmisFolderId(parentFolder.getId());
            acmNewFolder.setParentFolderId(pFolder.getId());
            acmNewFolder.setName(newFolder.getName());
            if ( isFirstFolder ) {
                 isFirstFolder = false;
                 copiedFolder =  getFolderDao().save(acmNewFolder);
            } else {
                 getFolderDao().save(acmNewFolder);
            }
        } catch ( PersistenceException | MuleException e ){
            copiedFolder = null;
            isFirstFolder = true;
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, null,
                    "Folder was not created under " + toBeCopiedFolder.getName() + " successfully", e);
        }
        copyChildren(newFolder, toBeCopiedFolder, targetObjectId, targetObjectType);
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
                     copiedFolder = null;
                     isFirstFolder = true;
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
        properties.put(AcmFolderConstants.ACM_FOLDER_ID,folder.getCmisFolderId());
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

    private AcmCmisObjectList prepareAcmCmisObjectList(String objectType, Long objectId, Long folderId, MuleMessage message) throws TransformerException {

        boolean isMoreItemsLeft = true;
        AcmCmisObjectList objectList = new AcmCmisObjectList();
        List<AcmCmisObject> acmCmisObjects = new ArrayList<>();
        ItemIterable<CmisObject> cmisObjects = message.getPayload(ItemIterable.class);
        objectList.setTotalChildren((int) cmisObjects.getPageNumItems());
        objectList.setContainerObjectId(objectId);
        objectList.setContainerObjectType(objectType);
        objectList.setFolderId(folderId);
        int pageNumber = AcmFolderConstants.ZERO;

        while (isMoreItemsLeft) {
            for (CmisObject cmisObject : cmisObjects) {
                String cmisObjectId = cmisObject.getId();
                ObjectType type = cmisObject.getBaseType();
                AcmFolder folder;
                EcmFile file;
                AcmCmisObject object = new AcmCmisObject();
                if (type.getId().equals(AcmFolderConstants.CMIS_OBJECT_TYPE_ID_FOLDER)) {
                    folder = getFolderDao().findByCmisFolderId(cmisObjectId);
                    object.setCreator(folder.getCreator());
                    object.setCreated(folder.getCreated());
                    object.setCmisObjectId(cmisObjectId);
                    object.setModified(folder.getModified());
                    object.setModifier(folder.getModifier());
                    object.setObjectId(folder.getId());
                    object.setObjectType(folder.getObjectType());
                    object.setName(folder.getName());
                } else if (type.getId().equals(AcmFolderConstants.CMIS_OBJECT_TYPE_ID_FILE)) {
                    file = getFileDao().findByCmisFileIdAndFolderId(cmisObjectId, folderId);
                    object.setCreator(file.getCreator());
                    object.setCreated(file.getCreated());
                    object.setCmisObjectId(cmisObjectId);
                    object.setModified(file.getModified());
                    object.setModifier(file.getModifier());
                    object.setObjectId(file.getId());
                    object.setObjectType(file.getObjectType());
                    object.setName(file.getFileName());
                    object.setCategory(file.getCategory());
                    object.setVersion(file.getActiveVersionTag());
                    object.setStatus(file.getStatus());
                    object.setMimeType(file.getFileMimeType());
                    object.setType(file.getFileType());
                }
                if (cmisObjects.getHasMoreItems()) {
                    acmCmisObjects.add(object);
                    cmisObjects = cmisObjects.getPage(pageNumber);
                    pageNumber++;
                } else {
                    acmCmisObjects.add(object);
                    isMoreItemsLeft = false;
                }
            }
        }
        objectList.setChildren(acmCmisObjects);
        return objectList;
    }
    @Override
    public AcmFolder findById(Long folderId) {
        return getFolderDao().find(folderId);
    }
    
    @Override
	public void addFolderStructure(AcmContainer container, AcmFolder parentFolder, JSONArray folderStructure) throws AcmCreateObjectFailedException, AcmUserActionFailedException {
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

    public AcmFolder getCopiedFolder() {
        return copiedFolder;
    }

    public void setCopiedFolder(AcmFolder copiedFolder) {
        this.copiedFolder = copiedFolder;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils() {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils) {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}

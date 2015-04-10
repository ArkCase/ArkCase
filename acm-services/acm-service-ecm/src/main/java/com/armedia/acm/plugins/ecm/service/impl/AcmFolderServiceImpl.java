package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 03.04.2015.
 */

public class AcmFolderServiceImpl implements AcmFolderService, ApplicationEventPublisherAware {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;
    private AcmFolderDao folderDao;
    private MuleClient muleClient;

    @Override
    public AcmFolder addNewFolder(Long parentFolderId, String newFolderName) throws AcmCreateObjectFailedException, AcmUserActionFailedException {

        AcmFolder folder = getFolderDao().find(parentFolderId);

        Map<String,Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.PARENT_FOLDER_ID,folder.getCmisFolderId());
        properties.put(AcmFolderConstants.NEW_FOLDER_NAME, FolderAndFilesUtils.buildSafeFolderName(newFolderName));
        String cmisFolderId = null;
        try{

            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_ADD_NEW_FOLDER,folder,properties);

            if (message.getInboundPropertyNames().contains(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY)){
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if(log.isErrorEnabled()) {
                    log.error("Folder not added successfully " + muleException.getMessage(),muleException);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),
                        "Folder was no created under "+folder.getName()+" successfully",muleException);
            }

            CmisObject cmisObject = message.getPayload(CmisObject.class);
            cmisFolderId = cmisObject.getId();

            //if folder already exists mule will return existing object, we will do the same
            AcmFolder existingFolder = getFolderDao().findByCmisFolderId(cmisFolderId);

            if ( log.isDebugEnabled() ) {
                log.debug("Folder with name: " + newFolderName +"  exists inside the folder: "+ folder.getName());
            }
            return existingFolder;
        } catch ( NoResultException e ) {
            AcmFolder newFolder = new AcmFolder();
            if(cmisFolderId!=null) {
                newFolder.setCmisFolderId(cmisFolderId);
            } else {
                if ( log.isErrorEnabled() ){
                    log.error("Folder not added under "+folder.getName()+" successfully" + e.getMessage(),e);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),"Folder was no added under "+folder.getName()+" successfully",null);
            }
            newFolder.setName(newFolderName);
            newFolder.setParentFolderId(folder.getId());

            AcmFolder result = getFolderDao().save(newFolder);

            return result;
        } catch ( PersistenceException | MuleException e ) {
            if ( log.isErrorEnabled() ){
                log.error("Folder not added under "+folder.getName()+" successfully" + e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_ADD_NEW_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),"Folder was no added under "+folder.getName()+" successfully",e);
        }
    }

    @Override
    public AcmFolder renameFolder( Long folderId, String newFolderName ) throws AcmUserActionFailedException {

        AcmFolder folder = getFolderDao().find(folderId);

        AcmFolder renamedFolder;

        Map<String,Object> properties = new HashMap<>();
        properties.put(AcmFolderConstants.ACM_FOLDER_ID,folder.getCmisFolderId());
        properties.put(AcmFolderConstants.NEW_FOLDER_NAME,newFolderName);

        try{

            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_RENAME_FOLDER,folder,properties);
            CmisObject cmisObject = message.getPayload(CmisObject.class);

            folder.setName(newFolderName);

            renamedFolder = getFolderDao().save(folder);

            if ( log.isDebugEnabled() ) {
               log.debug("Folder name is changed to "+ cmisObject.getName());
            }
            return renamedFolder;
        }  catch ( MuleException e ) {
            if ( log.isErrorEnabled() ){
                log.error("Folder "+folder.getName()+" was not renamed successfully" + e.getMessage(),e);
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_RENAME_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,folder.getId(),"Folder "+folder.getName()+" was not renamed successfully",e);
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

                if (message.getInboundPropertyNames().contains(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY)) {
                MuleException muleException = message.getInboundProperty(AcmFolderConstants.ADD_NEW_FOLDER_EXCEPTION_INBOUND_PROPERTY);
                if (log.isErrorEnabled()) {
                    log.error("Folder not deleted successfully " + muleException.getMessage(), muleException);
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_DELETE_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folder.getId(),
                        "Folder " + folder.getName() + "not deleted successfully", muleException);
            } else if (message.getInboundPropertyNames().contains(AcmFolderConstants.IS_FOLDER_NOT_EMPTY_INBOUND_PROPERTY)) {
                if (log.isErrorEnabled()) {
                    log.error("Folder "+folder.getName()+" is not empty and is not deleted!");
                }
                throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_DELETE_NEW_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folder.getId(),
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

    @Override
    public AcmFolder findById(Long folderId) {
        return getFolderDao().find(folderId);
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

    public MuleClient getMuleClient() {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient) {
        this.muleClient = muleClient;
    }
}

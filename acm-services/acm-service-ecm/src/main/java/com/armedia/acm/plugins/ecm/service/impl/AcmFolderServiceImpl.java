package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
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
        try{

            MuleMessage message = getMuleClient().send(AcmFolderConstants.MULE_ENDPOINT_ADD_NEW_FOLDER,folder,properties);
            CmisObject cmisObject = message.getPayload(CmisObject.class);

            String cmisFolderId = cmisObject.getId();

            AcmFolder newFolder = new AcmFolder();
            newFolder.setCmisFolderId(cmisFolderId);
            newFolder.setName(newFolderName);
            newFolder.setParentFolderId(folder.getId());

            AcmFolder result = getFolderDao().save(newFolder);
            if ( log.isDebugEnabled() ) {
                log.debug("New folder with name: " + newFolderName +"  is added inside the folder: "+ folder.getName());
            }
            return result;
        }  catch ( MuleException e ) {
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
        properties.put(AcmFolderConstants.ACM_FOLER_ID,folder.getCmisFolderId());
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
    public void deleteFolderIfEmpty(Long folderId) throws AcmUserActionFailedException {

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

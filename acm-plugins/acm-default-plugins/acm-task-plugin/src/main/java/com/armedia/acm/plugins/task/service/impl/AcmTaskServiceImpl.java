package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationConstants;
import com.armedia.acm.plugins.objectassociation.model.Reference;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationEventPublisher;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import org.activiti.engine.ActivitiException;
import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nebojsha on 22.06.2015.
 */
public class AcmTaskServiceImpl implements AcmTaskService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private TaskDao taskDao;
    private NoteDao noteDao;
    private EcmFileDao fileDao;
    private TaskEventPublisher taskEventPublisher;
    private ExecuteSolrQuery executeSolrQuery;
    private AcmContainerDao acmContainerDao;
    private SearchResults searchResults = new SearchResults();
    private AcmFolderService acmFolderService;
    private EcmFileService ecmFileService;
    private ObjectAssociationService objectAssociationService;
    private ObjectAssociationEventPublisher objectAssociationEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void copyTasks(Long fromObjectId,
                          String fromObjectType,
                          Long toObjectId,
                          String toObjectType,
                          String toObjectName,
                          Authentication auth,
                          String ipAddress) throws AcmTaskException, AcmCreateObjectFailedException
    {
        List<Long> tasksIdsFromOriginal = getTaskIdsFromSolr(fromObjectType, fromObjectId, auth);
        if (tasksIdsFromOriginal == null)
            return;
        for (Long taskIdFromOriginal : tasksIdsFromOriginal)
        {
            AcmTask task = new AcmTask();
            AcmTask taskFromOriginal = taskDao.findById(taskIdFromOriginal);
            try
            {
                BeanUtils.copyProperties(task, taskFromOriginal);
            } catch (InvocationTargetException | IllegalAccessException e)
            {
                log.error("couldn't copy bean.", e);
                continue;
            }

            task.setTaskId(null);
            task.setAttachedToObjectId(toObjectId);
            task.setAttachedToObjectName(toObjectName);
            task.setAttachedToObjectType(toObjectType);
            task.setParentObjectId(toObjectId);
            task.setParentObjectType(toObjectType);
            task.setOwner(auth.getName());
            task.setContainer(null);


            //create the task
            task = taskDao.createAdHocTask(task);

            //create container and folder for the task
            taskDao.createFolderForTaskEvent(task);

            //save again to get container and folder ids, must be after creating folderForTaskEvent in order to create cmisFolderId
            taskDao.save(task);

            //copy folder structure of the original task to the copy task
            try
            {
                AcmContainer originalTaskContainer = acmContainerDao.findFolderByObjectTypeAndId(taskFromOriginal.getObjectType(), taskFromOriginal.getId());
                if (originalTaskContainer != null && originalTaskContainer.getFolder() != null)
                    acmFolderService.copyFolderStructure(originalTaskContainer.getFolder().getId(), task.getContainer(), task.getContainer().getFolder());
            } catch (Exception e)
            {
                log.error("Error copying attachments for task id = {} into task id = {}", taskFromOriginal.getId(), task.getId());
            }

            copyNotes(taskFromOriginal, task);

            AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "create", auth.getName(), true, ipAddress);

            taskEventPublisher.publishTaskEvent(event);
        }
    }

    private void copyNotes(AcmTask taskFrom, AcmTask taskTo)
    {
        try
        {
            //copy notes
            List<Note> notesFromOriginal = noteDao.listNotes("GENERAL", taskFrom.getId(), taskFrom.getObjectType());
            for (Note note : notesFromOriginal)
            {
                Note newNote = new Note();
                newNote.setNote(note.getNote());
                newNote.setType(note.getType());
                newNote.setParentId(taskTo.getId());
                newNote.setParentType(taskTo.getObjectType());
                noteDao.save(newNote);
            }
        } catch (Exception e)
        {
            log.error("Error copying notes!", e);
        }
    }

    private List<Long> getTaskIdsFromSolr(String parentObjectType, Long parentObjectId, Authentication authentication)
    {
        List<Long> tasksIds = new LinkedList<>();


        log.debug("Taking task objects from Solr for parentObjectType = {} and parentObjectId={}", parentObjectType, parentObjectId);

        String query = "object_type_s:TASK AND parent_object_type_s :" + parentObjectType + " AND parent_object_id_i:" + parentObjectId;

        try
        {
            String retval = executeSolrQuery.getResultsByPredefinedQuery(authentication,
                    SolrCore.QUICK_SEARCH,
                    query, 0, 1000, "");

            if (retval != null && searchResults.getNumFound(retval) > 0)
            {
                JSONArray results = searchResults.getDocuments(retval);
                for (int index = 0; index < results.length(); index++)
                {
                    JSONObject result = results.getJSONObject(index);
                    if (result.has("object_id_s"))
                    {
                        tasksIds.add(result.getLong("object_id_s"));
                    }
                }
            }

            log.debug("Acm Task ids was retrieved. count({}).", tasksIds.size());
        } catch (MuleException e)
        {
            log.error("Cannot retrieve objects from Solr.", e);
        }

        return tasksIds;
    }

    @Override
    public void copyTaskFilesAndFoldersToParent(AcmTask task)
    {
        try
        {
            if (null != task.getParentObjectType() && null != task.getParentObjectId())
            {
                log.debug("Task event raised. Start coping folder to the parent folder ...");


                AcmContainer container = task.getContainer() != null ? task.getContainer() : getAcmContainerDao().findFolderByObjectTypeAndId(task.getObjectType(), task.getId());

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(container.getCreator(), container.getCreator());

                AcmCmisObjectList files = getEcmFileService().allFilesForContainer(auth, container);

                if (files != null && files.getChildren() != null && files.getTotalChildren() > 0)
                {
                    AcmFolder folderToBeCoppied = container.getFolder();

                    AcmContainer targetContainer = getAcmContainerDao().findFolderByObjectTypeAndId(task.getParentObjectType(), task.getParentObjectId());

                    AcmFolder targetFolder = getAcmFolderService().addNewFolderByPath(task.getParentObjectType(),
                            task.getParentObjectId(),
                            "/" + String.format("Task %d%n %s", task.getId(), task.getTitle()));

                    getAcmFolderService().copyFolderStructure(folderToBeCoppied.getId(), targetContainer, targetFolder);
                }
            }

        } catch (AcmFolderException | AcmListObjectsFailedException | AcmCreateObjectFailedException | AcmUserActionFailedException | AcmObjectNotFoundException e)
        {
            log.error("Could not copy folder for task id = {}", task.getId(), e);
        }
    }

    @Override
    public ObjectAssociation saveReferenceToTask(Reference reference, Authentication authentication)
            throws AcmCreateObjectFailedException
    {
        ObjectAssociation objectAssociation = new ObjectAssociation();
        objectAssociation.setTargetId(reference.getReferenceId());
        objectAssociation.setTargetType(reference.getReferenceType());
        objectAssociation.setTargetName(reference.getReferenceNumber());
        objectAssociation.setTargetTitle(reference.getReferenceTitle());
        objectAssociation.setStatus(reference.getReferenceStatus());
        objectAssociation.setAssociationType(ObjectAssociationConstants.OBJECT_TYPE);

        Long parentId = reference.getParentId();
        log.info("Saving reference to Task with id=[{}]", reference.getParentId());
        try
        {
            AcmTask parentObject = taskDao.findById(parentId);
            String referenceParentType = parentObject.getBusinessProcessId() == null ? TaskConstants.OBJECT_TYPE :
                    TaskConstants.SYSTEM_OBJECT_TYPE;
            Long referenceParentId = parentObject.getBusinessProcessId() == null ? parentObject.getTaskId() :
                    parentObject.getBusinessProcessId();

            objectAssociation.setParentType(referenceParentType);
            objectAssociation.setParentId(referenceParentId);
            objectAssociation = objectAssociationService.saveObjectAssociation(objectAssociation);
            objectAssociationEventPublisher.publishAddReferenceEvent(reference, authentication, true);
            return objectAssociation;
        } catch (AcmTaskException e)
        {
            log.error("Task with id=[{}] not found!", parentId, e);
            objectAssociationEventPublisher.publishAddReferenceEvent(reference, authentication, false);
            throw new AcmCreateObjectFailedException(ObjectAssociationConstants.OBJECT_TYPE,
                    String.format("Reference for task with id:[%d] was not added", parentId), e);
        }
    }

    @Override
    public List<ObjectAssociation> findChildObjects(Long taskId)
    {
        try
        {
            AcmTask parentObject = taskDao.findById(taskId);
            String parentType = parentObject.getBusinessProcessId() == null ? TaskConstants.OBJECT_TYPE :
                    TaskConstants.SYSTEM_OBJECT_TYPE;
            Long parentId = parentObject.getBusinessProcessId() == null ? parentObject.getTaskId() :
                    parentObject.getBusinessProcessId();
            return objectAssociationService.findByParentTypeAndId(parentType, parentId);
        } catch (AcmTaskException e)
        {
            log.error("Task with id=[{}] not found!", taskId, e);
        }

        return new ArrayList<>();
    }

    @Override
    public AcmTask retrieveTask(Long id)
    {
        try
        {
            AcmTask retval = taskDao.findById(id);
            if (retval.getReviewDocumentPdfRenditionId() != null)
            {
                EcmFile docUnderReview = fileDao.find(retval.getReviewDocumentPdfRenditionId());
                retval.setDocumentUnderReview(docUnderReview);
            }
            List<ObjectAssociation> childObjects = findChildObjects(id);
            retval.setChildObjects(childObjects);
            return retval;
        } catch (AcmTaskException | ActivitiException e)
        {
            log.error("Task with id=[{}] not found!", id, e);
        }
        return null;
    }


    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public AcmContainerDao getAcmContainerDao()
    {
        return acmContainerDao;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        this.acmContainerDao = acmContainerDao;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public void setNoteDao(NoteDao noteDao)
    {
        this.noteDao = noteDao;
    }


    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setObjectAssociationService(ObjectAssociationService objectAssociationService)
    {
        this.objectAssociationService = objectAssociationService;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public void setObjectAssociationEventPublisher(ObjectAssociationEventPublisher objectAssociationEventPublisher)
    {
        this.objectAssociationEventPublisher = objectAssociationEventPublisher;
    }
}

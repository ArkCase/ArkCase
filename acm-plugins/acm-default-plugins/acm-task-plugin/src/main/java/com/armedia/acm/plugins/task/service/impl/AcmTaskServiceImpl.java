package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nebojsha on 22.06.2015.
 */
public class AcmTaskServiceImpl implements AcmTaskService {
    private Logger log = LoggerFactory.getLogger(getClass());
    private TaskDao taskDao;
    private NoteDao noteDao;
    private TaskEventPublisher taskEventPublisher;
    private ExecuteSolrQuery executeSolrQuery;
    private AcmContainerDao acmContainerDao;
    private SearchResults searchResults = new SearchResults();
    private AcmFolderService acmFolderService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void copyTasks(Long fromObjectId,
                          String fromObjectType,
                          Long toObjectId,
                          String toObjectType,
                          String toObjectName,
                          Authentication auth,
                          String ipAddress) throws AcmTaskException, AcmCreateObjectFailedException {
        List<Long> tasksIdsFromOriginal = getTaskIdsFromSolr(fromObjectType, fromObjectId, auth);
        if (tasksIdsFromOriginal == null)
            return;
        for (Long taskIdFromOriginal : tasksIdsFromOriginal) {
            AcmTask task = new AcmTask();
            AcmTask taskFromOriginal = taskDao.findById(taskIdFromOriginal);
            try {
                BeanUtils.copyProperties(task, taskFromOriginal);
            } catch (InvocationTargetException | IllegalAccessException e) {
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

            //copy folder structurue of the original task to the copy task
            try {
                AcmContainer originalTaskContainer = acmContainerDao.findFolderByObjectTypeAndId(taskFromOriginal.getObjectType(), taskFromOriginal.getId());
                if (originalTaskContainer != null && originalTaskContainer.getFolder() != null)
                    acmFolderService.copyFolderStructure(originalTaskContainer.getFolder().getId(), task.getContainer(), task.getContainer().getFolder());
            } catch (Exception e) {
                log.error("Error copying attachments for task id = " + taskFromOriginal.getId() + " into task id = " + task.getId());
            }

            copyNotes(taskFromOriginal, task);

            AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "create", auth.getName(), true, ipAddress);

            taskEventPublisher.publishTaskEvent(event);
        }
    }

    private void copyNotes(AcmTask taskFrom, AcmTask taskTo) {
        try {
            //copy notes
            List<Note> notesFromOriginal = noteDao.listNotes("GENERAL", taskFrom.getId(), taskFrom.getObjectType());
            for (Note note : notesFromOriginal) {
                Note newNote = new Note();
                newNote.setNote(note.getNote());
                newNote.setType(note.getType());
                newNote.setParentId(taskTo.getId());
                newNote.setParentType(taskTo.getObjectType());
                noteDao.save(newNote);
            }
        } catch (Exception e) {
            log.error("Error copying notes!", e);
        }
    }

    private List<Long> getTaskIdsFromSolr(String parentObjectType, Long parentObjectId, Authentication authentication) {
        List<Long> tasksIds = new LinkedList<>();


        log.debug("Taking task objects from Solr for parentObjectType = {} and parentObjectId={}", parentObjectType, parentObjectId);

        String query = "object_type_s:TASK AND parent_object_type_s :" + parentObjectType + " AND parent_object_id_i:" + parentObjectId;

        try {
            String retval = executeSolrQuery.getResultsByPredefinedQuery(authentication,
                    SolrCore.QUICK_SEARCH,
                    query, 0, 1000, "");

            if (retval != null && searchResults.getNumFound(retval) > 0) {
                JSONArray results = searchResults.getDocuments(retval);
                for (int index = 0; index < results.length(); index++) {
                    JSONObject result = results.getJSONObject(index);
                    if (result.has("object_id_s")) {
                        tasksIds.add(result.getLong("object_id_s"));
                    }
                }
            }

            log.debug("Acm Task ids was retrieved. count({}).", tasksIds.size());
        } catch (MuleException e) {
            log.error("Cannot retrieve objects from Solr.", e);
        }

        return tasksIds;
    }

    @Override
    public void copyTaskFilesAndFoldersToParent(AcmTask task) {
    	try {

			if(null != task.getParentObjectType() && null != task.getParentObjectId()){
				log.debug("Task event raised. Start coppieng folder to the parent folder ...");

				
				AcmContainer container = task.getContainer() != null ? task.getContainer() : getAcmContainerDao().findFolderByObjectTypeAndId(task.getObjectType(), task.getId());
				
				AcmFolder folderToBeCoppied = container.getFolder();
				
				AcmContainer targetContainer = getAcmContainerDao().findFolderByObjectTypeAndId(task.getParentObjectType(), task.getParentObjectId());

				AcmFolder targetFolder = getAcmFolderService().addNewFolderByPath(task.getParentObjectType(), 
						task.getParentObjectId(), 
						"/" + String.format("Task %d%n %s", task.getId(), task.getTitle()));
				
				getAcmFolderService().copyFolderStructure(folderToBeCoppied.getId(), targetContainer, targetFolder);
				

			
			}
			
		} catch (AcmFolderException | AcmCreateObjectFailedException | AcmUserActionFailedException | AcmObjectNotFoundException e) {
			
			log.error("Could not coppy folder for task id = " + task.getId(), e);
			
		} 
    	
    }
    
    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher) {
        this.taskEventPublisher = taskEventPublisher;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery) {
        this.executeSolrQuery = executeSolrQuery;
    }

    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao) {
        this.acmContainerDao = acmContainerDao;
    }

    public AcmContainerDao getAcmContainerDao() {
		return acmContainerDao;
	}

	public void setAcmFolderService(AcmFolderService acmFolderService) {
        this.acmFolderService = acmFolderService;
    }

    public AcmFolderService getAcmFolderService() {
		return acmFolderService;
	}

	public void setNoteDao(NoteDao noteDao) {
        this.noteDao = noteDao;
    }


    
}

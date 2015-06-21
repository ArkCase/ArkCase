package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.SplitCaseFileException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SplitCaseOptions;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nebojsha on 01.06.2015.
 */
public class SplitCaseServiceImpl implements SplitCaseService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private Set<String> typesToCopy = new HashSet<>();
    private TaskEventPublisher taskEventPublisher;
    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults = new SearchResults();
    private TaskDao taskDao;
    private AcmContainerDao containerDao;

    @Override
    @Transactional
    public CaseFile splitCase(Authentication auth,
                              String ipAddress,
                              SplitCaseOptions splitCaseOptions) throws MuleException, SplitCaseFileException, AcmUserActionFailedException, AcmCreateObjectFailedException, AcmFolderException, AcmObjectNotFoundException {
        CaseFile original = caseFileDao.find(splitCaseOptions.getCaseFileId());
        if (original == null)
            throw new SplitCaseFileException("Case file with id = (" + splitCaseOptions.getCaseFileId() + ") not found");


        CaseFile copyCaseFile = new CaseFile();
        copyCaseFile.setCaseType(original.getCaseType());
        copyCaseFile.setCourtroomName(original.getCourtroomName());
        copyCaseFile.setNextCourtDate(original.getNextCourtDate());
        copyCaseFile.setResponsibleOrganization(original.getResponsibleOrganization());

        copyCaseFile.setDetails(original.getDetails());
        copyCaseFile.setStatus(original.getStatus());

        //add assignee to new case
        AcmParticipant participant = new AcmParticipant();
        participant.setParticipantLdapId(auth.getName());
        participant.setParticipantType(ParticipantTypes.ASSIGNEE);

        if (copyCaseFile.getParticipants() != null)
            copyCaseFile.setParticipants(new ArrayList<>());
        copyCaseFile.getParticipants().add(participant);

        if (typesToCopy.contains("participants"))
            copyParticipants(original, copyCaseFile, auth);


        ObjectAssociation childObjectCopy = new ObjectAssociation();
        childObjectCopy.setAssociationType("REFERENCE");
        childObjectCopy.setCategory("COPY_FROM");
        childObjectCopy.setTargetId(original.getId());
        childObjectCopy.setTargetType(original.getObjectType());
        childObjectCopy.setTargetName(original.getCaseNumber());
        copyCaseFile.addChildObject(childObjectCopy);


        if (typesToCopy.contains("people"))
            copyPeople(original, copyCaseFile);

        copyCaseFile = saveCaseService.saveCase(copyCaseFile, auth, ipAddress);

        ObjectAssociation childObjectOriginal = new ObjectAssociation();
        childObjectOriginal.setAssociationType("REFERENCE");
        childObjectOriginal.setCategory("COPY_TO");
        childObjectOriginal.setTargetId(copyCaseFile.getId());
        childObjectOriginal.setTargetType(copyCaseFile.getObjectType());
        childObjectOriginal.setTargetName(copyCaseFile.getCaseNumber());
        original.addChildObject(childObjectOriginal);
        saveCaseService.saveCase(original, auth, ipAddress);

        if (typesToCopy.contains("tasks")) {
            try {
                copyTasks(original, copyCaseFile, auth, ipAddress);
            } catch (AcmTaskException e) {
                e.printStackTrace();
            }
        }
        copyDocumentsAndFolders(copyCaseFile, splitCaseOptions);
        return copyCaseFile;
    }

    private void copyParticipants(CaseFile original, CaseFile copyCaseFile, Authentication auth) {
        //all participants are copied and assigned as followers, except current user is exist is not copied
        if (original.getParticipants() == null || original.getParticipants().isEmpty())
            return;
        if (copyCaseFile.getParticipants() == null)
            copyCaseFile.setParticipants(new ArrayList<>());
        for (AcmParticipant participant : original.getParticipants()) {
            if (participant.getParticipantLdapId().equals(auth.getName()))
                continue;
            if (ParticipantTypes.ASSIGNEE.equals(participant.getParticipantType())
                    || ParticipantTypes.FOLLOWER.equals(participant.getParticipantType())) {
                AcmParticipant copyParticipant = new AcmParticipant();
                copyParticipant.setParticipantType(ParticipantTypes.FOLLOWER);
                copyParticipant.setParticipantLdapId(participant.getParticipantLdapId());
                copyCaseFile.getParticipants().add(copyParticipant);
            }
        }
    }

    private void copyPeople(CaseFile original, CaseFile copyCaseFile) {
        if (original.getPersonAssociations() == null || original.getPersonAssociations().isEmpty())
            return;
        if (copyCaseFile.getPersonAssociations() == null)
            copyCaseFile.setPersonAssociations(new ArrayList<>());
        for (PersonAssociation person : original.getPersonAssociations()) {
            PersonAssociation copyPerson = new PersonAssociation();
            copyPerson.setPersonType(person.getPersonType());
            copyPerson.setPerson(person.getPerson());
            copyPerson.setNotes(person.getNotes());
            copyPerson.setPersonDescription(person.getPersonDescription());
            copyPerson.setTags(person.getTags());
            copyCaseFile.getPersonAssociations().add(copyPerson);
        }
    }

    private void copyDocumentsAndFolders(CaseFile saved, SplitCaseOptions options) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmFolderException, AcmCreateObjectFailedException {
        Map<Long, AcmFolder> folderMap = new HashMap<>();
        for (SplitCaseOptions.AttachmentDTO attachmentDTO : options.getAttachments()) {
            AcmContainer containerOfCopy = saved.getContainer();
            AcmFolder containerFolderOfCopy = containerOfCopy.getFolder();
            switch (attachmentDTO.getType()) {
                case "folder":
                    copyFolder(options.isPreserveFolderStructure(), folderMap, attachmentDTO.getId(), containerOfCopy, containerFolderOfCopy);
                    break;
                case "document":
                    copyDocument(options.isPreserveFolderStructure(), folderMap, attachmentDTO.getId(), containerOfCopy, containerFolderOfCopy);
                    break;
                default:
                    log.warn("Invalid type({}) for for splitting attachments", attachmentDTO.getType());
                    break;
            }
        }
    }

    private void copyFolder(boolean preserveFolderStructure,
                            Map<Long, AcmFolder> folderMap,
                            Long folderId,
                            AcmContainer containerOfCopy,
                            AcmFolder rootFolderOfCopy) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmFolderException {

        if (preserveFolderStructure) {
            AcmFolder folderForCopying = acmFolderService.findById(folderId);
            String folderPath = acmFolderService.getFolderPath(folderForCopying);

            AcmFolder alreadyHandledFolder = folderMap.get(folderId);
            //check if folder path exists in the copy
            if (alreadyHandledFolder != null || folderForCopying.getParentFolderId() == null || acmFolderService.folderPathExists(folderPath, containerOfCopy)) {
                //copy the folder children (folders documents) into existing folder of the copy case file
                //this should create or return existing path, but path already exists so it should return existing folder

                List<AcmObject> folderChildren = acmFolderService.getFolderChildren(folderForCopying.getId());
                for (AcmObject obj : folderChildren) {
                    if (obj.getObjectType() == null)
                        continue;
                    if ("FILE".equals(obj.getObjectType().toUpperCase())) {
                        copyDocument(preserveFolderStructure, folderMap, obj.getId(), containerOfCopy, rootFolderOfCopy);
                    } else if ("FOLDER".equals(obj.getObjectType().toUpperCase())) {
                        copyFolder(preserveFolderStructure, folderMap, obj.getId(), containerOfCopy, rootFolderOfCopy);
                    }
                }
            } else {
                if (folderForCopying.getParentFolderId() == null)
                    return;
                //just copy the folder to new parent
                AcmFolder parentInSource = acmFolderService.findById(folderForCopying.getParentFolderId());
                String folderPathParentInSource = acmFolderService.getFolderPath(parentInSource);

                //this folder will be created if doesn't exits
                AcmFolder createdParentFolderInCopy = acmFolderService.addNewFolderByPath(
                        containerOfCopy.getContainerObjectType(),
                        containerOfCopy.getContainerObjectId(),
                        folderPathParentInSource);
                AcmFolder copiedFolder = acmFolderService.copyFolder(folderForCopying,
                        createdParentFolderInCopy,
                        containerOfCopy.getContainerObjectId(),
                        containerOfCopy.getContainerObjectType());
                folderMap.put(folderForCopying.getId(), copiedFolder);
            }
        }

    }

    private void copyDocument(boolean preserveFolderStructure,
                              Map<Long, AcmFolder> folderMap,
                              Long documentId,
                              AcmContainer containerOfCopy,
                              AcmFolder rootFolderOfCopy) throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException {
        EcmFile fileForCopying = ecmFileService.findById(documentId);

        if (folderMap.containsKey(fileForCopying.getFolder().getId())) {
            //we have already created that folder
            AcmFolder foundFolder = folderMap.get(fileForCopying.getFolder().getId());
            ecmFileService.copyFile(documentId, foundFolder, containerOfCopy);

            // file is copied, we are done
            return;
        }

        AcmFolder documentFolder = fileForCopying.getFolder();
        if (documentFolder.getParentFolderId() == null || !preserveFolderStructure) {
            //recreate folder structure as on source
            //document is under root folder, no need to create additional folders
            ecmFileService.copyFile(documentId, rootFolderOfCopy, containerOfCopy);
        } else {
            //create folder structure in saved case file same as in source for the document
            String folderPath = acmFolderService.getFolderPath(fileForCopying.getFolder());
            log.debug("folder path = '{}' for folder(id={}, name={})", folderPath, fileForCopying.getFolder().getId(), fileForCopying.getFolder().getName());
            try {
                AcmFolder createdFolder = acmFolderService.addNewFolderByPath(
                        containerOfCopy.getContainerObjectType(),
                        containerOfCopy.getContainerObjectId(),
                        folderPath);
                ecmFileService.copyFile(documentId, createdFolder, containerOfCopy);
                folderMap.put(fileForCopying.getFolder().getId(), createdFolder);
            } catch (Exception e) {
                log.error("Couldn't create folder structure for document with id=" + documentId + " and will not be copied.", e);
            }
        }
    }


    private void copyTasks(CaseFile original, CaseFile copyCaseFile, Authentication auth, String ipAddress) throws AcmTaskException, AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException {
        List<Long> tasksIdsFromOriginal = getTaskIdsFromSolr(original.getObjectType(), original.getId(), auth);
        if (tasksIdsFromOriginal == null)
            return;
        for (Long taskIdFromOriginal : tasksIdsFromOriginal) {
            AcmTask task = new AcmTask();
            AcmTask taskFromOriginal = taskDao.findById(taskIdFromOriginal);
            try {
                BeanUtils.copyProperties(task, taskFromOriginal);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            task.setTaskId(null);
            task.setAttachedToObjectId(copyCaseFile.getId());
            task.setAttachedToObjectName(copyCaseFile.getTitle());
            task.setAttachedToObjectType(copyCaseFile.getObjectType());
            task.setParentObjectId(copyCaseFile.getId());
            task.setParentObjectType(copyCaseFile.getObjectType());
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
                AcmContainer originalTaskContainer = containerDao.findFolderByObjectTypeAndId(taskFromOriginal.getObjectType(), taskFromOriginal.getId());
                if (originalTaskContainer != null && originalTaskContainer.getFolder() != null)
                    copyFolder(true, new HashMap<>(), originalTaskContainer.getFolder().getId(), task.getContainer(), task.getContainer().getFolder());
            } catch (Exception e) {
                log.error("Error copying attachments for task id = " + taskFromOriginal.getId() + " into task id = " + task.getId());
            }

            AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "create", auth.getName(), true, ipAddress);

            taskEventPublisher.publishTaskEvent(event);
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

    public void setSaveCaseService(SaveCaseService saveCaseService) {
        this.saveCaseService = saveCaseService;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService) {
        this.acmFolderService = acmFolderService;
    }

    public void setTypesToCopy(String typesToCopyStr) {
        if (!StringUtils.isEmpty(typesToCopyStr))
            this.typesToCopy.addAll(Arrays.asList(typesToCopyStr.trim().replaceAll(",[\\s]*", ",").split(",")));

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

    public void setContainerDao(AcmContainerDao containerDao) {
        this.containerDao = containerDao;
    }
}

package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.spring.SpringContextHolder;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.EntityType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileFolderParticipantsUpdateExecutor implements AcmDataUpdateExecutor
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private SpringContextHolder contextHolder;
    private EcmFileParticipantService fileParticipantService;
    private AcmFolderDao folderDao;
    private EcmFileDao fileDao;

    @Override
    public String getUpdateId()
    {
        return "file-folder-participants-update";
    }

    @Override
    public void execute()
    {
        try
        {
            // since this code is run via a executor, there is no authenticated user, so we need to specify the user to
            // be used for CMIS connections. Some changes can trigger Mule flows.
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, "localhost");

            Set<Class<?>> assignedAndContainerClasses = getAssignedAndContainerClasses();
            Collection<AcmAbstractDao<?>> daoInstances = contextHolder.getAllBeansOfType(AcmAbstractDao.class).values().stream()
                    .map(dao -> (AcmAbstractDao<?>) dao).collect(Collectors.toSet());

            for (Class<?> assignedAndContainerClass : assignedAndContainerClasses)
            {
                log.info("Updating file and folder participants for entities from class: {}", assignedAndContainerClass.getName());
                AcmAbstractDao<?> daoInstance = getDaoInstanceForClass(assignedAndContainerClass, daoInstances);
                List<?> assignedAndContainerObjects = daoInstance.findAll();
                for (Object assignedAndContainerObject : assignedAndContainerObjects)
                {
                    List<AcmParticipant> assignedObjectParticipants = ((AcmAssignedObject) assignedAndContainerObject).getParticipants();
                    AcmContainer container = ((AcmContainerEntity) assignedAndContainerObject).getContainer();

                    log.info("Updating participants to entity: {} [{}]", ((AcmAssignedObject) assignedAndContainerObject).getObjectType(),
                            ((AcmAssignedObject) assignedAndContainerObject).getId());

                    setParticipantsAndRestrictedFlag(assignedObjectParticipants, container,
                            ((AcmAssignedObject) assignedAndContainerObject).getRestricted());
                }
            }

            // if there are files or folders without participants (should not happen, but extensions might have files
            // and folders not connected to a AcmContainerEntity), we'll let the Drools rules add some default
            // participants
            List<AcmFolder> folders = getFolderDao().getFoldersWithoutParticipants();
            folders.forEach(folder -> {
                folder.setModified(new Date());
                getFolderDao().save(folder);
            });

            List<EcmFile> files = getFileDao().getFilesWithoutParticipants();
            files.forEach(file -> {
                file.setModified(new Date());
                getFileDao().save(file);
            });
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // Setting participants to individual files and folders will not break older code if we want to revert the code.
    // We execute smaller transactions, so if the update fails, we can continue after fixing the issue.
    @Transactional
    private void setParticipantsAndRestrictedFlag(List<AcmParticipant> assignedObjectParticipants, AcmContainer container,
            boolean restricted)
    {
        if (assignedObjectParticipants != null && container != null)
        {
            assignedObjectParticipants.forEach(participant -> participant.setReplaceChildrenParticipant(true));
            getFileParticipantService().inheritParticipantsFromAssignedObject(assignedObjectParticipants, new ArrayList<>(),
                    container);
        }

        if (restricted)
        {
            getFileParticipantService().setRestrictedFlagRecursively(true, container);
        }
    }

    private AcmAbstractDao<?> getDaoInstanceForClass(Class<?> assignedAndContainerClass,
            Collection<AcmAbstractDao<?>> daoInstances) throws Exception
    {
        for (AcmAbstractDao<?> daoInstance : daoInstances)
        {

            Type type = daoInstance.getClass().getGenericSuperclass();

            while (!(type instanceof ParameterizedType) || ((ParameterizedType) type).getRawType() != AcmAbstractDao.class)
            {
                if (type instanceof ParameterizedType)
                {
                    type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
                }
                else
                {
                    type = ((Class<?>) type).getGenericSuperclass();
                }
            }

            type = ((ParameterizedType) type).getActualTypeArguments()[0];

            if (type.getTypeName().equals(assignedAndContainerClass.getName()))
            {
                return daoInstance;
            }
        }

        throw new RuntimeException("Cannot find DAO class for AcmObject of type: " + assignedAndContainerClass.getName());
    }

    public Set<Class<?>> getAssignedAndContainerClasses()
    {
        Set<EntityType<?>> entityTypes = getFileDao().getEm().getMetamodel().getEntities();
        return entityTypes.stream()
                .filter(entityType -> AcmAssignedObject.class.isAssignableFrom(entityType.getJavaType())
                        && AcmContainerEntity.class.isAssignableFrom(entityType.getJavaType()))
                .map(entityType -> entityType.getJavaType()).collect(Collectors.toSet());
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }
}

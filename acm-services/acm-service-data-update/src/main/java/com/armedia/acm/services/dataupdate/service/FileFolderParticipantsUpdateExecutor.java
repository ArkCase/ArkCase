package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.spring.SpringContextHolder;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.persistence.metamodel.EntityType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
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
    private ArkPermissionEvaluator arkPermissionEvaluator;

    @Override
    public String getUpdateId()
    {
        return "file-folder-participants-update";
    }

    @Override
    public void execute()
    {
        // do not update file and folder participants if the document ACL feature is disabled
        if (!getArkPermissionEvaluator().isEnableDocumentACL())
        {
            return;
        }

        try
        {
            // since this code is run via a executor, there is no authenticated user, so we need to specify the user to
            // be used for CMIS connections. Some changes can trigger Mule flows.
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, "DATA_UPDATE");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, "localhost");

            Set<Class<?>> assignedAndContainerClasses = getAssignedAndContainerClasses();
            Collection<AcmAbstractDao<?>> daoInstances = contextHolder.getAllBeansOfType(AcmAbstractDao.class).values().stream()
                    .map(dao -> (AcmAbstractDao<?>) dao).collect(Collectors.toSet());

            for (Class<?> assignedAndContainerClass : assignedAndContainerClasses)
            {
                log.info("Updating file and folder participants for entities from class: {}", assignedAndContainerClass.getName());
                AcmAbstractDao<?> daoInstance = null;
                Class targetClass = assignedAndContainerClass;
                while (daoInstance == null && targetClass != null)
                {
                    daoInstance = getDaoInstanceForClass(targetClass, daoInstances);
                    if (daoInstance == null)
                    {
                        targetClass = targetClass.getSuperclass();
                    }
                }
                if (daoInstance != null)
                {
                    log.debug("Found DAO class {} for entity class {}", daoInstance.getClass().getName(),
                            assignedAndContainerClass.getName());
                }
                else
                {
                    throw new RuntimeException("Cannot find DAO class for AcmObject of type: " + assignedAndContainerClass.getName());
                }
                List<?> assignedAndContainerObjects = daoInstance.findAll();
                for (Object assignedAndContainerObject : assignedAndContainerObjects)
                {
                    AcmContainer container = ((AcmContainerEntity) assignedAndContainerObject).getContainer();

                    // closed assigned object might have been merged. The participants for such object must be taken
                    // from the object with the root folder
                    if (container == null || container.getFolder() == null || container.getFolder().getParentFolder() != null)
                    {
                        continue;
                    }

                    List<AcmParticipant> assignedObjectParticipants = ((AcmAssignedObject) assignedAndContainerObject).getParticipants();

                    // we don't want to change the entity
                    daoInstance.getEm().detach(assignedAndContainerObject);

                    log.info("Updating participants to entity: {}[{}]", ((AcmAssignedObject) assignedAndContainerObject).getObjectType(),
                            ((AcmAssignedObject) assignedAndContainerObject).getId());

                    setParticipantsAndRestrictedFlag(assignedObjectParticipants, container,
                            ((AcmAssignedObject) assignedAndContainerObject).getRestricted());
                }
            }

            log.info("Finished updating ROOT folders participants!");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // We execute smaller transactions, so if the update fails, we can continue after fixing the issue.
    private void setParticipantsAndRestrictedFlag(List<AcmParticipant> assignedObjectParticipants, AcmContainer container,
            boolean restricted)
    {
        if (assignedObjectParticipants != null && container != null)
        {
            assignedObjectParticipants.forEach(participant -> participant.setReplaceChildrenParticipant(true));
            getFileParticipantService().inheritParticipantsFromAssignedObject(assignedObjectParticipants, new ArrayList<>(),
                    container, restricted);
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

        return null;
        // throw new RuntimeException("Cannot find DAO class for AcmObject of type: " +
        // assignedAndContainerClass.getName());
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

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }
}

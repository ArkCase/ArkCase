package com.armedia.acm.plugins.task.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.Reference;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;

import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Created by nebojsha on 22.06.2015.
 */
public interface AcmTaskService
{

    void copyTasks(Long fromObjectId,
                   String fromObjectType,
                   Long toObjectId,
                   String toObjectType,
                   String toObjectName,
                   Authentication auth,
                   String ipAddress) throws AcmTaskException, AcmCreateObjectFailedException;

    void copyTaskFilesAndFoldersToParent(AcmTask task);

    ObjectAssociation saveReferenceToTask(Reference reference, Authentication authentication)
            throws AcmCreateObjectFailedException;

    List<ObjectAssociation> findChildObjects(Long taskId);

    AcmTask retrieveTask(Long id);

    void createTasks(String taskAssignees, String taskName, String owningGroup, String parentType,
                     Long parentId);
}

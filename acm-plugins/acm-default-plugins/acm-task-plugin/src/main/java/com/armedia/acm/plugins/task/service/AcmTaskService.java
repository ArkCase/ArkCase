package com.armedia.acm.plugins.task.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;

import org.springframework.security.core.Authentication;

/**
 * Created by nebojsha on 22.06.2015.
 */
public interface AcmTaskService {

    public void copyTasks(Long fromObjectId,
                          String fromObjectType,
                          Long toObjectId,
                          String toObjectType,
                          String toObjectName,
                          Authentication auth,
                          String ipAddress) throws AcmTaskException, AcmCreateObjectFailedException;
    
    public void copyTaskFilesAndFoldersToParent(AcmTask task);
}

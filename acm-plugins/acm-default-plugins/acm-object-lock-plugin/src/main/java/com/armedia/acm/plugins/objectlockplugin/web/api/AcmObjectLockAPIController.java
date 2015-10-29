package com.armedia.acm.plugins.objectlockplugin.web.api;

import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * Created by nebojsha on 28.10.2015.
 */

@Controller
public class AcmObjectLockAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmObjectLockService objectLockService;

    @RequestMapping(value = {"/api/v1/plugin/{objectType}/{objectId}/lock", "/api/latest/plugin/{objectType}/{objectId}/lock"}, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmObjectLock lockObject(
            @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId,
            Authentication authentication
    ) throws MuleException, IOException
    {
        return objectLockService.createLock(objectId, objectType, authentication);
    }

    @RequestMapping(value = {"/api/v1/plugin/{objectType}/{objectId}/lock", "/api/latest/plugin/{objectType}/{objectId}/lock"}, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void unlockObject(
            @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId
    ) throws MuleException, IOException
    {
        //FIXME not sure if anyone can remove object lock or just owner, for now anyone can remove the lock
        objectLockService.removeLock(objectId, objectType);
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }
}

package com.armedia.acm.plugins.objectlockplugin.web.api;

import com.armedia.acm.service.objectlock.exception.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by nebojsha on 28.10.2015.
 */

@Controller
public class AcmObjectLockAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmObjectLockService objectLockService;

    /**
     * This method locks object identified with objectId and objectType.
     *
     * @param objectType
     *            object type
     * @param objectId
     *            object ID
     * @param authentication
     *            Authentication
     * @return AcmObjectLock lock details
     * @throws MuleException
     * @throws IOException
     */
    @PreAuthorize("hasPermission(#objectId, #objectType, 'lock')")
    @RequestMapping(value = { "/api/v1/plugin/{objectType}/{objectId}/lock",
            "/api/latest/plugin/{objectType}/{objectId}/lock" }, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmObjectLock lockObject(@PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") Long objectId,
            @RequestParam(value = "lockType", required = false, defaultValue = "OBJECT_LOCK") String lockType,
            @RequestParam(value = "lockInDB", required = false, defaultValue = "true") boolean lockInDB, Authentication authentication)
            throws MuleException, IOException

    {
        return objectLockService.createLock(objectId, objectType, lockType, lockInDB, authentication);
    }

    /**
     * This method unlocks already locked object identified with objectId and objectType.
     *
     * @param objectType
     *            object type
     * @param objectId
     *            object ID
     * @param auth
     *            Authentication
     * @return solr response
     * @throws MuleException
     * @throws IOException
     */
    @PreAuthorize("hasPermission(#objectId, #objectType, 'unlock')")
    @RequestMapping(value = { "/api/v1/plugin/{objectType}/{objectId}/lock",
            "/api/latest/plugin/{objectType}/{objectId}/lock" }, method = RequestMethod.DELETE)
    @ResponseBody
    public String unlockObject(@PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") Long objectId,
            @RequestParam(value = "lockType", required = false, defaultValue = "OBJECT_LOCK") String lockType, Authentication auth)
            throws MuleException, IOException
    {
        try
        {
            // FIXME not sure if anyone can remove object lock or just owner, for now anyone can remove the lock
            objectLockService.removeLock(objectId, objectType, lockType, auth);
        } catch (AcmObjectLockException e)
        {
            return e.getMessage();
        }
        return "Successfully removed lock";
    }

    /**
     * This method retrieves objects documents from solr, which are locked.
     *
     * @param objectType
     *            object type
     * @param firstRow
     *            start from row
     * @param maxRows
     *            max results
     * @param sort
     *            sort by solr document field
     * @param authentication
     *            injected by spring
     * @return solr response
     * @throws MuleException
     * @throws IOException
     */
    @RequestMapping(value = { "/api/v1/plugin/objects/{objectType}/locked",
            "/api/latest/plugin/objects/{objectType}/locked" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String listObjectsWithLock(@PathVariable(value = "objectType") String objectType,
            @RequestParam(value = "firstRow", defaultValue = "0", required = false) int firstRow,
            @RequestParam(value = "maxRows", defaultValue = "1000", required = false) int maxRows,
            @RequestParam(value = "sort", defaultValue = "", required = false) String sort, Authentication authentication)
            throws MuleException, IOException
    {
        return objectLockService.getDocumentsWithLock(objectType, authentication, null, firstRow, maxRows, sort, null);
    }

    /**
     * This method retrieves object locks documents which are indexed in solr.
     *
     * @param objectType
     *            object type
     * @param firstRow
     *            start from row
     * @param maxRows
     *            max results
     * @param sort
     *            sort by solr document field
     * @param authentication
     *            injected by spring
     * @return solr response
     * @throws MuleException
     * @throws IOException
     */
    @RequestMapping(value = { "/api/v1/plugin/locks/{objectType}",
            "/api/latest/plugin/locks/{objectType}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String listLocks(@PathVariable(value = "objectType") String objectType,
            @RequestParam(value = "firstRow", defaultValue = "0", required = false) int firstRow,
            @RequestParam(value = "maxRows", defaultValue = "1000", required = false) int maxRows,
            @RequestParam(value = "sort", defaultValue = "", required = false) String sort, Authentication authentication)
            throws MuleException, IOException
    {
        return objectLockService.getObjectLocks(objectType, authentication, null, firstRow, maxRows, sort, null);

    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }
}

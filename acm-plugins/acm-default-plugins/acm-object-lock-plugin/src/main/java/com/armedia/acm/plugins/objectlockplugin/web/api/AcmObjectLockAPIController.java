package com.armedia.acm.plugins.objectlockplugin.web.api;

/*-
 * #%L
 * ACM Plugins: Object lock plugin
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.search.exception.SolrException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

/**
 * Created by nebojsha on 28.10.2015.
 */

@Controller
public class AcmObjectLockAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private AcmObjectLockService objectLockService;
    private AcmObjectLockingManager objectLockingManager;

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
     * @throws IOException
     * @throws AcmObjectLockException
     */
    @PreAuthorize("hasPermission(#objectId, #objectType, 'lock')")
    @RequestMapping(value = { "/api/v1/plugin/{objectType}/{objectId}/lock",
            "/api/latest/plugin/{objectType}/{objectId}/lock" }, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmObjectLock lockObject(@PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") Long objectId,
            @RequestParam(value = "lockType", required = false, defaultValue = "OBJECT_LOCK") String lockType,
            @RequestParam(value = "lockInDB", required = false, defaultValue = "true") boolean lockInDB, Authentication authentication)
            throws IOException, AcmObjectLockException

    {
        return objectLockingManager.acquireObjectLock(objectId, objectType, lockType, null, true, authentication.getName());
    }

    /**
     * This method checks permissions for locks identified with objectId and objectType.
     *
     * @param objectType
     *            object type
     * @param objectId
     *            object ID
     * @param authentication
     *            Authentication
     * @return ResponseEntity permission status
     */
    @PreAuthorize("hasPermission(#objectId, #objectType, 'lock')")
    @RequestMapping(value = { "/api/v1/plugin/{objectType}/{objectId}/lockPermission",
            "/api/latest/plugin/{objectType}/{objectId}/lockPermission" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity hasPermissionToLockObject(@PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") Long objectId, Authentication authentication)
    {
        return new ResponseEntity(HttpStatus.OK);
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
     * @throws IOException
     * @throws AcmObjectLockException
     */
    @PreAuthorize("hasPermission(#objectId, #objectType, 'unlock')")
    @RequestMapping(value = { "/api/v1/plugin/{objectType}/{objectId}/lock",
            "/api/latest/plugin/{objectType}/{objectId}/lock" }, method = { RequestMethod.DELETE, RequestMethod.POST })
    @ResponseStatus(value = HttpStatus.OK)
    public void unlockObject(@PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") Long objectId,
            @RequestParam(value = "lockType", required = false, defaultValue = "OBJECT_LOCK") String lockType,
            @RequestParam(value = "lockId", required = false) Long lockId, Authentication auth)
            throws IOException, AcmObjectLockException
    {
        objectLockingManager.releaseObjectLock(objectId, objectType, lockType, true, auth.getName(), lockId);
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
     * @throws IOException
     */
    @RequestMapping(value = { "/api/v1/plugin/objects/{objectType}/locked",
            "/api/latest/plugin/objects/{objectType}/locked" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String listObjectsWithLock(@PathVariable(value = "objectType") String objectType,
            @RequestParam(value = "firstRow", defaultValue = "0", required = false) int firstRow,
            @RequestParam(value = "maxRows", defaultValue = "1000", required = false) int maxRows,
            @RequestParam(value = "sort", defaultValue = "", required = false) String sort, Authentication authentication)
            throws SolrException
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
     * @throws IOException
     */
    @RequestMapping(value = { "/api/v1/plugin/locks/{objectType}",
            "/api/latest/plugin/locks/{objectType}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String listLocks(@RequestParam(value = "parentObjectId", required = false, defaultValue = "") String objectId,
            @RequestParam(value = "creator", required = false, defaultValue = "") String creator,
            @PathVariable(value = "objectType") String objectType,
            @RequestParam(value = "firstRow", defaultValue = "0", required = false) int firstRow,
            @RequestParam(value = "maxRows", defaultValue = "1000", required = false) int maxRows,
            @RequestParam(value = "sort", defaultValue = "", required = false) String sort, Authentication authentication)
            throws SolrException
    {
        return objectLockService.getObjectLocks(objectType, authentication, objectId, creator, firstRow, maxRows, sort, null);

    }

    /**
     * This method will unlock as many case file locks as possible from selected.
     *
     * @param objectType
     *            object type
     * @param objectIds
     *            object ids
     * @param lockType
     *            lcok type
     * @param auth
     *            auth
     * @return list of successful or failed unlock requests
     */
    @PreAuthorize("hasPermission(#objectIds, #objectType, 'unlock')")
    @RequestMapping(value = { "/api/latest/plugin/locks/{objectType}/lock",
            "/api/v1/plugin/locks/{objectType}/lock" }, method = RequestMethod.DELETE)
    @ResponseBody
    public String releaseMultipleLocks(@PathVariable(value = "objectType") String objectType,
            @RequestParam(value = "parentObjectIds") List<Long> objectIds,
            @RequestParam(value = "lockType", required = false, defaultValue = "OBJECT_LOCK") String lockType, Authentication auth)
    {
        JSONArray resultList = new JSONArray();
        for (Long objectId : objectIds)
        {
            JSONObject result = new JSONObject();
            result.put("id", objectId);
            log.debug("Trying to remove the lock on object [{}] of type [{}]", objectId, StringUtils.normalizeSpace(objectType));
            try
            {
                objectLockingManager.releaseObjectLock(objectId, objectType, lockType, true, auth.getName(), null);
                log.debug("Successfully removed the lock on object [{}] of type [{}]", objectId, StringUtils.normalizeSpace(objectType));
                result.put("status", "Success");
            }
            catch (AcmObjectLockException e)
            {
                log.warn("Couldn't remove the lock on object [{}] of type [{}]", objectId, objectType, e);
                result.put("status", "Failed");
            }
            resultList.put(result);
        }
        return resultList.toString();
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }

    public AcmObjectLockService getObjectLockService()
    {
        return objectLockService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }
}

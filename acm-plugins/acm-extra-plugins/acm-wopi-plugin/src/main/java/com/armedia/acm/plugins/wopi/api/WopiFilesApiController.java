package com.armedia.acm.plugins.wopi.api;

/*-
 * #%L
 * ACM Service: Wopi service
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

import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.wopi.model.WopiFileInfo;
import com.armedia.acm.plugins.wopi.model.WopiLockInfo;
import com.armedia.acm.plugins.wopi.service.WopiAcmService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping(value = "/api/latest/plugin/wopi/files")
public class WopiFilesApiController
{
    private WopiAcmService wopiService;
    private ArkPermissionEvaluator permissionEvaluator;
    private static final Logger log = LogManager.getLogger(WopiFilesApiController.class);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WopiFileInfo> getFileInfo(@PathVariable Long id, Authentication authentication)
    {
        log.info("Getting file info for id [{}] per user [{}]", id, authentication.getName());
        boolean hasPermission = isActionAllowed(authentication, "read|group-read|write|group-write", id);
        if (!hasPermission)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return wopiService.getFileInfo(id)
                .map(it -> new ResponseEntity<>(it, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/{id}/contents", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getFileContents(@PathVariable Long id, Authentication authentication)
    {
        log.info("Getting contents for file with id [{}] per user [{}]", id, authentication.getName());

        boolean hasPermission = isActionAllowed(authentication, "read|group-read|write|group-write", id);
        if (!hasPermission)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try
        {
            InputStreamResource fileContents = wopiService.getFileContents(id);
            return new ResponseEntity<>(fileContents, HttpStatus.OK);
        }
        catch (AcmUserActionFailedException e)
        {
            log.error("Failed to retrieve contents for file with id [{}]", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}/contents", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity putFile(@PathVariable Long id,
            @RequestBody InputStreamResource resource,
            Authentication authentication)
    {
        log.info("Put file [{}] per user [{}]", id, authentication.getName());
        boolean hasPermission = isActionAllowed(authentication, "write|group-write", id);
        if (!hasPermission)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try
        {
            wopiService.putFile(id, resource, authentication);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.warn("File with id [{}] is not found", id, e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        catch (ArkCaseFileRepositoryException | IOException e)
        {
            log.error("Put file failed for file with id [{}]", id, e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}/lock", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<WopiLockInfo> lockFile(@PathVariable Long id, Authentication authentication)
    {
        log.info("Lock file [{}] per user [{}]", id, authentication.getName());
        boolean hasPermission = isActionAllowed(authentication, "write|group-write", id);

        if (!hasPermission)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(wopiService.lock(id, authentication), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/lock", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<WopiLockInfo> getLock(@PathVariable Long id, Authentication authentication)
    {
        log.info("Get lock for file [{}] per user [{}]", id, authentication.getName());
        boolean hasPermission = isActionAllowed(authentication, "write|group-write", id);
        if (!hasPermission)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(wopiService.getSharedLock(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/lock/{lockId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<WopiLockInfo> unlock(@PathVariable Long id, @PathVariable Long lockId, Authentication authentication)
    {
        log.info("Unlock file [{}] per user [{}]", id, authentication.getName());
        boolean hasPermission = isActionAllowed(authentication, "write|group-write", id);
        if (!hasPermission)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(wopiService.unlock(id, lockId, authentication), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/rename", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity renameFile(@PathVariable Long id, @RequestBody MultiValueMap<String, String> body,
            Authentication authentication)
    {
        log.info("Rename file [{}] per user [{}]", id, authentication.getName());
        boolean hasPermission = isActionAllowed(authentication, "write|group-write", id);
        if (!hasPermission)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try
        {
            wopiService.renameFile(id, body.getFirst("name"));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.warn("File with id [{}] is not found", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (AcmUserActionFailedException e)
        {
            log.error("Failed to rename file with id [{}]", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteFile(@PathVariable Long id, Authentication authentication)
    {
        log.info("Delete file [{}] per user [{}]", id, authentication.getName());
        boolean hasPermission = isActionAllowed(authentication, "write|group-write", id);
        if (!hasPermission)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try
        {
            wopiService.deleteFile(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.warn("File with id [{}] is not found", id, e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        catch (AcmUserActionFailedException e)
        {
            log.error("Failed to delete file with id [{}]", id, e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isActionAllowed(Authentication authentication, String action, Long targetId)
    {
        return permissionEvaluator.hasPermission(authentication, targetId, "FILE", action);
    }

    public void setWopiService(WopiAcmService wopiService)
    {
        this.wopiService = wopiService;
    }

    public void setPermissionEvaluator(ArkPermissionEvaluator permissionEvaluator)
    {
        this.permissionEvaluator = permissionEvaluator;
    }
}

package com.armedia.acm.services.wopi.api;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.wopi.model.WopiFileInfo;
import com.armedia.acm.services.wopi.service.WopiAcmService;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping(value = "/api/latest/wopi/files")
public class WopiFilesApiController
{
    private WopiAcmService wopiService;
    private static final Logger log = LoggerFactory.getLogger(WopiFilesApiController.class);

    @PreAuthorize("hasPermission(#id, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WopiFileInfo> getFileInfo(@PathVariable Long id, Authentication authentication)
    {
        log.info("Getting file info for id [{}] per user [{}]", id, authentication.getName());
        return wopiService.getFileInfo(id, authentication)
                .map(it -> new ResponseEntity<>(it, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasPermission(#id, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(value = "/{id}/contents", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getFileContents(@PathVariable Long id, Authentication authentication)
    {
        log.info("Getting contents for file with id [{}] per user [{}]", id, authentication.getName());
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
        catch (MuleException e)
        {
            log.warn("File with id [{}] is not found", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasPermission(#id, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/{id}/contents", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity putFile(@PathVariable Long id,
            @RequestBody InputStreamResource resource,
            Authentication authentication)
    {
        log.info("Put file [{}] per user [{}]", id, authentication.getName());
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
        catch (MuleException | IOException e)
        {
            log.error("Put file failed for file with id [{}]", id, e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasPermission(#id, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/{id}/lock", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Long> lockFile(@PathVariable Long id, Authentication authentication)
    {
        log.info("Lock file [{}] per user [{}]", id, authentication.getName());
        return new ResponseEntity<>(wopiService.lock(id, authentication), HttpStatus.OK);
    }

    @PreAuthorize("hasPermission(#id, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/{id}/lock", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Long> getLock(@PathVariable Long id, Authentication authentication)
    {
        log.info("Get lock for file [{}] per user [{}]", id, authentication.getName());
        return new ResponseEntity<>(wopiService.getSharedLock(id), HttpStatus.OK);
    }

    @PreAuthorize("hasPermission(#id, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/{id}/lock/{lockId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Long> unlock(@PathVariable Long id, @PathVariable Long lockId, Authentication authentication)
    {
        log.info("Unlock file [{}] per user [{}]", id, authentication.getName());
        return new ResponseEntity<>(wopiService.unlock(id, lockId, authentication), HttpStatus.OK);
    }

    @PreAuthorize("hasPermission(#id, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/{id}/rename", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity renameFile(@PathVariable Long id, @RequestParam String name,
            Authentication authentication)
    {
        log.info("Rename file [{}] per user [{}]", id, authentication.getName());
        try
        {
            wopiService.renameFile(id, name);
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

    @PreAuthorize("hasPermission(#id, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteFile(@PathVariable Long id, Authentication authentication)
    {
        log.info("Delete file [{}] per user [{}]", id, authentication.getName());
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

    public void setWopiService(WopiAcmService wopiService)
    {
        this.wopiService = wopiService;
    }
}

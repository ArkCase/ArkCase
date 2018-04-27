package com.armedia.acm.services.wopi.api;

import com.armedia.acm.services.wopi.model.WopiFileInfo;
import com.armedia.acm.services.wopi.service.WopiAcmService;

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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/api/latest/wopi")
public class WopiFilesApiController
{
    private WopiAcmService wopiService;
    private static final Logger log = LoggerFactory.getLogger(WopiFilesApiController.class);

    @PreAuthorize("hasPermission(#id, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(value = "/files/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WopiFileInfo> getFileInfo(@PathVariable Long id, Authentication authentication)
    {
        log.info("Getting file info for id [{}] per user [{}]", id, authentication.getName());
        return wopiService.getFileInfo(id, authentication)
                .map(it -> new ResponseEntity<>(it, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasPermission(#id, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(value = "/files/{id}/contents", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getFileContents(@PathVariable Long id, Authentication authentication)
    {
        log.info("Getting contents for file with id [{}] per user [{}]", id, authentication.getName());
        return wopiService.getFileContents(id)
                .map(it -> new ResponseEntity<>(it, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasPermission(#id, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/files/{id}/contents", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity putFile(@PathVariable Long id,
            @RequestBody InputStreamResource resource,
            Authentication authentication)
    {
        log.info("Put file [{}] per user [{}]", id, authentication.getName());
        boolean succeeded = wopiService.putFile(id, resource, authentication);
        return succeeded ? new ResponseEntity(HttpStatus.OK) : new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasPermission(#id, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/files/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Long> manageLock(@PathVariable("id") Long id, Authentication authentication,
            HttpServletRequest request)
    {
        String overrideType = request.getParameter("X-WOPI-Override");
        String lock = request.getParameter("X-WOPI-Lock");
        log.info("Manage lock per override header [{}]", overrideType);
        Long lockResult = wopiService.manageLock(overrideType, id, lock, authentication);
        if (lockResult == null)
        {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(lockResult, HttpStatus.OK);
    }

    public void setWopiService(WopiAcmService wopiService)
    {
        this.wopiService = wopiService;
    }
}

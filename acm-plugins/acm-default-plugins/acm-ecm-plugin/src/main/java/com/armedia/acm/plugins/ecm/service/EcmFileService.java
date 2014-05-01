package com.armedia.acm.plugins.ecm.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by armdev on 5/1/14.
 */
public interface EcmFileService
{
    @Transactional
    ResponseEntity<? extends Object> upload(
            MultipartFile file,
            String acceptHeader,
            String contextPath,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName);
}

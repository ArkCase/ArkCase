package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.io.InputStream;

/**
 * Created by armdev on 5/1/14.
 */
public interface EcmFileTransaction
{
    EcmFile addFileTransaction(
            Authentication authentication,
            InputStream fileInputStream,
            String mimeType,
            String fileName,
            String cmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName)
            throws MuleException;
}

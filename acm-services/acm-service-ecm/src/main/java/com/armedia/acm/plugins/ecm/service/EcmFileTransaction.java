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
                String fileType,
                InputStream fileInputStream,
                String mimeType,
                String fileName,
                String cmisFolderId,
                String parentObjectType,
                Long parentObjectId,
                String parentObjectName)
                throws MuleException;

        EcmFile addFileTransaction(
                Authentication authentication,
                String fileType,
                String fileCategory,
                InputStream fileInputStream,
                String mimeType,
                String fileName,
                String cmisFolderId,
                String parentObjectType,
                Long parentObjectId,
                String parentObjectName)
                throws MuleException;

        EcmFile updateFileTransaction(
                Authentication authentication,
                EcmFile ecmFile,
                InputStream fileInputStream)
                throws MuleException;
        
        String downloadFileTransaction(EcmFile ecmFile) throws MuleException;
}

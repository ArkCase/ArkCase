package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.io.InputStream;

/**
 * Created by armdev on 5/1/14.
 */
public interface EcmFileTransaction
{
        EcmFile addFileTransaction(
                String originalFileName,
                Authentication authentication,
                String fileType,
                InputStream fileInputStream,
                String mimeType,
                String fileName,
                String cmisFolderId,
                AcmContainer container)
                throws MuleException;

        EcmFile addFileTransaction(
                String originalFileName,
                Authentication authentication,
                String fileType,
                String fileCategory,
                InputStream fileInputStream,
                String mimeType,
                String fileName,
                String cmisFolderId,
                AcmContainer container)
                throws MuleException;

        EcmFile updateFileTransaction(
                Authentication authentication,
                EcmFile ecmFile,
                InputStream fileInputStream)
                throws MuleException;
        
        String downloadFileTransaction(EcmFile ecmFile) throws MuleException;

        InputStream downloadFileTransactionAsInputStream(EcmFile ecmFile) throws MuleException;

}

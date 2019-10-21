package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.chemistry.opencmis.client.api.Document;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by armdev on 5/1/14.
 */
public interface EcmFileTransaction
{
    @Deprecated
    EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, InputStream fileContents, EcmFile metadata,
            Document existingCmisDocument) throws MuleException, IOException;

    @Deprecated
    EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, InputStream fileContents, EcmFile metadata)
            throws MuleException, IOException;

    @Deprecated
    EcmFile addFileTransaction(String originalFileName, Authentication authentication, String fileType, String fileCategory,
            InputStream fileInputStream, String mimeType, String fileName, String cmisFolderId, AcmContainer container,
            String cmisRepositoryId)
            throws MuleException, IOException;

    @Deprecated
    EcmFile addFileTransaction(String originalFileName, Authentication authentication, String fileType,
            String fileCategory, InputStream fileContents, String fileContentType, String fileName,
            String targetCmisFolderId, AcmContainer container, String cmisRepositoryId,
            Document existingCmisDocument) throws MuleException, IOException;

    EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, EcmFile metadata,
            Document existingCmisDocument, MultipartFile file) throws MuleException, IOException;

    EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, EcmFile metadata, MultipartFile file)
            throws MuleException, IOException;

    EcmFile updateFileTransaction(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream)
            throws MuleException, IOException;

    EcmFile updateFileTransaction(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream, String fileExtension)
            throws MuleException, IOException;

    EcmFile updateFileTransactionEventAware(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream)
            throws MuleException, IOException;

    EcmFile updateFileTransactionEventAware(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream, String fileExtension)
            throws MuleException, IOException;

    String downloadFileTransaction(EcmFile ecmFile) throws ArkCaseFileRepositoryException;

    InputStream downloadFileTransactionAsInputStream(EcmFile ecmFile) throws ArkCaseFileRepositoryException;

    InputStream downloadFileTransactionAsInputStream(EcmFile ecmFile, String fileVersion) throws ArkCaseFileRepositoryException;
}

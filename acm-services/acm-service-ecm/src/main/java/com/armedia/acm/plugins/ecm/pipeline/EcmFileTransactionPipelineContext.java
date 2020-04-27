package com.armedia.acm.plugins.ecm.pipeline;

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

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.IOException;

/**
 * Created by joseph.mcgrady on 9/9/2015.
 */
public class EcmFileTransactionPipelineContext extends AbstractPipelineContext
{

    private static final Logger LOG = LogManager.getLogger(EcmFileTransactionPipelineContext.class);
    private String originalFileName;
    private Authentication authentication;
    private File fileContents;
    private String cmisRepositoryId;
    private String cmisFolderId;
    private AcmContainer container;
    private Document cmisDocument;
    private EcmFile ecmFile;
    private boolean isAppend;
    private boolean fileAlreadyInEcmSystem;
    private EcmTikaFile detectedFileMetadata;
    private boolean searchablePDF;
    private File mergedFile;
    private boolean fileNameAlreadyInEcmSystem;

    public String getOriginalFileName()
    {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName)
    {
        this.originalFileName = originalFileName;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public String getCmisRepositoryId()
    {
        return cmisRepositoryId;
    }

    public void setCmisRepositoryId(String cmisRepositoryId)
    {
        this.cmisRepositoryId = cmisRepositoryId;
    }

    public String getCmisFolderId()
    {
        return cmisFolderId;
    }

    public void setCmisFolderId(String cmisFolderId)
    {
        this.cmisFolderId = cmisFolderId;
    }

    public AcmContainer getContainer()
    {
        return container;
    }

    public void setContainer(AcmContainer container)
    {
        this.container = container;
    }

    public Document getCmisDocument()
    {
        return cmisDocument;
    }

    public void setCmisDocument(Document cmisDocument)
    {
        this.cmisDocument = cmisDocument;
    }

    public EcmFile getEcmFile()
    {
        return ecmFile;
    }

    public void setEcmFile(EcmFile ecmFile)
    {
        this.ecmFile = ecmFile;
    }

    public boolean getIsAppend()
    {
        return isAppend;
    }

    public void setIsAppend(boolean isAppend)
    {
        this.isAppend = isAppend;
    }

    public boolean isFileNameAlreadyInEcmSystem()
    {
        return fileNameAlreadyInEcmSystem;
    }

    public void setFileNameAlreadyInEcmSystem(boolean fileNameAlreadyInEcmSystem)
    {
        this.fileNameAlreadyInEcmSystem = fileNameAlreadyInEcmSystem;
    }

    @Deprecated
    /**
     * @deprecated use getMergedFile
     */
    public byte[] getMergedFileByteArray()
    {
        try
        {
            return getMergedFile() == null ? null : FileUtils.readFileToByteArray(getMergedFile());
        }
        catch (IOException e)
        {
            LOG.error("Could not convert file to byte array: {}", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    /**
     * @deprecated use setMergedFile
     */
    public void setMergedFileByteArray(byte[] mergedFileByteArray)
    {
        try
        {
            if (getMergedFile() == null)
            {
                // NOTE This file is stored in the context for later use, do NOT delete it at the end of this method.
                setMergedFile(File.createTempFile("arkcase-file-transaction-set-merged-file-byte-array-", null));
            }
            FileUtils.writeByteArrayToFile(getMergedFile(), mergedFileByteArray);
        }
        catch (IOException e)
        {
            LOG.error("Could not convert byte array to file: {}", e.getMessage(), e);
        }
    }

    public File getMergedFile()
    {
        return mergedFile;
    }

    public void setMergedFile(File mergedFile)
    {
        this.mergedFile = mergedFile;
    }

    public boolean isFileAlreadyInEcmSystem()
    {
        return fileAlreadyInEcmSystem;
    }

    public void setFileAlreadyInEcmSystem(boolean fileAlreadyInEcmSystem)
    {
        this.fileAlreadyInEcmSystem = fileAlreadyInEcmSystem;
    }

    public EcmTikaFile getDetectedFileMetadata()
    {
        return detectedFileMetadata;
    }

    public void setDetectedFileMetadata(EcmTikaFile detectedFileMetadata)
    {
        this.detectedFileMetadata = detectedFileMetadata;
    }

    public File getFileContents()
    {
        return fileContents;
    }

    public void setFileContents(File fileContents)
    {
        this.fileContents = fileContents;
    }

    public boolean isSearchablePDF()
    {
        return searchablePDF;
    }

    public void setSearchablePDF(boolean searchablePDF)
    {
        this.searchablePDF = searchablePDF;
    }

    @Deprecated
    /**
     * @deprecated use getFileContents
     */
    public synchronized byte[] getFileByteArray()
    {
        try
        {
            return getFileContents() == null ? null : FileUtils.readFileToByteArray(getFileContents());
        }
        catch (IOException e)
        {
            LOG.error("Could not convert file to byte array: {}", e.getMessage(), e);
            return null;
        }
    }

    @Deprecated
    /**
     * @deprecated use setFileContents
     */
    public synchronized void setFileByteArray(byte[] fileByteArray)
    {
        try
        {
            if (getFileContents() == null)
            {
                // NOTE This file is stored in the context for later use, do NOT delete it at the end of this method.
                setFileContents(File.createTempFile("arkcase-file-transaction-set-file-byte-array-", null));
            }
            FileUtils.writeByteArrayToFile(getFileContents(), fileByteArray);
        }
        catch (IOException e)
        {
            LOG.error("Could not convert byte array to file: {}", e.getMessage(), e);
        }
    }
}

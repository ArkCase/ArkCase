package com.armedia.acm.plugins.ecm.pipeline;

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.IOException;

/**
 * Created by joseph.mcgrady on 9/9/2015.
 */
public class EcmFileTransactionPipelineContext extends AbstractPipelineContext
{

    private static final Logger LOG = LoggerFactory.getLogger(EcmFileTransactionPipelineContext.class);
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

    private byte[] mergedFileByteArray;

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

    public byte[] getMergedFileByteArray()
    {
        return mergedFileByteArray;
    }

    public void setMergedFileByteArray(byte[] mergedFileByteArray)
    {
        this.mergedFileByteArray = mergedFileByteArray;
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

    @Deprecated
    /**
     * @deprecated use getFileContents
     */
    public byte[] getFileByteArray()
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

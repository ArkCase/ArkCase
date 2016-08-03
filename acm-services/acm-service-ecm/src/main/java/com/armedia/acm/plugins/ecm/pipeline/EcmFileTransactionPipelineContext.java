package com.armedia.acm.plugins.ecm.pipeline;

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.security.core.Authentication;

/**
 * Created by joseph.mcgrady on 9/9/2015.
 */
public class EcmFileTransactionPipelineContext extends AbstractPipelineContext
{

    private String originalFileName;
    private Authentication authentication;
    private byte[] fileByteArray;
    private String cmisFolderId;
    private AcmContainer container;
    private Document cmisDocument;
    private EcmFile ecmFile;
    private boolean isAppend;

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

    public byte[] getFileByteArray()
    {
        return fileByteArray;
    }

    public void setFileByteArray(byte[] fileByteArray)
    {
        this.fileByteArray = fileByteArray;
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
}
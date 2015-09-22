package com.armedia.acm.plugins.ecm.pipeline;

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.PipelineContext;
import org.springframework.security.core.Authentication;
import org.apache.chemistry.opencmis.client.api.Document;

import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/9/2015.
 */
public class EcmFileTransactionPipelineContext implements PipelineContext {

    private String originalFileName;
    private Authentication authentication;
    private InputStream fileInputStream;
    private String cmisFolderId;
    private AcmContainer container;
    private Document cmisDocument;
    private EcmFile ecmFile;
    private boolean isAppend;
    private boolean isPDF;
    private boolean isAuthorizationOrAbstract;

    public String getOriginalFileName() {
        return originalFileName;
    }
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    public Authentication getAuthentication() {
        return authentication;
    }
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
    public InputStream getFileInputStream() {
        return fileInputStream;
    }
    public void setFileInputStream(InputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }
    public String getCmisFolderId() {
        return cmisFolderId;
    }
    public void setCmisFolderId(String cmisFolderId) {
        this.cmisFolderId = cmisFolderId;
    }
    public AcmContainer getContainer() {
        return container;
    }
    public void setContainer(AcmContainer container) {
        this.container = container;
    }
    public Document getCmisDocument() {
        return cmisDocument;
    }
    public void setCmisDocument(Document cmisDocument) {
        this.cmisDocument = cmisDocument;
    }
    public EcmFile getEcmFile() {
        return ecmFile;
    }
    public void setEcmFile(EcmFile ecmFile) {
        this.ecmFile = ecmFile;
    }
    public boolean getIsAppend() {
        return isAppend;
    }
    public void setIsAppend(boolean isAppend) {
        this.isAppend = isAppend;
    }
    public boolean getIsPDF() {
        return isPDF;
    }
    public void setIsPDF(boolean isPDF) {
        this.isPDF = isPDF;
    }
    public boolean getIsAuthorizationOrAbstract() {
        return isAuthorizationOrAbstract;
    }
    public void setIsAuthorizationOrAbstract(boolean isAuthorizationOrAbstract) {
        this.isAuthorizationOrAbstract = isAuthorizationOrAbstract;
    }
}
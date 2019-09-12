package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class EcmFileConfig implements InitializingBean
{
    @JsonProperty("ecm.defaultCmisId")
    @Value("${ecm.defaultCmisId}")
    private String defaultCmisId;

    @JsonProperty("ecm.defaultBasePath")
    @Value("${ecm.defaultBasePath}")
    private String defaultBasePath;

    @JsonProperty("ecm.defaultPath.COMPLAINT")
    @Value("${ecm.defaultPath.COMPLAINT}")
    private String defaultPathComplaint;

    @JsonProperty("ecm.defaultPath.TASK")
    @Value("${ecm.defaultPath.TASK}")
    private String defaultPathTask;

    @JsonProperty("ecm.defaultPath.CASE_FILE")
    @Value("${ecm.defaultPath.CASE_FILE}")
    private String defaultPathCaseFile;

    @JsonProperty("ecm.defaultPath.DOC_REPO")
    @Value("${ecm.defaultPath.DOC_REPO}")
    private String defaultPathDocumentRepository;

    @JsonProperty("ecm.defaultPath.RECYCLE_BIN")
    @Value("${ecm.defaultPath.RECYCLE_BIN}")
    private String defaultPathRecycleBin;

    @JsonProperty("ecm.defaultPath.BUSINESS_PROCESS")
    @Value("${ecm.defaultPath.BUSINESS_PROCESS}")
    private String defaultPathBusinessProcess;

    @JsonProperty("ecm.documentsParticipantTypes.mappings.group-write")
    @Value("${ecm.documentsParticipantTypes.mappings.group-write}")
    private String documentParticipantTypeGroupWrite;

    @JsonProperty("ecm.documentsParticipantTypes.mappings.group-read")
    @Value("${ecm.documentsParticipantTypes.mappings.group-read}")
    private String documentParticipantTypeGroupRead;

    @JsonProperty("ecm.documentsParticipantTypes.mappings.group-no-access")
    @Value("${ecm.documentsParticipantTypes.mappings.group-no-access}")
    private String documentParticipantTypeGroupNoAccess;

    @JsonProperty("ecm.documentsParticipantTypes.mappings.write")
    @Value("${ecm.documentsParticipantTypes.mappings.write}")
    private String documentParticipantTypeWrite;

    @JsonProperty("ecm.documentsParticipantTypes.mappings.read")
    @Value("${ecm.documentsParticipantTypes.mappings.read}")
    private String documentParticipantTypeRead;

    @JsonProperty("ecm.documentsParticipantTypes.mappings.no-access")
    @Value("${ecm.documentsParticipantTypes.mappings.no-access}")
    private String documentParticipantTypeNoAccess;

    @JsonProperty("ecm.arkcaseAlfrescoDocumentsParticipantTypes.mappings.write")
    @Value("${ecm.arkcaseAlfrescoDocumentsParticipantTypes.mappings.write}")
    private String arkcaseAlfrescoDocumentsParticipantTypeWrite;

    @JsonProperty("ecm.arkcaseAlfrescoDocumentsParticipantTypes.mappings.read")
    @Value("${ecm.arkcaseAlfrescoDocumentsParticipantTypes.mappings.read}")
    private String arkcaseAlfrescoDocumentsParticipantTypeRead;

    @JsonProperty("ecm.viewer")
    @Value("${ecm.viewer}")
    private String viewer;

    @JsonProperty("ecm.viewer.js")
    @Value("${ecm.viewer.js}")
    private String viewerJs;

    @JsonProperty("ecm.viewer.snowbound.tabHeader")
    @Value("${ecm.viewer.snowbound.tabHeader}")
    private String snowboundTabHeader;

    @JsonProperty("ecm.viewer.snowbound")
    @Value("${ecm.viewer.snowbound}")
    private String snowboundUrl;

    @JsonProperty("ecm.viewer.snowbound.readonly.url")
    @Value("${ecm.viewer.snowbound.readonly.url}")
    private String snowboundReadonlyUrl;

    @JsonProperty("ecm.viewer.snowbound.readonly.initialState")
    @Value("${ecm.viewer.snowbound.readonly.initialState}")
    private Boolean snowBoundReadonlyInitialState;

    @JsonProperty("ecm.viewer.snowbound.encryptionKey")
    @Value("${ecm.viewer.snowbound.encryptionKey}")
    private String snowboundEncryptionKey;

    @JsonProperty("ecm.viewer.pdfViewer")
    @Value("${ecm.viewer.pdfViewer}")
    private String pdfViewer;

    @JsonProperty("ecm.viewer.snowbound.enableOCR")
    @Value("${ecm.viewer.snowbound.enableOCR}")
    private Boolean snowboundEnableOcr;

    private Map<String, String> documentsParticipantTypesFileMappings = new HashMap<>();

    private Map<String, String> arkcaseAlfrescoDocumentsParticipantTypesFileMappings = new HashMap<>();

    private Map<String, String> defaultPathByObjectType = new HashMap<>();

    @Override
    public void afterPropertiesSet()
    {
        documentsParticipantTypesFileMappings.put("group-write", documentParticipantTypeGroupWrite);
        documentsParticipantTypesFileMappings.put("group-read", documentParticipantTypeGroupRead);
        documentsParticipantTypesFileMappings.put("group-no-access", documentParticipantTypeGroupNoAccess);
        documentsParticipantTypesFileMappings.put("write", documentParticipantTypeWrite);
        documentsParticipantTypesFileMappings.put("read", documentParticipantTypeRead);
        documentsParticipantTypesFileMappings.put("no-access", documentParticipantTypeRead);

        arkcaseAlfrescoDocumentsParticipantTypesFileMappings.put("write", arkcaseAlfrescoDocumentsParticipantTypeWrite);
        arkcaseAlfrescoDocumentsParticipantTypesFileMappings.put("read", arkcaseAlfrescoDocumentsParticipantTypeRead);

        defaultPathByObjectType.put("COMPLAINT", defaultPathComplaint);
        defaultPathByObjectType.put("TASK", defaultPathTask);
        defaultPathByObjectType.put("DOC_REPO", defaultPathDocumentRepository);
        defaultPathByObjectType.put("CASE_FILE", defaultPathCaseFile);
        defaultPathByObjectType.put("RECYCLE_BIN", defaultPathRecycleBin);
        defaultPathByObjectType.put("BUSINESS_PROCESS", defaultPathBusinessProcess);
    }

    public String getDefaultCmisId()
    {
        return defaultCmisId;
    }

    public void setDefaultCmisId(String defaultCmisId)
    {
        this.defaultCmisId = defaultCmisId;
    }

    public String getDefaultBasePath()
    {
        return defaultBasePath;
    }

    public void setDefaultBasePath(String defaultBasePath)
    {
        this.defaultBasePath = defaultBasePath;
    }

    public String getDefaultPathComplaint()
    {
        return defaultPathComplaint;
    }

    public void setDefaultPathComplaint(String defaultPathComplaint)
    {
        this.defaultPathComplaint = defaultPathComplaint;
    }

    public String getDefaultPathTask()
    {
        return defaultPathTask;
    }

    public void setDefaultPathTask(String defaultPathTask)
    {
        this.defaultPathTask = defaultPathTask;
    }

    public String getDefaultPathCaseFile()
    {
        return defaultPathCaseFile;
    }

    public void setDefaultPathCaseFile(String defaultPathCaseFile)
    {
        this.defaultPathCaseFile = defaultPathCaseFile;
    }

    public String getDefaultPathDocumentRepository()
    {
        return defaultPathDocumentRepository;
    }

    public void setDefaultPathDocumentRepository(String defaultPathDocumentRepository)
    {
        this.defaultPathDocumentRepository = defaultPathDocumentRepository;
    }

    public String getDocumentParticipantTypeGroupWrite()
    {
        return documentParticipantTypeGroupWrite;
    }

    public void setDocumentParticipantTypeGroupWrite(String documentParticipantTypeGroupWrite)
    {
        this.documentParticipantTypeGroupWrite = documentParticipantTypeGroupWrite;
    }

    public String getDocumentParticipantTypeGroupRead()
    {
        return documentParticipantTypeGroupRead;
    }

    public void setDocumentParticipantTypeGroupRead(String documentParticipantTypeGroupRead)
    {
        this.documentParticipantTypeGroupRead = documentParticipantTypeGroupRead;
    }

    public String getDocumentParticipantTypeGroupNoAccess()
    {
        return documentParticipantTypeGroupNoAccess;
    }

    public void setDocumentParticipantTypeGroupNoAccess(String documentParticipantTypeGroupNoAccess)
    {
        this.documentParticipantTypeGroupNoAccess = documentParticipantTypeGroupNoAccess;
    }

    public String getDocumentParticipantTypeWrite()
    {
        return documentParticipantTypeWrite;
    }

    public void setDocumentParticipantTypeWrite(String documentParticipantTypeWrite)
    {
        this.documentParticipantTypeWrite = documentParticipantTypeWrite;
    }

    public String getDocumentParticipantTypeRead()
    {
        return documentParticipantTypeRead;
    }

    public void setDocumentParticipantTypeRead(String documentParticipantTypeRead)
    {
        this.documentParticipantTypeRead = documentParticipantTypeRead;
    }

    public String getDocumentParticipantTypeNoAccess()
    {
        return documentParticipantTypeNoAccess;
    }

    public void setDocumentParticipantTypeNoAccess(String documentParticipantTypeNoAccess)
    {
        this.documentParticipantTypeNoAccess = documentParticipantTypeNoAccess;
    }

    public String getArkcaseAlfrescoDocumentsParticipantTypeWrite()
    {
        return arkcaseAlfrescoDocumentsParticipantTypeWrite;
    }

    public void setArkcaseAlfrescoDocumentsParticipantTypeWrite(String arkcaseAlfrescoDocumentsParticipantTypeWrite)
    {
        this.arkcaseAlfrescoDocumentsParticipantTypeWrite = arkcaseAlfrescoDocumentsParticipantTypeWrite;
    }

    public String getArkcaseAlfrescoDocumentsParticipantTypeRead()
    {
        return arkcaseAlfrescoDocumentsParticipantTypeRead;
    }

    public void setArkcaseAlfrescoDocumentsParticipantTypeRead(String arkcaseAlfrescoDocumentsParticipantTypeRead)
    {
        this.arkcaseAlfrescoDocumentsParticipantTypeRead = arkcaseAlfrescoDocumentsParticipantTypeRead;
    }

    public Map<String, String> getDocumentsParticipantTypesFileMappings()
    {
        return documentsParticipantTypesFileMappings;
    }

    public void setDocumentsParticipantTypesFileMappings(Map<String, String> documentsParticipantTypesFileMappings)
    {
        this.documentsParticipantTypesFileMappings = documentsParticipantTypesFileMappings;
    }

    public Map<String, String> getDefaultPathByObjectType()
    {
        return defaultPathByObjectType;
    }

    public Map<String, String> getArkcaseAlfrescoDocumentsParticipantTypesFileMappings()
    {
        return arkcaseAlfrescoDocumentsParticipantTypesFileMappings;
    }

    public void setArkcaseAlfrescoDocumentsParticipantTypesFileMappings(
            Map<String, String> arkcaseAlfrescoDocumentsParticipantTypesFileMappings)
    {
        this.arkcaseAlfrescoDocumentsParticipantTypesFileMappings = arkcaseAlfrescoDocumentsParticipantTypesFileMappings;
    }

    public void setDefaultPathByObjectType(Map<String, String> defaultPathByObjectType)
    {
        this.defaultPathByObjectType = defaultPathByObjectType;
    }

    public String getViewer()
    {
        return viewer;
    }

    public void setViewer(String viewer)
    {
        this.viewer = viewer;
    }

    public String getViewerJs()
    {
        return viewerJs;
    }

    public void setViewerJs(String viewerJs)
    {
        this.viewerJs = viewerJs;
    }

    /**
     * @return the snowboundTabHeader
     */
    public String getSnowboundTabHeader()
    {
        return snowboundTabHeader;
    }

    /**
     * @param snowboundTabHeader
     *            the snowboundTabHeader to set
     */
    public void setSnowboundTabHeader(String snowboundTabHeader)
    {
        this.snowboundTabHeader = snowboundTabHeader;
    }

    public String getSnowboundUrl()
    {
        return snowboundUrl;
    }

    public void setSnowboundUrl(String snowboundUrl)
    {
        this.snowboundUrl = snowboundUrl;
    }

    public String getSnowboundReadonlyUrl()
    {
        return snowboundReadonlyUrl;
    }

    public void setSnowboundReadonlyUrl(String snowboundReadonlyUrl)
    {
        this.snowboundReadonlyUrl = snowboundReadonlyUrl;
    }

    public Boolean getSnowBoundReadonlyInitialState()
    {
        return snowBoundReadonlyInitialState;
    }

    public void setSnowBoundReadonlyInitialState(Boolean snowBoundReadonlyInitialState)
    {
        this.snowBoundReadonlyInitialState = snowBoundReadonlyInitialState;
    }

    public String getSnowboundEncryptionKey()
    {
        return snowboundEncryptionKey;
    }

    public void setSnowboundEncryptionKey(String snowboundEncryptionKey)
    {
        this.snowboundEncryptionKey = snowboundEncryptionKey;
    }

    public String getPdfViewer()
    {
        return pdfViewer;
    }

    public void setPdfViewer(String pdfViewer)
    {
        this.pdfViewer = pdfViewer;
    }

    public String getDefaultPathForObject(String objectType)
    {
        return defaultPathByObjectType.get(objectType);
    }

    public String getFileParticipant(String participantType)
    {
        return documentsParticipantTypesFileMappings.get(participantType);
    }

    public Boolean getSnowboundEnableOcr()
    {
        return snowboundEnableOcr;
    }

    public void setSnowboundEnableOcr(Boolean snowboundEnableOcr)
    {
        this.snowboundEnableOcr = snowboundEnableOcr;
    }

    /**
     * @return the defaultPathRecycleBin
     */
    public String getDefaultPathRecycleBin()
    {
        return defaultPathRecycleBin;
    }

    /**
     * @param defaultPathRecycleBin
     *            the defaultPathRecycleBin to set
     */
    public void setDefaultPathRecycleBin(String defaultPathRecycleBin)
    {
        this.defaultPathRecycleBin = defaultPathRecycleBin;
    }

    /**
     * @return the defaultPathBusinessProcess
     */
    public String getDefaultPathBusinessProcess()
    {
        return defaultPathBusinessProcess;
    }

    /**
     * @param defaultPathBusinessProcess
     *            the defaultPathBusinessProcess to set
     */
    public void setDefaultPathBusinessProcess(String defaultPathBusinessProcess)
    {
        this.defaultPathBusinessProcess = defaultPathBusinessProcess;
    }
}

package com.armedia.acm.plugins.ecm.web.api;

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

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileDownloadedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RequestMapping({ "/api/v1/plugin/ecm", "/api/latest/plugin/ecm" })
public class FileDownloadAPIController implements ApplicationEventPublisherAware
{
    private CamelContextManager camelContextManager;

    private EcmFileDao fileDao;

    private ApplicationEventPublisher applicationEventPublisher;

    private FolderAndFilesUtils folderAndFilesUtils;

    private CmisConfigUtils cmisConfigUtils;

    private Logger log = LogManager.getLogger(getClass());

    private ObjectConverter objectConverter;

    // #parentObjectType == 'USER_ORG' applies to uploading profile picture
    @PreAuthorize("hasPermission(#fileId, 'FILE', 'read|group-read|write|group-write') or #parentObjectType == 'USER_ORG'")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void downloadFileById(@RequestParam(value = "inline", required = false, defaultValue = "false") boolean inline,
            @RequestParam(value = "ecmFileId", required = true, defaultValue = "0") Long fileId,
            @RequestParam(value = "acm_email_ticket", required = false, defaultValue = "") String acm_email_ticket,
            @RequestParam(value = "version", required = false, defaultValue = "") String version,
            @RequestParam(value = "parentObjectType", required = false, defaultValue = "") String parentObjectType,
            Authentication authentication,
            HttpSession httpSession, HttpServletResponse response)
            throws IOException, ArkCaseFileRepositoryException, AcmObjectNotFoundException
    {
        log.info("Downloading file by ID '{}' for user '{}'", fileId, authentication.getName());

        EcmFile ecmFile = getFileDao().find(fileId);

        if (ecmFile != null)
        {
            EcmFileDownloadedEvent event = new EcmFileDownloadedEvent(ecmFile);
            event.setIpAddress((String) httpSession.getAttribute("acm_ip_address"));
            event.setUserId(authentication.getName());
            event.setSucceeded(true);

            getApplicationEventPublisher().publishEvent(event);
            String cmisFileId = getFolderAndFilesUtils().getVersionCmisId(ecmFile, version);
            download(cmisFileId, response, inline, ecmFile, version);
        }
        else
        {
            fileNotFound(fileId);
        }
    }

    protected void download(String fileId, HttpServletResponse response, boolean isInline, EcmFile ecmFile, String version)
            throws IOException, ArkCaseFileRepositoryException, AcmObjectNotFoundException
    {

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
        messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        messageProps.put(CamelCMISConstants.CMIS_OBJECT_ID, fileId);

        Object result = getCamelContextManager().send(ArkCaseCMISActions.DOWNLOAD_DOCUMENT, messageProps);

        if (result instanceof ContentStream)
        {
            handleFilePayload((ContentStream) result, response, isInline, ecmFile, version);
        }
        else
        {
            fileNotFound(ecmFile.getId());
        }

    }

    // called for normal processing - file was found
    private void handleFilePayload(ContentStream filePayload, HttpServletResponse response, boolean isInline, EcmFile ecmFile,
            String version) throws IOException
    {
        String mimeType = filePayload.getMimeType();

        EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(ecmFile, version);
        // OpenCMIS thinks this is an application/octet-stream since the file has no extension
        // we will use what Tika detected in such cases
        if (ecmFileVersion != null)
        {
            if (ecmFile != null && (mimeType == null || !mimeType.equals(ecmFileVersion.getVersionMimeType())))
            {
                mimeType = ecmFileVersion.getVersionMimeType();
            }
        }
        else
        {
            if (ecmFile != null && (mimeType == null || !mimeType.equals(ecmFile.getFileActiveVersionMimeType())))
            {
                mimeType = ecmFile.getFileActiveVersionMimeType();
            }
        }

        String fileName = filePayload.getFileName();
        // endWith will throw a NullPointerException on a null argument. But a file is not required to have an
        // extension... so the extension can be null. So we have to guard against it.
        if (ecmFileVersion != null)
        {
            if (ecmFile != null && ecmFileVersion.getVersionFileNameExtension() != null
                    && !fileName.endsWith(ecmFileVersion.getVersionFileNameExtension()))
            {
                fileName = fileName + ecmFileVersion.getVersionFileNameExtension();
            }
        }
        else
        {
            if (ecmFile != null && ecmFile.getFileActiveVersionNameExtension() != null
                    && !fileName.endsWith(ecmFile.getFileActiveVersionNameExtension()))
            {
                fileName = fileName + ecmFile.getFileActiveVersionNameExtension();
            }
        }

        InputStream fileIs = null;

        try
        {
            fileIs = filePayload.getStream();
            if (!isInline)
            {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                // add file metadata so it can be displayed in Snowbound
                JSONObject fileMetadata = new JSONObject();
                fileMetadata.put("fileName", ecmFile.getFileName());
                fileMetadata.put("fileType", ecmFile.getFileType());
                String versionTag = (version == null || version.equals("")) ? ecmFile.getActiveVersionTag() : version;
                fileMetadata.put("fileNameWithVersion", String.format("%s:%s", ecmFile.getFileName(), versionTag)); 
                fileMetadata.put("fileTypeCapitalized",
                        ecmFile.getFileType().substring(0, 1).toUpperCase() + ecmFile.getFileType().substring(1));
                response.setHeader("X-ArkCase-File-Metadata", fileMetadata.toString());
            }
            response.setContentType(mimeType);
            byte[] buffer = new byte[1024];
            int read;
            do
            {
                read = fileIs.read(buffer, 0, buffer.length);
                if (read > 0)
                {
                    response.getOutputStream().write(buffer, 0, read);
                }
            } while (read > 0);
            response.getOutputStream().flush();
        }
        finally
        {
            if (fileIs != null)
            {
                try
                {
                    fileIs.close();
                }
                catch (IOException e)
                {
                    log.error("Could not close CMIS content stream: {}", e.getMessage(), e);
                }
            }
        }
    }

    // called when the file was not found.
    private void fileNotFound(Long fileId) throws AcmObjectNotFoundException
    {
        throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, String.format("File %d not found", fileId), null);
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public CamelContextManager getCamelContextManager()
    {
        return camelContextManager;
    }

    public void setCamelContextManager(CamelContextManager camelContextManager)
    {
        this.camelContextManager = camelContextManager;
    }
}

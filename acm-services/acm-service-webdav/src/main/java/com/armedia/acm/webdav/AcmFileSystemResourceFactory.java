package com.armedia.acm.webdav;

/*-
 * #%L
 * ACM Service: WebDAV Integration Library
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

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.milton.http.LockManager;
import io.milton.http.ResourceFactory;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.Resource;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public class AcmFileSystemResourceFactory implements ResourceFactory
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileDao fileDao;
    private EcmFileTransaction ecmFileTransaction;
    private FolderAndFilesUtils folderAndFilesUtils;
    private MuleContextManager muleContextManager;
    private LockManager lockManager;
    private AcmWebDAVSecurityManager securityManager;
    private Long maxAgeSeconds;
    private String filterMapping;
    private Pattern fileExtensionPattern;
    private AuthenticationTokenService authenticationTokenService;
    private CmisConfigUtils cmisConfigUtils;
    /**
     * A pattern to distinguish between a file URL and the URL that Microsoft Office sends for an OPTIONS request. An
     * ArkCase WebDAV file URL is assumed to end in (someNumber.someExtension), e.g., "134.docx". If a WebDAV URL does
     * not end with this pattern, assume Office is sending an OPTIONS request, and we can reply with an empty (that is,a
     * dummy) resource. We can't send the real file resource, since Office did not send us the whole URL.
     */

    private Pattern realDocumentUrl = Pattern.compile("^.*\\/\\d*\\.\\w*$");
    private AcmRootResource acmRootResource;

    @Override
    public Resource getResource(String host, String path) throws NotAuthorizedException, BadRequestException
    {
        log.trace("host: {}, path: {}", host, path);

        // if the path does not end in some-number.some-extension let's suppose it is an OPTIONS request.

        Matcher m = realDocumentUrl.matcher(path);
        if (m.matches())
        {
            log.debug("The path {} seems to be a real file request", path);
            String noExtensionPath = removeFileExtension(path);
            String strippedPath = noExtensionPath.substring(path.indexOf(filterMapping) + filterMapping.length());
            if (strippedPath.endsWith("/"))
            {
                strippedPath = strippedPath.substring(0, strippedPath.length() - 1);
            }

            log.trace("stripped path: {}", strippedPath);

            ResourceHandler handler = getResourceHandler(strippedPath);
            return handler.getResource(host, strippedPath);
        }
        else
        {
            log.debug("The path {} seems to be an list folder structure request or OPTIONS request", path);
            // FIXME return always root folder, we should fix this to return correct folder, but since url consists of
            // "/" it will be hard to implement
            if (acmRootResource == null)
            {
                acmRootResource = new AcmRootResource(this);
            }
            return acmRootResource;
        }
    }

    private String removeFileExtension(String path)
    {
        // remove file extensions
        Matcher m = fileExtensionPattern.matcher(path);
        if (m.find())
        {
            path = m.replaceFirst("");
        }
        return path;
    }

    private ResourceHandler getResourceHandler(String path) throws BadRequestException
    {
        // in ArkCase, Milton only handles files.
        return new AcmFileResourceHandler();
    }

    public String getCmisFileId(EcmFile ecmFile)
    {
        return folderAndFilesUtils.getActiveVersionCmisId(ecmFile);
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public void setFilterMapping(String filterMapping)
    {
        if (filterMapping.endsWith("/"))
        {
            this.filterMapping = filterMapping;
        }
        else
        {
            this.filterMapping = filterMapping + "/";
        }
    }

    public void setFileExtensionPattern(Pattern fileExtensionPattern)
    {
        this.fileExtensionPattern = fileExtensionPattern;
    }

    public AcmWebDAVSecurityManager getSecurityManager()
    {
        return securityManager;
    }

    public void setSecurityManager(AcmWebDAVSecurityManager securityManager)
    {
        this.securityManager = securityManager;
    }

    public Long getMaxAgeSeconds()
    {
        return maxAgeSeconds;
    }

    public void setMaxAgeSeconds(Long maxAgeSeconds)
    {
        this.maxAgeSeconds = maxAgeSeconds;
    }

    public LockManager getLockManager()
    {
        return lockManager;
    }

    public void setLockManager(LockManager lockManager)
    {
        this.lockManager = lockManager;
    }

    public EcmFileTransaction getEcmFileTransaction()
    {
        return ecmFileTransaction;
    }

    public void setEcmFileTransaction(EcmFileTransaction ecmFileTransaction)
    {
        this.ecmFileTransaction = ecmFileTransaction;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }

    interface ResourceHandler
    {

        AcmFileSystemResource getResource(String host, String path) throws BadRequestException;

    }

    private class AcmFileResourceHandler implements ResourceHandler
    {
        @Override
        public AcmFileSystemResource getResource(String host, String path) throws BadRequestException
        {
            log.trace("host: {}, path: {}", host, path);

            String[] fileArgs = path.split("/");
            Long fileId = Long.valueOf(fileArgs[fileArgs.length - 1]);

            String acmTicket = fileArgs[0];
            String fileType = fileArgs[1];
            String lockType = fileArgs[2];

            log.trace("fileId: {}, lock type: {}, fileType: {}", fileId, lockType, fileType);

            EcmFile ecmFile = getFileDao().find(fileId);

            log.trace("ecmFile exists? {}", ecmFile != null);

            return new AcmFileResource(host, ecmFile, fileType, lockType, acmTicket, AcmFileSystemResourceFactory.this, cmisConfigUtils);
        }
    }
}

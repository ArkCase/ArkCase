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

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.service.lock.FileLockType;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

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

    private final Logger log = LogManager.getLogger(getClass());
    private EcmFileDao fileDao;
    private AcmFolderDao folderDao;
    private EcmFileTransaction ecmFileTransaction;
    private FolderAndFilesUtils folderAndFilesUtils;
    private CamelContextManager camelContextManager;
    private LockManager lockManager;
    private AcmWebDAVSecurityManager securityManager;
    private Long maxAgeSeconds;
    private EhCacheCacheManager webDAVContainerIdCacheManager;

    /**
     * A pattern to distinguish between a file URL and the URL that Microsoft Office sends for an OPTIONS request. An
     * ArkCase WebDAV file URL is assumed to end in (someNumber.someExtension), e.g., "134.docx". If a WebDAV URL does
     * not end with this pattern, assume Office is sending an OPTIONS request, and we can reply with an empty (that is,a
     * dummy) resource. We can't send the real file resource, since Office did not send us the whole URL.
     */
    private Pattern realDocumentUrlPattern;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private AcmRootResource acmRootResource;

    @Override
    public Resource getResource(String host, String path) throws NotAuthorizedException, BadRequestException
    {
        log.debug("host: {}, path: {}", host, path);

        // if the path does not end in some-number.some-extension let's suppose it is an OPTIONS request.

        if (realDocumentUrlPattern.matcher(path).matches())
        {
            log.debug("The path {} seems to be a real file request", path);
            if (path.endsWith("/"))
            {
                path = path.substring(0, path.length() - 1);
            }
            log.trace("Path : {}", path);

            ResourceHandler handler = getResourceHandler();
            log.debug("handler class: [{}]", handler.getClass().getName());
            return handler.getResource(host, path);
        }
        else
        {
            log.debug("The path {} seems to be a list folder structure request or OPTIONS or PROPFIND request", path);
            if (acmRootResource == null)
            {
                acmRootResource = new AcmRootResource(this);
            }
            String userId = extractUserId(path);
            String cachedValue = getCache().get(path) != null ? (String) getCache().get(path).get() : null;
            // FIXME return always root folder, we should fix this to return correct folder, but since url consists of
            // "/" it will be hard to implement
            if (cachedValue != null)
            {
                acmRootResource.setChildren(new ArrayList<>());
                String[] splittedCachedValue = cachedValue.split("-");
                Long rootFolderId = Long.valueOf(splittedCachedValue[2]);
                getFolderDao().find(rootFolderId).getChildrenFolders().forEach(acmFolder ->
                    acmRootResource.updateChildren(new AcmFolderResource(host, this, userId, splittedCachedValue[0],
                            splittedCachedValue[1], acmFolder)));
                new ArrayList<>(getFileDao().findByFolderId(rootFolderId)).forEach(ecmFile -> {
                    acmRootResource.updateChildren(new AcmFileResource(host, ecmFile, ecmFile.getFileType(),
                            FileLockType.READ.name(), userId, splittedCachedValue[0], splittedCachedValue[1], this));
                });
            }
            return acmRootResource;
        }
    }

    private String extractUserId(String path)
    {
        int webdavIdx = path.indexOf("webdav");
        int userIdIndex = path.indexOf("/", webdavIdx + 8);
        return userIdIndex > 0 ? path.substring(webdavIdx + 7, userIdIndex) : null;
    }

    private ResourceHandler getResourceHandler()
    {
        // in ArkCase, Milton only handles files.
        return new AcmFileResourceHandler(realDocumentUrlPattern);
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

    public void setRealDocumentUrlPattern(Pattern realDocumentUrlPattern)
    {
        this.realDocumentUrlPattern = realDocumentUrlPattern;
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

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public CamelContextManager getCamelContextManager()
    {
        return camelContextManager;
    }

    public void setCamelContextManager(CamelContextManager camelContextManager)
    {
        this.camelContextManager = camelContextManager;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public void setWebDAVContainerIdCacheManager(EhCacheCacheManager webDAVContainerIdCacheManager)
    {
        this.webDAVContainerIdCacheManager = webDAVContainerIdCacheManager;
    }

    public Cache getCache()
    {
        return webDAVContainerIdCacheManager.getCache("webdav_container_id_cache");
    }

    interface ResourceHandler
    {
        AcmFileSystemResource getResource(String host, String path) throws BadRequestException;
    }

    private class AcmFileResourceHandler implements ResourceHandler
    {
        private final Pattern documentPattern;
        public AcmFileResourceHandler(Pattern realDocumentUrlPattern)
        {
            documentPattern = realDocumentUrlPattern;
        }

        @Override
        public AcmFileSystemResource getResource(String host, String path) throws BadRequestException
        {
            log.debug("host: [{}], path: [{}], doc pattern: [{}]", host, path, documentPattern);
            Matcher m = documentPattern.matcher(path);
            if (m.matches())
            {
                log.debug("Path [{}] is a document path", path);
                // it must be either a .tmp file, or not a .tmp file.
                if (path.endsWith(".tmp"))
                {
                    // if it's a .tmp file, either we've already created it, or we haven't.  If we didn't create it,
                    // we must return null, so the WebDAV client knows there is no such .tmp file yet, so it can use
                    // this filename for .tmp file storage without messing up any existing process.  But we also have
                    // to create the file, so it's there when the WebDAV client asks to use it (next request it will
                    // send).  This is the behavior observed from Adobe Acrobat Pro DC.
                    log.info("return temp file resource for path [{}]", path);
                    String tempFilename = m.group(6);
                    File tempFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
                    if (!tempFile.exists())
                    {
                        log.info("temp file [{}] doesn't exist yet; touching it, and returning null", tempFilename);
                        try
                        {
                            FileUtils.touch(tempFile);
                        }
                        catch (IOException e)
                        {
                            log.warn("Could not touch temp file [{}]", tempFilename);
                        }
                        return null;
                    }
                    else
                    {
                        log.info("Temp file exists already, returning a temp file resource for [{}]", tempFilename);
                        Long fileId = Long.valueOf(m.group(5));
                        // note, we need the ArkCase file ID in the .tmp file resource, so when it's time to
                        // version the ArkCase file with the .tmp file contents, we know what ArkCase file should be
                        // versioned.
                        String userId = m.group(1);
                        String containerType = m.group(2);
                        String containerId = m.group(3);
                        String fileType = "FILE";
                        String lockType = FileLockType.WRITE.name();
                        return new AcmTempFileResource(
                                tempFilename,
                                host,
                                fileId,
                                fileType,
                                lockType,
                                userId,
                                containerType,
                                containerId,
                                AcmFileSystemResourceFactory.this);
                    }
                }
                else
                {
                    // we got a regular file request, so we create the normal resource instance.
                    log.info("return ArkCase file resource for path [{}]", path);
                    Long fileId = Long.valueOf(m.group(5));
                    long containerRootFolderId = Long.parseLong(m.group(4));
                    String userId = m.group(1);
                    String containerType = m.group(2);
                    String containerId = m.group(3);
                    String fileType = "FILE";
                    String lockType = FileLockType.WRITE.name();
                    String cachePath = path.substring(0, path.lastIndexOf("/"));
                    log.trace("fileId: {}, lock type: {}, fileType: {}", fileId, lockType, fileType);

                    EcmFile ecmFile = getFileDao().find(fileId);

                    log.trace("ecmFile exists? {}", ecmFile != null);
                    getCache().put(cachePath, containerType + "-" + containerId + "-" + containerRootFolderId + "-" + fileId);
                    return new AcmFileResource(host, ecmFile, fileType, lockType, userId, containerType, containerId,
                            AcmFileSystemResourceFactory.this);
                }
            }
            return null;
        }
    }
}

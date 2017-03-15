package com.armedia.acm.webdav;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import io.milton.http.LockManager;
import io.milton.http.ResourceFactory;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public class AcmFileSystemResourceFactory implements ResourceFactory
{

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

    private transient final Logger log = LoggerFactory.getLogger(getClass());
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
        } else
        {
            log.debug("The path {} seems to be an list folder structure request or OPTIONS request", path);
            //FIXME return always root folder, we should fix this to return correct folder, but since url consists of "/" it will be hard to implement
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
        } else
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

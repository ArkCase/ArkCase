package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileDownloadedEvent;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping({"/api/v1/plugin/ecm", "/api/latest/plugin/ecm"})
public class FileDownloadAPIController implements ApplicationEventPublisherAware
{
    private MuleContextManager muleContextManager;

    private EcmFileDao fileDao;

    private ApplicationEventPublisher applicationEventPublisher;

    private FolderAndFilesUtils folderAndFilesUtils;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void downloadFileById(@RequestParam(value = "inline", required = false, defaultValue = "false") boolean inline,
                                 @RequestParam(value = "ecmFileId", required = true, defaultValue = "0") Long fileId,
                                 @RequestParam(value = "acm_email_ticket", required = false, defaultValue = "") String acm_email_ticket,
                                 Authentication authentication, HttpSession httpSession, HttpServletResponse response)
            throws IOException, MuleException, AcmObjectNotFoundException
    {
        if (log.isInfoEnabled())
        {
            log.info("Downloading file by ID '" + fileId + "' for user '" + authentication.getName() + "'");
        }

        EcmFile ecmFile = getFileDao().find(fileId);

        if (ecmFile != null)
        {
            EcmFileDownloadedEvent event = new EcmFileDownloadedEvent(ecmFile);
            event.setIpAddress((String) httpSession.getAttribute("acm_ip_address"));
            event.setUserId(authentication.getName());
            event.setSucceeded(true);

            getApplicationEventPublisher().publishEvent(event);
            String cmisFileId = getFolderAndFilesUtils().getActiveVersionCmisId(ecmFile);
            download(cmisFileId, response, inline, ecmFile);
        } else
        {
            fileNotFound();
        }
    }

    protected void download(String fileId, HttpServletResponse response, boolean isInline, EcmFile ecmFile)
            throws IOException, MuleException, AcmObjectNotFoundException
    {

        MuleMessage downloadedFile = getMuleContextManager().send("vm://downloadFileFlow.in", fileId);

        if (downloadedFile.getPayload() instanceof ContentStream)
        {
            handleFilePayload((ContentStream) downloadedFile.getPayload(), response, isInline, ecmFile);
        } else
        {
            fileNotFound();
        }

    }

    // called for normal processing - file was found
    private void handleFilePayload(ContentStream filePayload, HttpServletResponse response, boolean isInline, EcmFile ecmFile)
            throws IOException
    {

        String mimeType = filePayload.getMimeType();
        String fileName = filePayload.getFileName();

        // endWith will throw a NullPointerException on a null argument.  But a file is not required to have an
        // extension... so the extension can be null.  So we have to guard against it.
        if (ecmFile != null && ecmFile.getFileActiveVersionNameExtension() != null
                && !fileName.endsWith(ecmFile.getFileActiveVersionNameExtension()))
        {
            fileName = fileName + ecmFile.getFileActiveVersionNameExtension();
        }

        InputStream fileIs = null;

        try
        {
            fileIs = filePayload.getStream();
            if (!isInline)
            {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
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
        } finally
        {
            if (fileIs != null)
            {
                try
                {
                    fileIs.close();
                } catch (IOException e)
                {
                    log.error("Could not close CMIS content stream: " + e.getMessage(), e);
                }
            }
        }
    }

    // called when the file was not found.
    private void fileNotFound() throws AcmObjectNotFoundException
    {
        throw new AcmObjectNotFoundException(null, null, "File not found", null);
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
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
}

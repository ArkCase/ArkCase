package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileDownloadedEvent;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping({ "/api/v1/plugin/ecm", "/api/latest/plugin/ecm" })
public class FileDownloadAPIController implements ApplicationEventPublisherAware
{
    private MuleClient muleClient;

    private EcmFileDao fileDao;

    private ApplicationEventPublisher applicationEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/download/byId/{ecmFileId}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadFileById(
            @PathVariable("ecmFileId") Long fileId,
            Authentication authentication,
            HttpSession httpSession,
            HttpServletResponse response
    ) throws IOException, MuleException, AcmObjectNotFoundException
    {
        if (log.isInfoEnabled())
        {
            log.info("Downloading file by ID '" + fileId + "' for user '" + authentication.getName() + "'");
        }

        EcmFile ecmFile = getFileDao().find(fileId);

        if ( ecmFile != null )
        {
            EcmFileDownloadedEvent event = new EcmFileDownloadedEvent(ecmFile);
            event.setIpAddress((String) httpSession.getAttribute("acm_ip_address"));
            event.setUserId(authentication.getName());
            event.setSucceeded(true);

            getApplicationEventPublisher().publishEvent(event);

            download(ecmFile.getEcmFileId(), response);
        }
        else
        {
            fileNotFound(response);
        }
    }

    @RequestMapping(value = "/download/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadFile(
            @PathVariable("fileId") String fileId,
            Authentication authentication,
            HttpSession httpSession,
            HttpServletResponse response
    ) throws IOException, MuleException, AcmObjectNotFoundException
    {
        if (log.isInfoEnabled())
        {
            log.info("Downloading file '" + fileId + "'");
        }

        download(fileId, response);
    }

    /**
     * special help for Alfresco object IDs
     * @return
     */
    @RequestMapping(value = "/download/workspace:/SpacesStore/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadAlfrescoFile(
            @PathVariable("fileId") String fileId,
            Authentication authentication,
            HttpSession httpSession,
            HttpServletResponse response
    ) throws IOException, MuleException, AcmObjectNotFoundException
    {
        if (log.isInfoEnabled())
        {
            log.info("Downloading Alfresco file '" + fileId + "'");
        }

        fileId = "workspace://SpacesStore/" + fileId;

        download(fileId, response);
    }

    protected void download(String fileId, HttpServletResponse response) throws IOException, MuleException, AcmObjectNotFoundException
    {

        MuleMessage downloadedFile = getMuleClient().send("vm://downloadFileFlow.in", fileId, null);

        if ( downloadedFile.getPayload() instanceof ContentStream )
        {
            handleFilePayload((ContentStream) downloadedFile.getPayload(), response);
        }
        else
        {
            fileNotFound(response);
        }

    }

    // called for normal processing - file was found
    private void handleFilePayload(ContentStream filePayload, HttpServletResponse response) throws IOException
    {

        String mimeType = filePayload.getMimeType();
        String fileName = filePayload.getFileName();

        InputStream fileIs = null;

        try
        {
            fileIs = filePayload.getStream();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
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
            if ( fileIs != null )
            {
                try
                {
                    fileIs.close();
                }
                catch (IOException e)
                {
                    log.error("Could not close CMIS content stream: " + e.getMessage(), e);
                }
            }
        }
    }

    // called when the file was not found.
    private void fileNotFound(HttpServletResponse response) throws AcmObjectNotFoundException
    {
        throw new AcmObjectNotFoundException(null, null, "File not found", null);
    }

    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
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
}

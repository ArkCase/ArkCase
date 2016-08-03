package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.apache.tika.mime.MimeTypeException;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileTransactionImpl implements EcmFileTransaction
{
    private MuleContextManager muleContextManager;
    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;
    private FolderAndFilesUtils folderAndFilesUtils;
    private FileEventPublisher fileEventPublisher;
    private SpringContextHolder springContextHolder;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public EcmFile addFileTransaction(String originalFileName, Authentication authentication, String fileType, InputStream fileInputStream,
            String mimeType, String fileName, String cmisFolderId, AcmContainer container) throws MuleException, IOException
    {
        // by default, files are documents
        String category = "Document";
        EcmFile retval = addFileTransaction(originalFileName, authentication, fileType, category, fileInputStream, mimeType, fileName,
                cmisFolderId, container);

        return retval;
    }

    @Override
    public EcmFile addFileTransaction(String originalFileName, Authentication authentication, String fileType, String fileCategory,
            InputStream fileInputStream, String mimeType, String fileName, String cmisFolderId, AcmContainer container)
            throws MuleException, IOException
    {
        // originalFileName = getFolderAndFilesUtils().getBaseFileName(originalFileName);
        // fileName = getFolderAndFilesUtils().getBaseFileName(fileName);

        log.debug("Creating ecm file pipeline context");
        EcmFileTransactionPipelineContext pipelineContext = new EcmFileTransactionPipelineContext();
        pipelineContext.setCmisFolderId(cmisFolderId);
        // we are storing byte array so we can read this stream multiple times
        pipelineContext.setFileByteArray(IOUtils.toByteArray(fileInputStream));
        pipelineContext.setOriginalFileName(originalFileName);
        pipelineContext.setContainer(container);
        pipelineContext.setAuthentication(authentication);

        String[] detectedContentTypeAndExtension = new String[2];

        try
        {
            detectedContentTypeAndExtension = getFolderAndFilesUtils()
                    .detectFileContentTypeAndExtension(new ByteArrayInputStream(pipelineContext.getFileByteArray()), originalFileName);
        } catch (MimeTypeException | IOException e1)
        {
            log.debug("Can not auto detect content type");
        }

        EcmFile ecmFile = new EcmFile();
        // do not change content type in case of freevo
        if (!mimeType.contains("frevvo"))
        {
            ecmFile.setFileActiveVersionMimeType(detectedContentTypeAndExtension[0]);
        }
        ecmFile.setFileActiveVersionNameExtension(detectedContentTypeAndExtension[1]);
        ecmFile.setFileName(fileName);
        ecmFile.setFileType(fileType);
        ecmFile.setCategory(fileCategory);
        try
        {
            log.debug("Calling pipeline manager handlers");
            PipelineManager pipelineManager = (PipelineManager) getSpringContextHolder().getBeanByName("ecmFileUploadPipelineManager",
                    PipelineManager.class);
            pipelineManager.onPreSave(ecmFile, pipelineContext);
            pipelineManager.onPostSave(ecmFile, pipelineContext);
            ecmFile = pipelineContext.getEcmFile();
        } catch (Exception e)
        {
            log.error("pipeline handler call failed: {}", e.getMessage(), e);
        }

        log.debug("Returning from addFileTransaction method");
        return ecmFile;
    }

    @Override
    public EcmFile updateFileTransaction(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream)
            throws MuleException, IOException
    {

        // ecmFile.setFileName(getFolderAndFilesUtils().getBaseFileName(ecmFile.getFileName()));

        log.debug("Creating ecm file pipeline context");
        EcmFileTransactionPipelineContext pipelineContext = new EcmFileTransactionPipelineContext();

        pipelineContext.setAuthentication(authentication);
        pipelineContext.setEcmFile(ecmFile);
        pipelineContext.setFileByteArray(IOUtils.toByteArray(fileInputStream));

        String[] detectedContentTypeAndExtension = new String[2];

        try
        {
            detectedContentTypeAndExtension = getFolderAndFilesUtils()
                    .detectFileContentTypeAndExtension(new ByteArrayInputStream(pipelineContext.getFileByteArray()), ecmFile.getFileName());
        } catch (MimeTypeException | IOException e1)
        {
            log.debug("Can not auto detect content type");
        }

        if (!ecmFile.getFileActiveVersionMimeType().contains("frevvo"))
        {
            ecmFile.setFileActiveVersionMimeType(detectedContentTypeAndExtension[0]);
        }
        ecmFile.setFileActiveVersionNameExtension(detectedContentTypeAndExtension[1]);

        try
        {
            log.debug("Calling pipeline manager handlers");
            PipelineManager pipelineManager = (PipelineManager) getSpringContextHolder().getBeanByName("ecmFileUpdatePipelineManager",
                    PipelineManager.class);
            pipelineManager.onPreSave(ecmFile, pipelineContext);
            pipelineManager.onPostSave(ecmFile, pipelineContext);
            ecmFile = pipelineContext.getEcmFile();
        } catch (Exception e)
        {
            log.error("pipeline handler call failed: {}", e.getMessage(), e);
        }

        log.debug("Returning from updateFileTransaction method");
        return ecmFile;
    }

    @Override
    public EcmFile updateFileTransactionEventAware(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream)
            throws MuleException, IOException
    {
        ecmFile = updateFileTransaction(authentication, ecmFile, fileInputStream);
        String ipAddress = null;
        if (authentication != null)
        {
            if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
            {
                ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
            }
        }

        getFileEventPublisher().publishFileActiveVersionSetEvent(ecmFile, authentication, ipAddress, true);

        return ecmFile;
    }

    @Override
    public String downloadFileTransaction(EcmFile ecmFile) throws MuleException
    {
        try
        {
            MuleMessage message = getMuleContextManager().send("vm://downloadFileFlow.in", ecmFile.getVersionSeriesId());

            String result = getContent((ContentStream) message.getPayload());

            return result;
        } catch (MuleException e)
        {
            log.error("Cannot download file: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public InputStream downloadFileTransactionAsInputStream(EcmFile ecmFile) throws MuleException
    {
        try
        {
            MuleMessage message = getMuleContextManager().send("vm://downloadFileFlow.in", ecmFile.getVersionSeriesId());

            InputStream result = ((ContentStream) message.getPayload()).getStream();

            return result;
        } catch (MuleException e)
        {
            log.error("Cannot download file: " + e.getMessage(), e);
            throw e;
        }
    }

    private String getContent(ContentStream contentStream)
    {
        String content = "";
        InputStream inputStream = null;

        try
        {
            inputStream = contentStream.getStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            content = writer.toString();
        } catch (IOException e)
        {
            log.error("Could not copy input stream to the writer: " + e.getMessage(), e);
        } finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                } catch (IOException e)
                {
                    log.error("Could not close CMIS content stream: " + e.getMessage(), e);
                }
            }
        }

        return content;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public FileEventPublisher getFileEventPublisher()
    {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher)
    {
        this.fileEventPublisher = fileEventPublisher;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }
}
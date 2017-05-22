package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileTransactionImpl implements EcmFileTransaction
{
    private MuleContextManager muleContextManager;
    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;
    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmTikaFileServiceImpl ecmTikaFileService;
    private FileEventPublisher fileEventPublisher;
    private SpringContextHolder springContextHolder;
    private CmisConfigUtils cmisConfigUtils;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public EcmFile addFileTransaction(String originalFileName, Authentication authentication, String fileType, InputStream fileInputStream,
                                      String mimeType, String fileName, String cmisFolderId, AcmContainer container, String cmisRepositoryId) throws MuleException, IOException
    {
        // by default, files are documents
        String category = "Document";
        EcmFile retval = addFileTransaction(originalFileName, authentication, fileType, category, fileInputStream, mimeType, fileName,
                cmisFolderId, container, cmisRepositoryId);

        return retval;
    }


    @Override
    public EcmFile addFileTransaction(String originalFileName, Authentication authentication, String fileType,
                                      String fileCategory, InputStream fileInputStream, String mimeType, String fileName,
                                      String cmisFolderId, AcmContainer container, String cmisRepositoryId)
            throws MuleException, IOException
    {
        Document existingCmisDocument = null;
        return addFileTransaction(originalFileName, authentication, fileType, fileCategory, fileInputStream,
                mimeType, fileName, cmisFolderId, container, cmisRepositoryId, existingCmisDocument);
    }

    @Override
    public EcmFile addFileTransaction(String originalFileName, Authentication authentication, String fileType,
                                      String fileCategory, InputStream fileContents, String fileContentType,
                                      String fileName, String targetCmisFolderId, AcmContainer container,
                                      String cmisRepositoryId, Document existingCmisDocument) throws MuleException, IOException
    {


        log.debug("Creating ecm file pipeline context");
        EcmFileTransactionPipelineContext pipelineContext = buildEcmFileTransactionPipelineContext(authentication,
                fileContents, targetCmisFolderId, container, originalFileName, existingCmisDocument);

        Pair<String, String> mimeTypeAndExtension = buildMimeTypeAndExtension(originalFileName, fileContentType,
                pipelineContext.getFileByteArray());
        String finalMimeType = mimeTypeAndExtension.getLeft();
        String finalExtension = mimeTypeAndExtension.getRight();


        originalFileName = getFolderAndFilesUtils().getBaseFileName(originalFileName, finalExtension);
        pipelineContext.setOriginalFileName(originalFileName);

        fileName = getFolderAndFilesUtils().getBaseFileName(fileName, finalExtension);

        final EcmFile ecmFile = buildEcmFileForUploadTransaction(fileType, fileCategory, finalMimeType,
                fileName, finalExtension, cmisRepositoryId);
        try
        {
            log.debug("Calling pipeline manager handlers");
            PipelineManager pipelineManager = getSpringContextHolder().getBeanByName("ecmFileUploadPipelineManager",
                    PipelineManager.class);
            pipelineManager.executeOperation(ecmFile, pipelineContext, () -> ecmFile);
        } catch (Exception e)
        {
            log.error("pipeline handler call failed: {}", e.getMessage(), e);
        }

        log.debug("Returning from addFileTransaction method");
        return pipelineContext.getEcmFile();
    }


    protected Pair<String, String> buildMimeTypeAndExtension(String filename, String mimeType, byte[] fileByteArray)
    {
        String finalMimeType;
        String finalExtension;

        try
        {
            EcmTikaFile ecmTikaFile = getEcmTikaFileService().detectFileUsingTika(new ByteArrayInputStream(fileByteArray),
                    filename);
            finalMimeType = ecmTikaFile.getContentType();
            finalExtension = ecmTikaFile.getNameExtension();

        } catch (MimeTypeException | IOException e1)
        {
            finalMimeType = mimeType;
            finalExtension = getFolderAndFilesUtils().getFileNameExtension(filename);
        }

        // do not change content type in case of freevo
        if (mimeType.contains("frevvo"))
        {
            finalMimeType = mimeType;
        }

        return Pair.of(finalMimeType, finalExtension);
    }

    protected EcmFile buildEcmFileForUploadTransaction(String fileType, String fileCategory, String mimeType,
                                                       String fileName, String fileExtension, String cmisRepositoryId,
                                                       Object... otherArgs)
    {
        final EcmFile ecmFile = new EcmFile();

        ecmFile.setFileActiveVersionMimeType(mimeType);

        ecmFile.setFileActiveVersionNameExtension(fileExtension);
        ecmFile.setFileName(fileName);
        ecmFile.setFileType(fileType);
        ecmFile.setCategory(fileCategory);
        ecmFile.setCmisRepositoryId(cmisRepositoryId);
        return ecmFile;
    }

    protected EcmFileTransactionPipelineContext buildEcmFileTransactionPipelineContext(Authentication authentication,
                                                                                       InputStream fileInputStream,
                                                                                       String cmisFolderId,
                                                                                       AcmContainer container,
                                                                                       String filename,
                                                                                       Document existingCmisDocument,
                                                                                       Object... otherArgs) throws IOException
    {
        EcmFileTransactionPipelineContext pipelineContext = new EcmFileTransactionPipelineContext();
        pipelineContext.setCmisFolderId(cmisFolderId);
        // we are storing byte array so we can read this stream multiple times
        pipelineContext.setFileByteArray(IOUtils.toByteArray(fileInputStream));
        pipelineContext.setContainer(container);
        pipelineContext.setAuthentication(authentication);
        pipelineContext.setOriginalFileName(filename);
        pipelineContext.setCmisDocument(existingCmisDocument);
        return pipelineContext;
    }

    @Override
    public EcmFile updateFileTransaction(Authentication authentication, final EcmFile ecmFile, InputStream fileInputStream)
            throws MuleException, IOException
    {

        if (ecmFile.getFileActiveVersionNameExtension() != null
                && ecmFile.getFileName().endsWith(ecmFile.getFileActiveVersionNameExtension()))
        {
            ecmFile.setFileName(getFolderAndFilesUtils().getBaseFileName(ecmFile.getFileName()));
        }

        log.debug("Creating ecm file pipeline context");
        EcmFileTransactionPipelineContext pipelineContext = new EcmFileTransactionPipelineContext();

        pipelineContext.setAuthentication(authentication);
        pipelineContext.setEcmFile(ecmFile);
        pipelineContext.setFileByteArray(IOUtils.toByteArray(fileInputStream));

        EcmTikaFile ecmTikaFile = new EcmTikaFile();

        try
        {
            ecmTikaFile = getEcmTikaFileService().detectFileUsingTika(new ByteArrayInputStream(pipelineContext.getFileByteArray()),
                    ecmFile.getFileName());
        } catch (MimeTypeException | IOException e1)
        {
            log.debug("Can not auto detect file using Tika");
            ecmTikaFile.setContentType(ecmFile.getFileActiveVersionMimeType());
            ecmTikaFile.setNameExtension(getFolderAndFilesUtils().getFileNameExtension(ecmFile.getFileName()));
        }

        if (!ecmFile.getFileActiveVersionMimeType().contains("frevvo"))
        {
            ecmFile.setFileActiveVersionMimeType(ecmTikaFile.getContentType());
        }
        ecmFile.setFileActiveVersionNameExtension(ecmTikaFile.getNameExtension());

        try
        {
            log.debug("Calling pipeline manager handlers");
            PipelineManager pipelineManager = (PipelineManager) getSpringContextHolder().getBeanByName("ecmFileUpdatePipelineManager",
                    PipelineManager.class);
            pipelineManager.executeOperation(ecmFile, pipelineContext, () ->
            {
                return ecmFile;
            });
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
            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(ecmFile.getCmisRepositoryId()));
            MuleMessage message = getMuleContextManager().send("vm://downloadFileFlow.in", ecmFile.getVersionSeriesId(), messageProps);

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
            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(ecmFile.getCmisRepositoryId()));
            MuleMessage message = getMuleContextManager().send("vm://downloadFileFlow.in", ecmFile.getVersionSeriesId(), messageProps);

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

    public EcmTikaFileServiceImpl getEcmTikaFileService()
    {
        return ecmTikaFileService;
    }

    public void setEcmTikaFileService(EcmTikaFileServiceImpl ecmTikaFileService)
    {
        this.ecmTikaFileService = ecmTikaFileService;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }
}

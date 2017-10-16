package com.armedia.acm.ephesoft.service;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.capture.AbstractConvertFileEvent;
import com.armedia.acm.files.capture.CaptureConstants;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.web.api.MDCConstants;
import liquibase.util.file.FilenameUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

import javax.activation.MimetypesFileTypeMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by nebojsha on 15/9/2015.
 */
public class AttachmentCaptureFileListener implements ApplicationListener<AbstractConvertFileEvent>
{
    private final String PARENT_ID_PARENT_TYPE_FILE_ID_PATTERN = "^\\d+_.+_\\d+$";
    private final String PARENT_ID_FILE_ID_PATTERN = "^\\d+_\\d+$";
    private final String FILE_ID_PATTERN = "^\\d+$";

    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private FileObject errorFolder;
    private FileObject convertedFolder;

    @Override
    public void onApplicationEvent(AbstractConvertFileEvent event)
    {
        FileInfo fileInfo = null;

        if ( (fileInfo = getSupported(event)) != null )
        {
            log.debug("File {} is supported for attachment processing!", event.getBaseFileName());

            processAttachment(event, fileInfo);

            log.debug("File {} finished processing!", event.getBaseFileName());
        } else
        {
            log.info("File {} is not supported for processing!", event.getBaseFileName());
        }
    }

    private void processAttachment(AbstractConvertFileEvent event, FileInfo fileInfo)
    {
        auditPropertyEntityAdapter.setUserId(CaptureConstants.PROCESS_ATTACHMENTS_USER);
        EcmFile file = ecmFileService.findById(fileInfo.getFileId());

        if ( file != null )
        {
            //verify that all information is correct
            if ( fileInfo.getParentObjectType() != null && !file.getContainer().getContainerObjectType().equalsIgnoreCase(fileInfo.getParentObjectType()) )
            {
                log.warn("unable to process File {}, reason: parent object type doesn't match. Contains in file name:{}, but should be {}.",
                        event.getBaseFileName(),
                        fileInfo.getParentObjectType(),
                        file.getContainer().getContainerObjectType());
                moveToFolder(event.getConvertedFile(), errorFolder);
                return;
            }
            if ( fileInfo.getParentObjectId() != null && !file.getContainer().getContainerObjectId().equals(fileInfo.getParentObjectId()) )
            {
                log.warn("unable to process File {}, reason: parent object id doesn't match. Contains in file name:{}, but should be {}.",
                        event.getBaseFileName(),
                        fileInfo.getParentObjectId(),
                        file.getContainer().getContainerObjectId());
                moveToFolder(event.getConvertedFile(), errorFolder);
                return;
            }

            //everything is fine just upload the document
            try
            {
                saveAttachment(file, event.getConvertedFile());
                log.debug("successfully processed File {}, ready for deletion.", event.getBaseFileName());
                event.getConvertedFile().delete();
                log.info("successfully deleted File {} after uploading.", event.getBaseFileName());
            } catch (Exception e)
            {
                log.error("file movement was not successful: {}", e.getMessage(), e);
                moveToFolder(event.getConvertedFile(), errorFolder);
            }
        } else
        {
            log.warn("unable to process File {}, reason: doesn't exists in the database.", event.getBaseFileName());
            moveToFolder(event.getConvertedFile(), errorFolder);
        }

    }

    private File moveToFolder(File file, FileObject folder)
    {
        try
        {
            File parentFolder = new File(new URI(folder.toString().replace(" ", "%20")));
            File workingFile = new File(parentFolder, file.getName());

            FileUtils.moveFile(file, workingFile);

            return workingFile;
        } catch (Exception e)
        {
            log.error("Cannot move {} to {} directory: {}", file.getName(), folder.getName(), e.getMessage());
        }
        return null;
    }


    private FileInfo getSupported(AbstractConvertFileEvent event)
    {
        String fileName = event.getBaseFileName();


        //checks for null and empty string
        if ( StringUtils.isEmpty(fileName) )
            return null;

        if ( !fileName.toLowerCase().endsWith(".pdf") )
            return null;

        //remove extension in file name
        if ( fileName.contains(".") )
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));

        //ephesoft processing always adds _DOC1 at the end, so we are removing just to extract the information about object and his parent.
        if ( fileName.endsWith("_DOC1") )
            fileName = fileName.substring(0, fileName.lastIndexOf('_'));

        //matches files with name like 123123_case_file_123 OR 12313_123 or 123
        if ( Pattern.matches(PARENT_ID_PARENT_TYPE_FILE_ID_PATTERN, fileName) )
            return parseParentIdParentTypeFileIdPattern(fileName);
        else if ( Pattern.matches(PARENT_ID_FILE_ID_PATTERN, fileName) )
            return parseParentIdFileIdPattern(fileName);
        else if ( Pattern.matches(FILE_ID_PATTERN, fileName) )
            return parseFileIdPattern(fileName);
        else
            return null;
    }

    private FileInfo parseFileIdPattern(String fileName)
    {
        FileInfo fInfo = new FileInfo();
        fInfo.setFileId(Long.parseLong(fileName));
        return fInfo;
    }

    private FileInfo parseParentIdFileIdPattern(String fileName)
    {
        String[] split = fileName.split("_");
        FileInfo fInfo = new FileInfo();
        fInfo.setFileId(Long.parseLong(split[0]));
        fInfo.setFileId(Long.parseLong(split[1]));
        return fInfo;
    }

    private FileInfo parseParentIdParentTypeFileIdPattern(String fileName)
    {
        //its better to use substring cause in object_type can have '_' and split is not usable
        FileInfo fInfo = new FileInfo();
        int firstIndex = fileName.indexOf("_");
        int lastIndex = fileName.lastIndexOf("_");

        fInfo.setFileId(Long.parseLong(fileName.substring(lastIndex + 1)));
        fInfo.setParentObjectId(Long.parseLong(fileName.substring(0, firstIndex)));
        fInfo.setParentObjectType(fileName.substring(firstIndex + 1, lastIndex));

        return fInfo;
    }

    private void saveAttachment(EcmFile originalFile, File toBeUploaded) throws Exception
    {
        try
        {
            // This will help us to recognize content type
            MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

            // Take the input stream for given file
            InputStream originalIS = new BufferedInputStream(new FileInputStream(toBeUploaded));
            byte[] bytes = IOUtils.toByteArray(originalIS);

            // Create clone of the input stream and close the original. We need this to be able to release
            // original input stream to be able to move files through folders
            InputStream cloneIS = new ByteArrayInputStream(bytes);
            originalIS.close();

            // Take content type and create authentication object (we need authentication object for
            // EcmFileService - we need userID which in this case is set to CaptureConstants.XML_BATCH_USER value)
            String contentType = mimetypesFileTypeMap.getContentType(toBeUploaded);
            Authentication auth = new AcmAuthentication(null, null, null, true, CaptureConstants.PROCESS_ATTACHMENTS_USER);

            // Create multipart file object - used "upload" service require it and using this service method is the best
            // way to upload file for given object - it creates AcmContainer object that we need for uploading
            String fileName = FilenameUtils.removeExtension(toBeUploaded.getName()) + ".pdf";

            AcmMultipartFile file = new AcmMultipartFile(
                    fileName,
                    fileName,
                    contentType,
                    false,
                    toBeUploaded.length(),
                    bytes,
                    cloneIS,
                    true);

            // set the Alfresco user name, so we can upload the files.
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

            // Upload file
            getEcmFileService().upload(fileName,
                    originalFile.getFileType(),
                    file,
                    auth,
                    originalFile.getContainer().getFolder().getCmisFolderId(),
                    originalFile.getContainer().getContainerObjectType(),
                    originalFile.getContainer().getContainerObjectId());
        } catch (Throwable e)
        {
            throw new Exception("Cannot save attachment: " + e.getMessage(), e);
        }
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public void setConvertedFolder(FileObject convertedFolder)
    {
        this.convertedFolder = convertedFolder;
    }

    public void setErrorFolder(FileObject errorFolder)
    {
        this.errorFolder = errorFolder;
    }

    class FileInfo
    {
        private Long fileId;
        private Long parentObjectId;
        private String parentObjectType;

        public Long getFileId()
        {
            return fileId;
        }

        public void setFileId(Long fileId)
        {
            this.fileId = fileId;
        }

        public Long getParentObjectId()
        {
            return parentObjectId;
        }

        public void setParentObjectId(Long parentObjectId)
        {
            this.parentObjectId = parentObjectId;
        }

        public String getParentObjectType()
        {
            return parentObjectType;
        }

        public void setParentObjectType(String parentObjectType)
        {
            this.parentObjectType = parentObjectType;
        }
    }
}

package com.armedia.acm.ephesoft.service;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.capture.AbstractCaptureFileEvent;
import com.armedia.acm.files.capture.CaptureConstants;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

import javax.activation.MimetypesFileTypeMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by nebojsha on 15/9/2015.
 */
public class AttachmentCaptureFileListener implements ApplicationListener<AbstractCaptureFileEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private FileObject errorFolder;
    private FileObject captureFolder;
    private FileObject completedFolder;
    private Set<String> supportedObjectTypes;

    @Override
    public void onApplicationEvent(AbstractCaptureFileEvent event)
    {
        FileInfo fileInfo = null;
        if ((fileInfo = isSupported(event)) != null)
        {
            log.debug("File {} is supported for processing!", event.getBaseFileName());

            processAttachment(event, fileInfo);

            log.debug("File {} finished processing!", event.getBaseFileName());
        } else
        {
            log.debug("File {} is not supported for processing!", event.getBaseFileName());
        }
    }

    private void processAttachment(AbstractCaptureFileEvent event, FileInfo fileInfo)
    {


        EcmFile file = ecmFileService.findById(fileInfo.getFileId());

        if (file != null)
        {
            //verify that all information is correct
            if (file.getContainer().getContainerObjectType().toLowerCase().compareTo(fileInfo.getParentObjectType().toLowerCase()) != 0)
            {
                log.warn("unable to process File {}, reason: parent object type doesn't match. Contains in file name:{}, but should be {}.",
                        event.getBaseFileName(),
                        fileInfo.getParentObjectType(),
                        file.getContainer().getContainerObjectType());
                moveToFolder(event.getCaptureFile(), errorFolder);
                return;
            }
            if (!file.getContainer().getContainerObjectId().equals(fileInfo.getParentObjectId()))
            {
                log.warn("unable to process File {}, reason: parent object id doesn't match. Contains in file name:{}, but should be {}.",
                        event.getBaseFileName(),
                        fileInfo.getParentObjectId(),
                        file.getContainer().getContainerObjectId());
                moveToFolder(event.getCaptureFile(), errorFolder);
                return;
            }

            //everything is fine just upload the document
            try
            {
                saveAttachment(file.getContainer(), event.getCaptureFile(), file.getFileName());
                log.debug("successfully processed File {}, moving to completed folder.", event.getBaseFileName());
                moveToFolder(event.getCaptureFile(), completedFolder);
                log.info("successfully moved File {} to completed folder.", event.getBaseFileName());
            } catch (Exception e)
            {
                log.error("file movement was not sucessfull: {}", e.getMessage(), e);
                moveToFolder(event.getCaptureFile(), errorFolder);
            }
        } else
        {
            log.warn("unable to process File {}, reason: doesn't exists in the database.", event.getBaseFileName());
            moveToFolder(event.getCaptureFile(), errorFolder);
        }

    }

    private File moveToFolder(File file, FileObject folder)
    {
        try
        {
            File workingFile = new File(folder.getURL().toString().replace("file:///", "") + File.separator + file.getName());

            FileUtils.moveFile(file, workingFile);

            return workingFile;
        } catch (Exception e)
        {
            log.error("Cannot move {} to {} directory: {}", file.getName(), folder.getName(), e.getMessage());
        }
        return null;
    }


    private FileInfo isSupported(AbstractCaptureFileEvent event)
    {


        FileInfo fileInfo = new FileInfo();
        String fileName = event.getBaseFileName();
        if (fileName.contains("."))
            fileName = fileName.substring(0, fileName.indexOf('.'));

        String parentObjectIdStr = extractParentObjectIdStr(fileName);
        String fileIdStr = extractFileIdStr(fileName);
        String parentObjectType = extractParentObjectType(fileName);

        if (fileIdStr == null || parentObjectIdStr == null || parentObjectType == null)
        {
            return null;
        }

        //verify object_type
        Optional<String> found = supportedObjectTypes.stream().filter(s -> parentObjectType.toLowerCase().compareTo(s) == 0).findFirst();
        if (!found.isPresent())
            return null;

        //verify attachment parent id exists and is only numbers
        if (parentObjectIdStr.length() < 1 || parentObjectIdStr.replaceAll("\\d", "").length() > 0)
            return null;
        //verify attachment id exists and is only numbers
        if (fileIdStr.length() < 1 || fileIdStr.replaceAll("\\d", "").length() > 0)
            return null;

        fileInfo.setParentObjectId(Long.parseLong(parentObjectIdStr));
        fileInfo.setParentObjectType(parentObjectType);
        fileInfo.setFileId(Long.parseLong(fileIdStr));

        return fileInfo;
    }

    private String extractFileIdStr(String fileName)
    {
        if (!fileName.contains("_"))
            return null;
        return fileName.substring(fileName.lastIndexOf('_') + 1);
    }

    private String extractParentObjectIdStr(String fileName)
    {
        if (!fileName.contains("_"))
            return null;
        return fileName.substring(0, fileName.indexOf('_'));
    }


    private String extractParentObjectType(String fileName)
    {
        if (!fileName.contains("_"))
            return null;
        return fileName.substring(fileName.indexOf('_') + 1, fileName.lastIndexOf('_'));
    }

    private void saveAttachment(AcmContainer container, File toBeUploaded, String originalFileName) throws Exception
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
            AcmMultipartFile file = new AcmMultipartFile(
                    toBeUploaded.getName(),
                    originalFileName,
                    contentType,
                    false,
                    toBeUploaded.length(),
                    bytes,
                    cloneIS,
                    true);

            // Upload file
            getEcmFileService().upload(originalFileName,
                    "pdf",
                    file,
                    auth,
                    container.getFolder().getCmisFolderId(),
                    container.getContainerObjectType(),
                    container.getContainerObjectId());
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

    public void setCaptureFolder(FileObject captureFolder)
    {
        this.captureFolder = captureFolder;
    }

    public void setCompletedFolder(FileObject completedFolder)
    {
        this.completedFolder = completedFolder;
    }

    public void setErrorFolder(FileObject errorFolder)
    {
        this.errorFolder = errorFolder;
    }

    public void setSupportedObjectTypes(String supportedObjectTypesStr)
    {
        if (supportedObjectTypesStr != null && supportedObjectTypesStr.length() > 0)
        {
            supportedObjectTypes = Arrays.stream(supportedObjectTypesStr.split(",[\\s]*")).collect(Collectors.toSet());
        } else
        {
            supportedObjectTypes = new HashSet<>();
        }
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

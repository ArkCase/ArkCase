package com.armedia.acm.plugins.report.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.report.model.FileProperties;
import com.armedia.acm.plugins.report.model.ScheduleReportConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

/**
 * Created by joseph.mcgrady on 6/13/2017.
 */
public class UploadGeneratedReportService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadGeneratedReportService.class);
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private DocumentRepositoryDao documentRepositoryDao;
    private EcmFileDao ecmFileDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private String uploadUserId;
    private String reportDocumentRepository;

    @Transactional
    public EcmFile uploadReport(InputStream reportDataStream, FileProperties fileProperties)
            throws AcmObjectNotFoundException
    {
        DocumentRepository documentRepository = getDocumentRepositoryDao().findByName(getReportDocumentRepository());
        if (documentRepository != null)
        {
            Authentication auth = new UsernamePasswordAuthenticationToken(getUploadUserId(), "SYSTEM");
            getAuditPropertyEntityAdapter().setUserId(getUploadUserId());
            AcmContainer container = documentRepository.getContainer();
            AcmFolder yearFolder = null;
            EcmFile ecmFile = null;
            try
            {
                yearFolder = findOrCreateYearFolder(container);
                String fileName = getUniqueFileName(yearFolder, fileProperties);
                ecmFile = getEcmFileService().upload(fileName, "Other", "Document",
                        reportDataStream, ScheduleReportConstants.EXCEL_MIMETYPE, fileName,
                        auth, yearFolder.getCmisFolderId(),
                        container.getContainerObjectType(), container.getContainerObjectId());
            } catch (AcmCreateObjectFailedException e)
            {
                LOGGER.error("Fail to create object", e);
            } catch (AcmUserActionFailedException e)
            {
                LOGGER.error("User action fail", e);
            }
            return ecmFile;
        } else
        {
            throw new AcmObjectNotFoundException("Document Repository", null, "Failed to find report document repository");
        }
    }

    public AcmFolder findOrCreateYearFolder(AcmContainer container)
    {
        AcmFolder yearFolder = null;
        if (container != null)
        {
            String year = "" + LocalDateTime.now().getYear();
            Optional<AcmFolder> folderFound = container.getFolder().getChildrenFolders().stream().filter(f -> year.equals(f.getName())).findAny();
            if (folderFound.isPresent())
            {
                yearFolder = folderFound.get();
            } else
            {
                try
                {
                    yearFolder = acmFolderService.addNewFolder(container.getFolder().getId(), year);
                } catch (AcmCreateObjectFailedException e)
                {
                    LOGGER.error("Year folder creation failed", e);
                } catch (AcmUserActionFailedException e)
                {
                    LOGGER.error("User action failed", e);
                } catch (AcmObjectNotFoundException e)
                {
                    LOGGER.error("Year folder not found", e);
                }
            }
        }
        return yearFolder;
    }

    private String getUniqueFileName(AcmFolder acmFolder, FileProperties fileProperties)
    {
        String fileName = generateFileName(fileProperties);
        String nameNoExtension = fileName.substring(0, fileName.lastIndexOf("."));
        String extension = fileName.substring(fileName.lastIndexOf("."));
        EcmFile[] filesFound = getEcmFileDao().findByFolderId(acmFolder.getId()).stream().filter(f -> f.getFileName().contains(nameNoExtension)).toArray(EcmFile[]::new);
        if (filesFound.length > 0)
        {
            fileName = nameNoExtension + "-" + filesFound.length + extension;
        }
        return fileName;
    }

    private String generateFileName(FileProperties fileProperties)
    {
        Date reportGeneratedDate = Date.from(Instant.ofEpochMilli(Long.parseLong(fileProperties.getCreatedDate())));
        SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fileDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return fileDateFormat.format(reportGeneratedDate) + " " + fileProperties.getName();
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public DocumentRepositoryDao getDocumentRepositoryDao()
    {
        return documentRepositoryDao;
    }

    public void setDocumentRepositoryDao(DocumentRepositoryDao documentRepositoryDao)
    {
        this.documentRepositoryDao = documentRepositoryDao;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public String getUploadUserId()
    {
        return uploadUserId;
    }

    public void setUploadUserId(String uploadUserId)
    {
        this.uploadUserId = uploadUserId;
    }

    public String getReportDocumentRepository()
    {
        return reportDocumentRepository;
    }

    public void setReportDocumentRepository(String reportDocumentRepository)
    {
        this.reportDocumentRepository = reportDocumentRepository;
    }
}
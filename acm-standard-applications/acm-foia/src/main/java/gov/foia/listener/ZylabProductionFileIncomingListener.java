package gov.foia.listener;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.exemption.service.DocumentExemptionService;
import com.armedia.acm.services.zylab.model.ZylabFile;
import com.armedia.acm.services.zylab.model.ZylabFileMetadata;
import com.armedia.acm.services.zylab.service.ZylabEventPublisher;
import com.armedia.acm.services.zylab.service.ZylabProductionUtils;
import com.armedia.acm.tool.zylab.exception.ZylabProductionSyncException;
import com.armedia.acm.tool.zylab.model.ZylabIntegrationConfig;
import com.armedia.acm.tool.zylab.model.ZylabProductionFileIncomingEvent;
import com.armedia.acm.tool.zylab.service.ZylabIntegrationService;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIAFile;
import gov.foia.model.FOIARequest;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on March, 2021
 */
public class ZylabProductionFileIncomingListener implements ApplicationListener<ZylabProductionFileIncomingEvent>
{
    public static final String ZYLAB_INTEGRATION_SYSTEM_USER = "ZYLAB-INTEGRATION-SYSTEM-USER";
    public static final String RESPONSIVE_FOLDER_NAME = "Release";
    public static final String WITHELD_FOLDER_NAME = "Exempt Withheld";
    public static final String EXEMPTION_WITHELD_CODES_SEPARATOR = ",";
    public static final String RESPONSIVE_CODES_SEPARATOR = ";";
    private transient final Logger log = LogManager.getLogger(getClass());

    private AcmFolderService acmFolderService;
    private EcmFileService ecmFileService;
    private ZylabIntegrationService zylabIntegrationService;
    private ZylabIntegrationConfig zylabIntegrationConfig;
    private FOIARequestDao foiaRequestDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private String workingFolderName;
    private DocumentExemptionService documentExemptionService;
    private LookupDao lookupDao;
    private ZylabEventPublisher zylabEventPublisher;

    @Override
    public void onApplicationEvent(ZylabProductionFileIncomingEvent fileIncomingEvent)
    {
        if (getZylabIntegrationConfig().isEnabled())
        {
            Authentication auth = setCredentials();

            Long matterId = fileIncomingEvent.getMatterId();
            String productionKey = fileIncomingEvent.getProductionKey();
            FOIARequest foiaRequest = findRequestForMatter(matterId, productionKey, auth);

            try
            {
                if (foiaRequest != null)
                {
                    uploadZylabProductionToRequest(foiaRequest, matterId, productionKey, auth);
                }
            }
            catch (Exception e)
            {
                getZylabEventPublisher().publishProductionFailedEvent(foiaRequest, foiaRequest.getId(), foiaRequest.getObjectType(),
                        matterId, productionKey, e.getMessage(), ExceptionUtils.getStackTrace(e), auth);

                log.error("Processing of production [{}] unsuccessful for matter [{}]", matterId,
                        productionKey, e);
            }
        }
    }

    private FOIARequest findRequestForMatter(Long matterId, String productionKey, Authentication auth)
    {
        FOIARequest foiaRequest = null;
        try
        {
            foiaRequest = foiaRequestDao.findByExternalIdentifier(String.valueOf(matterId));
        }
        catch (NoResultException e)
        {
            String error = "No associated request found for ZyLAB Matter ID: " + matterId;
            getZylabEventPublisher().publishProductionFailedEvent(matterId, null, null,
                    matterId, productionKey, error, ExceptionUtils.getStackTrace(e), auth);

            log.error(error, e);
        }
        catch (NonUniqueResultException e)
        {
            String error = "Multiple requests found for ZyLAB Matter ID: " + matterId;
            getZylabEventPublisher().publishProductionFailedEvent(null, null, null,
                    matterId, productionKey, error, ExceptionUtils.getStackTrace(e), auth);

            log.error(error, e);
        }
        return foiaRequest;
    }

    /**
     *
     * The code is triggered by a JMS message, there is no authenticated user, so we need to specify a user
     * for CMIS connections and DB entries
     *
     * @return Zylab Integration System User Authentication
     */
    private Authentication setCredentials()
    {
        getAuditPropertyEntityAdapter().setUserId(ZYLAB_INTEGRATION_SYSTEM_USER);

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, ZYLAB_INTEGRATION_SYSTEM_USER);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        return new UsernamePasswordAuthenticationToken(ZYLAB_INTEGRATION_SYSTEM_USER, "");
    }

    public void uploadZylabProductionToRequest(FOIARequest foiaRequest, long matterId, String productionKey, Authentication auth)
            throws ZylabProductionSyncException
    {
        File uncompressedProductionFolder = getZylabIntegrationService().getZylabProductionFolder(matterId, productionKey);

        List<File> productionFiles = (List<File>) FileUtils.listFiles(uncompressedProductionFolder,
                FileFilterUtils.trueFileFilter(),
                FileFilterUtils.trueFileFilter());

        File loadFile = ZylabProductionUtils.getLoadFile(matterId, productionKey, productionFiles);

        List<ZylabFileMetadata> zylabFileMetadataList = ZylabProductionUtils.getFileMetadataFromLoadFile(loadFile, matterId,
                productionKey);
        List<ZylabFile> zylabFiles = ZylabProductionUtils.linkMetadataToZylabFiles(productionFiles, zylabFileMetadataList);

        AcmFolder newProductionFolder = null;

        try
        {
            List<ZylabFile> exemptWithheldFiles = zylabFiles.stream()
                    .filter(zylabFile -> zylabFile.getFileMetadata().getExemptWithheld())
                    .collect(Collectors.toList());
            List<ZylabFile> responsiveFiles = zylabFiles.stream()
                    .filter(zylabFile -> !zylabFile.getFileMetadata().getExemptWithheld())
                    .collect(Collectors.toList());

            log.info("Creating new production folder structure for production [{}]", productionKey);

            AcmFolder rootFolder = foiaRequest.getContainer().getFolder();
            AcmFolder workingFolder = getAcmFolderService().getSubfolderByName(rootFolder, workingFolderName);

            String productionFolderName = FilenameUtils.getBaseName(loadFile.getName());
            newProductionFolder = getAcmFolderService().addNewFolder(workingFolder, productionFolderName);

            if (!responsiveFiles.isEmpty())
            {
                log.info("Uploading responsive files from ZyLAB production [{}]", productionKey);
                AcmFolder responsiveFolder = getAcmFolderService().addNewFolder(newProductionFolder, RESPONSIVE_FOLDER_NAME);
                List<EcmFile> arkResponsiveFiles = uploadZylabFiles(responsiveFolder, responsiveFiles, foiaRequest, auth);
                addExemptionCodesToResponsiveFiles(arkResponsiveFiles);
            }

            if (!exemptWithheldFiles.isEmpty())
            {
                log.info("Uploading exempt witheld files from ZyLAB production [{}]", productionKey);
                AcmFolder exemptWithheldFolder = getAcmFolderService().addNewFolder(newProductionFolder, WITHELD_FOLDER_NAME);
                List<EcmFile> arkExemptWitheldFiles = uploadZylabFiles(exemptWithheldFolder, exemptWithheldFiles, foiaRequest, auth);
                addExemptionCodesToWitheldFiles(arkExemptWitheldFiles);
            }

            log.info("ZyLAB Production files for request [{}] processed successfully", foiaRequest.getId());

            getZylabEventPublisher().publishProductionSucceededEvent(foiaRequest, foiaRequest.getId(), foiaRequest.getObjectType(),
                    matterId, productionKey, auth);
        }
        catch (Exception e)
        {
            log.error("Uploading Zylab production files to Arkcase failed", e);
            if (newProductionFolder != null)
            {
                try
                {
                    getAcmFolderService().deleteFolderTree(newProductionFolder.getId(), auth);
                }
                catch (AcmUserActionFailedException | AcmObjectNotFoundException acmUserActionFailedException)
                {
                    log.warn("Deleting root production folder for Production [{}], Matter [{}] failed", productionKey, matterId);
                }
            }
            throw new ZylabProductionSyncException("Uploading Zylab production files to Arkcase failed", e);
        }
        finally
        {
            log.info("Deleting temporary folder for Production [{}], Matter [{}]", productionKey, matterId);
            getZylabIntegrationService().cleanupTemporaryProductionFiles(uncompressedProductionFolder);
        }

    }

    private List<EcmFile> uploadZylabFiles(AcmFolder parentFolder, List<ZylabFile> zylabFiles, FOIARequest foiaRequest, Authentication auth)
            throws IOException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        List<EcmFile> uploadedFiles = new ArrayList<>();

        for (ZylabFile zylabFile : zylabFiles)
        {
            AcmMultipartFile multipartFile = new AcmMultipartFile(getMultipartFile(zylabFile), false);
            EcmFile uploadedFile = getEcmFileService().upload(auth, multipartFile, parentFolder.getCmisFolderId(),
                    foiaRequest.getObjectType(),
                    foiaRequest.getId(), getEcmFile(zylabFile));
            uploadedFiles.add(uploadedFile);
        }

        return uploadedFiles;
    }

    private CommonsMultipartFile getMultipartFile(ZylabFile zylabFile) throws IOException
    {
        File file = zylabFile.getFile();
        DiskFileItem fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false,
                zylabFile.getFileMetadata().getName(), (int) file.length(), file.getParentFile());

        try (InputStream docStream = new FileInputStream(file);
                OutputStream outputStream = fileItem.getOutputStream())
        {
            FileCopyUtils.copy(docStream, outputStream);
        }

        return new CommonsMultipartFile(fileItem);
    }

    private FOIAFile getEcmFile(ZylabFile zylabFile) throws IOException
    {
        FOIAFile metadata = new FOIAFile();
        metadata.setFileName(zylabFile.getFileMetadata().getName() + "." + FilenameUtils.getExtension(zylabFile.getFile().getPath()));
        metadata.setFileType("Other");
        metadata.setFileActiveVersionMimeType(Files.probeContentType(zylabFile.getFile().toPath()));
        metadata.setCustodian(zylabFile.getFileMetadata().getCustodian());
        metadata.setZylabFileMetadata(zylabFile.getFileMetadata());
        return metadata;
    }

    private void addExemptionCodesToResponsiveFiles(List<EcmFile> files)
    {
        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) lookupDao.getLookupByName("annotationTags").getEntries();

        List<EcmFile> redactionJustificationFiles = files.stream()
                .filter(containsResponsiveFileExemptions)
                .collect(Collectors.toList());

        for (EcmFile file : redactionJustificationFiles)
        {
            String[] redactionJustificationExemptions = file.getZylabFileMetadata().getRedactionJustification()
                    .split(RESPONSIVE_CODES_SEPARATOR);
            String[] redactionCodeExemptions = file.getZylabFileMetadata().getRedactionCode1().split(RESPONSIVE_CODES_SEPARATOR);
            int[] exemptionCodes = Stream.concat(Arrays.stream(redactionJustificationExemptions), Arrays.stream(redactionCodeExemptions))
                    .filter(NumberUtils::isParsable)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            addExemptionCodesToFileByOrder(file, exemptionCodes, lookupEntries);
        }
    }

    private Predicate<EcmFile> containsResponsiveFileExemptions = ecmFile -> StringUtils
            .isNotBlank(ecmFile.getZylabFileMetadata().getRedactionJustification())
            || StringUtils.isNotBlank(ecmFile.getZylabFileMetadata().getRedactionCode1());

    private void addExemptionCodesToWitheldFiles(List<EcmFile> files)
    {
        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) lookupDao.getLookupByName("annotationTags").getEntries();

        List<EcmFile> redactionJustificationFiles = files.stream()
                .filter(ecmFile -> !ecmFile.getZylabFileMetadata().getExemptWithheldReason().isEmpty())
                .collect(Collectors.toList());

        for (EcmFile file : redactionJustificationFiles)
        {
            String[] exemptionCodesSent = file.getZylabFileMetadata().getExemptWithheldReason().split(EXEMPTION_WITHELD_CODES_SEPARATOR);
            int[] exemptionCodes = Arrays.stream(exemptionCodesSent)
                    .map(code -> StringUtils.substringBetween(code, "[", "]"))
                    .filter(NumberUtils::isParsable)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            addExemptionCodesToFileByOrder(file, exemptionCodes, lookupEntries);
        }
    }

    private void addExemptionCodesToFileByOrder(EcmFile file, int[] exemptionCodes, List<StandardLookupEntry> lookupEntries)
    {
        for (int exemptionCodeNumber : exemptionCodes)
        {
            StandardLookupEntry exemption = lookupEntries.stream()
                    .filter(code -> code.getOrder() == exemptionCodeNumber)
                    .findFirst().orElse(null);
            if (exemption != null)
            {
                documentExemptionService.saveExemptionCodeAndNumberForFile(file, exemption.getKey(), exemption.getOrder());
            }
            else
            {
                log.warn("Exemption code not found by order. Exemption codes in Arkcase and ZyLAB should match");
            }
        }
    }

    private void addExemptionCodesToFileByKey(EcmFile file, String[] exemptionCodes, List<StandardLookupEntry> lookupEntries)
    {
        for (String exemptionCodeSubstring : exemptionCodes)
        {
            StandardLookupEntry exemption = lookupEntries.stream()
                    .filter(code -> code.getKey().startsWith(exemptionCodeSubstring))
                    .findFirst().orElse(null);
            if (exemption != null)
            {
                documentExemptionService.saveExemptionCodeAndNumberForFile(file, exemption.getKey(), exemption.getOrder());
            }
            else
            {
                log.warn("Exemption code not found by key. Exemption codes in Arkcase and ZyLAB should match");
                String exemptionCode = String.format("%s %s", file.getZylabFileMetadata().getRedactionCode1(),
                        file.getZylabFileMetadata().getRedactionCode2());
                documentExemptionService.saveExemptionCodeAndNumberForFile(file, exemptionCode, null);
            }
        }
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public ZylabIntegrationService getZylabIntegrationService()
    {
        return zylabIntegrationService;
    }

    public void setZylabIntegrationService(ZylabIntegrationService zylabIntegrationService)
    {
        this.zylabIntegrationService = zylabIntegrationService;
    }

    public ZylabIntegrationConfig getZylabIntegrationConfig()
    {
        return zylabIntegrationConfig;
    }

    public void setZylabIntegrationConfig(ZylabIntegrationConfig zylabIntegrationConfig)
    {
        this.zylabIntegrationConfig = zylabIntegrationConfig;
    }

    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
    }

    public String getWorkingFolderName()
    {
        return workingFolderName;
    }

    public void setWorkingFolderName(String workingFolderName)
    {
        this.workingFolderName = workingFolderName;
    }

    public DocumentExemptionService getDocumentExemptionService()
    {
        return documentExemptionService;
    }

    public void setDocumentExemptionService(DocumentExemptionService documentExemptionService)
    {
        this.documentExemptionService = documentExemptionService;
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

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    public ZylabEventPublisher getZylabEventPublisher()
    {
        return zylabEventPublisher;
    }

    public void setZylabEventPublisher(ZylabEventPublisher zylabEventPublisher)
    {
        this.zylabEventPublisher = zylabEventPublisher;
    }
}

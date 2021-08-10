package com.armedia.acm.services.ocr.service;

/*-
 * #%L
 * acm-ocr
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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.configuration.service.ConfigurationPropertyException;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.MediaEngineProviderNotFound;
import com.armedia.acm.services.mediaengine.exception.SaveConfigurationException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessModel;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessVariableKey;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.mediaengine.model.MediaEngineConstants;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.mediaengine.service.ArkCaseMediaEngineServiceImpl;
import com.armedia.acm.services.ocr.dao.OCRDao;
import com.armedia.acm.services.ocr.factory.OCRProviderFactory;
import com.armedia.acm.services.ocr.model.OCR;
import com.armedia.acm.services.ocr.model.OCRConfiguration;
import com.armedia.acm.services.ocr.model.OCRConstants;
import com.armedia.acm.services.ocr.utils.OCRUtils;
import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.exception.GetMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.ocr.model.OCRIntegrationConstants;
import com.armedia.acm.web.api.MDCConstants;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class ArkCaseOCRServiceImpl extends ArkCaseMediaEngineServiceImpl<OCR>
        implements ArkCaseOCRService
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private OCRDao ocrDao;
    private OCRProviderFactory ocrProviderFactory;
    private OCRConfigurationService ocrConfigurationService;

    @Override
    public OCR getByFileId(Long fileId) throws GetMediaEngineException
    {
        return getOcrDao().findByFileId(fileId);
    }

    @Override
    public OCR getByFileIdAndStatus(Long fileId, MediaEngineStatusType statusType) throws GetMediaEngineException
    {
        return getOcrDao().findByFileIdAndStatus(fileId, statusType);
    }

    @Override
    public void saveConfiguration(MediaEngineConfiguration configuration) throws SaveConfigurationException
    {
        if (configuration.isEnabled())
        {
            verifyOCR();
        }

        try
        {
            getOcrConfigurationService().saveProperties(configuration);
        }
        catch (ConfigurationPropertyException e)
        {
            throw new SaveConfigurationException(e);
        }
    }

    @Override
    public OCRConfiguration getConfiguration()
    {
        return getOcrConfigurationService().loadProperties();
    }

    @Override
    public void checkStatus(DelegateExecution delegateExecution)
    {
        LOG.debug("Check the status for PROCESS_ID=[{}]", delegateExecution.getProcessInstanceId());

        getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());
        String action = MediaEngineActionType.PROCESSING.toString();
        String previousAction = (String) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString());

        if (ids != null && !ids.isEmpty() && action.equalsIgnoreCase(previousAction))
        {
            // Because all IDs are follow the same business process (OCR objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            MediaEngine mediaEngine = null;

            try
            {
                mediaEngine = saveProcessingStatus(ids);
                MediaEngine providerOCR = getProviderOCR(mediaEngine);

                if (providerOCR != null && !MediaEngineStatusType.PROCESSING.toString().equals(providerOCR.getStatus()))
                {
                    String status = providerOCR.getStatus();

                    switch (MediaEngineStatusType.valueOf(providerOCR.getStatus()))
                    {
                    case COMPLETED:
                        action = doComplete(ids, mediaEngine, delegateExecution);
                        break;
                    case FAILED:
                        action = doFailed(mediaEngine);
                        break;

                    default:
                        throw new RuntimeException(
                                String.format("Received OCR status type of [%s] for OCR_ID=[%s] and FILE_ID=[%s], but cannot handle it.",
                                        status, mediaEngine.getId(), mediaEngine.getMediaEcmFileVersion().getFile().getId()));
                    }

                    delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.STATUS.toString(), status);
                    delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(), action);
                }
            }
            catch (GetMediaEngineException | GetMediaEngineToolException | SaveMediaEngineException
                    | MediaEngineProviderNotFound e)
            {
                LOG.warn("Could not check if OCR should be completed. PROCESS_ID=[{}], REASON=[{}]",
                        delegateExecution.getProcessInstanceId(), e.getMessage());
                if (mediaEngine != null)
                {
                    processFailedAttempt(delegateExecution, mediaEngine);
                }
            }
        }
    }

    private void processFailedAttempt(DelegateExecution delegateExecution, MediaEngine mediaEngine)
    {
        OCR ocr = (OCR) mediaEngine;
        int retryAttempt = Optional.ofNullable(ocr.getOcrRetryAttempt()).orElse(1);
        if (retryAttempt > getConfiguration().getMaxFailedAttempts())
        {
            String action = doFailed(mediaEngine);
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(), action);
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.STATUS.toString(), MediaEngineActionType.FAILED.toString());
        }
        else
        {
            ocr.setOcrRetryAttempt(++retryAttempt);
            ocrDao.save(ocr);
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                    MediaEngineActionType.PROCESSING.toString());
        }
    }

    private MediaEngine saveProcessingStatus(List<Long> ids) throws GetMediaEngineException, SaveMediaEngineException
    {
        MediaEngine mediaEngine = get(ids.get(0));
        if (!(MediaEngineStatusType.PROCESSING.toString().equalsIgnoreCase(mediaEngine.getStatus())))
        {
            mediaEngine.setStatus(MediaEngineStatusType.PROCESSING.toString());
            save(mediaEngine);
        }

        return mediaEngine;
    }

    private MediaEngine getProviderOCR(MediaEngine mediaEngine)
            throws GetMediaEngineToolException, MediaEngineProviderNotFound
    {
        OCRConfiguration configuration = getOcrConfigurationService().loadProperties();
        String providerName = configuration.getProvider();

        Map<String, Object> props = new HashMap<>();
        props.put("tempPath", configuration.getTempPath());
        MediaEngineDTO providerDTO = getOcrProviderFactory().getProvider(providerName).get(mediaEngine.getRemoteId(),
                props);

        return getMediaEngineMapper().dtoToMediaEngine(providerDTO);
    }

    private String doComplete(List<Long> ids, MediaEngine mediaEngine, DelegateExecution delegateExecution)
    {
        if (ids != null)
        {
            ids.forEach(id -> doComplete(id, mediaEngine, delegateExecution));
        }

        return MediaEngineActionType.COMPLETED.toString();
    }

    private void doComplete(Long id, MediaEngine mediaEngine, DelegateExecution delegateExecution)
    {
        try
        {
            Authentication authentication = SecurityContextHolder.getContext() != null
                    && SecurityContextHolder.getContext().getAuthentication() != null
                            ? SecurityContextHolder.getContext().getAuthentication()
                            : new AcmAuthentication(null, null, null, true,
                                    OCRConstants.OCR_SYSTEM_USER);

            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, OCRConstants.OCR_SYSTEM_USER);
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

            String fileName = mediaEngine.getMediaEcmFileVersion().getFile().getFileName();

            String fileLocation = getOcrConfigurationService().loadProperties().getTempPath() + mediaEngine.getRemoteId()
                    + OCRIntegrationConstants.QPDF_TMP
                    + OCRIntegrationConstants.TEMP_FILE_PDF_SUFFIX;

            File file = new File(fileLocation);
            try (InputStream stream = new FileInputStream(fileLocation))
            {
                AcmMultipartFile multipartFile = new AcmMultipartFile(fileName, fileName, "application/pdf",
                        false,
                        file.length(), stream, true);

                getEcmFileService().update(mediaEngine.getMediaEcmFileVersion().getFile(), multipartFile, authentication);
            }

            getObjectLockingManager().releaseObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                    EcmFileConstants.OBJECT_FILE_TYPE, MediaEngineConstants.LOCK_TYPE_WRITE,
                    true,
                    OCRConstants.OCR_SYSTEM_USER, null);
        }
        catch (Exception e)
        {
            LOG.warn("Taking items for OCR with ID=[{}] and PROCESS_ID=[{}] failed. REASON=[{}]", id,
                    delegateExecution.getProcessInstanceId(), e.getMessage());
        }
    }

    private String doFailed(MediaEngine mediaEngine)
    {
        getObjectLockingManager().releaseObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                EcmFileConstants.OBJECT_FILE_TYPE, MediaEngineConstants.LOCK_TYPE_WRITE, true,
                OCRConstants.OCR_SYSTEM_USER, null);

        return MediaEngineActionType.FAILED.toString();
    }

    @Override
    public void process(DelegateExecution delegateExecution)
    {
        getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            // Because all IDs are follow the same business process (OCR objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                MediaEngine mediaEngine = get(ids.get(0));
                if (MediaEngineStatusType.QUEUED.toString().equals(mediaEngine.getStatus()))
                {
                    OCRConfiguration configuration = getOcrConfigurationService().loadProperties();
                    List<MediaEngine> processingOCRObjects = getAllByStatus(MediaEngineStatusType.PROCESSING.toString());
                    List<MediaEngine> processingOCRAutomaticObjects = processingOCRObjects.stream()
                            .filter(t -> MediaEngineType.AUTOMATIC.toString().equalsIgnoreCase(t.getType())).collect(Collectors.toList());
                    List<MediaEngine> processingOCRObjectsDistinctByProcessId = processingOCRAutomaticObjects.stream()
                            .filter(OCRUtils.distinctByProperty(MediaEngine::getProcessId)).collect(Collectors.toList());

                    if (configuration.getNumberOfFilesForProcessing() > processingOCRObjectsDistinctByProcessId.size())
                    {
                        moveFromQueue(delegateExecution, mediaEngine, configuration);
                    }
                }
            }
            catch (GetMediaEngineException e)
            {
                LOG.warn("Could not check if OCR should be processed for PROCESS_ID=[{}]. REASON=[{}]",
                        delegateExecution.getProcessInstanceId(), e.getMessage());
            }
        }
    }

    private void moveFromQueue(DelegateExecution delegateExecution, MediaEngine mediaEngine, OCRConfiguration configuration)
    {
        try
        {
            acquireLock(mediaEngine);

            if (mediaEngine.getProcessId() == null)
            {
                mediaEngine.setProcessId(delegateExecution.getProcessInstanceId());
            }
            String providerName = configuration.getProvider();
            MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().mediaEngineToDTO(mediaEngine, configuration.getTempPath());
            mediaEngineDTO.setMediaEcmFileVersion(createTempFile(mediaEngine, configuration.getTempPath()));
            getOcrProviderFactory().getProvider(providerName).create(mediaEngineDTO);
            save(mediaEngine);
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.STATUS.toString(),
                    MediaEngineStatusType.PROCESSING.toString());
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                    MediaEngineActionType.PROCESSING.toString());
        }
        catch (SaveMediaEngineException e)
        {
            LOG.error("Error while calling PROVIDER=[{}] to OCR the media for PROCESS_ID=[{}]. REASON=[{}]",
                    configuration.getService().toString(), delegateExecution.getProcessInstanceId(), e.getMessage(), e);
        }
        catch (CreateMediaEngineToolException | AcmObjectLockException | IOException | ArkCaseFileRepositoryException e)
        {
            LOG.error("Error while creating OCR with PROVIDER=[{}] for PROCESS_ID=[{}]. REASON=[{}]",
                    configuration.getService().toString(), delegateExecution.getProcessInstanceId(), e.getMessage(), e);
        }
        catch (MediaEngineProviderNotFound e)
        {
            LOG.error("Error while creating OCR, Provider not found. PROVIDER=[{}] for PROCESS_ID=[{}]. REASON=[{}]",
                    configuration.getProvider(), delegateExecution.getProcessInstanceId(), e.getMessage(), e);
        }
    }

    private void acquireLock(MediaEngine mediaEngine)
    {
        AcmObjectLock lock = getObjectLockService().findLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                EcmFileConstants.OBJECT_FILE_TYPE);
        if (lock == null || lock.getCreator().equalsIgnoreCase(OCRConstants.OCR_SYSTEM_USER))
        {
            getObjectLockingManager().acquireObjectLock(mediaEngine.getMediaEcmFileVersion().getFile().getId(),
                    EcmFileConstants.OBJECT_FILE_TYPE,
                    MediaEngineConstants.LOCK_TYPE_WRITE,
                    null,
                    true,
                    OCRConstants.OCR_SYSTEM_USER);
        }
        else
        {
            throw new AcmObjectLockException(String.format("Cannot acquire lock object with id={%d}!", mediaEngine.getId()));
        }
    }

    @Override
    public void purge(DelegateExecution delegateExecution)
    {
        LOG.debug("Purge OCR information for PROCESS_ID=[{}]", delegateExecution.getProcessInstanceId());

        getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            // Because all IDs are follow the same business process (OCR objects for these IDS have the same
            // information, just different ids),
            // we need just to take one of them (if there are many)
            try
            {
                MediaEngine mediaEngine = get(ids.get(0));
                OCRConfiguration configuration = getOcrConfigurationService().loadProperties();
                int purgeAttempts = 0;
                int purgeAttemptsInConfiguration = configuration.getProviderPurgeAttempts();
                if (delegateExecution.hasVariable(MediaEngineBusinessProcessVariableKey.PURGE_ATTEMPTS.toString()))
                {
                    purgeAttempts = (int) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.PURGE_ATTEMPTS.toString());
                }

                if (purgeAttempts < purgeAttemptsInConfiguration)
                {
                    boolean purged = purge(mediaEngine);

                    if (purged)
                    {
                        LOG.debug("OCR information for OCR with PROCESS_ID=[{}], REMOTE_ID=[{}] on provider side are purged.",
                                delegateExecution.getProcessInstanceId(), mediaEngine.getRemoteId());
                        delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                                MediaEngineActionType.PURGE_SUCCESS.toString());
                    }
                    else
                    {
                        LOG.warn("OCR information for OCR with PROCESS_ID=[{}], REMOTE_ID=[{}] on provider side are not purged.",
                                delegateExecution.getProcessInstanceId(), mediaEngine.getRemoteId());
                        delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                                MediaEngineActionType.PURGE_FAILED.toString());
                    }

                    delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.PURGE_ATTEMPTS.toString(), purgeAttempts + 1);
                }
                else
                {
                    LOG.warn("Purging attempts for OCR with PROCESS_ID=[{}], REMOTE_ID=[{}] exceeded. Terminating purge job.",
                            delegateExecution.getProcessInstanceId(), mediaEngine.getRemoteId());
                    delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                            MediaEngineActionType.PURGE_TERMINATE.toString());
                }
            }
            catch (Exception e)
            {
                LOG.warn("Could not purge OCR information on provider side. PROCESS_ID=[{}], REASON=[{}]",
                        delegateExecution.getProcessInstanceId(), e.getMessage());
                delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                        MediaEngineActionType.PURGE_TERMINATE.toString());
            }
        }
        else
        {
            LOG.warn("Purging job for PROCESS_ID=[{}] cannot proceed because there is no OCR. Terminating purge job.",
                    delegateExecution.getProcessInstanceId());
            delegateExecution.setVariable(MediaEngineBusinessProcessVariableKey.ACTION.toString(),
                    MediaEngineActionType.PURGE_TERMINATE.toString());
        }
    }

    @Override
    public void removeProcessId(DelegateExecution delegateExecution)
    {
        LOG.debug("Remove Process ID OCR information for PROCESS_ID=[{}]", delegateExecution.getProcessInstanceId());

        getAuditPropertyEntityAdapter().setUserId(OCRConstants.OCR_SYSTEM_USER);

        List<Long> ids = (List<Long>) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.IDS.toString());

        if (ids != null && !ids.isEmpty())
        {
            ids.forEach(id -> {
                try
                {
                    MediaEngine mediaEngine = get(id);
                    mediaEngine.setProcessId(null);
                    save(mediaEngine);
                }
                catch (Exception e)
                {
                    LOG.error("Could not remove process id from OCR. REASON=[{}]", e.getMessage());
                }
            });
        }
    }

    @Override
    protected MediaEngine createEntity()
    {
        return new OCR();
    }

    @Override
    protected MediaEngineBusinessProcessModel createMediaEngineBusinessProcessModelEntity()
    {
        return new MediaEngineBusinessProcessModel();
    }

    @Override
    public void verifyOCR() throws SaveConfigurationException
    {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try
        {
            if (isWindows)
            {
                pr = rt.exec("cmd /c tesseract --version");
            }
            else
            {
                pr = rt.exec("sh -c tesseract --version");
            }

            pr.waitFor();
        }
        catch (Exception e)
        {
            throw new SaveConfigurationException("The Tesseract engine must be installed in order to enable OCR", e);
        }

        try
        {
            if (isWindows)
            {
                pr = rt.exec("cmd /c qpdf --version");
            }
            else
            {
                pr = rt.exec("sh -c qpdf --version");
            }

            pr.waitFor();
        }
        catch (Exception e)
        {
            throw new SaveConfigurationException("The QPDF engine must be installed in order to enable OCR", e);
        }

        try
        {
            if (isWindows)
            {
                pr = rt.exec("cmd /c magick --version");
            }
            else
            {
                pr = rt.exec("sh -c magick --version");
            }

            pr.waitFor();
        }
        catch (Exception e)
        {
            throw new SaveConfigurationException("The Image Magick engine must be installed in order to enable OCR", e);
        }
    }

    @Override
    public boolean isProcessable(EcmFileVersion ecmFileVersion)
    {
        if (ecmFileVersion != null && ecmFileVersion.getVersionMimeType() != null)
        {
            if (ecmFileVersion.getVersionMimeType().startsWith(OCRConstants.MEDIA_TYPE_IMAGE_RECOGNITION_KEY)
                    || (ecmFileVersion.getVersionMimeType().startsWith(OCRConstants.MEDIA_TYPE_PDF_RECOGNITION_KEY)
                            && !ecmFileVersion.isSearchablePDF()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public OCR get(String remoteId)
    {
        throw new NotImplementedException();
    }

    @Override
    public void notifyMultiple(List<Long> ids, String action)
    {
        LOG.warn("Not implemented");
    }

    @Override
    public void notify(Long id, String action)
    {
        LOG.warn("Not implemented");
    }

    @Override
    public boolean purge(MediaEngine mediaEngine) throws MediaEngineProviderNotFound
    {
        OCRConfiguration configuration = getOcrConfigurationService().loadProperties();
        String providerName = configuration.getProvider();
        MediaEngineDTO mediaEngineDTO = getMediaEngineMapper().mediaEngineToDTO(mediaEngine, configuration.getTempPath());
        return getOcrProviderFactory().getProvider(providerName).purge(mediaEngineDTO);
    }

    @Override
    public boolean allow(EcmFileVersion ecmFileVersion)
    {
        return ecmFileVersion.isValidFile() &&
                isServiceEnabled() && !isExcludedFileTypes(ecmFileVersion.getFile().getFileType()) && isProcessable(ecmFileVersion);
    }

    @Override
    public MediaEngine getExisting(MediaEngine mediaEngine) throws GetMediaEngineException
    {
        return getByFileIdAndStatus(mediaEngine.getMediaEcmFileVersion().getFile().getId(), MediaEngineStatusType.QUEUED);
    }

    @Override
    public EcmFileVersion getExistingMediaVersionId(MediaEngine mediaEngine) throws CreateMediaEngineException
    {
        return mediaEngine.getMediaEcmFileVersion();
    }

    @Override
    public String resetRemoteId(MediaEngine mediaEngine)
    {
        return mediaEngine.getRemoteId();
    }

    @Override
    public String getServiceName()
    {
        return OCRConstants.SERVICE;
    }

    @Override
    public String getSystemUser()
    {
        return OCRConstants.OCR_SYSTEM_USER;
    }

    @Override
    public MediaEngine getForCopiedFile(Long fileId, Long versionId) throws GetMediaEngineException
    {
        return getByFileId(fileId);
    }

    // <editor-fold desc="getters and setters">
    public OCRDao getOcrDao()
    {
        return ocrDao;
    }

    public void setOcrDao(OCRDao ocrDao)
    {
        this.ocrDao = ocrDao;
    }

    public OCRProviderFactory getOcrProviderFactory()
    {
        return ocrProviderFactory;
    }

    public void setOcrProviderFactory(OCRProviderFactory ocrProviderFactory)
    {
        this.ocrProviderFactory = ocrProviderFactory;
    }

    public OCRConfigurationService getOcrConfigurationService()
    {
        return ocrConfigurationService;
    }

    public void setOcrConfigurationService(OCRConfigurationService ocrConfigurationService)
    {
        this.ocrConfigurationService = ocrConfigurationService;
    }
    // </editor-fold>
}

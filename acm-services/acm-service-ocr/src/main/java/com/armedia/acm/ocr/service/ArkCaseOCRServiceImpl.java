package com.armedia.acm.ocr.service;

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

import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.ocr.dao.OCRDao;
import com.armedia.acm.ocr.exception.CreateOCRException;
import com.armedia.acm.ocr.exception.GetConfigurationException;
import com.armedia.acm.ocr.exception.GetOCRException;
import com.armedia.acm.ocr.exception.SaveConfigurationException;
import com.armedia.acm.ocr.exception.SaveOCRException;
import com.armedia.acm.ocr.factory.OCRServiceFactory;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRActionType;
import com.armedia.acm.ocr.model.OCRBusinessProcessModel;
import com.armedia.acm.ocr.model.OCRBusinessProcessVariableKey;
import com.armedia.acm.ocr.model.OCRConfiguration;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.model.OCRStatusType;
import com.armedia.acm.ocr.model.OCRType;
import com.armedia.acm.ocr.pipline.OCRPipelineContext;
import com.armedia.acm.ocr.rules.OCRBusinessProcessRulesExecutor;
import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang.NotImplementedException;
import org.mule.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class ArkCaseOCRServiceImpl implements ArkCaseOCRService
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private OCRConfigurationPropertiesService ocrConfigurationPropertiesService;
    private OCRDao ocrDao;
    private EcmFileVersionDao ecmFileVersionDao;
    private PipelineManager<OCR, OCRPipelineContext> pipelineManager;
    private OCREventPublisher ocrEventPublisher;
    private RuntimeService activitiRuntimeService;
    private ArkCaseBeanUtils ocrArkCaseBeanUtils;
    private OCRBusinessProcessRulesExecutor ocrBusinessProcessRulesExecutor;
    private OCRServiceFactory ocrServiceFactory;
    private Properties ecmFileServiceProperties;

    @Override
    public OCR create(Long versionId, OCRType type) throws CreateOCRException
    {
        EcmFileVersion ecmFileVersion = getEcmFileVersionDao().find(versionId);

        return create(ecmFileVersion, type);
    }

    @Override
    public OCR create(EcmFileVersion ecmFileVersion, OCRType type) throws CreateOCRException
    {
        OCR ocr = new OCR();
        ocr.setEcmFileVersion(ecmFileVersion);
        ocr.setType(OCRType.AUTOMATIC.toString());
        return create(ocr);
    }

    @Override
    public OCR get(Long id) throws GetOCRException
    {
        return getOcrDao().find(id);
    }

    @Override
    public OCR getByMediaVersionId(Long mediaVersionId) throws GetOCRException
    {
        return getOcrDao().findByMediaVersionId(mediaVersionId);
    }

    @Override
    public OCR save(OCR ocr) throws SaveOCRException
    {
        OCR saved = getOcrDao().save(ocr);
        String action = ocr.getId() == null ? OCRActionType.CREATED.toString() : OCRActionType.UPDATED.toString();
        getOcrEventPublisher().publish(saved, action);

        return saved;
    }

    @Override
    public OCR copy(OCR ocr, EcmFileVersion ecmFileVersion) throws CreateOCRException
    {
        OCR copy = null;
        try
        {
            copy = new OCR();
            copy.setEcmFileVersion(ecmFileVersion);
            getOcrArkCaseBeanUtils().copyProperties(copy, ocr);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            LOG.warn("Could not copy properties for OCR object with ID=[{}]. REASON=[{}]",
                    ocr != null ? ocr.getId() : null, e.getMessage());
        }

        if (copy != null)
        {
            OCR savedCopy = null;
            try
            {
                savedCopy = save(copy);
            }
            catch (SaveOCRException e)
            {
                throw new CreateOCRException(String.format("Could not create copy for OCR object with ID=[{}]. REASON=[{}]",
                        ocr != null ? ocr.getId() : null, e.getMessage()));
            }
            if (ocr != null)
            {
                if (StringUtils.isNotEmpty(ocr.getProcessId()))
                {
                    ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                            .processInstanceId(ocr.getProcessId()).singleResult();
                    if (processInstance != null)
                    {
                        List<Long> ids = (List<Long>) processInstance.getProcessVariables()
                                .get(OCRBusinessProcessVariableKey.IDS.toString());
                        if (ids == null)
                        {
                            ids = new ArrayList<>();
                        }

                        ids.add(savedCopy.getId());
                        getActivitiRuntimeService().setVariable(processInstance.getId(),
                                OCRBusinessProcessVariableKey.IDS.toString(),
                                ids);
                    }
                }

                return savedCopy;
            }

        }

        throw new CreateOCRException(
                String.format("Could not create copy for OCR object with ID=[%d]", ocr != null ? ocr.getId() : null));
    }

    @Override
    public OCR complete(Long id) throws SaveOCRException
    {
        OCR ocr = getOcrDao().find(id);
        if (ocr != null && StringUtils.isNotEmpty(ocr.getProcessId()))
        {
            ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(ocr.getProcessId()).singleResult();
            if (processInstance != null)
            {
                signal(processInstance, OCRStatusType.COMPLETED.toString(), OCRActionType.COMPLETED.toString());
                ocr.setStatus(OCRStatusType.COMPLETED.toString());
                return ocr;
            }
        }

        throw new SaveOCRException(String.format("Could not complete OCR object with ID=[%d]", id));
    }

    @Override
    public OCR cancel(Long id) throws SaveOCRException
    {

        OCR ocr = getOcrDao().find(id);
        if (ocr != null && StringUtils.isNotEmpty(ocr.getProcessId()))
        {
            ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(ocr.getProcessId()).singleResult();
            if (processInstance != null)
            {
                signal(processInstance, OCRStatusType.DRAFT.toString(), OCRActionType.CANCELLED.toString());
                ocr.setStatus(OCRStatusType.DRAFT.toString());
                return ocr;
            }
        }

        throw new SaveOCRException(String.format("Could not cancel OCR object with ID=[%d]", id));
    }

    @Override
    public OCR fail(Long id) throws SaveOCRException
    {
        OCR ocr = getOcrDao().find(id);
        if (ocr != null && StringUtils.isNotEmpty(ocr.getProcessId()))
        {
            ProcessInstance processInstance = getActivitiRuntimeService().createProcessInstanceQuery().includeProcessVariables()
                    .processInstanceId(ocr.getProcessId()).singleResult();
            if (processInstance != null)
            {
                String statusKey = OCRBusinessProcessVariableKey.STATUS.toString();
                String actionKey = OCRBusinessProcessVariableKey.ACTION.toString();
                String status = OCRStatusType.FAILED.toString();
                String action = OCRActionType.FAILED.toString();

                getActivitiRuntimeService().setVariable(processInstance.getId(), statusKey, status);
                getActivitiRuntimeService().setVariable(processInstance.getId(), actionKey, action);

                ocr.setStatus(OCRStatusType.FAILED.toString());

                return ocr;
            }
        }

        throw new SaveOCRException(String.format("Could not set as failed OCR object with ID=[%d]", id));
    }

    @Override
    public OCR changeStatus(Long id, String status) throws SaveOCRException
    {
        LOG.debug("Changing status of the OCR with ID=[{}]. New status will be STATUS=[{}]", id, status);
        if (id == null || StringUtils.isEmpty(status))
        {
            String message = String.format("Status of the OCR cannot be changed. ID=[%d], STATUS=[%s]", id, status);
            LOG.error(message);
            throw new SaveOCRException(message);
        }

        OCR ocr = getOcrDao().find(id);

        if (ocr == null)
        {
            String message = String.format("OCR with ID=[%d] cannot be found or retrieved from database.", id);
            LOG.error(message);
            throw new SaveOCRException(message);
        }

        ocr.setStatus(status);

        try
        {
            return getOcrDao().save(ocr);
        }
        catch (Exception e)
        {
            String message = String.format("Status of the OCR cannot be changed. ID=[%d], STATUS=[%s]. REASON=[%s]", id, status,
                    e.getMessage());
            LOG.error(message);
            throw new SaveOCRException(message, e);
        }
    }

    @Override
    public List<OCR> changeStatusMultiple(List<Long> ids, String status) throws SaveOCRException
    {
        if (ids != null)
        {
            List<OCR> changedOCRs = new ArrayList<>();
            ids.forEach(id -> {
                try
                {
                    OCR changed = changeStatus(id, status);
                    changedOCRs.add(changed);
                }
                catch (SaveOCRException e)
                {
                    LOG.warn("Changing status for OCR with ID=[{}] in bulk operation failed. REASON=[{}]", id, e.getMessage());
                }
            });

            return changedOCRs;
        }

        String message = String.format("Status of multiple OCR objects cannot be changed. IDS=[null], STATUS=[%s]", status);
        LOG.error(message);
        throw new SaveOCRException(message);
    }

    @Override
    public void notify(Long id, String action)
    {
        throw new NotImplementedException();
    }

    @Override
    public void notifyMultiple(List<Long> ids, String action)
    {
        LOG.warn("Not implemented");
    }

    @Override
    public void audit(Long id, String action)
    {
        if (id != null && action != null)
        {
            OCR ocr = getOcrDao().find(id);
            if (ocr != null)
            {
                getOcrEventPublisher().publish(ocr, action);
            }
        }
    }

    @Override
    public void auditMultiple(List<Long> ids, String action)
    {
        if (ids != null)
        {
            ids.forEach(id -> audit(id, action));
        }
    }

    @Override
    public EcmFile compile(Long id)
    {
        throw new NotImplementedException();
    }

    @Override
    public ProcessInstance startBusinessProcess(OCR ocr)
    {
        LOG.debug("Checking if starting business process is allowed for OCR Object [{}]", ocr);
        ProcessInstance processInstance = null;
        if (ocr != null)
        {
            // Check drools if we need to start workflow for provided OCR object
            OCRBusinessProcessModel ocrBusinessProcessModel = new OCRBusinessProcessModel();
            ocrBusinessProcessModel.setType(ocr.getType());

            LOG.debug(
                    "Executing Drools Business rules for [{}] OCR with ID=[{}], MEDIA_FILE_ID=[{}] and MEDIA_FILE_VERSION_ID=[{}]",
                    ocr.getType(), ocr.getId(), ocr.getEcmFileVersion().getFile().getId(),
                    ocr.getEcmFileVersion().getId());

            ocrBusinessProcessModel = getOcrBusinessProcessRulesExecutor().applyRules(ocrBusinessProcessModel);

            LOG.debug("Start business process: [{}]", ocrBusinessProcessModel.isStart());

            if (ocrBusinessProcessModel.isStart())
            {
                // Check if there is already startes business process. This can be the case when we replace media file
                // and in the OCR Configuration (properties file) we have set "copy ocr" instead of
                // "new ocr".
                // In that case we have complete two copies of OCR object, just different IDS. So we should use
                // the same Process
                // for both OCR objects
                if (StringUtils.isNotEmpty(ocr.getProcessId()))
                {
                    processInstance = getActivitiRuntimeService().createProcessInstanceQuery().processInstanceId(ocr.getProcessId())
                            .includeProcessVariables().singleResult();
                }

                if (processInstance == null)
                {
                    // When we don't have process instance, create it
                    processInstance = createProcessInstance(ocr, ocrBusinessProcessModel);
                }
                else
                {
                    // When we have process instance, just update the variable 'IDS'
                    updateProcessInstance(ocr, processInstance);
                }
            }
        }

        LOG.debug("There is no OCR Object. It's [{}]", ocr);

        return processInstance;
    }

    private ProcessInstance createProcessInstance(OCR ocr, OCRBusinessProcessModel ocrBusinessProcessModel)
    {
        String status = OCRType.AUTOMATIC.toString().equalsIgnoreCase(ocr.getType()) ? OCRStatusType.QUEUED.toString()
                : ocr.getStatus();
        String action = OCRType.AUTOMATIC.toString().equalsIgnoreCase(ocr.getType()) ? OCRActionType.QUEUED.toString()
                : ocr.getStatus();

        List<Long> ids = new ArrayList<>();
        ids.add(ocr.getId());

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put(OCRBusinessProcessVariableKey.IDS.toString(), ids);
        processVariables.put(OCRBusinessProcessVariableKey.REMOTE_ID.toString(), ocr.getRemoteId());
        processVariables.put(OCRBusinessProcessVariableKey.STATUS.toString(), status);
        processVariables.put(OCRBusinessProcessVariableKey.ACTION.toString(), action);
        processVariables.put(OCRBusinessProcessVariableKey.TYPE.toString(), ocr.getType());
        processVariables.put(OCRBusinessProcessVariableKey.CREATED.toString(), new Date());

        ProcessInstance processInstance = getActivitiRuntimeService().startProcessInstanceByKey(ocrBusinessProcessModel.getName(),
                processVariables);

        ocr.setProcessId(processInstance.getId());

        return processInstance;
    }

    private void updateProcessInstance(OCR ocr, ProcessInstance processInstance)
    {
        List<Long> ids = (List<Long>) processInstance.getProcessVariables().get(OCRBusinessProcessVariableKey.IDS.toString());
        if (ids != null)
        {
            if (!ids.contains(ocr.getId()))
            {
                ids.add(ocr.getId());
            }

            getActivitiRuntimeService().setVariable(processInstance.getId(), OCRBusinessProcessVariableKey.IDS.toString(), ids);
        }
    }

    @Override
    public void signal(ProcessInstance processInstance, String status, String action)
    {
        if (processInstance != null && StringUtils.isNotEmpty(status) && StringUtils.isNotEmpty(action))
        {
            String statusKey = OCRBusinessProcessVariableKey.STATUS.toString();
            String actionKey = OCRBusinessProcessVariableKey.ACTION.toString();
            getActivitiRuntimeService().setVariable(processInstance.getId(), statusKey, status);
            getActivitiRuntimeService().setVariable(processInstance.getId(), actionKey, action);
            getActivitiRuntimeService().signal(processInstance.getId());
        }
    }

    @Override
    public OCRConfiguration getConfiguration() throws GetConfigurationException
    {
        return getOcrConfigurationPropertiesService().get();
    }

    @Override
    public void saveConfiguration(OCRConfiguration configuration) throws SaveConfigurationException
    {
        if (configuration.isEnableOCR())
        {
            verifyOCR(configuration);
        }

        getOcrConfigurationPropertiesService().save(configuration);

    }

    @Override
    public void verifyOCR(OCRConfiguration configuration) throws SaveConfigurationException
    {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try
        {
            pr = rt.exec("tesseract --version");

            pr.waitFor();
        }
        catch (Exception e)
        {
            throw new SaveConfigurationException("The Tesseract engine must be installed in order to enable OCR");
        }

        try
        {
            pr = rt.exec("qpdf --version");

            pr.waitFor();
        }
        catch (Exception e)
        {
            throw new SaveConfigurationException("The QPDF engine must be installed in order to enable OCR");
        }

        try
        {
            pr = rt.exec("magick --version");

            pr.waitFor();
        }
        catch (Exception e)
        {
            throw new SaveConfigurationException("The Image Magick engine must be installed in order to enable OCR");
        }
    }

    @Override
    public boolean allow(EcmFileVersion ecmFileVersion)
    {
        return isOCREnabled() && isFileVersionOCRable(ecmFileVersion);
    }

    @Override
    public boolean isOCREnabled()
    {
        try
        {
            OCRConfiguration configuration = getConfiguration();
            boolean allow = configuration != null && configuration.isEnableOCR();

            if (!allow)
            {
                LOG.warn("OCR is not enabled. It will be terminated.");
            }

            return allow;
        }
        catch (GetConfigurationException e)
        {
            LOG.error("Failed to retrieve OCR configuration.", e);
            return false;
        }
    }

    @Override
    public boolean isFileVersionOCRable(EcmFileVersion ecmFileVersion)
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
    public OCR create(OCR ocr) throws CreateOCRException
    {
        // Here we need OCR without id - new OCR
        if (!allow(ocr.getEcmFileVersion()) || ocr.getId() != null)
        {
            throw new CreateOCRException("OCR service is not allowed.");
        }

        OCR existingOCR = null;
        try
        {
            existingOCR = getByMediaVersionId(ocr.getEcmFileVersion().getId());
        }
        catch (GetOCRException e)
        {
            throw new CreateOCRException(String.format("Creating OCR job is aborted. REASON=[%s]", e.getMessage()), e);
        }

        if (existingOCR != null && (OCRStatusType.QUEUED.toString().equalsIgnoreCase(existingOCR.getStatus()) ||
                OCRStatusType.PROCESSING.toString().equalsIgnoreCase(existingOCR.getStatus())))
        {
            throw new CreateOCRException(
                    String.format("Creating OCR job is aborted. There is already OCR object for ECM_FILE_VERSION_ID=[%d]",
                            ocr.getEcmFileVersion().getId()));
        }

        OCRPipelineContext context = new OCRPipelineContext();
        context.setEcmFileVersion(ocr.getEcmFileVersion());
        context.setType(OCRType.valueOf(ocr.getType()));

        try
        {
            OCR ocrForProcessing = existingOCR != null ? existingOCR : ocr;
            if (ocrForProcessing.getId() != null)
            {
                // Reset 'remoteId' for existing OCR's that we want to be OCRed again
                ocrForProcessing.setRemoteId(null);
            }

            return getPipelineManager().executeOperation(ocrForProcessing, context, () -> {
                try
                {
                    return save(ocrForProcessing);
                }
                catch (SaveOCRException e)
                {
                    throw new PipelineProcessException(
                            String.format("OCR for MEDIA_VERSION_ID=[%d] was not created successfully. REASON=[%s]",
                                    ocrForProcessing.getEcmFileVersion() != null
                                            ? ocrForProcessing.getEcmFileVersion().getId()
                                            : null,
                                    e.getMessage()));
                }
            });
        }
        catch (PipelineProcessException e)
        {
            throw new CreateOCRException(String.format(
                    "OCR for MEDIA_VERSION_ID=[%d] was not created successfully. REASON=[%s]",
                    ocr.getEcmFileVersion() != null ? ocr.getEcmFileVersion().getId() : null, e.getMessage()), e);
        }
    }

    @Override
    public OCR get(String remoteId)
    {
        throw new NotImplementedException();
    }

    @Override
    public List<OCR> getAll() throws GetOCRException
    {
        return getOcrDao().findAll();
    }

    @Override
    public List<OCR> getAllByStatus(String status) throws GetOCRException
    {
        return getOcrDao().findAllByStatus(status);
    }

    @Override
    public List<OCR> getPage(int start, int n) throws GetOCRException
    {
        throw new NotImplementedException();
    }

    @Override
    public List<OCR> getPageByStatus(int start, int n, String status) throws GetOCRException
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean purge(OCR ocr)
    {
        throw new NotImplementedException();
    }

    public boolean isExcludedFileTypes(String fileType)
    {
        return Arrays.asList(getEcmFileServiceProperties().getProperty("ocr.excludedFileTypes").split(",")).contains(fileType);
    }

    // <editor-fold desc="getters and setters">
    public void setOcrConfigurationPropertiesService(OCRConfigurationPropertiesService ocrConfigurationPropertiesService)
    {
        this.ocrConfigurationPropertiesService = ocrConfigurationPropertiesService;
    }

    public OCRConfigurationPropertiesService getOcrConfigurationPropertiesService()
    {
        return ocrConfigurationPropertiesService;
    }

    public OCRDao getOcrDao()
    {
        return ocrDao;
    }

    public void setOcrDao(OCRDao ocrDao)
    {
        this.ocrDao = ocrDao;
    }

    public EcmFileVersionDao getEcmFileVersionDao()
    {
        return ecmFileVersionDao;
    }

    public void setEcmFileVersionDao(EcmFileVersionDao ecmFileVersionDao)
    {
        this.ecmFileVersionDao = ecmFileVersionDao;
    }

    public PipelineManager<OCR, OCRPipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<OCR, OCRPipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }

    public OCREventPublisher getOcrEventPublisher()
    {
        return ocrEventPublisher;
    }

    public void setOcrEventPublisher(OCREventPublisher ocrEventPublisher)
    {
        this.ocrEventPublisher = ocrEventPublisher;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public ArkCaseBeanUtils getOcrArkCaseBeanUtils()
    {
        return ocrArkCaseBeanUtils;
    }

    public void setOcrArkCaseBeanUtils(ArkCaseBeanUtils ocrArkCaseBeanUtils)
    {
        this.ocrArkCaseBeanUtils = ocrArkCaseBeanUtils;
    }

    public OCRBusinessProcessRulesExecutor getOcrBusinessProcessRulesExecutor()
    {
        return ocrBusinessProcessRulesExecutor;
    }

    public void setOcrBusinessProcessRulesExecutor(OCRBusinessProcessRulesExecutor ocrBusinessProcessRulesExecutor)
    {
        this.ocrBusinessProcessRulesExecutor = ocrBusinessProcessRulesExecutor;
    }

    public Properties getEcmFileServiceProperties()
    {
        return ecmFileServiceProperties;
    }

    public void setEcmFileServiceProperties(Properties ecmFileServiceProperties)
    {
        this.ecmFileServiceProperties = ecmFileServiceProperties;
    }

    @Override
    public OCRServiceFactory getOCRServiceFactory()
    {
        return ocrServiceFactory;
    }

    public void setOCRServiceFactory(OCRServiceFactory ocrServiceFactory)
    {
        this.ocrServiceFactory = ocrServiceFactory;
    }

    // </editor-fold>
}

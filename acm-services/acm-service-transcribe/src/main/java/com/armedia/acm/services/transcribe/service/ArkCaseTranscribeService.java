package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.transcribe.dao.TranscribeDao;
import com.armedia.acm.services.transcribe.exception.*;
import com.armedia.acm.services.transcribe.factory.TranscribeServiceFactory;
import com.armedia.acm.services.transcribe.model.*;
import com.armedia.acm.services.transcribe.pipline.TranscribePipelineContext;
import com.armedia.acm.services.transcribe.rules.TranscribeBusinessProcessRulesExecutor;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.mule.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class ArkCaseTranscribeService extends AbstractArkCaseTranscribeService
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private TranscribeDao transcribeDao;
    private EcmFileVersionDao ecmFileVersionDao;
    private TranscribeConfigurationService transcribeConfigurationService;
    private PipelineManager<Transcribe, TranscribePipelineContext> pipelineManager;
    private TranscribeBusinessProcessRulesExecutor transcribeBusinessProcessRulesExecutor;
    private RuntimeService activitiRuntimeService;

    @Override
    @Transactional
    public Transcribe create(Long versionId, TranscribeType type) throws CreateTranscribeException
    {
        EcmFileVersion ecmFileVersion = getEcmFileVersionDao().find(versionId);

        return create(ecmFileVersion, type);
    }

    @Override
    @Transactional
    public Transcribe create(EcmFileVersion ecmFileVersion, TranscribeType type) throws CreateTranscribeException
    {
        if (!allow(ecmFileVersion))
        {
            throw new CreateTranscribeException("Transcribe service is not allowed.");
        }

        Transcribe existingTranscribe = null;
        try
        {
            existingTranscribe = getByMediaVersionId(ecmFileVersion.getId());
        }
        catch (GetTranscribeException e)
        {
            throw new CreateTranscribeException(String.format("Creating Transcribe job is aborted. REASON=[%s]", e.getMessage()), e);
        }

        // TODO: If there is already automatic transcription and another is requested, abort, or abort every time when there is existing transcribe?
        if (existingTranscribe != null && TranscribeType.AUTOMATIC.toString().equals(existingTranscribe.getType()) && TranscribeType.AUTOMATIC.equals(type))
        {
            throw new CreateTranscribeException(String.format("Creating Transcribe job is aborted. There is already Transcribe object for MEDIA_FILE_VERSION_ID=[%d]", ecmFileVersion.getId()));
        }

        TranscribePipelineContext context = new TranscribePipelineContext();
        context.setEcmFileVersion(ecmFileVersion);
        context.setType(type);

        try
        {
            Transcribe transcribe = existingTranscribe != null ? existingTranscribe : new Transcribe();
            return getPipelineManager().executeOperation(transcribe, context, () ->{
                Transcribe saved = getTranscribeDao().save(transcribe);
                return saved;
            });
        }
        catch (PipelineProcessException e)
        {
            throw new CreateTranscribeException(String.format("Transcribe for MEDIA_VERSION_ID=[%d] was not created successfully. REASON=[%s]", ecmFileVersion != null ? ecmFileVersion.getId() : null, e.getMessage()), e);
        }
    }

    @Override
    public Transcribe get(Long id) throws GetTranscribeException
    {
        return null;
    }

    @Override
    public Transcribe getByMediaVersionId(Long mediaVersionId) throws GetTranscribeException
    {
        return getTranscribeDao().findByMediaVersionId(mediaVersionId);
    }

    @Override
    public Transcribe save(Transcribe transcribe) throws SaveTranscribeException
    {
        return null;
    }

    @Override
    public TranscribeItem createItem(Long id, TranscribeItem item) throws CreateTranscribeItemException
    {
        return null;
    }

    @Override
    public TranscribeItem saveItem(TranscribeItem item) throws SaveTranscribeItemException
    {
        return null;
    }

    @Override
    public Transcribe changeStatus(Long id, String status) throws SaveTranscribeException
    {

        return null;
    }

    @Override
    public List<Transcribe> changeStatusMultiple(List<Long> ids, String status) throws SaveTranscribeException
    {

        return null;
    }

    @Override
    public void notify(Long id, String userType, String action)
    {

    }

    @Override
    public void notifyMultiple(List<Long> ids, String userType, String action)
    {

    }

    @Override
    public void audit(Long id, String action)
    {

    }

    @Override
    public void auditMultiple(List<Long> ids, String action)
    {

    }

    @Override
    public EcmFile compile(Long id) throws CompileTranscribeException
    {
        return null;
    }

    @Override
    public TranscribeConfiguration getConfiguration() throws GetTranscribeConfigurationException
    {
        return getTranscribeConfigurationService().get();
    }

    @Override
    public TranscribeConfiguration saveConfiguration(TranscribeConfiguration configuration) throws SaveTranscribeConfigurationException
    {
        return getTranscribeConfigurationService().save(configuration);
    }

    @Override
    public ProcessInstance startBusinessProcess(Transcribe transcribe)
    {
        LOG.debug("Checking if starting business process is allowed for Transcribe Object [{}]", transcribe);
        ProcessInstance processInstance = null;
        if (transcribe != null)
        {
            // Check drools if we need to start workflow for provided Transcribe object
            TranscribeBusinessProcessModel transcribeBusinessProcessModel = new TranscribeBusinessProcessModel();
            transcribeBusinessProcessModel.setType(transcribe.getType());

            LOG.debug("Executing Drools Business rules for [{}] Transcribe with ID=[{}], MEDIA_FILE_ID=[{}] and MEDIA_FILE_VERSION_ID=[{}]", transcribe.getType(), transcribe.getMediaEcmFileVersion().getFile().getId(), transcribe.getMediaEcmFileVersion().getId());

            transcribeBusinessProcessModel = getTranscribeBusinessProcessRulesExecutor().applyRules(transcribeBusinessProcessModel);

            LOG.debug("Start business process: [{}]", transcribeBusinessProcessModel.isStart());

            if (transcribeBusinessProcessModel.isStart())
            {
                // Check if there is already startes business process. This can be the case when we replace media file
                // and in the Transcribe Configuration (properties file) we have set "copy transcription" instead of "new transcription".
                // In that case we have complete two copies of Transcribe object, just different IDS. So we should use the same Process
                // for both Transcribe objects
                if (StringUtils.isNotEmpty(transcribe.getProcessId()))
                {
                    processInstance = getActivitiRuntimeService().createProcessInstanceQuery().processInstanceId(transcribe.getProcessId()).includeProcessVariables().singleResult();
                }

                if (processInstance == null)
                {
                    // When we don't have process instance, create it
                    processInstance = createProcessInstance(transcribe, transcribeBusinessProcessModel);
                }
                else
                {
                    // When we have process instance, just update the variable 'IDS'
                    updateProcessInstance(transcribe, processInstance);
                }
            }
        }

        LOG.debug("There is no Transcribe Object. It's [{}]", transcribe);

        return processInstance;
    }

    @Override
    public TranscribeServiceFactory getFactory()
    {
        return null;
    }

    @Override
    public Transcribe create(Transcribe transcribe) throws CreateTranscribeException
    {
        return null;
    }

    @Override
    public Transcribe get(String remoteId) throws GetTranscribeException
    {
        return null;
    }

    @Override
    public List<Transcribe> getAll() throws GetTranscribeException
    {
        return null;
    }

    @Override
    public List<Transcribe> getAllByStatus(String status) throws GetTranscribeException
    {
        return null;
    }

    @Override
    public List<Transcribe> getPage(int start, int n) throws GetTranscribeException
    {
        return null;
    }

    @Override
    public List<Transcribe> getPageByStatus(int start, int n, String status) throws GetTranscribeException
    {
        return null;
    }

    /**
     * This method will return true if all conditions are reached for proceeding with automatic transcription
     *
     * @param ecmFileVersion - File version
     * @return true/false
     */
    private boolean allow(EcmFileVersion ecmFileVersion)
    {
        // TODO: Restrict only for Case/Complaints?
        return isFileVersionTranscribable(ecmFileVersion) &&
                isTranscribeOn() &&
                isAutomaticTranscribeOn() &&
                isMediaDurationAllowed(ecmFileVersion);
    }

    /**
     * This method will return if transcribe is enabled
     *
     * @return true/false
     */
    public boolean isTranscribeOn()
    {
        try
        {
            TranscribeConfiguration configuration = getConfiguration();
            boolean allow = configuration != null && configuration.isEnabled();

            if (!allow)
            {
                LOG.warn("Transcribe is not enabled. It will be terminated.");
            }

            return allow;
        }
        catch (GetTranscribeConfigurationException e)
        {
            LOG.error("Failed to retrieve Transcribe configuration.", e);
            return false;
        }
    }

    /**
     * This method will return if automatic transcribe is enabled
     *
     * @return true/false
     */
    public boolean isAutomaticTranscribeOn()
    {
        try
        {
            TranscribeConfiguration configuration = getConfiguration();
            boolean allow =  configuration != null && configuration.isAutomaticEnabled();

            if (!allow)
            {
                LOG.warn("Automatic Transcribe is not enabled. It will be terminated.");
            }

            return  allow;
        }
        catch (GetTranscribeConfigurationException e)
        {
            LOG.warn("Failed to retrieve Transcribe configuration. Automatic Transcribe will be terminated.");
            return false;
        }
    }

    /**
     * This method will return if provided audio/video is less than 2 hours
     *
     * @param ecmFileVersion - Media file version
     * @return true/false
     */
    public boolean isMediaDurationAllowed(EcmFileVersion ecmFileVersion)
    {
        try
        {
            TranscribeConfiguration configuration = getConfiguration();
            boolean allow = configuration != null && ecmFileVersion != null && ecmFileVersion.getDurationSeconds() <= configuration.getAllowedMediaDuration();

            if (!allow)
            {
                LOG.warn("The duration of the media file is more than allowed [{}] seconds. Automatic Transcription will be terminated.", configuration.getAllowedMediaDuration());
            }

            return  allow;
        }
        catch (GetTranscribeConfigurationException e)
        {
            LOG.warn("Failed to retrieve Transcribe configuration. Automatic Transcribe will be terminated.");
            return false;
        }
    }

    /**
     * This method will return if provided file is audio or video
     *
     * @param ecmFileVersion - File version
     * @return true/false
     */
    public boolean isFileVersionTranscribable(EcmFileVersion ecmFileVersion)
    {

        boolean allow = ecmFileVersion != null &&
                        ecmFileVersion.getVersionMimeType() != null &&
                        (
                            ecmFileVersion.getVersionMimeType().startsWith(TranscribeConstants.MEDIA_TYPE_AUDIO_RECOGNITION_KEY) ||
                            ecmFileVersion.getVersionMimeType().startsWith(TranscribeConstants.MEDIA_TYPE_VIDEO_RECOGNITION_KEY)
                        );

        if (!allow)
        {
            LOG.warn("The media file is not transcribable. Automatic Transcription will be terminated.");
        }

        return allow;
    }

    private ProcessInstance createProcessInstance(Transcribe transcribe, TranscribeBusinessProcessModel transcribeBusinessProcessModel)
    {
        List<Long> ids = new ArrayList<>();
        ids.add(transcribe.getId());

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("IDS", ids);
        processVariables.put("REMOTE_ID", transcribe.getRemoteId());
        processVariables.put("STATUS", StatusType.QUEUED);
        processVariables.put("ACTION", ActionType.QUEUED);

        ProcessInstance processInstance = getActivitiRuntimeService().startProcessInstanceByKey(transcribeBusinessProcessModel.getName(), processVariables);

        transcribe.setProcessId(processInstance.getId());

        return processInstance;
    }

    private void updateProcessInstance(Transcribe transcribe, ProcessInstance processInstance)
    {
        List<Long> ids = (List<Long>) processInstance.getProcessVariables().get("IDS");
        if (ids != null)
        {
            if (!ids.contains(transcribe.getId()))
            {
                ids.add(transcribe.getId());
            }

            getActivitiRuntimeService().setVariable(processInstance.getId(), "IDS", ids);
        }
    }

    public TranscribeDao getTranscribeDao()
    {
        return transcribeDao;
    }

    public void setTranscribeDao(TranscribeDao transcribeDao)
    {
        this.transcribeDao = transcribeDao;
    }

    public EcmFileVersionDao getEcmFileVersionDao()
    {
        return ecmFileVersionDao;
    }

    public void setEcmFileVersionDao(EcmFileVersionDao ecmFileVersionDao)
    {
        this.ecmFileVersionDao = ecmFileVersionDao;
    }

    public TranscribeConfigurationService getTranscribeConfigurationService()
    {
        return transcribeConfigurationService;
    }

    public void setTranscribeConfigurationService(TranscribeConfigurationService transcribeConfigurationService)
    {
        this.transcribeConfigurationService = transcribeConfigurationService;
    }

    public PipelineManager<Transcribe, TranscribePipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<Transcribe, TranscribePipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }

    public TranscribeBusinessProcessRulesExecutor getTranscribeBusinessProcessRulesExecutor()
    {
        return transcribeBusinessProcessRulesExecutor;
    }

    public void setTranscribeBusinessProcessRulesExecutor(TranscribeBusinessProcessRulesExecutor transcribeBusinessProcessRulesExecutor)
    {
        this.transcribeBusinessProcessRulesExecutor = transcribeBusinessProcessRulesExecutor;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }
}

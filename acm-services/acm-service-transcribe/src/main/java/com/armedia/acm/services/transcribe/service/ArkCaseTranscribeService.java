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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            String message = String.format("Creating Transcribe job is aborted. REASON=[%s]", e.getMessage());
            throw new CreateTranscribeException(message, e);
        }

        if (existingTranscribe != null)
        {
            String message = String.format("Creating Transcribe job is aborted. There is already Transcribe object for MEDIA_FILE_VERSION_ID=[%d]", ecmFileVersion.getId());
            throw new CreateTranscribeException(message);
        }

        TranscribePipelineContext context = new TranscribePipelineContext();
        context.setEcmFileVersion(ecmFileVersion);
        context.setType(type);

        try
        {
            Transcribe transcribe = new Transcribe();
            return getPipelineManager().executeOperation(transcribe, context, () ->{
                Transcribe saved = getTranscribeDao().save(transcribe);
                return saved;
            });
        }
        catch (PipelineProcessException e)
        {
            LOG.error("Transcribe for MEDIA_VERSION_ID=[{}] was not created successfully. REASON=[{}]", ecmFileVersion != null ? ecmFileVersion.getId() : null, e.getMessage(), e);
            throw new CreateTranscribeException(String.format("Transcribe for MEDIA_VERSION_ID=[%d] was not created successfully. REASON=[%s]", ecmFileVersion != null ? ecmFileVersion.getId() : null, e.getMessage()));
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
    public List<Transcribe> changeStatus(List<Long> ids, String status) throws SaveTranscribeException
    {
        return null;
    }

    @Override
    public void notify(Long id, UserType userType, ActionType action)
    {

    }

    @Override
    public void notify(List<Long> ids, UserType userType, ActionType action)
    {

    }

    @Override
    public void audit(Long id, ActionType action)
    {

    }

    @Override
    public void audit(List<Long> ids, ActionType action)
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

    private boolean allow(EcmFileVersion ecmFileVersion)
    {
        // TODO: Restrict only for Case/Complaints?
        return isAudioOrVideo(ecmFileVersion) &&
                isTranscribeOn() &&
                isAutomaticTranscribeOn() &&
                isLessThan2Hours(ecmFileVersion);
    }

    private boolean isTranscribeOn()
    {
        try
        {
            TranscribeConfiguration configuration = getConfiguration();
            return configuration != null && configuration.isEnabled();
        }
        catch (GetTranscribeConfigurationException e)
        {
            LOG.error("Failed to retrieve Transcribe configuration.", e);
            return false;
        }
    }

    private boolean isAutomaticTranscribeOn()
    {
        try
        {
            TranscribeConfiguration configuration = getConfiguration();
            return configuration != null && configuration.isAutomaticEnabled();
        }
        catch (GetTranscribeConfigurationException e)
        {
            LOG.error("Failed to retrieve Transcribe configuration.", e);
            return false;
        }
    }

    private boolean isLessThan2Hours(EcmFileVersion ecmFileVersion)
    {
        return ecmFileVersion != null && ecmFileVersion.getDurationSeconds() <= 60 * 60 * 2;
    }

    private boolean isAudioOrVideo(EcmFileVersion ecmFileVersion)
    {

        return ecmFileVersion != null &&
               ecmFileVersion.getVersionMimeType() != null &&
               (
                   ecmFileVersion.getVersionMimeType().startsWith(TranscribeConstants.MEDIA_TYPE_AUDIO_RECOGNITION_KEY) ||
                   ecmFileVersion.getVersionMimeType().startsWith(TranscribeConstants.MEDIA_TYPE_VIDEO_RECOGNITION_KEY)
               );
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
}

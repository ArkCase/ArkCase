package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.transcribe.exception.*;
import com.armedia.acm.services.transcribe.factory.TranscribeServiceFactory;
import com.armedia.acm.services.transcribe.model.*;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class ArkCaseTranscribeService extends AbstractArkCaseTranscribeService
{
    private TranscribeConfigurationService transcribeConfigurationService;

    @Override
    public Transcribe create(Long mediaId, Long versionId) throws CreateTranscribeException
    {
        return null;
    }

    @Override
    public Transcribe get(Long id) throws GetTranscribeException
    {
        return null;
    }

    @Override
    public Transcribe getByMediaIdAndVersionId(Long mediaId, Long versionId) throws GetTranscribeException
    {
        return null;
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

    public TranscribeConfigurationService getTranscribeConfigurationService()
    {
        return transcribeConfigurationService;
    }

    public void setTranscribeConfigurationService(TranscribeConfigurationService transcribeConfigurationService)
    {
        this.transcribeConfigurationService = transcribeConfigurationService;
    }
}

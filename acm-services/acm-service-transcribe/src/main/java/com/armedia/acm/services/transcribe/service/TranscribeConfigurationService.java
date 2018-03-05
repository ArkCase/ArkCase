package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.services.transcribe.exception.GetTranscribeConfigurationException;
import com.armedia.acm.services.transcribe.exception.SaveTranscribeConfigurationException;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
public interface TranscribeConfigurationService
{
    /**
     * This method will return Transcribe configuration object from datasource
     *
     * @return TranscribeConfiguration object
     * @throws GetTranscribeConfigurationException
     */
    public TranscribeConfiguration get() throws GetTranscribeConfigurationException;

    /**
     * This method will save provided Transcribe configuration to the datasource
     *
     * @param configuration - Transcribe configuration object
     * @return Saved TranscrribeConfiguration object
     * @throws SaveTranscribeConfigurationException
     */
    public TranscribeConfiguration save(TranscribeConfiguration configuration) throws SaveTranscribeConfigurationException;
}

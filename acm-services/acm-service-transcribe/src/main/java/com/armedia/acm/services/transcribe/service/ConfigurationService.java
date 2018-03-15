package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.SaveConfigurationException;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
public interface ConfigurationService<T>
{
    /**
     * This method will return T configuration object from datasource
     *
     * @return T object
     * @throws GetConfigurationException
     */
    public T get() throws GetConfigurationException;

    /**
     * This method will save provided T configuration to the datasource
     *
     * @param configuration - configuration object
     * @return Saved T object
     * @throws SaveConfigurationException
     */
    public T save(T configuration) throws SaveConfigurationException;
}

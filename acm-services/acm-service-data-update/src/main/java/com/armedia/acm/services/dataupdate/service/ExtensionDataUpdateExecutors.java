package com.armedia.acm.services.dataupdate.service;

import java.util.List;

/**
 * Interface to be implemented by extensions if they want to provide custom
 * {@link AcmDataUpdateExecutor} implementations. These implementations are
 * automatically searched in the Spring context and executed in {@link AcmDataUpdateManager}.
 */
public interface ExtensionDataUpdateExecutors
{
    /**
     * Returns list of executors.
     *
     * @return List<AcmDataUpdateExecutor>
     */
    List<AcmDataUpdateExecutor> getExecutors();
}

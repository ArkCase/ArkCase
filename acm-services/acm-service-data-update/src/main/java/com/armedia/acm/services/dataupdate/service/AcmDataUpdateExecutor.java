package com.armedia.acm.services.dataupdate.service;

/**
 * Data update executors should implement this interface and
 * provide unique executor id and implement operations to be executed.
 */
public interface AcmDataUpdateExecutor
{
    /**
     * Returns the executor id.
     *
     * @return String
     */
    String getUpdateId();

    /**
     * Executes update operations.
     */
    void execute();
}

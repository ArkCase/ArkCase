package com.armedia.acm.services.pipeline.handler;

import com.armedia.acm.services.pipeline.PipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

/**
 * Interface that all handlers for particular entity type must implement.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 26.07.2015.
 */
public interface PipelineHandler<T, S extends PipelineContext>
{
    /**
     * Execute handler actions.
     *
     * @param entity          currently processed entity
     * @param pipelineContext pipeline context
     * @throws PipelineProcessException on error
     */
    void execute(T entity, S pipelineContext) throws PipelineProcessException;

    /**
     * In case of error, try to revert all the changes applied with execute() method.
     *
     * @param entity          currently processed entity
     * @param pipelineContext pipeline context
     * @throws PipelineProcessException on error
     */
    void rollback(T entity, S pipelineContext) throws PipelineProcessException;
}

package com.armedia.acm.services.pipeline;

/*-
 * #%L
 * ACM Service: Pipeline
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

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;

import java.util.List;
import java.util.ListIterator;

/**
 * Pipeline manager which holds a list of pre-save and post-save handlers which are to be executed prior to and after
 * the DAO save method, respectively.
 * <p/>
 * Note: pre-save handlers, save method and post-save handlers should all reside in a single transaction (@Transactional
 * annotated method) so even the changes on the entity applied in the post-save handlers (after the save method) will be
 * persisted at the end of the transaction.
 * <p/>
 * Created by Petar Ilin <petar.ilin@armedia.com> on 26.07.2015.
 */
public class PipelineManager<T, S extends AbstractPipelineContext>
{

    private static final String PRE_SAVE_HANDLER_PROCESSING = "Pre-save handler: [{}] processing";

    private static final String PRE_SAVE_HANDLER_ROLLING_BACK = "Pre-save handler: [{}] rolling back";

    private static final String PRE_SAVE_HANDLER_ROLLBACK_FAILED = "Pre-save handler [{}] rollback failed with {}.";

    private static final String POST_SAVE_HANDLER_PROCESSING = "Post-save handler: [{}] processing";

    private static final String POST_SAVE_HANDLER_ROLLING_BACK = "Post-save handler: [{}] rolling back";

    private static final String POST_SAVE_HANDLER_ROLLBACK_FAILED = "Post-save handler [{}] rollback failed with {}.";
    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * List of handlers executed before saving the entity to database.
     */
    private List<PipelineHandler<T, S>> preSaveHandlers;
    /**
     * List of handlers executed after saving the entity to database.
     */
    private List<PipelineHandler<T, S>> postSaveHandlers;

    public T executeOperation(T entity, S pipelineContext, PipelineManagerOperation<T> operation) throws PipelineProcessException
    {
        executeOnPreSaveHandlers(entity, pipelineContext);
        T result = null;
        try
        {
            result = operation.execute();
            executeOnPostSaveHandlers(result, pipelineContext);
            return result;
        }
        catch (PipelineProcessException e)
        {
            // rollback pre-save handlers before rethrowing the exception;
            ListIterator<PipelineHandler<T, S>> it = preSaveHandlers.listIterator(preSaveHandlers.size());
            rollbackHandlers(result != null ? result : entity, pipelineContext, e, it, PRE_SAVE_HANDLER_ROLLING_BACK,
                    PRE_SAVE_HANDLER_ROLLBACK_FAILED);
            throw e;
        }
    }

    /**
     * Execute all registered pre-save handlers. Stop processing if any of the handlers throws exception and try to
     * rollback the ones that already executed.
     *
     * @param entity
     *            entity to process
     * @param pipelineContext
     *            context associated with this pipeline
     * @throws PipelineProcessException
     *             on error
     * @see #executeOperation(Object, AbstractPipelineContext, PipelineManagerOperation)
     * @deprecated This method has been deprecated, the new <code>executeOperation</code> method that was introduced
     *             should be used instead. <code>executeOperation</code> method will call the <code>pre</code> and
     *             <code>post</code> handlers on the client code behalf in order to make sure that <code>pre</code> and
     *             <code>post</code> handlers are always called in proper order, and to to try to rollback all the
     *             handlers regardless if a handler failed in <code>pre</code> or <code>post</code> operation phase.
     */
    @Deprecated
    public void onPreSave(T entity, S pipelineContext) throws PipelineProcessException
    {
        executeOnPreSaveHandlers(entity, pipelineContext);
    }

    /**
     * Execute all registered pre-save handlers. Stop processing if any of the handlers throws exception and try to
     * rollback the ones that already executed.
     *
     * @param entity
     *            entity to process
     * @param pipelineContext
     *            context associated with this pipeline
     * @throws PipelineProcessException
     *             on error
     */
    protected void executeOnPreSaveHandlers(T entity, S pipelineContext) throws PipelineProcessException
    {
        log.debug("Pre-save handler of [{}] started", entity);
        ListIterator<PipelineHandler<T, S>> it = preSaveHandlers.listIterator();
        try
        {
            executeHandlers(entity, pipelineContext, it, PRE_SAVE_HANDLER_PROCESSING);
        }
        catch (PipelineProcessException e)
        {
            log.warn("Pre-save handler execution failed, going to rollback changes");
            // iterate backwards starting with last iterator position
            rollbackHandlers(entity, pipelineContext, e, it, PRE_SAVE_HANDLER_ROLLING_BACK, PRE_SAVE_HANDLER_ROLLBACK_FAILED);
            // rethrow the exception
            throw e;
        }
        log.debug("Pre-save handler of [{}] finished", entity);
    }

    /**
     * Execute all post-save handlers. Stop processing if any of the handlers throws exception and try to rollback the
     * ones that already executed.
     *
     * @param entity
     *            entity to process
     * @param pipelineContext
     *            context associated with this pipeline
     * @throws PipelineProcessException
     *             on error
     * @see #executeOperation(Object, AbstractPipelineContext, PipelineManagerOperation)
     * @deprecated This method has been deprecated, the new <code>executeOperation</code> method that was introduced
     *             should be used instead. <code>executeOperation</code> method will call the <code>pre</code> and
     *             <code>post</code> handlers on the client code behalf in order to make sure that <code>pre</code> and
     *             <code>post</code> handlers are always called in proper order, and to to try to rollback all the
     *             handlers regardless if a handler failed in <code>pre</code> or <code>post</code> operation phase.
     */
    @Deprecated
    public void onPostSave(T entity, S pipelineContext) throws PipelineProcessException
    {
        executeOnPostSaveHandlers(entity, pipelineContext);
    }

    /**
     * Execute all post-save handlers. Stop processing if any of the handlers throws exception and try to rollback the
     * ones that already executed. The excpetion is propagated, in order to attempt rolling back any pre-handlers that
     * might have been executed.
     *
     * @param entity
     *            entity to process
     * @param pipelineContext
     *            context associated with this pipeline
     * @throws PipelineProcessException
     *             on error
     */
    protected void executeOnPostSaveHandlers(T entity, S pipelineContext) throws PipelineProcessException
    {
        log.debug("Post-save handler of [{}] started", entity);
        ListIterator<PipelineHandler<T, S>> it = postSaveHandlers.listIterator();
        try
        {
            executeHandlers(entity, pipelineContext, it, POST_SAVE_HANDLER_PROCESSING);
        }
        catch (PipelineProcessException e)
        {
            log.warn("Post-save handler execution failed, going to rollback changes");
            // iterate backwards starting with last iterator position
            rollbackHandlers(entity, pipelineContext, e, it, POST_SAVE_HANDLER_ROLLING_BACK, POST_SAVE_HANDLER_ROLLBACK_FAILED);
            // rethrow the exception
            throw e;
        }
        log.debug("Post-save handler of [{}] finished", entity);
    }

    private void executeHandlers(T entity, S pipelineContext, ListIterator<PipelineHandler<T, S>> it, String debugMessage)
            throws PipelineProcessException
    {
        while (it.hasNext())
        {
            PipelineHandler<T, S> handler = it.next();
            log.debug(debugMessage, handler.getClass().getName());
            try
            {
                handler.execute(entity, pipelineContext);
            }
            catch (ParserConfigurationException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void rollbackHandlers(T entity, S pipelineContext, PipelineProcessException e, ListIterator<PipelineHandler<T, S>> it,
            String debugMessage, String warnMessage) throws PipelineProcessException
    {
        while (it.hasPrevious())
        {
            PipelineHandler<T, S> handler = it.previous();
            log.debug(debugMessage, handler.getClass().getName());
            handler.rollback(entity, pipelineContext);
        }
    }

    /**
     * Pre-save handler list setter.
     *
     * @param preSaveHandlers
     */
    public void setPreSaveHandlers(List<PipelineHandler<T, S>> preSaveHandlers)
    {
        this.preSaveHandlers = preSaveHandlers;
    }

    /**
     * Post-save handler list setter.
     *
     * @param postSaveHandlers
     */
    public void setPostSaveHandlers(List<PipelineHandler<T, S>> postSaveHandlers)
    {
        this.postSaveHandlers = postSaveHandlers;
    }

    public static interface PipelineManagerOperation<T>
    {
        T execute() throws PipelineProcessException;
    }
}

package com.armedia.acm.services.pipeline;

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    /**
     * List of handlers executed before saving the entity to database.
     */
    private List<PipelineHandler<T, S>> preSaveHandlers;

    /**
     * List of handlers executed after saving the entity to database.
     */
    private List<PipelineHandler<T, S>> postSaveHandlers;

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static interface PipelineManagerOperation<T>
    {
        T execute();
    }

    public T executeOperation(T entity, S pipelineContext, PipelineManagerOperation<T> operation) throws PipelineProcessException
    {
        onPreSave(entity, pipelineContext);
        T result = operation.execute();
        onPostSave(entity, pipelineContext);
        return result;
    }

    /**
     * Execute all registered pre-save handlers. Stop processing if any of the handlers throws exception
     *
     * @param entity
     *            entity to process
     * @param pipelineContext
     *            context associated with this pipeline
     * @throws PipelineProcessException
     *             on error
     */
    protected void onPreSave(T entity, S pipelineContext) throws PipelineProcessException
    {
        log.debug("Pre-save handler of [{}] started", entity);
        ListIterator<PipelineHandler<T, S>> it = preSaveHandlers.listIterator();
        try
        {
            while (it.hasNext())
            {
                PipelineHandler<T, S> preSaveHandler = it.next();
                log.debug("Pre-save handler: [{}] processing", preSaveHandler.getClass().getName());
                preSaveHandler.execute(entity, pipelineContext);
            }
        } catch (PipelineProcessException e)
        {
            log.warn("Pre-save handler execution failed, going to rollback changes");
            // iterate backwards starting with last iterator position
            while (it.hasPrevious())
            {
                PipelineHandler<T, S> preSaveHandler = it.previous();
                log.debug("Pre-save handler: [{}] rolling back", preSaveHandler.getClass().getName());
                try
                {
                    preSaveHandler.rollback(entity, pipelineContext);
                } catch (PipelineProcessException rollbackException)
                {
                    log.warn("Pre-save handler [{}] rollback failed with {}.", preSaveHandler, rollbackException);
                    e.addSuppressed(rollbackException);
                }
            }
            // rethrow the exception
            throw e;
        }
        log.debug("Pre-save handler of [{}] finished", entity);
    }

    /**
     * Execute all post-save handlers. Stop processing if any of the handlers throws exception
     *
     * @param entity
     *            entity to process
     * @param pipelineContext
     *            context associated with this pipeline
     * @throws PipelineProcessException
     *             on error
     */
    protected void onPostSave(T entity, S pipelineContext) throws PipelineProcessException
    {
        log.debug("Post-save handler of [{}] started", entity);
        ListIterator<PipelineHandler<T, S>> it = postSaveHandlers.listIterator();
        try
        {
            while (it.hasNext())
            {
                PipelineHandler<T, S> postSaveHandler = it.next();
                log.debug("Post-save handler: [{}] processing", postSaveHandler.getClass().getName());
                postSaveHandler.execute(entity, pipelineContext);
            }
        } catch (PipelineProcessException e)
        {
            log.warn("Post-save handler execution failed, going to rollback changes");
            // iterate backwards starting with last iterator position
            while (it.hasPrevious())
            {
                PipelineHandler<T, S> postSaveHandler = it.previous();
                log.debug("Post-save handler: [{}] rolling back", postSaveHandler.getClass().getName());
                try
                {
                    postSaveHandler.rollback(entity, pipelineContext);
                } catch (PipelineProcessException rollbackException)
                {
                    log.warn("Post-save handler [{}] rollback failed with {}.", postSaveHandler, rollbackException);
                    e.addSuppressed(rollbackException);
                }
            }
            // rollback pre-save handlers before rethrowing the exception;
            it = preSaveHandlers.listIterator();
            while (it.hasNext())
            {
                PipelineHandler<T, S> preSaveHandler = it.next();
                log.debug("Pre-save handler: [{}] rolling back", preSaveHandler.getClass().getName());
                try
                {
                    preSaveHandler.rollback(entity, pipelineContext);
                } catch (PipelineProcessException rollbackException)
                {
                    log.warn("Pre-save handler [{}] rollback failed with {}.", preSaveHandler, rollbackException);
                    e.addSuppressed(rollbackException);
                }
            }
            // rethrow the exception
            throw e;
        }
        log.debug("Post-save handler of [{}] finished", entity);
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
}

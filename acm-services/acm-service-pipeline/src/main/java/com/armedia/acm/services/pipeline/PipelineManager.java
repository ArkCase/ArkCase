package com.armedia.acm.services.pipeline;

import com.armedia.acm.core.AcmExtensible;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by Petar Ilin <petar.ilin@armedia.com> on 26.07.2015.
 */
public class PipelineManager<T extends AcmExtensible>
{
    /**
     * List of handlers executed before saving the entity to database.
     */
    private List<PipelineHandler<T>> preSaveHandlers;

    /**
     * List of handlers executed after saving the entity to database.
     */
    private List<PipelineHandler<T>> postSaveHandlers;

    /**
     * Pipeline context.
     */
    private PipelineContext pipelineContext;

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Execute all registered pre-save handlers.
     * Stop processing if any of the handlers throws exception
     *
     * @param entity entity to execute
     * @throws PipelineProcessException on error
     */
    public void onPreSave(T entity) throws PipelineProcessException
    {
        log.debug("Pre-save handler of [{}] started", entity);
        ListIterator<PipelineHandler<T>> it = preSaveHandlers.listIterator();
        try
        {
            while (it.hasNext())
            {
                PipelineHandler<T> preSaveHandler = it.next();
                log.debug("Pre-save handler: [{}] processing", preSaveHandler.getClass().getName());
                preSaveHandler.execute(entity, pipelineContext);
            }
        } catch (PipelineProcessException e)
        {
            log.warn("Pre-save handler execution failed, going to rollback changes");
            // iterate backwards starting with last iterator position
            while (it.hasPrevious())
            {
                PipelineHandler<T> preSaveHandler = it.previous();
                log.debug("Pre-save handler: [{}] rolling back", preSaveHandler.getClass().getName());
                preSaveHandler.rollback(entity, pipelineContext);
            }
            // rethrow the exception
            throw e;
        }
        log.debug("Pre-save handler of [{}] finished", entity);
    }

    /**
     * Execute all post-save handlers.
     * Stop processing if any of the handlers throws exception
     *
     * @param entity case file to execute
     * @throws PipelineProcessException on error
     */
    public void onPostSave(T entity) throws PipelineProcessException
    {
        log.debug("Post-save handler of [{}] started", entity);
        ListIterator<PipelineHandler<T>> it = postSaveHandlers.listIterator();
        try
        {
            while (it.hasNext())
            {
                PipelineHandler<T> postSaveHandler = it.next();
                log.debug("Post-save handler: [{}] processing", postSaveHandler.getClass().getName());
                postSaveHandler.execute(entity, pipelineContext);
            }
        } catch (PipelineProcessException e)
        {
            log.warn("Post-save handler execution failed, going to rollback changes");
            // iterate backwards starting with last iterator position
            while (it.hasPrevious())
            {
                PipelineHandler<T> postSaveHandler = it.previous();
                log.debug("Post-save handler: [{}] rolling back", postSaveHandler.getClass().getName());
                postSaveHandler.rollback(entity, pipelineContext);
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
    public void setPreSaveHandlers(List<PipelineHandler<T>> preSaveHandlers)
    {
        this.preSaveHandlers = preSaveHandlers;
    }

    /**
     * Post-save handler list setter.
     *
     * @param postSaveHandlers
     */
    public void setPostSaveHandlers(List<PipelineHandler<T>> postSaveHandlers)
    {
        this.postSaveHandlers = postSaveHandlers;
    }

    /**
     * Case file saving context getter.
     */
    public PipelineContext getPipelineContext()
    {
        return pipelineContext;
    }

    /**
     * Case file saving context setter.
     *
     * @param pipelineContext
     */
    public void setPipelineContext(PipelineContext pipelineContext)
    {
        this.pipelineContext = pipelineContext;
    }
}

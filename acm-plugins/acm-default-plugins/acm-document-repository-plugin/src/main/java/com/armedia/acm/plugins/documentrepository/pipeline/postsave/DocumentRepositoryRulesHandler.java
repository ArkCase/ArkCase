package com.armedia.acm.plugins.documentrepository.pipeline.postsave;

import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.pipeline.DocumentRepositoryPipelineContext;
import com.armedia.acm.plugins.documentrepository.service.SaveDocumentRepositoryBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apply business rules to a Document Repository
 */
public class DocumentRepositoryRulesHandler implements PipelineHandler<DocumentRepository, DocumentRepositoryPipelineContext>
{

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * Business rule manager.
     */
    private SaveDocumentRepositoryBusinessRule saveRule;

    @Override
    public void execute(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.info("DocumentRepository entering DocumentRepositoryRulesHandler : [{}]", entity.getName());

        entity = saveRule.applyRules(entity);

        log.info("DocumentRepository exiting DocumentRepositoryRulesHandler : [{}]", entity.getName());

    }

    @Override
    public void rollback(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public SaveDocumentRepositoryBusinessRule getSaveRule()
    {
        return saveRule;
    }

    public void setSaveRule(SaveDocumentRepositoryBusinessRule saveRule)
    {
        this.saveRule = saveRule;
    }
}

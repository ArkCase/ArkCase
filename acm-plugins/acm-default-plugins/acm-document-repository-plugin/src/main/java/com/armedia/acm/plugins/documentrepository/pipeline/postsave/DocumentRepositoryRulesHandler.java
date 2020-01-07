package com.armedia.acm.plugins.documentrepository.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Document Repository
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

import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.pipeline.DocumentRepositoryPipelineContext;
import com.armedia.acm.plugins.documentrepository.service.SaveDocumentRepositoryBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Apply business rules to a Document Repository
 */
public class DocumentRepositoryRulesHandler implements PipelineHandler<DocumentRepository, DocumentRepositoryPipelineContext>
{

    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
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

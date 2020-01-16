package com.armedia.acm.plugins.documentrepository.service;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.search.exception.SolrException;

import org.springframework.security.core.Authentication;

public interface DocumentRepositoryService
{
    DocumentRepository findById(Long id);

    DocumentRepository findByName(String name);

    DocumentRepository save(DocumentRepository documentRepository, Authentication authentication)
            throws PipelineProcessException;

    void delete(Long id, Authentication authentication) throws AcmObjectNotFoundException, AcmUserActionFailedException;

    String getDocumentRepositoryTasks(Long id, String sort, int startRow, int maxRows, Authentication authentication) throws SolrException;
}

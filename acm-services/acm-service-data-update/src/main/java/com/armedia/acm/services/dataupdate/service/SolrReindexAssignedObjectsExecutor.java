package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.services.participants.model.AcmAssignedObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SolrReindexAssignedObjectsExecutor implements AcmDataUpdateExecutor
{
    @PersistenceContext
    private EntityManager entityManager;
    private SolrReindexService solrReindexService;
    private static final Logger log = LoggerFactory.getLogger(SolrReindexAssignedObjectsExecutor.class);

    @Override
    public String getUpdateId()
    {
        return "solr-assigned-objects-reindex";
    }

    @Override
    public void execute()
    {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        List<Class> assignedObjects = entities.stream()
                .filter(entityType -> AcmAssignedObject.class.isAssignableFrom(entityType.getJavaType()))
                .map(Type::getJavaType)
                .peek(it -> log.debug("Found entity [{}] for solr reindex", it.getSimpleName()))
                .collect(Collectors.toList());
        solrReindexService.reindex(assignedObjects);
    }

    public SolrReindexService getSolrReindexService()
    {
        return solrReindexService;
    }

    public void setSolrReindexService(SolrReindexService solrReindexService)
    {
        this.solrReindexService = solrReindexService;
    }
}

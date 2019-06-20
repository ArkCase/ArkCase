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

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SolrReindexAssignedObjectsExecutor implements AcmDataUpdateExecutor
{
    private SolrReindexService solrReindexService;
    private String packages;
    private static final Logger log = LogManager.getLogger(SolrReindexAssignedObjectsExecutor.class);

    @Override
    public String getUpdateId()
    {
        return "solr-assigned-objects-reindex-2";
    }

    @Override
    public void execute()
    {
        Object[] packagesToScan = Arrays.stream(packages.split(","))
                .map(it -> StringUtils.substringBeforeLast(it, ".*"))
                .toArray();

        Reflections reflections = new Reflections(packagesToScan);

        Set<Class<? extends AcmAssignedObject>> acmObjects = reflections.getSubTypesOf(AcmAssignedObject.class);

        List<Class> assignedObjects = acmObjects.stream()
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

    public String getPackages()
    {
        return packages;
    }

    public void setPackages(String packages)
    {
        this.packages = packages;
    }
}

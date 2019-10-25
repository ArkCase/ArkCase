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

import com.armedia.acm.quartz.scheduler.AcmSchedulerService;

import org.quartz.JobDataMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SolrReindexService
{
    private AcmSchedulerService schedulerService;

    @PersistenceContext
    private EntityManager entityManager;

    public void reindex(List<Class> entities)
    {
        entities.addAll(getExtendedClasses(entities));
        JobDataMap lastRunDatePerObject = schedulerService.getJobDataMap("jpaBatchUpdateJob");
        if (lastRunDatePerObject == null)
        {
            return;
        }
        entities.forEach(entity -> lastRunDatePerObject.remove(entity.getName()));

        schedulerService.triggerJob("jpaBatchUpdateJob", lastRunDatePerObject);
    }

    private List<Class> getExtendedClasses(List<Class> entities)
    {
        List<Class> extendedClasses = new ArrayList<>();

        for (Class clazz : entities)
        {
            Set<EntityType<?>> entityTypes = getEntityManager().getMetamodel().getEntities();
            extendedClasses.addAll(entityTypes.stream()
                    .filter(entityType -> clazz.isAssignableFrom(entityType.getJavaType()))
                    .map(entityType -> entityType.getJavaType()).collect(Collectors.toSet()));
        }

        return extendedClasses;
    }

    public AcmSchedulerService getSchedulerService()
    {
        return schedulerService;
    }

    public void setSchedulerService(AcmSchedulerService schedulerService)
    {
        this.schedulerService = schedulerService;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
}

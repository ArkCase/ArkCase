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

import com.armedia.acm.services.dataupdate.model.AcmDataUpdateExecutorLog;
import com.armedia.acm.services.search.service.IJpaBatchUpdatePrerequisite;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that will manage all registered {@link AcmDataUpdateExecutor} beans
 * and ensure to execute only once. The execution is after Root Application
 * context is initialized to ensure that all beans are initialized and can be used.
 * Executors may depend one of another, so order of executions is enforced
 * and planned to finish in one transaction.
 */
public class AcmDataUpdateManager implements ApplicationListener<ContextRefreshedEvent>, IJpaBatchUpdatePrerequisite
{
    private static final Logger log = LogManager.getLogger(AcmDataUpdateManager.class);
    private AcmDataUpdateService dataUpdateService;
    private final Consumer<AcmDataUpdateExecutor> dataUpdateExecutor = service -> {
        log.debug("Execute updates from: [{}]", service.getUpdateId());
        service.execute();
        dataUpdateService.save(service.getUpdateId());
    };
    private List<AcmDataUpdateExecutor> dataUpdateExecutors;
    @Autowired(required = false)
    private ExtensionDataUpdateExecutors extensionDataUpdateExecutors;

    private boolean finished = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        boolean isRootContext = ((ApplicationContext) event.getSource()).getParent() == null;
        if (isRootContext)
        {
            Set<String> executedExecutorIds = dataUpdateService.findAll()
                    .stream()
                    .map(AcmDataUpdateExecutorLog::getExecutorId)
                    .collect(Collectors.toSet());

            Predicate<AcmDataUpdateExecutor> updatesNotExecuted = service -> !executedExecutorIds.contains(service.getUpdateId());

            log.info("Starting [{}] core data update executors...", dataUpdateExecutors.size());
            dataUpdateExecutors.stream()
                    .filter(updatesNotExecuted)
                    .forEach(dataUpdateExecutor);

            if (extensionDataUpdateExecutors != null && extensionDataUpdateExecutors.getExecutors() != null)
            {
                log.info("Starting [{}] extensions data update executors...",
                        extensionDataUpdateExecutors.getExecutors().size());

                extensionDataUpdateExecutors.getExecutors().stream()
                        .filter(updatesNotExecuted)
                        .forEach(dataUpdateExecutor);
            }

            finished = true;
        }
    }

    public void setDataUpdateService(AcmDataUpdateService dataUpdateService)
    {
        this.dataUpdateService = dataUpdateService;
    }

    public void setDataUpdateExecutors(List<AcmDataUpdateExecutor> dataUpdateExecutors)
    {
        this.dataUpdateExecutors = dataUpdateExecutors;
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }
}

package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.dataupdate.model.AcmDataUpdateExecutorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;

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
public class AcmDataUpdateManager implements ApplicationListener<ContextRefreshedEvent>
{
    private AcmDataUpdateService dataUpdateService;

    private List<AcmDataUpdateExecutor> dataUpdateExecutors;

    @Autowired(required = false)
    private ExtensionDataUpdateExecutors extensionDataUpdateExecutors;

    private static final Logger log = LoggerFactory.getLogger(AcmDataUpdateManager.class);

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        boolean isRootContext = ((ApplicationContext) event.getSource()).getParent() == null;
        if (isRootContext)
        {
            Set<String> executedExecutorIds = dataUpdateService.findAll()
                    .stream()
                    .map(AcmDataUpdateExecutorLog::getExecutorId)
                    .collect(Collectors.toSet());

            Predicate<AcmDataUpdateExecutor> updatesNotExecuted =
                    service -> !executedExecutorIds.contains(service.getUpdateId());

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
        }
    }

    private final Consumer<AcmDataUpdateExecutor> dataUpdateExecutor = service -> {
        log.debug("Execute updates from: [{}]", service.getUpdateId());
        service.execute();
        dataUpdateService.save(service.getUpdateId());
    };

    public void setDataUpdateService(AcmDataUpdateService dataUpdateService)
    {
        this.dataUpdateService = dataUpdateService;
    }

    public void setDataUpdateExecutors(List<AcmDataUpdateExecutor> dataUpdateExecutors)
    {
        this.dataUpdateExecutors = dataUpdateExecutors;
    }
}

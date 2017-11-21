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
import java.util.stream.Collectors;

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
        boolean rootAppContext = ((ApplicationContext) event.getSource()).getParent() == null;
        if (rootAppContext)
        {
            Set<String> executedDataUpdates = dataUpdateService.findAll()
                    .stream()
                    .map(AcmDataUpdateExecutorLog::getExecutorId)
                    .collect(Collectors.toSet());

            log.info("Starting [{}] core data update executors...", dataUpdateExecutors.size());
            dataUpdateExecutors.stream()
                    .filter(service -> !executedDataUpdates.contains(service.getUpdateId()))
                    .forEach(service -> {
                        log.debug("Execute updates from: [{}]", service.getUpdateId());
                        service.execute();
                        dataUpdateService.save(service.getUpdateId());
                    });

            if (extensionDataUpdateExecutors != null && extensionDataUpdateExecutors.getExecutors() != null)
            {
                log.info("Starting [{}] extensions data update executors...",
                        extensionDataUpdateExecutors.getExecutors().size());
                extensionDataUpdateExecutors.getExecutors().forEach(service -> {
                    log.debug("Execute updates from: [{}]", service.getUpdateId());
                    service.execute();
                    dataUpdateService.save(service.getUpdateId());
                });
            }
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
}

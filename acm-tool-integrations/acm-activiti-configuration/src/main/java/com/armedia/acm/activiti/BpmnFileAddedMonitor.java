package com.armedia.acm.activiti;

import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import org.activiti.engine.ActivitiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by armdev on 4/11/14.
 */
public class BpmnFileAddedMonitor implements ApplicationListener<ConfigurationFileAddedEvent> {
    private AcmBpmnService acmBpmnService;

    private transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(ConfigurationFileAddedEvent configurationFileAddedEvent) {
        File eventFile = configurationFileAddedEvent.getConfigFile();
        if (eventFile.getParentFile().getName().equals("activiti") && eventFile.getName().endsWith("bpmn20.xml")) {

            try {
                if (log.isDebugEnabled()) {
                    log.debug("Deploying new Activiti file: " + eventFile.getCanonicalPath());
                }
                acmBpmnService.deploy(eventFile, false, false);

                if (log.isDebugEnabled()) {
                    log.debug("... finished deploying from: " + eventFile.getCanonicalPath());
                }
            } catch (IOException | ActivitiException e) {
                log.error("Could not deploy Activiti definition file: " + e.getMessage(), e);
            }
        }
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService) {
        this.acmBpmnService = acmBpmnService;
    }
}

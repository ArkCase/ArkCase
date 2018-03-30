package com.armedia.acm.service.stateofarkcase.service;

import com.armedia.acm.service.stateofarkcase.dao.StateOfArkcaseRegistry;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;
import com.armedia.acm.service.stateofarkcase.model.StateOfArkcase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Generates state of arkcase report
 */
public class StateOfArkcaseReportGenerator
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private StateOfArkcaseRegistry stateOfArkcaseRegistry;
    private ObjectMapper mapper;

    /**
     * method for generating StateOfArkcase report.
     * Collects all the data from all registered status providers
     * 
     * @return StateOfArkcase as json string
     */
    public JsonNode generateReportAsJSON()
    {
        StateOfArkcase stateOfArkcase = generateReport();
        return mapper.valueToTree(stateOfArkcase);
    }

    /**
     * method for generating StateOfArkcase report.
     * Collects all the data from all registered status providers
     *
     * @return StateOfArkcase object
     */
    public StateOfArkcase generateReport()
    {
        long stateOfArkcaseStartTime = System.currentTimeMillis();
        log.debug("Generating of state of arkcase report started.");
        StateOfArkcase stateOfArkcase = new StateOfArkcase();
        stateOfArkcase.setDateGenerated(LocalDateTime.now());
        for (StateOfModuleProvider provider : stateOfArkcaseRegistry.getStateOfModuleProviders())
        {
            long moduleStartTime = System.currentTimeMillis();
            StateOfModule moduleState = provider.getModuleState();
            log.debug("State of module [{}] generated in [{}] milliseconds.", provider.getModuleName(),
                    System.currentTimeMillis() - moduleStartTime);
            stateOfArkcase.addModuleState(provider.getModuleName(), moduleState);
        }
        log.info("Generating of state of arkcase report finished in [{}] milliseconds.",
                System.currentTimeMillis() - stateOfArkcaseStartTime);
        return stateOfArkcase;
    }

    public void setStateOfArkcaseRegistry(StateOfArkcaseRegistry stateOfArkcaseRegistry)
    {
        this.stateOfArkcaseRegistry = stateOfArkcaseRegistry;
    }

    public void setMapper(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }
}

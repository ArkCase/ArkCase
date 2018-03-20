package com.armedia.acm.service.stateofarkcase.service;

import com.armedia.acm.service.stateofarkcase.dao.StateOfArkcaseRegistry;
import com.armedia.acm.service.stateofarkcase.exceptions.StateOfArkcaseReportException;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;
import com.armedia.acm.service.stateofarkcase.model.StateOfArkcase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Generates state of arkcase report
 */
class StateOfArkcaseReportGenerator
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
    public String generateReportAsJSON()
    {
        StateOfArkcase stateOfArkcase = generateReport();
        try
        {
            return mapper.writeValueAsString(stateOfArkcase);
        }
        catch (JsonProcessingException e)
        {
            log.error("Can't generate state of Arkase report.", e);
            throw new StateOfArkcaseReportException(e);
        }
    }

    /**
     * method for generating StateOfArkcase report.
     * Collects all the data from all registered status providers
     *
     * @return StateOfArkcase object
     */
    public StateOfArkcase generateReport()
    {
        StateOfArkcase stateOfArkcase = new StateOfArkcase();
        stateOfArkcase.setDateGenerated(LocalDateTime.now());
        for (StateOfModuleProvider provider : stateOfArkcaseRegistry.getStatesOfModules())
        {
            stateOfArkcase.addModuleState(provider.getModuleName(), provider.getModuleState());
        }
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

package com.armedia.acm.service.stateofarkcase.service;

/*-
 * #%L
 * ACM Service: State of Arkcase
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

import com.armedia.acm.service.stateofarkcase.dao.StateOfArkcaseRegistry;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;
import com.armedia.acm.service.stateofarkcase.model.StateOfArkcase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Generates state of arkcase report
 */
public class StateOfArkcaseReportGenerator
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private StateOfArkcaseRegistry stateOfArkcaseRegistry;
    private ObjectMapper mapper;

    /**
     * method for generating StateOfArkcase report.
     * Collects all the data from all registered status providers
     *
     * If day is provided(can be null), all records are collected also including that day
     *
     * @param day
     *            for which day should be generated
     *
     * @return StateOfArkcase as json string
     */
    public JsonNode generateReportAsJSON(LocalDate day)
    {
        StateOfArkcase stateOfArkcase = generateReport(day);
        return mapper.valueToTree(stateOfArkcase);
    }

    /**
     * method for generating StateOfArkcase report.
     * Collects all the data from all registered status providers
     *
     * If day is provided(can be null), all records are collected also including that day
     *
     * @param day
     *            for which day should be generated
     *
     * @return StateOfArkcase object
     */
    public StateOfArkcase generateReport(LocalDate day)
    {
        long stateOfArkcaseStartTime = System.currentTimeMillis();
        log.debug("Generating of state of arkcase report started.");
        StateOfArkcase stateOfArkcase = new StateOfArkcase();
        stateOfArkcase.setDateGenerated(LocalDateTime.now());
        for (StateOfModuleProvider provider : stateOfArkcaseRegistry.getStateOfModuleProviders())
        {
            long moduleStartTime = System.currentTimeMillis();
            StateOfModule moduleState = day != null ? provider.getModuleState(day) : provider.getModuleState();
            log.debug("State of module [{}] generated in [{}] milliseconds.", provider.getModuleName(),
                    System.currentTimeMillis() - moduleStartTime);
            stateOfArkcase.addModuleState(provider.getModuleName(), moduleState);
        }
        log.debug("Generating of state of arkcase report finished in [{}] milliseconds.",
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

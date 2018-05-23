package com.armedia.acm.service.stateofarkcase.model;

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

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StateOfArkcase
{
    private LocalDateTime dateGenerated;
    private Map<String, StateOfModule> modulesState = new HashMap<>();

    public LocalDateTime getDateGenerated()
    {
        return dateGenerated;
    }

    public void setDateGenerated(LocalDateTime dateGenerated)
    {
        this.dateGenerated = dateGenerated;
    }

    @JsonAnyGetter
    public Map<String, StateOfModule> getModulesState()
    {
        return Collections.unmodifiableMap(modulesState);
    }

    public void addModuleState(String moduleName, StateOfModule moduleState)
    {
        modulesState.put(moduleName, moduleState);
    }

    public boolean containsModuleState(String moduleName)
    {
        return modulesState.containsKey(moduleName);
    }

    public void removeModuleState(String moduleName)
    {
        modulesState.remove(moduleName);
    }
}

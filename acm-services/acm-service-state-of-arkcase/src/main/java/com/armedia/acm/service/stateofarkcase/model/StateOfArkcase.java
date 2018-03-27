package com.armedia.acm.service.stateofarkcase.model;

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

package com.armedia.acm.service.stateofarkcase.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StateOfArkcase
{
    private LocalDateTime dateGenerated;
    private Map<String, Object> modulesState = new HashMap<>();

    public LocalDateTime getDateGenerated()
    {
        return dateGenerated;
    }

    public void setDateGenerated(LocalDateTime dateGenerated)
    {
        this.dateGenerated = dateGenerated;
    }

    @JsonAnyGetter
    public Map<String, Object> getModulesState()
    {
        return modulesState;
    }

    public void addModuleState(String moduleName, Object moduleState)
    {
        modulesState.put(moduleName, moduleState);
    }
}

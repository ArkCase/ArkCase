package com.armedia.acm.service.stateofarkcase.service;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;
import com.armedia.acm.service.stateofarkcase.model.TestModuleState;

import java.time.LocalDate;

public class TestModuleUsersStateProvider implements StateOfModuleProvider
{
    @Override
    public String getModuleName()
    {
        return "test_module_users";
    }

    @Override
    public StateOfModule getModuleState()
    {
        return getModuleState(LocalDate.now());
    }

    @Override
    public StateOfModule getModuleState(LocalDate day)
    {
        TestModuleState state = new TestModuleState();
        state.addProperty("numberOfUsers", 5)
                .addProperty("weekOfReport", 36)
                .addProperty("addedNewUsers", 1)
                .addProperty("removedUsers", 2);

        return state;
    }
}

package com.armedia.acm.service.stateofarkcase.interfaces;

/**
 * Interface which needs to be implemented and registered as spring bean
 * in each module which need to provide it's current state.
 */
public interface StateOfModuleProvider
{
    String getModuleName();

    StateOfModule getModuleState();
}

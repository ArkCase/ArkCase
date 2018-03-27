package com.armedia.acm.service.stateofarkcase.dao;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import java.util.List;

/**
 * 
 * Repository for holding all the providers of the state of the module.
 *
 */
public interface StateOfArkcaseRegistry
{
    /**
     * manually register state of module provider
     * 
     * @param stateOfModuleProvider
     */
    void registerStateOfModuleProvider(StateOfModuleProvider stateOfModuleProvider);

    /**
     * get all registered state of module providers
     * 
     * @return StateOfModuleProvider
     */
    List<StateOfModuleProvider> getStateOfModuleProviders();
}

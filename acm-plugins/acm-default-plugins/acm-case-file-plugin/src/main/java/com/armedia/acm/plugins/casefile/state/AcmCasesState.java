package com.armedia.acm.plugins.casefile.state;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;

public class AcmCasesState extends StateOfModule
{
    private Long numberOfCases;

    public Long getNumberOfCases()
    {
        return numberOfCases;
    }

    public void setNumberOfCases(Long numberOfCases)
    {
        this.numberOfCases = numberOfCases;
    }
}

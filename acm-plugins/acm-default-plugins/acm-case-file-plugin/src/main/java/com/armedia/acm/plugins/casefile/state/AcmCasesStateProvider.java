package com.armedia.acm.plugins.casefile.state;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

public class AcmCasesStateProvider implements StateOfModuleProvider
{
    private CaseFileDao caseFileDao;

    @Override
    public String getModuleName()
    {
        return "acm-cases";
    }

    @Override
    public StateOfModule getModuleState()
    {
        AcmCasesState acmCasesState = new AcmCasesState();
        acmCasesState.setNumberOfCases(caseFileDao.getCaseCount());
        return acmCasesState;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}

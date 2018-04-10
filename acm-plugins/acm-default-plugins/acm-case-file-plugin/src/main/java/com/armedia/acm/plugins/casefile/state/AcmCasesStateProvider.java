package com.armedia.acm.plugins.casefile.state;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import java.time.LocalDate;

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
        return getModuleState(LocalDate.now());
    }

    @Override
    public StateOfModule getModuleState(LocalDate day)
    {
        AcmCasesState acmCasesState = new AcmCasesState();
        acmCasesState.setNumberOfCases(caseFileDao.getCaseCount(day.atTime(23, 59, 59)));
        return acmCasesState;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}

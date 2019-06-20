package com.armedia.acm.plugins.casefile.state;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalDate;

public class AcmCasesStateProvider implements StateOfModuleProvider
{
    private transient final Logger log = LogManager.getLogger(getClass());
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
        try
        {
            acmCasesState.setNumberOfCases(caseFileDao.getCaseCount(day.atTime(23, 59, 59)));
        }
        catch (Exception e)
        {
            log.error("Not able to provide cases state.", e.getMessage());
            acmCasesState.addProperty("error", "Not able to provide status." + e.getMessage());
        }
        return acmCasesState;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}

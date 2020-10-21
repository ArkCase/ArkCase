package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.services.exemption.dao.ExemptionCodeDao;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.exemption.service.ExemptionStatuteService;

import java.util.List;

public class ExemptionCodeStatuteToExemptionStatuteClassUpdateExecutor implements AcmDataUpdateExecutor
{
    private ExemptionCodeDao exemptionCodeDao;
    private ExemptionStatuteService exemptionStatuteService;

    @Override
    public String getUpdateId()
    {

        {
            return "exemption_code_statute_to_exemption_statute_class";
        }
    }

    @Override
    public void execute()
    {

        List<ExemptionCode> exemptionCodeList = getExemptionCodeDao().getAllExemptionCodesWithExemptionStatuteFilled();

        if (exemptionCodeList != null && !exemptionCodeList.isEmpty())
            for (ExemptionCode exemptionCode : exemptionCodeList)
            {
                getExemptionStatuteService().saveExemptionStatutesFromExemptionCodesExecutor(exemptionCode);
            }
    }

    public ExemptionCodeDao getExemptionCodeDao()
    {
        return exemptionCodeDao;
    }

    public void setExemptionCodeDao(ExemptionCodeDao exemptionCodeDao)
    {
        this.exemptionCodeDao = exemptionCodeDao;
    }

    public ExemptionStatuteService getExemptionStatuteService()
    {
        return exemptionStatuteService;
    }

    public void setExemptionStatuteService(ExemptionStatuteService exemptionStatuteService)
    {
        this.exemptionStatuteService = exemptionStatuteService;
    }
}

package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.exemption.dao.ExemptionCodeDao;
import com.armedia.acm.services.exemption.exception.SaveExemptionStatuteException;
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

        List<ExemptionCode> exemptionCodeList = getExemptionCodeDao().getAllExemptionCode3();

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

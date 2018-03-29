package com.armedia.acm.audit.state;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AcmAuditStateProvider implements StateOfModuleProvider
{
    private AuditDao auditDao;

    @Override
    public String getModuleName()
    {
        return "acm-audit";
    }

    @Override
    public StateOfModule getModuleState()
    {
        AcmAuditState acmAuditState = new AcmAuditState();
        LocalDate now = LocalDate.now();

        String eventType = "";
        acmAuditState.setTotalLoginPastSevenDays(auditDao.getCountAuditEventSince(eventType, now.minus(7, ChronoUnit.DAYS)));
        acmAuditState.setTotalLoginPastThirtyDays(auditDao.getCountAuditEventSince(eventType, now.minus(30, ChronoUnit.DAYS)));

        return acmAuditState;
    }

    public void setAuditDao(AuditDao auditDao)
    {
        this.auditDao = auditDao;
    }
}

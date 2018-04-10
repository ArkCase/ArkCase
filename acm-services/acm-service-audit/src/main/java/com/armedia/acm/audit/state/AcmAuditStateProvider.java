package com.armedia.acm.audit.state;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AcmAuditStateProvider implements StateOfModuleProvider
{
    public static final String COM_ARMEDIA_ACM_LOGIN_STATE = "com.armedia.acm.login";
    private AuditDao auditDao;

    @Override
    public String getModuleName()
    {
        return "acm-audit";
    }

    @Override
    public StateOfModule getModuleState()
    {
        return getModuleState(LocalDate.now());
    }

    @Override
    public StateOfModule getModuleState(LocalDate day)
    {
        AcmAuditState acmAuditState = new AcmAuditState();
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime to = day.atTime(23, 59, 59);
        acmAuditState.setTotalLoginPastSevenDays(auditDao.getCountAuditEventSince(COM_ARMEDIA_ACM_LOGIN_STATE,
                now.minus(7, ChronoUnit.DAYS), to));
        acmAuditState.setTotalLoginPastThirtyDays(auditDao.getCountAuditEventSince(COM_ARMEDIA_ACM_LOGIN_STATE,
                now.minus(30, ChronoUnit.DAYS), to));
        return acmAuditState;
    }

    public void setAuditDao(AuditDao auditDao)
    {
        this.auditDao = auditDao;
    }
}

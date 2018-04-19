package com.armedia.acm.auth.state;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AcmLoginStateProvider implements StateOfModuleProvider
{
    public static final String COM_ARMEDIA_ACM_LOGIN_STATE = "com.armedia.acm.login";
    private AuditDao auditDao;

    @Override
    public String getModuleName()
    {
        return "acm-login";
    }

    @Override
    public StateOfModule getModuleState()
    {
        return getModuleState(LocalDate.now());
    }

    @Override
    public StateOfModule getModuleState(LocalDate day)
    {
        AcmLoginState acmLoginState = new AcmLoginState();
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime to = day.atTime(23, 59, 59);
        acmLoginState.setTotalLoginPastSevenDays(auditDao.getCountAuditEventSince(COM_ARMEDIA_ACM_LOGIN_STATE,
                now.minus(7, ChronoUnit.DAYS), to));
        acmLoginState.setTotalLoginPastThirtyDays(auditDao.getCountAuditEventSince(COM_ARMEDIA_ACM_LOGIN_STATE,
                now.minus(30, ChronoUnit.DAYS), to));
        return acmLoginState;
    }

    public void setAuditDao(AuditDao auditDao)
    {
        this.auditDao = auditDao;
    }
}

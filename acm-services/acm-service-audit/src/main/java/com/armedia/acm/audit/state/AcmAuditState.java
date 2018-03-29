package com.armedia.acm.audit.state;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;

public class AcmAuditState extends StateOfModule
{
    private Long totalLoginPastSevenDays;
    private Long totalLoginPastThirtyDays;

    public Long getTotalLoginPastSevenDays()
    {
        return totalLoginPastSevenDays;
    }

    public void setTotalLoginPastSevenDays(Long totalLoginPastSevenDays)
    {
        this.totalLoginPastSevenDays = totalLoginPastSevenDays;
    }

    public Long getTotalLoginPastThirtyDays()
    {
        return totalLoginPastThirtyDays;
    }

    public void setTotalLoginPastThirtyDays(Long totalLoginPastThirtyDays)
    {
        this.totalLoginPastThirtyDays = totalLoginPastThirtyDays;
    }

}

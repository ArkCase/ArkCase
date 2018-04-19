package com.armedia.acm.auth.state;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;

public class AcmLoginState extends StateOfModule
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

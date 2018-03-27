package com.armedia.acm.services.users.state;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;

public class AcmUsersState extends StateOfModule
{
    private Long numberOfUsers;
    private Long totalLoginPastSevenDays;
    private Long distinctLoginPastSevenDays;
    private Long totalLoginPassivityDays;
    private Long distinctLoginPastThirtyDays;

    public Long getTotalLoginPastSevenDays()
    {
        return totalLoginPastSevenDays;
    }

    public void setTotalLoginPastSevenDays(Long totalLoginPastSevenDays)
    {
        this.totalLoginPastSevenDays = totalLoginPastSevenDays;
    }

    public Long getDistinctLoginPastSevenDays()
    {
        return distinctLoginPastSevenDays;
    }

    public void setDistinctLoginPastSevenDays(Long distinctLoginPastSevenDays)
    {
        this.distinctLoginPastSevenDays = distinctLoginPastSevenDays;
    }

    public Long getTotalLoginPassivityDays()
    {
        return totalLoginPassivityDays;
    }

    public void setTotalLoginPassivityDays(Long totalLoginPassivityDays)
    {
        this.totalLoginPassivityDays = totalLoginPassivityDays;
    }

    public Long getDistinctLoginPastThirtyDays()
    {
        return distinctLoginPastThirtyDays;
    }

    public void setDistinctLoginPastThirtyDays(Long distinctLoginPastThirtyDays)
    {
        this.distinctLoginPastThirtyDays = distinctLoginPastThirtyDays;
    }

    public Long getNumberOfUsers()
    {
        return numberOfUsers;
    }

    public void setNumberOfUsers(Long numberOfUsers)
    {
        this.numberOfUsers = numberOfUsers;
    }
}

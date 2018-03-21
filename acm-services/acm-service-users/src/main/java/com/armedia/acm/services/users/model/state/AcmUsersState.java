package com.armedia.acm.services.users.model.state;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;

public class AcmUsersState extends StateOfModule
{
    private Long numberOfUsers;

    public Long getNumberOfUsers()
    {
        return numberOfUsers;
    }

    public void setNumberOfUsers(Long numberOfUsers)
    {
        this.numberOfUsers = numberOfUsers;
    }
}

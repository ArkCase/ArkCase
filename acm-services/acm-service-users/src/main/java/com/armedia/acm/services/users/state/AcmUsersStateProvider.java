package com.armedia.acm.services.users.state;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;
import com.armedia.acm.services.users.dao.UserDao;

import java.time.LocalDate;

public class AcmUsersStateProvider implements StateOfModuleProvider
{
    private UserDao userDao;

    @Override
    public String getModuleName()
    {
        return "acm-users";
    }

    @Override
    public StateOfModule getModuleState()
    {
        return getModuleState(LocalDate.now());
    }

    @Override
    public StateOfModule getModuleState(LocalDate day)
    {
        AcmUsersState acmUsersState = new AcmUsersState();
        acmUsersState.setNumberOfUsers(userDao.getUserCount(day.atTime(23, 59, 59)));
        return acmUsersState;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}

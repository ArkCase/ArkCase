package com.armedia.acm.services.users.service;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.state.AcmUsersState;

public class UsersStateProvider implements StateOfModuleProvider
{
    private AcmUserService acmUserService;
    private UserDao userDao;

    @Override
    public String getModuleName()
    {
        return "acm-users";
    }

    @Override
    public StateOfModule getModuleState()
    {
        AcmUsersState acmUsersState = new AcmUsersState();
        acmUsersState.setNumberOfUsers(userDao.getUserCount());
        return acmUsersState;
    }

    public void setAcmUserService(AcmUserService acmUserService)
    {
        this.acmUserService = acmUserService;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}

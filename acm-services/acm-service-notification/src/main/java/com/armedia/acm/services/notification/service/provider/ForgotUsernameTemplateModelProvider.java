package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;

import java.util.List;
import java.util.stream.Collectors;

public class ForgotUsernameTemplateModelProvider implements TemplateModelProvider
{

    private UserDao userDao;

    @Override
    public Object getModel(Notification notification)
    {
        List<AcmUser> users = userDao.findByEmailAddress(notification.getUser());
        users = users.stream()
                .filter(user -> user.getUserState() == AcmUserState.VALID)
                .collect(Collectors.toList());
        List<String> userAccounts = users.stream()
                .map(AcmUser::getUserId)
                .collect(Collectors.toList());
        notification.setAccountsNumber(users.size());
        notification.setUserAccounts(String.join(",", userAccounts));
        return notification;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}

package com.armedia.acm.services.users.service.tracker;

import com.armedia.acm.services.users.model.tracker.UserTracker;

public class UserTrackerServiceImpl implements UserTrackerService
{

    private ThreadLocal<UserTracker> userTracker = new ThreadLocal<UserTracker>();

    @Override
    public void trackUser(String ipAddress)
    {
        userTracker.set(new UserTracker(ipAddress));
    }

    @Override
    public UserTracker getTrackedUser()
    {
        return userTracker.get();
    }

}

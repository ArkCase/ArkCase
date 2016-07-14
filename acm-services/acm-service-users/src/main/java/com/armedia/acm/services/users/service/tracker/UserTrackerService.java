package com.armedia.acm.services.users.service.tracker;

import com.armedia.acm.services.users.model.tracker.UserTracker;

public interface UserTrackerService
{
    void trackUser(String ipAddress);

    UserTracker getTrackedUser();
}

package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.objectchangestatus.service.ChangeObjectStatusService;

/**
 * Created by armdev on 4/1/15.
 */
public class MockChangeObjectStatusService implements ChangeObjectStatusService
{
    int timesCalled = 0;

    @Override
    public void change(Long objectId, String objectType, String status)
    {
        ++timesCalled;
    }

    public int getTimesCalled()
    {
        return timesCalled;
    }

    public void setTimesCalled(int timesCalled)
    {
        this.timesCalled = timesCalled;
    }
}

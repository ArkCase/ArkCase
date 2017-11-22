package com.armedia.acm.data;

import com.armedia.acm.core.exceptions.AcmAccessControlException;

/**
 * Created by armdev on 11/24/14.
 */
public interface AcmBeforeUpdateListener
{
    void beforeUpdate(Object object, Object originalObject) throws AcmAccessControlException;
}

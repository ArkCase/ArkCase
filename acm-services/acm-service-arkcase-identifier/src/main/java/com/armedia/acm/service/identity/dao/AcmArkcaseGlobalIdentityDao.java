package com.armedia.acm.service.identity.dao;

import com.armedia.acm.service.identity.exceptions.AcmIdentityException;

/**
 * Provides information of global identity. All instances of arkcase must share same identity.
 */
public class AcmArkcaseGlobalIdentityDao implements AcmArkcaseIdentityDao
{
    @Override
    public String getIdentity() throws AcmIdentityException
    {
        // TODO we currently don't have mechanism for centralized properties,
        // as soon as we create or use some server for containing all properties identity should be set there
        throw new AcmIdentityException("Not implemented yet!");
    }
}

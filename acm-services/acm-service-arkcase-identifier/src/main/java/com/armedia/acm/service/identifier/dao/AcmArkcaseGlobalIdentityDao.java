package com.armedia.acm.service.identifier.dao;

import com.armedia.acm.service.identifier.exceptions.AcmIdentityException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        throw new NotImplementedException();
    }
}

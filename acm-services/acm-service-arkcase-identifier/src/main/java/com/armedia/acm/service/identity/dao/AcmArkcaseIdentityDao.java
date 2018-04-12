package com.armedia.acm.service.identity.dao;

import com.armedia.acm.service.identity.exceptions.AcmIdentityException;

public interface AcmArkcaseIdentityDao
{
    String getIdentity() throws AcmIdentityException;
}

package com.armedia.acm.service.identifier.dao;

import com.armedia.acm.service.identifier.exceptions.AcmIdentityException;

public interface AcmArkcaseIdentityDao
{
    String getIdentity() throws AcmIdentityException;
}

package com.armedia.acm.service.identity.service;

import com.armedia.acm.service.identity.exceptions.AcmIdentityException;
import com.armedia.acm.service.identity.model.AcmArkcaseIdentity;

public interface AcmArkcaseIdentityService
{
    AcmArkcaseIdentity getIdentity() throws AcmIdentityException;
}

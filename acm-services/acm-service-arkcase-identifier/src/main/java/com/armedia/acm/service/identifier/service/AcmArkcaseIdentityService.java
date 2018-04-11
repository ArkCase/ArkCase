package com.armedia.acm.service.identifier.service;

import com.armedia.acm.service.identifier.exceptions.AcmIdentityException;
import com.armedia.acm.service.identifier.model.AcmArkcaseIdentity;

public interface AcmArkcaseIdentityService
{
    AcmArkcaseIdentity getIdentity() throws AcmIdentityException;
}

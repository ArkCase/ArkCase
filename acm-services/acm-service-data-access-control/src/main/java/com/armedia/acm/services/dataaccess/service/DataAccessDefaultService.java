package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;

/**
 * Created by armdev on 7/9/14.
 */
public interface DataAccessDefaultService
{
    AcmAccessControlDefault save(
            Long defaultAccessId,
            AcmAccessControlDefault toSave,
            Authentication authentication) throws AcmUserActionFailedException;

    void persistDefaultDataAccessControls(ApplicationContext applicationContext);
}

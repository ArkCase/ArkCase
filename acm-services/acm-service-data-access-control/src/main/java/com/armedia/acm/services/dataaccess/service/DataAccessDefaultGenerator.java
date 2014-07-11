package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;

import java.util.List;

/**
 * Created by armdev on 7/9/14.
 */
public interface DataAccessDefaultGenerator
{
    List<AcmAccessControlDefault> generateDefaultAccessFromApplication(AcmApplication acmApplication);
}

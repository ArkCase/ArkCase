package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.profile.model.UserOrg;

/**
 * Created by nebojsha on 07.05.2015.
 */
public interface UserOrgService
{

    UserOrg getUserOrgForUserId(String userId) throws AcmObjectNotFoundException;

    String getProfileLocation();
}

package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.plugins.profile.model.ProfileDTO;
import com.armedia.acm.plugins.profile.model.UserOrg;
import org.springframework.security.core.Authentication;

public interface UserOrgService
{
    String getProfileLocation();

    UserOrg getUserOrgForUserId(String userId);

    ProfileDTO saveUserOrgInfo(ProfileDTO profileDTO, Authentication authentication);

    ProfileDTO getProfileInfo(String userId, Authentication authentication);
}

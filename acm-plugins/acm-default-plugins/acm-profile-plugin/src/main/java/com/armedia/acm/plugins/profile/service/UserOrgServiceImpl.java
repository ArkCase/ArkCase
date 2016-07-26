package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.UserOrg;

/**
 * Created by nebojsha on 07.05.2015.
 */
public class UserOrgServiceImpl implements UserOrgService
{

    private UserOrgDao userOrgDao;

    private AcmCryptoUtils acmCryptoUtils;
    private String profileLocation;

    @Override
    public UserOrg getUserOrgForUserId(String userId) throws AcmObjectNotFoundException
    {
        return userOrgDao.getUserOrgForUserId(userId);
    }

    public UserOrgDao getUserOrgDao()
    {
        return userOrgDao;
    }

    public void setUserOrgDao(UserOrgDao userOrgDao)
    {
        this.userOrgDao = userOrgDao;
    }

    public AcmCryptoUtils getAcmCryptoUtils()
    {
        return acmCryptoUtils;
    }

    public void setAcmCryptoUtils(AcmCryptoUtils acmCryptoUtils)
    {
        this.acmCryptoUtils = acmCryptoUtils;
    }

    public void setProfileLocation(String profileLocation)
    {
        this.profileLocation = profileLocation;
    }

    @Override
    public String getProfileLocation()
    {
        return profileLocation;
    }
}

package com.armedia.acm.plugins.profile.dao;

import com.armedia.acm.data.AcmAbstractDao;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class UserInfoDao extends AcmAbstractDao<UserInfoDao> {

    @Override
    protected Class<UserInfoDao> getPersistenceClass() {
        return null;
    }
}

package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Organization;

public class OrganizationDao extends AcmAbstractDao<Organization>
{
    @Override
    protected Class<Organization> getPersistenceClass()
    {
        return Organization.class;
    }
}



package com.armedia.acm.plugins.addressable.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.addressable.model.ContactMethod;

/**
 * Created by armdev on 10/28/14.
 */
public class ContactMethodDao extends AcmAbstractDao<ContactMethod>
{
    @Override
    protected Class<ContactMethod> getPersistenceClass()
    {
        return ContactMethod.class;
    }
}

package com.armedia.acm.plugins.addressable.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.addressable.model.PostalAddress;

/**
 * Created by armdev on 10/28/14.
 */
public class PostalAddressDao extends AcmAbstractDao<PostalAddress>
{
    @Override
    protected Class<PostalAddress> getPersistenceClass()
    {
        return PostalAddress.class;
    }
}

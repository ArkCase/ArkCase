package com.armedia.acm.plugins.person.dao;



import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.PersonAlias;

public class PersonAliasDao extends AcmAbstractDao<PersonAlias>
{
    @Override
    protected Class<PersonAlias> getPersistenceClass()
    {
        return PersonAlias.class;
    }
}



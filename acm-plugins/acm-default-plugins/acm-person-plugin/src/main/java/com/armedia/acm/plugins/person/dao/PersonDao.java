package com.armedia.acm.plugins.person.dao;


import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Person;

public class PersonDao extends AcmAbstractDao<Person>
{
    @Override
    protected Class<Person> getPersistenceClass()
    {
        return Person.class;
    }
}

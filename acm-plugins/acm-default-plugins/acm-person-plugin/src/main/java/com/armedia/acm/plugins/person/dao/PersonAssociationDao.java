package com.armedia.acm.plugins.person.dao;



import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.PersonAssociation;

public class PersonAssociationDao extends AcmAbstractDao<PersonAssociation>
{
    @Override
    protected Class<PersonAssociation> getPersistenceClass()
    {
        return PersonAssociation.class;
    }
}



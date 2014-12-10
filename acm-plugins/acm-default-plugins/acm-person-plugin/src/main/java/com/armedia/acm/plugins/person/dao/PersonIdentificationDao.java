package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonIdentification;

/**
 * Created by marjan.stefanoski on 09.12.2014.
 */
public class PersonIdentificationDao extends AcmAbstractDao<PersonIdentification> {

    @Override
    protected Class<PersonIdentification> getPersistenceClass() {
        return PersonIdentification.class;
    }
}

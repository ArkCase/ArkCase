package com.armedia.acm.plugins.person.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionException;

public class PersonAssociationServiceImpl implements PersonAssociationService
{
    private SavePersonAssociationTransaction personAssociationTransaction;

    private PersonAssociationDao personAssociationDao;

    private PersonAssociationEventPublisher personAssociationEventPublisher;

    @Override
    public PersonAssociation savePersonAssociation(PersonAssociation personAssociation, Authentication authentication)
            throws AcmCreateObjectFailedException
    {
        Long id = personAssociation.getId();
        String personAssociationHistory = null;

        if (id != null)
        {
            PersonAssociation exPersonAssociation = getPersonAssociationDao().find(id);
            AcmMarshaller marshaller = ObjectConverter.createJSONMarshaller();
            // keep copy from the existing object to compare with the updated one
            // otherwise JPA will update all references and no changes can be detected
            personAssociationHistory = marshaller.marshal(exPersonAssociation);
        }

        try
        {
            PersonAssociation savedPersonAssociation = getPersonAssociationTransaction()
                    .savePersonAsssociation(personAssociation, authentication);
            getPersonAssociationEventPublisher().publishPersonAssociationEvent(personAssociationHistory, savedPersonAssociation, true);
            return savedPersonAssociation;
        } catch (MuleException | TransactionException e)
        {
            getPersonAssociationEventPublisher().publishPersonAssociationEvent(personAssociationHistory, personAssociation, false);
            throw new AcmCreateObjectFailedException("personAssociation", e.getMessage(), e);
        }
    }

    public SavePersonAssociationTransaction getPersonAssociationTransaction()
    {
        return personAssociationTransaction;
    }

    public void setPersonAssociationTransaction(SavePersonAssociationTransaction personAssociationTransaction)
    {
        this.personAssociationTransaction = personAssociationTransaction;
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public PersonAssociationEventPublisher getPersonAssociationEventPublisher()
    {
        return personAssociationEventPublisher;
    }

    public void setPersonAssociationEventPublisher(PersonAssociationEventPublisher personAssociationEventPublisher)
    {
        this.personAssociationEventPublisher = personAssociationEventPublisher;
    }
}

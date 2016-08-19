package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.PersonContactDao;
import com.armedia.acm.plugins.person.model.PersonContact;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by riste.tutureski on 10/21/14.
 */
public class PersonContactToSolrTransformer implements AcmObjectToSolrDocTransformer<PersonContact>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private PersonContactDao personContactDao;
    private UserDao userDao;

    @Override
    public List<PersonContact> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPersonContactDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PersonContact personContact)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        solrDoc.setId(personContact.getId() + "-PERSON_CONTACT");
        solrDoc.setObject_type_s("PERSON_CONTACT");
        solrDoc.setObject_id_s(personContact.getId() + "");

        solrDoc.setCreate_date_tdt(personContact.getCreated());
        solrDoc.setCreator_lcs(personContact.getCreator());
        solrDoc.setModified_date_tdt(personContact.getModified());
        solrDoc.setModifier_lcs(personContact.getModifier());

        if (personContact.getCompanyName() != null && !personContact.getCompanyName().isEmpty())
        {
            solrDoc.setName(personContact.getCompanyName());
        } else if (personContact.getPersonName() != null && !personContact.getPersonName().isEmpty())
        {
            solrDoc.setName(personContact.getPersonName());
        }

        solrDoc.setFirst_name_lcs(personContact.getFirstName());
        solrDoc.setLast_name_lcs(personContact.getLastName());

        addContactMethods(personContact, solrDoc);

        addAddresses(personContact, solrDoc);

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(personContact.getCreator());
        if (creator != null)
        {
            solrDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(personContact.getModifier());
        if (modifier != null)
        {
            solrDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return solrDoc;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(PersonContact in)
    {
        // No implementation needed
        return null;
    }

    private void addAddresses(PersonContact personContact, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> addressIds = new ArrayList<>();
        if (personContact.getAddresses() != null)
        {
            for (PostalAddress address : personContact.getAddresses())
            {
                addressIds.add(address.getId() + "-LOCATION");
            }

        }
        solrDoc.setPostal_address_id_ss(addressIds);
    }

    private void addContactMethods(PersonContact personContact, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> contactMethodIds = new ArrayList<>();
        if (personContact.getContactMethods() != null)
        {
            for (ContactMethod cm : personContact.getContactMethods())
            {
                contactMethodIds.add(cm.getId() + "-CONTACT-METHOD");
            }
        }
        solrDoc.setContact_method_ss(contactMethodIds);
    }

    @Override
    public SolrDocument toSolrQuickSearch(PersonContact in)
    {
        SolrDocument solrDoc = new SolrDocument();
        solrDoc.setId(in.getId() + "-PERSON_CONTACT");
        solrDoc.setObject_type_s("PERSON_CONTACT");

        if (in.getCompanyName() != null && !in.getCompanyName().isEmpty())
        {
            solrDoc.setName(in.getCompanyName());
        } else if (in.getPersonName() != null && !in.getPersonName().isEmpty())
        {
            solrDoc.setName(in.getPersonName());
        }

        solrDoc.setName((in.getFirstName() + " " + in.getLastName()).trim());

        solrDoc.setObject_id_s(in.getId() + "");

        return solrDoc;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return PersonContact.class.equals(acmObjectType);
    }

    public PersonContactDao getPersonContactDao()
    {
        return personContactDao;
    }

    public void setPersonContactDao(PersonContactDao personContactDao)
    {
        this.personContactDao = personContactDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return PersonContact.class;
    }
}

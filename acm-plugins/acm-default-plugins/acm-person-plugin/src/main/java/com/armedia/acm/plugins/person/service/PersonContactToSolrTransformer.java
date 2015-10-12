package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.PersonContactDao;
import com.armedia.acm.plugins.person.model.PersonContact;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
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
        }
        else if (personContact.getPersonName() != null && !personContact.getPersonName().isEmpty())
        {
            solrDoc.setName(personContact.getPersonName());
        }

        solrDoc.setFirst_name_lcs(personContact.getFirstName());
        solrDoc.setLast_name_lcs(personContact.getLastName());

        addContactMethods(personContact, solrDoc);

        addAddresses(personContact, solrDoc);

        return solrDoc;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(PersonContact in) {
        //No implementation needed
        return null;
    }

    private void addAddresses(PersonContact personContact, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> addressIds = new ArrayList<String>();
        if ( personContact.getAddresses() != null )
        {
            for ( PostalAddress address : personContact.getAddresses() )
            {
                addressIds.add(address.getId() + "-LOCATION");
            }

        }
        solrDoc.setPostal_address_id_ss(addressIds);
    }


    private void addContactMethods(PersonContact personContact, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> contactMethodIds = new ArrayList<>();
        if ( personContact.getContactMethods() != null )
        {
            for ( ContactMethod cm : personContact.getContactMethods() )
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
        }
        else if (in.getPersonName() != null && !in.getPersonName().isEmpty())
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
        boolean objectNotNull = acmObjectType != null;
        String ourClassName = PersonContact.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        log.debug("Incoming: " + acmObjectType.getName() + "; do we handle it? " + isSupported);

        return isSupported;
    }

    public PersonContactDao getPersonContactDao()
    {
        return personContactDao;
    }

    public void setPersonContactDao(PersonContactDao personContactDao)
    {
        this.personContactDao = personContactDao;
    }
}

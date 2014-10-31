package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/21/14.
 */
public class PersonToSolrTransformer implements AcmObjectToSolrDocTransformer<Person>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private PersonDao personDao;


    @Override
    public List<Person> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPersonDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Person person)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        solrDoc.setId(person.getId() + "-PERSON");
        solrDoc.setObject_type_s("PERSON");
        solrDoc.setObject_id_s(person.getId() + "");
        solrDoc.setPerson_title_lcs(person.getTitle());
        solrDoc.setFirst_name_lcs(person.getGivenName());
        solrDoc.setLast_name_lcs(person.getFamilyName());
        solrDoc.setCreate_date_tdt(person.getCreated());
        solrDoc.setCreator_lcs(person.getCreator());
        solrDoc.setModified_date_tdt(person.getModified());
        solrDoc.setModifier_lcs(person.getModifier());

        solrDoc.setName(person.getGivenName() + " " + person.getFamilyName());

        addContactMethods(person, solrDoc);

        addOrganizations(person, solrDoc);

        addAddresses(person, solrDoc);

        return solrDoc;
    }

    private void addAddresses(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> addressIds = new ArrayList<String>();
        if ( person.getAddresses() != null )
        {
            for ( PostalAddress address : person.getAddresses() )
            {
                addressIds.add(address.getId() + "-LOCATION");
            }

        }
        solrDoc.setPostal_address_id_ss(addressIds);
    }

    private void addOrganizations(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> organizationIds = new ArrayList<>();
        if ( person.getOrganizations() != null )
        {
            for ( Organization org : person.getOrganizations() )
            {
                organizationIds.add(org.getOrganizationId() + "-ORGANIZATION");
            }
        }
        solrDoc.setOrganization_id_ss(organizationIds);
    }

    private void addContactMethods(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> contactMethodIds = new ArrayList<>();
        if ( person.getContactMethods() != null )
        {
            for ( ContactMethod cm : person.getContactMethods() )
            {
                contactMethodIds.add(cm.getId() + "-CONTACT-METHOD");
            }
        }
        solrDoc.setContact_method_ss(contactMethodIds);
    }

    @Override
    public SolrDocument toSolrQuickSearch(Person in)
    {
        SolrDocument solrDoc = new SolrDocument();
        solrDoc.setId(in.getId() + "-PERSON");
        solrDoc.setObject_type_s("PERSON");
        solrDoc.setName(in.getGivenName() + " " + in.getFamilyName());
        solrDoc.setObject_id_s(in.getId() + "");

        return solrDoc;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        boolean objectNotNull = acmObjectType != null;
        String ourClassName = Person.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        log.debug("Incoming: " + acmObjectType.getName() + "; do we handle it? " + isSupported);

        return isSupported;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }
}

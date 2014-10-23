package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.search.model.solr.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by armdev on 10/21/14.
 */
public class PersonChangeToSolrTransformer implements AcmObjectToSolrDocTransformer<Person>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

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

        addContactMethods(person, solrDoc);

        addOrganizations(person, solrDoc);

        addAddresses(person, solrDoc);

        return solrDoc;
    }

    private void addAddresses(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        if ( person.getAddresses() != null )
        {
            for ( PostalAddress address : person.getAddresses() )
            {
                SolrAdvancedSearchDocument addrDoc = new SolrAdvancedSearchDocument();
                addrDoc.setId(address.getId() + "-LOCATION");
                addrDoc.setObject_type_s("LOCATION");
                addrDoc.setObject_id_s(address.getId() + "");
                addrDoc.setLocation_city_lcs(address.getCity());
                addrDoc.setLocation_postal_code_sdo(address.getZip());
                addrDoc.setLocation_state_lcs(address.getState());
                addrDoc.setLocation_street_address_lcs(address.getStreetAddress());

                solrDoc.get_childDocuments_().add(addrDoc);
            }
        }
    }

    private void addOrganizations(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        if ( person.getOrganizations() != null )
        {
            for ( Organization org : person.getOrganizations() )
            {
                SolrAdvancedSearchDocument orgDoc = new SolrAdvancedSearchDocument();
                orgDoc.setId(org.getOrganizationId() + "-ORGANIZATION");
                orgDoc.setObject_type_s("ORGANIZATION");
                orgDoc.setObject_id_s(org.getOrganizationId() + "");
                orgDoc.setType_lcs(org.getOrganizationType());
                orgDoc.setValue_parseable(org.getOrganizationValue());

                solrDoc.get_childDocuments_().add(orgDoc);
            }
        }
    }

    private void addContactMethods(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        if ( person.getContactMethods() != null )
        {
            for ( ContactMethod cm : person.getContactMethods() )
            {
                SolrAdvancedSearchDocument cmDoc = new SolrAdvancedSearchDocument();
                cmDoc.setId(cm.getId() + "-CONTACT-METHOD");
                cmDoc.setObject_type_s("CONTACT-METHOD");
                cmDoc.setObject_id_s(cm.getId() + "");
                cmDoc.setType_lcs(cm.getType());
                cmDoc.setValue_parseable(cm.getValue());

                solrDoc.get_childDocuments_().add(cmDoc);
            }
        }
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
}

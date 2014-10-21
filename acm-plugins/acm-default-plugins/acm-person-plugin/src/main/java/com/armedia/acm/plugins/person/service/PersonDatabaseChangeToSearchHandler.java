package com.armedia.acm.plugins.person.service;

import com.armedia.acm.data.AcmDatabaseChangesEvent;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.plugins.search.model.solr.SolrLocation;
import com.armedia.acm.services.search.service.SendDocumentsToSolr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * Created by armdev on 10/21/14.
 */
public class PersonDatabaseChangeToSearchHandler implements ApplicationListener<AcmDatabaseChangesEvent>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private SendDocumentsToSolr sendDocumentsToSolr;

    @Override
    public void onApplicationEvent(AcmDatabaseChangesEvent acmDatabaseChangesEvent)
    {
        AcmObjectChangelist acmObjectChangelist = acmDatabaseChangesEvent.getObjectChangelist();

        updatePersonsInSearchRepository(acmObjectChangelist.getAddedObjects());
        updatePersonsInSearchRepository(acmObjectChangelist.getUpdatedObjects());
    }

    private void updatePersonsInSearchRepository(List<Object> updatedObjects)
    {
        for ( Object obj : updatedObjects )
        {
            if ( obj instanceof Person )
            {
                sendPersonToSolr((Person) obj);
            }
            else if ( obj instanceof PersonAssociation )
            {
                sendPersonAssociationToSolr((PersonAssociation) obj);
            }
        }
    }

    private void sendPersonAssociationToSolr(PersonAssociation personAssociation)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        solrDoc.setPerson_id_i(personAssociation.getPerson().getId());
        solrDoc.setId(personAssociation.getId() + "-PERSON-ASSOCIATION");
        solrDoc.setObject_type_s("PERSON-ASSOCIATION");
        solrDoc.setPerson_type_s(personAssociation.getPersonType());

        getSendDocumentsToSolr().sendSolrDocument(solrDoc);


    }

    private void sendPersonToSolr(Person person)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        solrDoc.setId(person.getId() + "-PERSON");
        solrDoc.setObject_type_s("PERSON");
        solrDoc.setPerson_title_s(person.getTitle());
        solrDoc.setFirst_name_s(person.getGivenName());
        solrDoc.setLast_name_s(person.getFamilyName());

        if ( person.getContactMethods() != null )
        {
            for ( ContactMethod cm : person.getContactMethods() )
            {
                if ( cm.getType() != null && cm.getType().toLowerCase().indexOf("phone") > 0
                    && cm.getValue() != null && !cm.getValue().trim().isEmpty() )
                {
                    solrDoc.getPhone_numbers().add(cm.getValue().trim());
                }
                else if ( cm.getType() != null && cm.getType().toLowerCase().indexOf("mail") > 0
                        && cm.getValue() != null && !cm.getValue().trim().isEmpty() )
                {
                    solrDoc.getEmail_address_ss().add(cm.getValue().trim());
                }
            }
        }

        if ( person.getOrganizations() != null )
        {
            for ( Organization org : person.getOrganizations() )
            {
                if ( org.getOrganizationValue() != null && !org.getOrganizationValue().trim().isEmpty() )
                {
                    solrDoc.getOrganizations_ss().add(org.getOrganizationValue().trim());
                }
            }
        }

        if ( person.getAddresses() != null )
        {
            for ( PostalAddress address : person.getAddresses() )
            {
                SolrLocation location = new SolrLocation();
                location.setId(address.getId() + "-LOCATION");
                location.setObject_type_s("LOCATION");
                location.setLocation_city_s(address.getCity());
                location.setLocation_postal_code_s(address.getZip());
                location.setLocation_state_s(address.getState());
                location.setLocation_street_address_s(address.getStreetAddress());

                solrDoc.get_childDocuments_().add(location);
            }
        }

        getSendDocumentsToSolr().sendSolrDocument(solrDoc);

    }

    public SendDocumentsToSolr getSendDocumentsToSolr()
    {
        return sendDocumentsToSolr;
    }

    public void setSendDocumentsToSolr(SendDocumentsToSolr sendDocumentsToSolr)
    {
        this.sendDocumentsToSolr = sendDocumentsToSolr;
    }
}

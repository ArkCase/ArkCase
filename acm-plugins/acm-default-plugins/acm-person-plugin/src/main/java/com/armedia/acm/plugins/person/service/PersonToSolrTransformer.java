package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

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
    private UserDao userDao;
    private SearchAccessControlFields searchAccessControlFields;

    @Override
    public List<Person> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPersonDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Person person)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();

        getSearchAccessControlFields().setAccessControlFields(solrDoc, person);

        solrDoc.setId(person.getId() + "-PERSON");
        solrDoc.setObject_type_s("PERSON");
        solrDoc.setObject_id_s(person.getId() + "");
        solrDoc.setPerson_title_lcs(person.getTitle());
        solrDoc.setFirst_name_lcs(person.getGivenName());
        solrDoc.setLast_name_lcs(person.getFamilyName());

        solrDoc.setFull_name_lcs(person.getGivenName() + " " + person.getFamilyName());

        solrDoc.setCreate_date_tdt(person.getCreated());
        solrDoc.setCreator_lcs(person.getCreator());
        solrDoc.setModified_date_tdt(person.getModified());
        solrDoc.setModifier_lcs(person.getModifier());

        solrDoc.setName(person.getGivenName() + " " + person.getFamilyName());

        solrDoc.setTitle_parseable(person.getFamilyName() + " " + person.getGivenName());
        solrDoc.setTitle_parseable_lcs(person.getFamilyName() + " " + person.getGivenName());

        addContactMethods(person, solrDoc);

        addOrganizations(person, solrDoc);

        addAddresses(person, solrDoc);

        addAliases(person, solrDoc);

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(person.getCreator());
        if (creator != null)
        {
            solrDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(person.getModifier());
        if (modifier != null)
        {
            solrDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        solrDoc.setAdditionalProperty("default_organization_s",
                person.getDefaultOrganization() != null ? person.getDefaultOrganization().getOrganization().getOrganizationValue() : null);
        solrDoc.setAdditionalProperty("default_phone_s", getDefaultPhone(person));
        solrDoc.setAdditionalProperty("default_location_s", getDefaultAddress(person));

        String participantsListJson = ParticipantUtils.createParticipantsListJson(person.getParticipants());
        solrDoc.setAdditionalProperty("acm_participants_lcs", participantsListJson);

        return solrDoc;
    }

    private String getDefaultPhone(Person person)
    {
        if (person.getDefaultPhone() == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(person.getDefaultPhone().getValue());
        if (person.getDefaultPhone().getSubType() != null)
        {
            sb.append(" [").append(person.getDefaultPhone().getSubType()).append("]");
        }
        return sb.toString();
    }

    private String getDefaultAddress(Person person)
    {
        if (person.getDefaultAddress() == null)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (person.getDefaultAddress().getCity() != null)
        {
            sb.append(person.getDefaultAddress().getCity());
        }
        if (person.getDefaultAddress().getState() != null)
        {
            if (sb.length() > 0)
            {
                sb.append(", ");
            }
            sb.append(person.getDefaultAddress().getState());
        }
        return sb.toString();
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(Person in)
    {
        // No implementation needed
        return null;
    }

    private void addAddresses(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> addressIds = new ArrayList<>();
        if (person.getAddresses() != null)
        {
            for (PostalAddress address : person.getAddresses())
            {
                addressIds.add(address.getId() + "-LOCATION");
            }

        }
        solrDoc.setPostal_address_id_ss(addressIds);
    }

    private void addOrganizations(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> organizationIds = new ArrayList<>();
        if (person.getOrganizations() != null)
        {
            for (Organization org : person.getOrganizations())
            {
                organizationIds.add(org.getOrganizationId() + "-ORGANIZATION");
            }
        }
        solrDoc.setOrganization_id_ss(organizationIds);
    }

    private void addContactMethods(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> contactMethodIds = new ArrayList<>();
        if (person.getContactMethods() != null)
        {
            for (ContactMethod cm : person.getContactMethods())
            {
                contactMethodIds.add(cm.getId() + "-CONTACT-METHOD");
            }
        }
        solrDoc.setContact_method_ss(contactMethodIds);
    }

    private void addAliases(Person person, SolrAdvancedSearchDocument solrDoc)
    {
        List<String> aliasIds = new ArrayList<>();
        if (person.getPersonAliases() != null)
        {
            for (PersonAlias pa : person.getPersonAliases())
            {
                aliasIds.add(pa.getId() + "-PERSON-ALIAS");
            }
        }
        solrDoc.setPerson_alias_ss(aliasIds);
    }

    @Override
    public SolrDocument toSolrQuickSearch(Person in)
    {
        SolrDocument solrDoc = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(solrDoc, in);

        solrDoc.setId(in.getId() + "-PERSON");
        solrDoc.setObject_type_s("PERSON");
        solrDoc.setName(in.getGivenName() + " " + in.getFamilyName());
        solrDoc.setObject_id_s(in.getId() + "");

        solrDoc.setCreate_tdt(in.getCreated());
        solrDoc.setAuthor_s(in.getCreator());
        solrDoc.setLast_modified_tdt(in.getModified());
        solrDoc.setModifier_s(in.getModifier());

        solrDoc.setTitle_parseable(in.getFamilyName() + " " + in.getGivenName());
        solrDoc.setTitle_parseable_lcs(in.getFamilyName() + " " + in.getGivenName());

        return solrDoc;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Person.class.equals(acmObjectType);
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
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
        return Person.class;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }
}

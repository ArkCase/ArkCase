package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.dao.PersonAliasDao;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by will.phillips on 8/4/2016.
 */
public class PersonAliasToSolrTransformer implements AcmObjectToSolrDocTransformer<PersonAlias>
{

    private PersonAliasDao personAliasDao;
    private UserDao userDao;

    @Override
    public List<PersonAlias> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPersonAliasDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PersonAlias org)
    {
        SolrAdvancedSearchDocument orgDoc = new SolrAdvancedSearchDocument();
        orgDoc.setId(org.getId() + "-PERSON-ALIAS");
        orgDoc.setObject_type_s("PERSON-ALIAS");
        orgDoc.setObject_id_s(org.getId() + "");

        if (org.getPerson() != null && org.getPerson().getId() != null)
        {
            orgDoc.setParent_type_s("PERSON");
            orgDoc.setParent_id_s(Long.toString(org.getPerson().getId()));
            orgDoc.setParent_ref_s(Long.toString(org.getPerson().getId()) + "-PERSON");
        }

        orgDoc.setCreate_date_tdt(org.getCreated());
        orgDoc.setCreator_lcs(org.getCreator());
        orgDoc.setModified_date_tdt(org.getModified());
        orgDoc.setModifier_lcs(org.getModifier());

        orgDoc.setType_lcs(org.getAliasType());
        orgDoc.setValue_parseable(org.getAliasValue());

        orgDoc.setName(org.getAliasValue());
        orgDoc.setTitle_parseable(org.getAliasValue());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(org.getCreator());
        if (creator != null)
        {
            orgDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(org.getModifier());
        if (modifier != null)
        {
            orgDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return orgDoc;
    }

    // No implementation needed because we don't want PersonAlias indexed in the SolrQuickSearch
    @Override
    public SolrDocument toSolrQuickSearch(PersonAlias in)
    {
        return null;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(PersonAlias in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return PersonAlias.class.equals(acmObjectType);
    }

    public PersonAliasDao getPersonAliasDao()
    {
        return personAliasDao;
    }

    public void setPersonAliasDao(PersonAliasDao personAliasDao)
    {
        this.personAliasDao = personAliasDao;
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
        return PersonAlias.class;
    }
}

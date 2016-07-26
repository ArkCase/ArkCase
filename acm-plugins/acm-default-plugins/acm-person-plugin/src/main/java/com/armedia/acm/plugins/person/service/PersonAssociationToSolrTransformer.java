package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/23/14.
 */
public class PersonAssociationToSolrTransformer implements AcmObjectToSolrDocTransformer<PersonAssociation>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private PersonAssociationDao personAssociationDao;
    private UserDao userDao;

    @Override
    public List<PersonAssociation> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPersonAssociationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PersonAssociation personAssociation)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        solrDoc.setId(personAssociation.getId() + "-PERSON-ASSOCIATION");
        solrDoc.setObject_id_s(personAssociation.getId() + "");
        solrDoc.setObject_type_s("PERSON-ASSOCIATION");
        solrDoc.setCreate_date_tdt(personAssociation.getCreated());
        solrDoc.setCreator_lcs(personAssociation.getCreator());
        solrDoc.setModified_date_tdt(personAssociation.getModified());
        solrDoc.setModifier_lcs(personAssociation.getModifier());

        solrDoc.setChild_id_s(personAssociation.getPerson().getId() + "");
        solrDoc.setChild_type_s("PERSON");
        solrDoc.setParent_id_s(personAssociation.getParentId() + "");
        solrDoc.setParent_type_s(personAssociation.getParentType());

        solrDoc.setType_lcs(personAssociation.getPersonType());

        solrDoc.setName(personAssociation.getPerson().getGivenName() + " " + personAssociation.getPerson().getFamilyName() + " (" + personAssociation.getPersonType() + ")");

        solrDoc.setParent_ref_s(personAssociation.getParentId() + "-" + personAssociation.getParentType());

        solrDoc.setDescription_parseable(personAssociation.getPersonDescription());

        solrDoc.setNotes_no_html_tags_parseable(personAssociation.getNotes());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(personAssociation.getCreator());
        if (creator != null)
        {
            solrDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(personAssociation.getModifier());
        if (modifier != null)
        {
            solrDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return solrDoc;
    }

    @Override
    public SolrDocument toSolrQuickSearch(PersonAssociation in)
    {
        // we don't want person associations in quick search, so just return null
        return null;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(PersonAssociation in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        boolean objectNotNull = acmObjectType != null;
        String ourClassName = PersonAssociation.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        log.trace("Incoming: " + acmObjectType.getName() + "; do we handle it? " + isSupported);

        return isSupported;
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}

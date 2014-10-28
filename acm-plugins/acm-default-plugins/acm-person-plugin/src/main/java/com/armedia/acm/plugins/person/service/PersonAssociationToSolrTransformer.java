package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.search.model.solr.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by armdev on 10/23/14.
 */
public class PersonAssociationToSolrTransformer implements AcmObjectToSolrDocTransformer<PersonAssociation>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PersonAssociation personAssociation)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        solrDoc.setId(personAssociation.getId() + "-PERSON-ASSOCIATION");
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

        solrDoc.setName(personAssociation.getPerson().getGivenName() + " " +
            personAssociation.getPerson().getFamilyName() + " (" +
            personAssociation.getPersonType() + ")");

        solrDoc.setDescription_parseable(personAssociation.getPersonDescription());

        return solrDoc;
    }

    @Override
    public SolrDocument toSolrQuickSearch(PersonAssociation in)
    {
        // we don't want person associations in quick search, so just return null
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

        log.debug("Incoming: " + acmObjectType.getName() + "; do we handle it? " + isSupported);

        return isSupported;
    }
}

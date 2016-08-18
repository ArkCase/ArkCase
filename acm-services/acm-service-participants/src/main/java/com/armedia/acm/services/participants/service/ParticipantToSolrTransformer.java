package com.armedia.acm.services.participants.service;

import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by will.phillips on 7/21/2016.
 */
public class ParticipantToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmParticipant>
{

    private UserDao userDao;
    private AcmParticipantDao acmParticipantDao;

    @Override
    public List<AcmParticipant> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getAcmParticipantDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmParticipant in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(String.format("%d-%s", in.getId(), ParticipantConstants.OBJECT_TYPE));

        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(ParticipantConstants.OBJECT_TYPE);

        solr.setName(String.format("%s_%d", ParticipantConstants.OBJECT_TYPE, in.getId()));

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        solr.setAdditionalProperty("parent_object_type_s", in.getObjectType());
        solr.setAdditionalProperty("parent_object_id_i", in.getObjectId());
        solr.setParent_ref_s(String.format("%d-%s", in.getObjectId(), in.getObjectType()));

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmParticipant in)
    {
        SolrDocument solrDoc = new SolrDocument();
        solrDoc.setId(String.format("%d-%s", in.getId(), ParticipantConstants.OBJECT_TYPE));
        solrDoc.setObject_type_s(ParticipantConstants.OBJECT_TYPE);
        solrDoc.setName(String.format("%s_%d", ParticipantConstants.OBJECT_TYPE, in.getId()));
        solrDoc.setObject_id_s(in.getId() + "");
        solrDoc.setCreate_tdt(in.getCreated());
        solrDoc.setAuthor(in.getCreator());
        solrDoc.setLast_modified_tdt(in.getModified());
        solrDoc.setAdditionalProperty("parent_object_type_s", in.getObjectType());
        solrDoc.setAdditionalProperty("parent_object_id_i", in.getObjectId());
        solrDoc.setParent_ref_s(String.format("%d-%s", in.getObjectId(), in.getObjectType()));

        return solrDoc;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmParticipant in)
    {
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmParticipant.class.equals(acmObjectType);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmParticipantDao getAcmParticipantDao()
    {
        return acmParticipantDao;
    }

    public void setAcmParticipantDao(AcmParticipantDao acmParticipantDao)
    {
        this.acmParticipantDao = acmParticipantDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmParticipant.class;
    }
}

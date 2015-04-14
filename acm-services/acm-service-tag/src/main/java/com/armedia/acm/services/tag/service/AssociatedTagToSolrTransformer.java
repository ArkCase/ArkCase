package com.armedia.acm.services.tag.service;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 13.04.2015.
 */
public class AssociatedTagToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmAssociatedTag> {

    private AssociatedTagDao associatedTagDao;

    @Override
    public List<AcmAssociatedTag> getObjectsModifiedSince(Date lastModified, int start, int pageSize) {
        return getAssociatedTagDao().findModifiedSince(lastModified,start,pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmAssociatedTag in) {

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-"+in.getObjectType());

        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setParent_type_s(in.getParentType());
        solr.setParent_id_s(Long.toString(in.getParentId()));

        solr.setTag_token_lcs(in.getTag().getTagToken());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmAssociatedTag in) {

        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-"+in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_tdt(in.getCreated());
        solr.setAuthor(in.getCreator());
        solr.setLast_modified_tdt(in.getModified());
        solr.setModifier_s(in.getModifier());


        solr.setParent_object_id_s(Long.toString(in.getParentId()));

        solr.setParent_object_type_s(in.getParentType());

        solr.setTag_token_lcs(in.getTag().getTagToken());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmAssociatedTag in) {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType) {

        boolean objectNotNull = acmObjectType != null;
        String ourClassName = AcmAssociatedTag.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        return isSupported;
    }

    public AssociatedTagDao getAssociatedTagDao() {
        return associatedTagDao;
    }

    public void setAssociatedTagDao(AssociatedTagDao associatedTagDao) {
        this.associatedTagDao = associatedTagDao;
    }
}

package com.armedia.acm.services.tag.service;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmTag;

import java.util.Date;
import java.util.List;

/**
 * Created by bojan.mickoski on 19.02.2016.
 */

public class TagToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmTag>
{

    private TagDao tagDao;

    @Override
    public List<AcmTag> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getTagDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmTag in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());

        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());
        
        solr.setTitle_parseable(in.getTagText());
        solr.setDescription_parseable(in.getTagDescription());
        solr.setName(in.getTagName());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setAdditionalProperty("tags_s", in.getTagName());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmTag in)
    {
        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());

        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_tdt(in.getCreated());
        solr.setAuthor(in.getCreator());
        solr.setLast_modified_tdt(in.getModified());
        solr.setModifier_s(in.getModifier());

        solr.setAdditionalProperty("tags_s", in.getTagName());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmTag in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmTag.class.equals(acmObjectType);
    }

    public TagDao getTagDao()
    {
        return tagDao;
    }

    public void setTagDao(TagDao tagDao)
    {
        this.tagDao = tagDao;
    }

}

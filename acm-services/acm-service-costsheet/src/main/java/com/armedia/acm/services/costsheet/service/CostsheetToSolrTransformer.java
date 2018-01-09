/**
 *
 */
package com.armedia.acm.services.costsheet.service;

import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 */
public class CostsheetToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmCostsheet>
{

    private AcmCostsheetDao acmCostsheetDao;

    @Override
    public List<AcmCostsheet> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getAcmCostsheetDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmCostsheet in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();  

        solr.setId(in.getId() + "-" + CostsheetConstants.OBJECT_TYPE);
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_type_s(CostsheetConstants.OBJECT_TYPE);
        solr.setTitle_parseable(in.getTitle());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setName(in.getTitle());        
        
        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());        
        
        solr.setParent_id_s(Long.toString(in.getParentId()));
        solr.setParent_type_s(in.getParentType());
        solr.setParent_ref_s(in.getParentId() + "-" + in.getParentType());
        
        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmCostsheet in)
    {
        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + CostsheetConstants.OBJECT_TYPE);
        solr.setName(in.getTitle());
        solr.setTitle_parseable(in.getTitle());
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_type_s(CostsheetConstants.OBJECT_TYPE);
        solr.setParent_object_id_s(Long.toString(in.getParentId()));
        solr.setParent_object_type_s(in.getParentType());
        solr.setAuthor_s(in.getUser().getUserId());

        solr.setParent_ref_s(in.getParentId() + "-" + in.getParentType());

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setStatus_s(in.getStatus());

        return solr;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmCostsheet.class.equals(acmObjectType);
    }

    public AcmCostsheetDao getAcmCostsheetDao()
    {
        return acmCostsheetDao;
    }

    public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao)
    {
        this.acmCostsheetDao = acmCostsheetDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmCostsheet.class;
    }

}

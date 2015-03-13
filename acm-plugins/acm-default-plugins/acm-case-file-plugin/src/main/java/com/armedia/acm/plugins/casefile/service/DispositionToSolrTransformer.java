package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.DispositionDao;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 09.03.2015.
 */
public class DispositionToSolrTransformer implements AcmObjectToSolrDocTransformer<Disposition> {

    private DispositionDao dispositionDao;

    @Override
    public List<Disposition> getObjectsModifiedSince(Date lastModified, int start, int pageSize) {
        return getDispositionDao().findModifiedSince(lastModified, start, pageSize);

    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Disposition in) {
        return null;
    }

    @Override
    public SolrDocument toSolrQuickSearch(Disposition in) {

        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId()+"-"+in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_tdt(in.getCreated());
        solr.setAuthor(in.getCreator());
        solr.setLast_modified_tdt(in.getModified());
        solr.setModifier_s(in.getModifier());

        solr.setDisposition_type_s(in.getDispositionType());
        if(in.getExistingCaseNumber()!=null)
        solr.setTarget_object_number_s(in.getExistingCaseNumber());

        return solr;

    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(Disposition in) {
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType) {

        boolean objectNotNull = acmObjectType != null;
        String ourClassName = Disposition.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        return isSupported;
    }

    public DispositionDao getDispositionDao() {
        return dispositionDao;
    }

    public void setDispositionDao(DispositionDao dispositionDao) {
        this.dispositionDao = dispositionDao;
    }
}

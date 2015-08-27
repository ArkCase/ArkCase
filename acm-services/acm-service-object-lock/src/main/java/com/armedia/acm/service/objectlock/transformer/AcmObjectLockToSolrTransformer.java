package com.armedia.acm.service.objectlock.transformer;

import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 3/23/15.
 */
public class AcmObjectLockToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmObjectLock> {
    private AcmObjectLockDao dao;

    @Override
    public List<AcmObjectLock> getObjectsModifiedSince(Date lastModified, int start, int pageSize) {
        return getDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmObjectLock in) {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-OBJECT_LOCK");
        solr.setObject_id_s(in.getId().toString());
        solr.setObject_type_s("OBJECT_LOCK");

        solr.setParent_id_s(in.getObjectId().toString());
        solr.setParent_type_s(in.getObjectType());


        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmObjectLock in) {

        SolrDocument solr = new SolrDocument();

        solr.setObject_id_s(in.getId().toString());
        solr.setObject_type_s("OBJECT_LOCK");
        solr.setId(in.getId() + "-OBJECT_LOCK");

        solr.setParent_object_id_s(in.getObjectId().toString());
        solr.setParent_object_type_s(in.getObjectType());

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmObjectLock in) {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-OBJECT_LOCK");
        solr.setObject_id_s(in.getId().toString());
        solr.setObject_type_s("OBJECT_LOCK");

        solr.setParent_id_s(in.getObjectId().toString());
        solr.setParent_type_s(in.getObjectType());


        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        return solr;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType) {
        return AcmObjectLock.class.equals(acmObjectType);
    }

    public AcmObjectLockDao getDao() {
        return dao;
    }

    public void setDao(AcmObjectLockDao dao) {
        this.dao = dao;
    }


}

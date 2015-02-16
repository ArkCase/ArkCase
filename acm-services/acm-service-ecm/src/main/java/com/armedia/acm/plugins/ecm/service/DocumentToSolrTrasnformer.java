package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 13.11.2014.
 */
public class DocumentToSolrTrasnformer implements AcmObjectToSolrDocTransformer<EcmFile> {

    private EcmFileDao ecmFileDao;

    @Override
    public List<EcmFile> getObjectsModifiedSince(Date lastModified, int start, int pageSize) {
        return getEcmFileDao().findModifiedSince(lastModified,start,pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(EcmFile in) {

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getFileId() + "-DOCUMENT");
        solr.setObject_id_s(in.getFileId() + "");
        solr.setObject_type_s("DOCUMENT");
        solr.setName(in.getFileName());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setStatus_lcs(in.getStatus());

        ObjectAssociation parent = null;

        if (in.getParentObjects() != null) {
            for (ObjectAssociation objectAssociation : in.getParentObjects()) {
                parent = objectAssociation;
                break;
            }
        }

        if( parent != null ) {
            solr.setParent_id_s(Long.toString(parent.getParentId()));
            solr.setParent_type_s(parent.getParentType());
        }

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(EcmFile in) {

        SolrDocument solr = new SolrDocument();
        solr.setName(in.getFileName());
        solr.setObject_id_s(in.getFileId() + "");
        solr.setObject_type_s("DOCUMENT");
        solr.setId(in.getFileId() + "-DOCUMENT");


        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setStatus_s(in.getStatus());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(EcmFile in) {
        //No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType) {

        boolean objectNotNull = acmObjectType != null;
        String ourClassName = EcmFile.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        return isSupported;
    }

    public EcmFileDao getEcmFileDao() {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }

}

package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.tag.model.AcmTag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 05.02.2015.
 */
public class EcmFileToSolrTransformer implements AcmObjectToSolrDocTransformer<EcmFile> {

    private EcmFileDao ecmFileDao;


    @Override
    public List<EcmFile> getObjectsModifiedSince(Date lastModified, int start, int pageSize) {
        return getEcmFileDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(EcmFile in) {

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setName(in.getFileName());
        solr.setContent_type(in.getFileMimeType());
        solr.setStatus_lcs(in.getStatus());

        solr.setParent_id_s(Long.toString(in.getContainer().getId()));
        solr.setParent_type_s(in.getContainer().getObjectType());
        solr.setParent_number_lcs(in.getContainer().getContainerObjectTitle());


        solr.setEcmFileId(in.getVersionSeriesId());

        List<String> tags = prepareTagList(in.getTags());
        solr.setTags_ss(tags);

        return solr;
    }

    //No implementation needed
    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(EcmFile in) {
        return null;
    }

    //No implementation needed
    @Override
    public SolrDocument toSolrQuickSearch(EcmFile in) {
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

    private List<String> prepareTagList(List<AcmTag> tagList) {
        List<String> tagTextList = new ArrayList<>();
        for(AcmTag tag: tagList){
            tagTextList.add(tag.getTagText());
        }
        return tagTextList;
    }

    public EcmFileDao getEcmFileDao() {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }
}

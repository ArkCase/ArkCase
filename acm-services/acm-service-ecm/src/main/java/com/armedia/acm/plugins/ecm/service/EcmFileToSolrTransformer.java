package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 05.02.2015.
 */
public class EcmFileToSolrTransformer implements AcmObjectToSolrDocTransformer<EcmFile> {

    private EcmFileDao ecmFileDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());


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

        solr.setParent_ref_s(in.getContainer().getContainerObjectId() + "-" + in.getContainer().getContainerObjectType());

        solr.setEcmFileId(in.getVersionSeriesId());

        List<String> tags = prepareTagList(in.getTags());
        solr.setTags_ss(tags);

        solr.setPublic_doc_b(true);
        solr.setProtected_object_b(false);

        return solr;
    }

    //No implementation needed
    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(EcmFile in) {
        return null;
    }

    @Override
    public SolrDocument toSolrQuickSearch(EcmFile in) {
        SolrDocument doc = new SolrDocument();

        // no access control on files (yet)
        doc.setPublic_doc_b(true);
        doc.setProtected_object_b(false);

        doc.setAuthor_s(in.getCreator());
        doc.setAuthor(in.getCreator());
        doc.setObject_type_s(in.getObjectType());
        doc.setObject_id_s("" + in.getId());
        doc.setCreate_tdt(in.getCreated());
        doc.setId(in.getId() + "-" + in.getObjectType());
        doc.setLast_modified_tdt(in.getModified());
        doc.setName(in.getFileName());
        doc.setModifier_s(in.getModifier());

        doc.setParent_object_id_i(in.getContainer().getContainerObjectId());
        doc.setParent_object_id_s("" + in.getContainer().getContainerObjectId());
        doc.setParent_object_type_s(in.getContainer().getContainerObjectType());

        doc.setParent_ref_s(in.getContainer().getContainerObjectId() + "-" + in.getContainer().getContainerObjectType());

        doc.setTitle_parseable(in.getFileName());
        doc.setTitle_t(in.getFileName());

        doc.setParent_folder_id_i(in.getFolder().getId());

        doc.setVersion_s(in.getActiveVersionTag());
        doc.setType_s(in.getFileType());
        doc.setCategory_s(in.getCategory());

        // need an _lcs field for sorting
        doc.setName_lcs(in.getFileName());

        doc.setCmis_version_series_id_s(in.getVersionSeriesId());
        
        doc.setMime_type_s(in.getFileMimeType());
        
        doc.setStatus_s(in.getStatus());
        
        doc.setHidden_b(isHidden(in));

        return doc;
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

    private List<String> prepareTagList(List<AcmAssociatedTag> tagList) {
        List<String> tagTextList = new ArrayList<>();
        for(AcmAssociatedTag tag: tagList){
            tagTextList.add(tag.getTag().getTagText());
        }
        return tagTextList;
    }
    
    private boolean isHidden(EcmFile file)
    {
    	if (file != null)
    	{
	    	String mimeType = file.getFileMimeType();
	    	
	    	if (mimeType != null && mimeType.contains(EcmFileConstants.MIME_TYPE_XML) && mimeType.contains(EcmFileConstants.MIME_TYPE_FREVVO_URL))
	    	{
	    		return true;
	    	}
    	}
    	
    	return false;
    }

    public EcmFileDao getEcmFileDao() {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }
}

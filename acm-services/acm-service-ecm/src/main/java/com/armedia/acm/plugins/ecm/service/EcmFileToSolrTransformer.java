package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 05.02.2015.
 */
public class EcmFileToSolrTransformer implements AcmObjectToSolrDocTransformer<EcmFile>
{

    private EcmFileDao ecmFileDao;
    private UserDao userDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    // whether to index file contents or just store document-related metadata
    private Boolean enableContentFileIndexing;

    @Override
    public List<EcmFile> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getEcmFileDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(EcmFile in)
    {
        if (enableContentFileIndexing)
        {
            return mapDocumentProperties(in);
        } else
        {
            return null;
        }
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(EcmFile in)
    {
        if (enableContentFileIndexing)
        {
            return null;
        } else
        {
            return mapDocumentProperties(in);
        }
    }

    @Override
    public SolrDocument toSolrQuickSearch(EcmFile in)
    {
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
        doc.setExt_s(in.getFileActiveVersionNameExtension());
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

        doc.setMime_type_s(in.getFileActiveVersionMimeType());

        doc.setStatus_s(in.getStatus());

        doc.setHidden_b(isHidden(in));

        mapAdditionalProperties(in, doc.getAdditionalProperties());

        return doc;
    }

    private SolrAdvancedSearchDocument mapDocumentProperties(EcmFile in)
    {

        // NOTE!!!! For EcmFile, if you need to add a field to the Solr content model, you must take an extra
        // step!!! Update the contentFileToSolrFlow.xml to also include the new field!!!

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setName(in.getFileName());
        solr.setExt_s(in.getFileActiveVersionNameExtension());
        solr.setContent_type(in.getFileActiveVersionMimeType());
        solr.setStatus_lcs(in.getStatus());
        solr.setTitle_parseable(in.getFileName());

        solr.setParent_id_s(Long.toString(in.getContainer().getId()));
        solr.setParent_type_s(in.getContainer().getObjectType());
        solr.setParent_number_lcs(in.getContainer().getContainerObjectTitle());

        solr.setParent_ref_s(in.getContainer().getContainerObjectId() + "-" + in.getContainer().getContainerObjectType());

        solr.setEcmFileId(in.getVersionSeriesId());

        solr.setPublic_doc_b(true);
        solr.setProtected_object_b(false);

        solr.setHidden_b(isHidden(in));

        mapAdditionalProperties(in, solr.getAdditionalProperties());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFullName());
            solr.setAssignee_full_name_lcs(creator.getFullName());
        } else
        {
            solr.setAssignee_full_name_lcs(in.getCreator());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFullName());
        }

        solr.setAdditionalProperty("security_field_lcs", in.getSecurityField());

        return solr;
    }

    private void mapAdditionalProperties(EcmFile in, Map<String, Object> additionalProperties)
    {
        if (in.getFileSource() != null)
        {
            additionalProperties.put("file_source_s", in.getFileSource());
        }

        additionalProperties.put("page_count_i", in.getPageCount());
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return EcmFile.class.equals(acmObjectType);
    }

    private boolean isHidden(EcmFile file)
    {
        if (file != null)
        {
            String mimeType = file.getFileActiveVersionMimeType();

            if ((mimeType != null && mimeType.contains(EcmFileConstants.MIME_TYPE_XML)
                    && mimeType.contains(EcmFileConstants.MIME_TYPE_FREVVO_URL))
                    || (mimeType != null && mimeType.contains(EcmFileConstants.MIME_TYPE_PNG)
                            && mimeType.contains(EcmFileConstants.MIME_TYPE_FREVVO_SIGNATURE_KEY)))
            {
                return true;
            }
        }

        return false;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public Boolean getEnableContentFileIndexing()
    {
        return enableContentFileIndexing;
    }

    public void setEnableContentFileIndexing(Boolean enableContentFileIndexing)
    {
        this.enableContentFileIndexing = enableContentFileIndexing;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return EcmFile.class;
    }
}

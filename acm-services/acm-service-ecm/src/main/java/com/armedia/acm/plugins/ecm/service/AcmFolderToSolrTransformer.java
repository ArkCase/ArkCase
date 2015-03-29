package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 3/23/15.
 */
public class AcmFolderToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmFolder>
{
    private AcmFolderDao dao;

    @Override
    public List<AcmFolder> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmFolder in)
    {
        // no implementation needed yet
        return null;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmFolder in)
    {

        SolrDocument doc = new SolrDocument();

        // no access control on folders (yet)
        doc.setPublic_doc_b(true);

        doc.setAuthor_s(in.getCreator());
        doc.setAuthor(in.getCreator());
        doc.setObject_type_s(in.getObjectType());
        doc.setObject_id_s("" + in.getId());
        doc.setCreate_tdt(in.getCreated());
        doc.setId(in.getId() + "-" + in.getObjectType());
        doc.setLast_modified_tdt(in.getModified());
        doc.setName(in.getName());
        doc.setModifier_s(in.getModifier());
        doc.setParent_object_id_i(in.getParentFolderId());
        doc.setParent_object_id_s(in.getParentFolderId() == null ? null : "" + in.getParentFolderId());
        doc.setParent_object_type_s(in.getParentFolderId() == null ? null : in.getObjectType());
        doc.setTitle_parseable(in.getName());
        doc.setTitle_t(in.getName());

        // folder id will be used to find files and folders that belong to this container
        doc.setFolder_id_i(in.getId());
        doc.setFolder_name_s(in.getName());

        // need an _lcs field for sorting
        doc.setName_lcs(in.getName());

        doc.setParent_folder_id_i(in.getParentFolderId());

        return doc;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmFolder in)
    {
        // no implementation needed yet
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmFolder.class.equals(acmObjectType);
    }

    public AcmFolderDao getDao()
    {
        return dao;
    }

    public void setDao(AcmFolderDao dao)
    {
        this.dao = dao;
    }
}

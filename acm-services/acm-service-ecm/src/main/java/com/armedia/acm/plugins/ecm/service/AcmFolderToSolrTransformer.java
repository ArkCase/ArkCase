package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 3/23/15.
 */
public class AcmFolderToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmFolder>
{
    private AcmFolderService folderService;

    private SearchAccessControlFields searchAccessControlFields;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<AcmFolder> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getFolderService().findModifiedSince(lastModified, start, pageSize);
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

        AcmFolder parentFolder = in.getParentFolder();

        SolrDocument doc = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(doc, in);

        doc.setAuthor_s(in.getCreator());
        doc.setAuthor(in.getCreator());
        doc.setObject_type_s(in.getObjectType());
        doc.setObject_id_s("" + in.getId());
        doc.setCreate_tdt(in.getCreated());
        doc.setId(in.getId() + "-" + in.getObjectType());
        doc.setLast_modified_tdt(in.getModified());
        doc.setName(in.getName());
        doc.setModifier_s(in.getModifier());
        doc.setParent_object_id_i(parentFolder == null ? null : parentFolder.getId());
        doc.setParent_object_id_s(parentFolder == null ? null : "" + parentFolder.getId());
        doc.setParent_object_type_s(parentFolder == null ? null : in.getObjectType());
        doc.setTitle_parseable(in.getName());
        doc.setTitle_t(in.getName());

        // folder id will be used to find files and folders that belong to this container
        doc.setFolder_id_i(in.getId());
        doc.setFolder_name_s(in.getName());

        // need an _lcs field for sorting
        doc.setName_lcs(in.getName());

        doc.setParent_folder_id_i(parentFolder == null ? null : parentFolder.getId());

        doc.setStatus_s(in.getStatus());

        doc.setAdditionalProperty("name_partial", in.getName());

        if (parentFolder != null)
        {
            try
            {
                AcmContainer container = getFolderService().findContainerByFolderIdTransactionIndependent(parentFolder.getId());
                doc.getAdditionalProperties().put("parent_container_object_type_s", container.getContainerObjectType());
                doc.getAdditionalProperties().put("parent_container_object_id_s", container.getContainerObjectId());
            } catch (AcmObjectNotFoundException e)
            {
                log.debug("Failed to index AcmContainer info fields for folder with id: [{}] ", in.getId(), e);
            }
        }
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

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmFolder.class;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }
}

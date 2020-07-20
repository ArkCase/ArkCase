package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConfig;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrConfig;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 05.02.2015.
 */
public class EcmFileToSolrTransformer implements AcmObjectToSolrDocTransformer<EcmFile>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileDao ecmFileDao;
    private UserDao userDao;
    private SearchAccessControlFields searchAccessControlFields;
    private ArkCaseBeanUtils arkCaseBeanUtils = new ArkCaseBeanUtils();
    private SolrConfig solrConfig;
    private DataAccessControlConfig dacConfig;
    private AcmDataService acmDataService;

    @Override
    public List<EcmFile> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getEcmFileDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrContentDocument toContentFileIndex(EcmFile in)
    {
        // whether to index file contents or just store document-related metadata
        if (solrConfig.getEnableContentFileIndexing())
        {
            return mapContentDocumentProperties(in);
        }

        return null;
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(EcmFile in)
    {
        if (solrConfig.getEnableContentFileIndexing())
        {
            return null;
        }
        else
        {
            return mapDocumentProperties(in);
        }
    }

    @Override
    public SolrDocument toSolrQuickSearch(EcmFile in)
    {
        SolrDocument doc = new SolrDocument();

        getSearchAccessControlFields().setAccessControlFields(doc, in);

        mapParentAclProperties(doc, in);

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
        doc.setTitle_parseable_lcs(in.getFileName());
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

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        doc.setAdditionalProperty("acm_participants_lcs", participantsListJson);
        doc.setAdditionalProperty("duplicate_b", in.isDuplicate());
        return doc;
    }

    private void mapParentAclProperties(SolrBaseDocument doc, EcmFile in)
    {
        if (!dacConfig.getEnableDocumentACL() && in.getParentObjectType() != null)
        {
            AcmAbstractDao<AcmObject> parentDAO = acmDataService.getDaoByObjectType(in.getParentObjectType());
            AcmObject parent = parentDAO.find(in.getParentObjectId());
            if (parent instanceof AcmAssignedObject)
            {
                getSearchAccessControlFields().setParentAccessControlFields(doc, (AcmAssignedObject) parent);
            }
        }
    }

    private SolrContentDocument mapContentDocumentProperties(EcmFile in)
    {
        SolrContentDocument solr = new SolrContentDocument();
        SolrAdvancedSearchDocument solrAdvancedSearchDocument = mapDocumentProperties(in);

        if (solrAdvancedSearchDocument != null)
        {
            try
            {
                getArkCaseBeanUtils().copyProperties(solr, solrAdvancedSearchDocument);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                log.error("Could not copy properties from SolrAdvancedSearchDocument to SolrContentDocument");
            }

            // Somehow "org.apache.commons.beanutils.BeanUtilBean.copyProperties(..)" is not finding
            // "additionalProperties" property. Copy them explicitly here.
            solr.getAdditionalProperties().putAll(solrAdvancedSearchDocument.getAdditionalProperties());
        }

        List<String> skipAdditionalPropertiesInURL = new ArrayList<>();
        skipAdditionalPropertiesInURL.add("file_source_s");
        skipAdditionalPropertiesInURL.add("name_partial");
        skipAdditionalPropertiesInURL.add("url");

        solr.setSkipAdditionalPropertiesInURL(skipAdditionalPropertiesInURL);

        return solr;
    }

    private SolrAdvancedSearchDocument mapDocumentProperties(EcmFile in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        getSearchAccessControlFields().setAccessControlFields(solr, in);

        mapParentAclProperties(solr, in);

        solr.setId(in.getId() + "-" + in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setName(in.getFileName());
        solr.setExt_s(in.getFileActiveVersionNameExtension());
        solr.setMime_type_s(in.getFileActiveVersionMimeType());
        solr.setContent_type(in.getFileActiveVersionMimeType());
        solr.setStatus_lcs(in.getStatus());
        solr.setTitle_parseable(in.getFileName());
        solr.setTitle_parseable_lcs(in.getFileName());

        solr.setParent_id_s(Long.toString(in.getContainer().getId()));
        solr.setParent_type_s(in.getContainer().getObjectType());
        solr.setParent_number_lcs(in.getContainer().getContainerObjectTitle());

        solr.setParent_ref_s(in.getContainer().getContainerObjectId() + "-" + in.getContainer().getContainerObjectType());
        solr.setParent_folder_id_i(in.getFolder().getId());

        solr.setEcmFileId(in.getVersionSeriesId());
        solr.setCmis_version_series_id_s(in.getVersionSeriesId());

        solr.setType_lcs(in.getFileType());

        solr.setHidden_b(isHidden(in));

        mapAdditionalProperties(in, solr.getAdditionalProperties());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFullName());
        }
        else
        {
            solr.setAdditionalProperty("creator_full_name_lcs", in.getCreator());
        }

        AcmUser assignee = getUserDao().quietFindByUserId(ParticipantUtils.getAssigneeIdFromParticipants(in.getParticipants()));
        if (assignee != null)
        {
            solr.setAssignee_full_name_lcs(assignee.getFullName());
        }
        else
        {
            solr.setAssignee_full_name_lcs("");
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFullName());
        }
        else
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", in.getModifier());
        }

        solr.setAdditionalProperty("security_field_lcs", in.getSecurityField());

        solr.setAdditionalProperty("cmis_repository_id_s", in.getCmisRepositoryId());

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        solr.setAdditionalProperty("acm_participants_lcs", participantsListJson);

        solr.setAdditionalProperty("duplicate_b", in.isDuplicate());

        return solr;
    }

    private void mapAdditionalProperties(EcmFile in, Map<String, Object> additionalProperties)
    {
        if (in.getFileSource() != null)
        {
            additionalProperties.put("file_source_s", in.getFileSource());
        }

        additionalProperties.put("page_count_i", in.getPageCount());
        additionalProperties.put("name_partial", in.getFileName());
        additionalProperties.put("description_s", in.getDescription());
        additionalProperties.put("file_lang_s", in.getFileLang());
        additionalProperties.put("link_b", in.isLink());
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

            // rendition support - to be moved from the MVA extension to core soon
            if ("Rendition".equals(file.getFileType()))
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

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }

    public ArkCaseBeanUtils getArkCaseBeanUtils()
    {
        return arkCaseBeanUtils;
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }

    public SolrConfig getSolrConfig()
    {
        return solrConfig;
    }

    public void setSolrConfig(SolrConfig solrConfig)
    {
        this.solrConfig = solrConfig;
    }

    public DataAccessControlConfig getDacConfig()
    {
        return dacConfig;
    }

    public void setDacConfig(DataAccessControlConfig dacConfig)
    {
        this.dacConfig = dacConfig;
    }
}

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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ACM_PARTICIPANTS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ASSIGNEE_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CMIS_VERSION_SERIES_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CONTENT_TYPE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ECM_FILE_ID;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.EXT_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.HIDDEN_B;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MIME_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_FOLDER_ID_I;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_NUMBER_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_OBJECT_ID_I;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TYPE_LCS;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.objectonverter.ArkCaseBeanUtils;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConfig;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrConfig;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
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
    private EcmFileConfig fileConfig;

    @Override
    public List<EcmFile> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getEcmFileDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrContentDocument toContentFileIndex(EcmFile in)
    {
        /**
         * indexing file contents along with file metadata to Solr.
         */
        if (solrConfig.getEnableContentFileIndexing() && getFileSizeBytes(in) < fileConfig.getDocumentSizeBytesLimit() && in.getVersions().size() > 0 && in.getVersions().get(in.getVersions().size() - 1).isValidFile())
        {
            return mapContentDocumentProperties(in);
        }

        return null;
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(EcmFile in)
    {
        /**
         * indexing only file metadata to Solr.
         */

        if (!(solrConfig.getEnableContentFileIndexing() && getFileSizeBytes(in) < fileConfig.getDocumentSizeBytesLimit() && in.getVersions().size() > 0 && in.getVersions().get(in.getVersions().size() - 1).isValidFile()))
        {
            return mapDocumentProperties(in);
        }
     
         return null;
    }

    private Long getFileSizeBytes(EcmFile in)
    {
        return in.getVersions().stream()
                .filter(fileVersion -> fileVersion.getVersionTag().equals(in.getActiveVersionTag()))
                .findFirst()
                .map(EcmFileVersion::getFileSizeBytes)
                .orElse(0L);
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
        log.debug("Creating Solr Content Document for file_id [{}] and fine_name [{}]", in.getVersionSeriesId(), in.getFileName());
        SolrAdvancedSearchDocument solrAdvancedSearchDocument = mapDocumentProperties(in);

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

        List<String> skipAdditionalPropertiesInURL = new ArrayList<>();
        skipAdditionalPropertiesInURL.add("file_source_s");
        skipAdditionalPropertiesInURL.add("url");

        solr.setSkipAdditionalPropertiesInURL(skipAdditionalPropertiesInURL);

        return solr;
    }

    private SolrAdvancedSearchDocument mapDocumentProperties(EcmFile in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        log.debug("Creating Solr advanced search document for file_id [{}] and fine_name [{}]", in.getVersionSeriesId(), in.getFileName());
        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                in.getObjectType(), in.getFileName());

        getSearchAccessControlFields().setAccessControlFields(solrDoc, in);

        mapParentAclProperties(solrDoc, in);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(EcmFile in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(EXT_S, in.getFileActiveVersionNameExtension());
        additionalProperties.put(MIME_TYPE_S, in.getFileActiveVersionMimeType());
        additionalProperties.put(CONTENT_TYPE, in.getFileActiveVersionMimeType());
        additionalProperties.put(STATUS_LCS, in.getStatus());
        additionalProperties.put(TITLE_PARSEABLE, in.getFileName());
        additionalProperties.put(TITLE_PARSEABLE_LCS, in.getFileName());
        additionalProperties.put("title_t", in.getFileName());

        additionalProperties.put(PARENT_ID_S, Long.toString(in.getContainer().getId()));
        additionalProperties.put(PARENT_TYPE_S, in.getContainer().getObjectType());
        additionalProperties.put(PARENT_NUMBER_LCS, in.getContainer().getContainerObjectTitle());
        additionalProperties.put(PARENT_REF_S, in.getContainer().getContainerObjectId() + "-" + in.getContainer().getContainerObjectType());
        additionalProperties.put(PARENT_FOLDER_ID_I, in.getFolder().getId());

        additionalProperties.put(ECM_FILE_ID, in.getVersionSeriesId());
        additionalProperties.put(CMIS_VERSION_SERIES_ID_S, in.getVersionSeriesId());

        additionalProperties.put(TYPE_LCS, in.getFileType());
        additionalProperties.put(HIDDEN_B, isHidden(in));

        if (in.getContainer() != null)
        {
            additionalProperties.put(PARENT_OBJECT_ID_I, in.getContainer().getContainerObjectId());
            additionalProperties.put(PARENT_ID_S, Long.toString(in.getContainer().getContainerObjectId()));
            additionalProperties.put(PARENT_TYPE_S, in.getContainer().getContainerObjectType());
        }

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            additionalProperties.put(CREATOR_FULL_NAME_LCS, creator.getFullName());
        }
        else
        {
            additionalProperties.put(CREATOR_FULL_NAME_LCS, in.getCreator());
        }

        AcmUser assignee = getUserDao().quietFindByUserId(ParticipantUtils.getAssigneeIdFromParticipants(in.getParticipants()));
        if (assignee != null)
        {
            additionalProperties.put(ASSIGNEE_FULL_NAME_LCS, assignee.getFullName());
        }
        else
        {
            additionalProperties.put(ASSIGNEE_FULL_NAME_LCS, in.getCreator());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            additionalProperties.put(MODIFIER_FULL_NAME_LCS, modifier.getFullName());
        }
        else
        {
            additionalProperties.put(MODIFIER_FULL_NAME_LCS, in.getModifier());
        }

        additionalProperties.put("security_field_lcs", in.getSecurityField());

        additionalProperties.put("cmis_repository_id_s", in.getCmisRepositoryId());
        additionalProperties.put("custodian_s", in.getCustodian());

        if (in.getZylabFileMetadata() != null)
        {
            additionalProperties.put("zylab_review_analysis_lcs", in.getZylabFileMetadata().getReviewedAnalysis());
        }

        String participantsListJson = ParticipantUtils.createParticipantsListJson(in.getParticipants());
        additionalProperties.put(ACM_PARTICIPANTS_LCS, participantsListJson);
        additionalProperties.put("duplicate_b", in.isDuplicate());

        additionalProperties.put("category_s", in.getCategory());
        additionalProperties.put("version_s", in.getActiveVersionTag());

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

    public EcmFileConfig getFileConfig()
    {
        return fileConfig;
    }

    public void setFileConfig(EcmFileConfig fileConfig)
    {
        this.fileConfig = fileConfig;
    }
    
}

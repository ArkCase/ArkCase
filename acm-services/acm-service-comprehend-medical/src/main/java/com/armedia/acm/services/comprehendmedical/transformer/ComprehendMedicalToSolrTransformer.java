package com.armedia.acm.services.comprehendmedical.transformer;

/*-
 * #%L
 * ACM Service: Transcribe
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

import com.armedia.acm.services.comprehendmedical.dao.ComprehendMedicalDao;
import com.armedia.acm.services.comprehendmedical.model.ComprehendMedical;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public class ComprehendMedicalToSolrTransformer implements AcmObjectToSolrDocTransformer<ComprehendMedical>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private ComprehendMedicalDao comprehendMedicalDao;

    @Override
    public List<ComprehendMedical> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getComprehendMedicalDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(ComprehendMedical in)
    {
        LOG.debug("Creating Solr advanced search document for Transcribe.");

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();
        solr.setId(String.format("%d-%s", in.getId(), in.getObjectType()));
        solr.setObject_id_s(String.valueOf(in.getId()));
        solr.setObject_type_s(in.getObjectType());
        solr.setName(in.getMediaEcmFileVersion().getFile().getFileName());
        solr.setTitle_parseable(in.getMediaEcmFileVersion().getFile().getFileName());
        solr.setTitle_parseable_lcs(in.getMediaEcmFileVersion().getFile().getFileName());
        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());
        solr.setStatus_lcs(in.getStatus());
        solr.setType_lcs(in.getType());

        solr.setAdditionalProperty("remote_id_s", in.getRemoteId());
        solr.setAdditionalProperty("language_s", in.getLanguage());
        solr.setAdditionalProperty("media_file_version_id_l", in.getMediaEcmFileVersion().getId());

        solr.setAdditionalProperty("process_id_s", in.getProcessId());

        solr.setAdditionalProperty("parent_root_id_s", in.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        solr.setAdditionalProperty("parent_root_type_s", in.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        solr.setAdditionalProperty("parent_file_id_s", in.getMediaEcmFileVersion().getFile().getId());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(ComprehendMedical in)
    {
        LOG.debug("Creating Solr quick search document for Transcribe.");

        SolrDocument solr = new SolrDocument();
        solr.setId(String.format("%d-%s", in.getId(), in.getObjectType()));
        solr.setObject_id_s(String.valueOf(in.getId()));
        solr.setObject_type_s(in.getObjectType());
        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());
        solr.setStatus_s(in.getStatus());
        solr.setName(in.getMediaEcmFileVersion().getFile().getFileName());
        solr.setName_lcs(in.getMediaEcmFileVersion().getFile().getFileName());
        solr.setTitle_parseable(in.getMediaEcmFileVersion().getFile().getFileName());
        solr.setTitle_parseable_lcs(in.getMediaEcmFileVersion().getFile().getFileName());
        solr.setTitle_t(in.getMediaEcmFileVersion().getFile().getFileName());

        return solr;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return ComprehendMedical.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return ComprehendMedical.class;
    }

    public ComprehendMedicalDao getComprehendMedicalDao()
    {
        return comprehendMedicalDao;
    }

    public void setComprehendMedicalDao(ComprehendMedicalDao comprehendMedicalDao)
    {
        this.comprehendMedicalDao = comprehendMedicalDao;
    }
}

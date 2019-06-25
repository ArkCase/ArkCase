package com.armedia.acm.services.transcribe.transformer;

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

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.transcribe.dao.TranscribeItemDao;
import com.armedia.acm.services.transcribe.model.TranscribeItem;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public class TranscribeItemToSolrTransformer implements AcmObjectToSolrDocTransformer<TranscribeItem>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private TranscribeItemDao transcribeItemDao;

    @Override
    public List<TranscribeItem> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getTranscribeItemDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(TranscribeItem in)
    {
        LOG.debug("Creating Solr advanced search document for TranscribeItem.");

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();
        solr.setId(String.format("%d-%s", in.getId(), in.getObjectType()));
        solr.setObject_id_s(String.valueOf(in.getId()));
        solr.setObject_type_s(in.getObjectType());
        solr.setName(TranscribeUtils.getFirstWords(in.getText(), 5));
        solr.setTitle_parseable(TranscribeUtils.getFirstWords(in.getText(), 5));
        solr.setTitle_parseable_lcs(TranscribeUtils.getFirstWords(in.getText(), 5));
        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setParent_id_s(String.valueOf(in.getTranscribe().getId()));
        solr.setParent_type_s(in.getTranscribe().getObjectType());
        solr.setParent_name_t(in.getTranscribe().getMediaEcmFileVersion().getFile().getFileName());
        solr.setParent_ref_s(String.format("%d-%s", in.getTranscribe().getId(), in.getTranscribe().getObjectType()));

        solr.setAdditionalProperty("start_time_s", in.getStartTime().toString());
        solr.setAdditionalProperty("end_time_s", in.getEndTime().toString());
        solr.setAdditionalProperty("confidence_l", in.getConfidence());
        solr.setAdditionalProperty("corrected_b", in.getCorrected().booleanValue());
        solr.setAdditionalProperty("text_s", in.getText());

        solr.setAdditionalProperty("parent_root_id_s",
                in.getTranscribe().getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        solr.setAdditionalProperty("parent_root_type_s",
                in.getTranscribe().getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        solr.setAdditionalProperty("parent_file_id_s", in.getTranscribe().getMediaEcmFileVersion().getFile().getId());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(TranscribeItem in)
    {
        SolrDocument solr = new SolrDocument();
        solr.setId(String.format("%d-%s", in.getId(), in.getObjectType()));
        solr.setObject_id_s(String.valueOf(in.getId()));
        solr.setObject_type_s(in.getObjectType());
        solr.setName(TranscribeUtils.getFirstWords(in.getText(), 5));
        solr.setName_lcs(TranscribeUtils.getFirstWords(in.getText(), 5));
        solr.setTitle_parseable(TranscribeUtils.getFirstWords(in.getText(), 5));
        solr.setTitle_parseable_lcs(TranscribeUtils.getFirstWords(in.getText(), 5));
        solr.setTitle_t(TranscribeUtils.getFirstWords(in.getText(), 5));
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        return solr;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return TranscribeItem.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return TranscribeItem.class;
    }

    public TranscribeItemDao getTranscribeItemDao()
    {
        return transcribeItemDao;
    }

    public void setTranscribeItemDao(TranscribeItemDao transcribeItemDao)
    {
        this.transcribeItemDao = transcribeItemDao;
    }
}

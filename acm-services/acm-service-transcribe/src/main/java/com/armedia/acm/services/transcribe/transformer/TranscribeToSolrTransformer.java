package com.armedia.acm.services.transcribe.transformer;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.transcribe.dao.TranscribeDao;
import com.armedia.acm.services.transcribe.model.Transcribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public class TranscribeToSolrTransformer implements AcmObjectToSolrDocTransformer<Transcribe>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private TranscribeDao transcribeDao;

    @Override
    public List<Transcribe> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getTranscribeDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Transcribe in)
    {
        LOG.debug("Creating Solr advanced search document for Transcribe.");

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();
        solr.setId(String.format("%d-%s", in.getId(), in.getObjectType()));
        solr.setObject_id_s(String.valueOf(in.getId()));
        solr.setObject_type_s(in.getObjectType());
        solr.setName(in.getMediaEcmFile().getFileName());
        solr.setTitle_parseable(in.getMediaEcmFile().getFileName());
        solr.setTitle_parseable_lcs(in.getMediaEcmFile().getFileName());
        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());
        solr.setStatus_lcs(in.getStatus());
        solr.setType_lcs(in.getType());

        solr.setAdditionalProperty("remote_id_l", in.getRemoteId());
        solr.setAdditionalProperty("language_s", in.getLanguage());
        solr.setAdditionalProperty("media_file_id_l", in.getMediaEcmFile().getId());
        solr.setAdditionalProperty("media_file_version_id_l", in.getMediaEcmFileVersion().getId());

        if (in.getTranscribeEcmFile() != null)
        {
            solr.setAdditionalProperty("transcribe_file_id_l", in.getTranscribeEcmFile().getId());
        }
        else
        {
            solr.setAdditionalProperty("transcribe_file_id_l", null);
        }

        solr.setAdditionalProperty("process_id_s", in.getProcessId());
        solr.setAdditionalProperty("word_count_l", in.getWordCount());


        return null;
    }

    @Override
    public SolrDocument toSolrQuickSearch(Transcribe in)
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
        solr.setName(in.getMediaEcmFile().getFileName());
        solr.setName_lcs(in.getMediaEcmFile().getFileName());
        solr.setTitle_parseable(in.getMediaEcmFile().getFileName());
        solr.setTitle_parseable_lcs(in.getMediaEcmFile().getFileName());
        solr.setTitle_t(in.getMediaEcmFile().getFileName());

        return solr;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Transcribe.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Transcribe.class;
    }

    public TranscribeDao getTranscribeDao()
    {
        return transcribeDao;
    }

    public void setTranscribeDao(TranscribeDao transcribeDao)
    {
        this.transcribeDao = transcribeDao;
    }
}

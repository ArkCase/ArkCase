package com.armedia.acm.services.transcribe.transformer;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;
import com.armedia.acm.services.transcribe.dao.TranscribeItemDao;
import com.armedia.acm.services.transcribe.model.TranscribeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public class TranscribeItemToSolrTransformer implements AcmObjectToSolrDocTransformer<TranscribeItem>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

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
        solr.setParent_name_t(in.getTranscribe().getMediaEcmFile().getFileName());
        solr.setParent_ref_s(String.format("%d-%s", in.getTranscribe().getId(), in.getTranscribe().getObjectType()));

        solr.setAdditionalProperty("start_time_s", in.getStartTime().toString());
        solr.setAdditionalProperty("end_time_s", in.getEndTime().toString());
        solr.setAdditionalProperty("confidence_l", in.getConfidence());
        solr.setAdditionalProperty("corrected_b", in.getCorrected().booleanValue());
        solr.setAdditionalProperty("text_s", in.getText());

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

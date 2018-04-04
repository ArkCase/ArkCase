package com.armedia.acm.services.search.service.solr;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.InputStream;

public interface SolrPostClient
{

    @Retryable(maxAttempts = 10, value = SolrPostException.class, backoff = @Backoff(delay = 3000, multiplier = 1.5, random = true))
    void sendToSolr(SolrCore core, String json) throws SolrPostException;

    @Retryable(maxAttempts = 10, value = SolrPostException.class, backoff = @Backoff(delay = 3000, multiplier = 1.5, random = true))
    void sendToSolr(SolrCore core, SolrContentDocument solrContentDocument, InputStream stream, long contentLength)
            throws SolrPostException;

}
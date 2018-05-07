package com.armedia.acm.services.search.service.solr;

import com.armedia.acm.services.search.model.SolrCore;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public interface SolrPostClient
{

    @Retryable(maxAttempts = 10, value = SolrPostException.class, backoff = @Backoff(delay = 3000, multiplier = 1.5, random = true))
    void sendToSolr(SolrCore core, String json) throws SolrPostException;

}
package com.armedia.acm.services.dataupdate.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class SolrFullReindexExecutor implements AcmDataUpdateExecutor
{
    @Autowired
    SolrReindexService solrReindexService;

    @Override
    public String getUpdateId() {
        return "solr-full-reindex-remove-quick-search";
    }

    @Override
    public void execute()
    {
        List<Class> solr = new ArrayList<>();
        solrReindexService.reindex(solr);
    }
}

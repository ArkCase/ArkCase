package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.plugins.task.model.AcmTask;

import java.util.Arrays;

public class SolrReindexTaskExecutor implements AcmDataUpdateExecutor
{
    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-acm-task-reindex-v1";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(AcmTask.class));
    }

    public SolrReindexService getSolrReindexService()
    {
        return solrReindexService;
    }

    public void setSolrReindexService(SolrReindexService solrReindexService)
    {
        this.solrReindexService = solrReindexService;
    }
}

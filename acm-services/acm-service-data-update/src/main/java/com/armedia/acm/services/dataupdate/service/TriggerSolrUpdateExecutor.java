package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import java.util.Arrays;

public class TriggerSolrUpdateExecutor implements AcmDataUpdateExecutor
{

    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-users-and-groups-update-v2";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(AcmUser.class, AcmGroup.class));
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

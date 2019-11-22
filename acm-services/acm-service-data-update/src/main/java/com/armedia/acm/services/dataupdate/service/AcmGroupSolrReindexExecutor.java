package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.users.model.group.AcmGroup;

import java.util.Arrays;

/**
 * @author aleksandar.bujaroski
 */
public class AcmGroupSolrReindexExecutor implements AcmDataUpdateExecutor
{
    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-control-group-update";
    }

    @Override
    public void execute()
    {
        getSolrReindexService().reindex(Arrays.asList(AcmGroup.class));
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

package gov.foia.service.dataupdate;

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import com.armedia.acm.services.dataupdate.service.SolrReindexService;

import java.util.Arrays;

import gov.foia.model.FOIAPerson;

/**
 * Created by ana.serafimoska
 */
public class SolrReindexFOIAPersonExecutor implements AcmDataUpdateExecutor
{
    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-foia-person-reindex-v1";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(FOIAPerson.class));
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

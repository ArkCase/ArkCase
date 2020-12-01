package gov.foia.service.dataupdate;

import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import com.armedia.acm.services.dataupdate.service.SolrReindexService;

import java.util.Arrays;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on December, 2020
 */
public class SolrReindexPersonAssociationsExecutor implements AcmDataUpdateExecutor
{

    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-person-associations-reindex-v1";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(PersonAssociation.class));
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

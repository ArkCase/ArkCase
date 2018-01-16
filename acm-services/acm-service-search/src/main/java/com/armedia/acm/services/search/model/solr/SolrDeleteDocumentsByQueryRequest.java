package com.armedia.acm.services.search.model.solr;

/**
 * Created by nebojsha.davidovikj on 11/28/17.
 */
public class SolrDeleteDocumentsByQueryRequest
{
    private SolrDocumentsQuery delete;

    public SolrDeleteDocumentsByQueryRequest(SolrDocumentsQuery delete)
    {
        this.delete = delete;
    }

    public SolrDocumentsQuery getDelete()
    {
        return delete;
    }
}

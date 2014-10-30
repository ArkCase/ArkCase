package com.armedia.acm.services.search.model.solr;

/**
 * Created by armdev on 10/27/14.
 */
public class SolrDeleteDocumentByIdRequest
{
    private SolrDocumentId delete;

    public SolrDeleteDocumentByIdRequest(SolrDocumentId delete)
    {
        this.delete = delete;
    }

    public SolrDocumentId getDelete()
    {
        return delete;
    }
}

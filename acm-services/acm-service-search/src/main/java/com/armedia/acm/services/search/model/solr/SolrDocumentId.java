package com.armedia.acm.services.search.model.solr;

/**
 * Created by armdev on 10/27/14.
 */
public class SolrDocumentId implements SolrBaseDocument
{
    private String id;

    public SolrDocumentId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }
}

package com.armedia.acm.services.search.model.solr;

import java.util.List;

/**
 * Created by armdev on 10/27/14.
 */
public class SolrDocumentId
{
    private String id;

    public SolrDocumentId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

}

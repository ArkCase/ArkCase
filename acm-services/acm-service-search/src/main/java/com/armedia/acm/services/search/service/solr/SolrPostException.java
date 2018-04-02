package com.armedia.acm.services.search.service.solr;

public class SolrPostException extends Exception
{

    public SolrPostException()
    {
        super();
    }

    public SolrPostException(String message)
    {
        super(message);
    }

    public SolrPostException(Throwable cause)
    {
        super(cause);
    }

    public SolrPostException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SolrPostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

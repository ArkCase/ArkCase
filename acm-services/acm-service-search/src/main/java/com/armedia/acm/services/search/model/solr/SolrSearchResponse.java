package com.armedia.acm.services.search.model.solr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 16, 2017
 *
 */
@JsonInclude(Include.NON_NULL)
public class SolrSearchResponse<K extends ResponseHeader, T>
{

    private K header;

    private int numFound;

    private int start;

    private T payload;

    /**
     * @return the header
     */
    public K getHeader()
    {
        return header;
    }

    /**
     * @param header
     *            the header to set
     */
    public void setHeader(K header)
    {
        this.header = header;
    }

    /**
     * @return the numFound
     */
    public int getNumFound()
    {
        return numFound;
    }

    /**
     * @param numFound
     *            the numFound to set
     */
    public void setNumFound(int numFound)
    {
        this.numFound = numFound;
    }

    /**
     * @return the start
     */
    public int getStart()
    {
        return start;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStart(int start)
    {
        this.start = start;
    }

    /**
     * @return the payload
     */
    public T getPayload()
    {
        return payload;
    }

    /**
     * @param payload
     *            the payload to set
     */
    public void setPayload(T payload)
    {
        this.payload = payload;
    }

}
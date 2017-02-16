/**
 *
 */
package com.armedia.acm.plugins.category.web.api;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 16, 2017
 *
 */
public class SolrResponse<T>
{

    private int numFound;

    private int start;

    private T payload;

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
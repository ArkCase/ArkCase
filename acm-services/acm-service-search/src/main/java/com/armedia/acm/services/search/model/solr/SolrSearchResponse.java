package com.armedia.acm.services.search.model.solr;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

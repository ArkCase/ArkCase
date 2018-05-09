/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import javax.xml.bind.annotation.XmlTransient;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class SearchResult
{

    private List<String> result;
    private Long id;
    private Long page;
    private Long size;
    private String information;

    /**
     * @return the result
     */
    @XmlTransient
    public List<String> getResult()
    {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(List<String> result)
    {
        this.result = result;
    }

    /**
     * @return the id
     */
    @XmlTransient
    public Long getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the page
     */
    @XmlTransient
    public Long getPage()
    {
        return page;
    }

    /**
     * @param page
     *            the page to set
     */
    public void setPage(Long page)
    {
        this.page = page;
    }

    /**
     * @return the size
     */
    @XmlTransient
    public Long getSize()
    {
        return size;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize(Long size)
    {
        this.size = size;
    }

    /**
     * @return the information
     */
    @XmlTransient
    public String getInformation()
    {
        return information;
    }

    /**
     * @param information
     *            the information to set
     */
    public void setInformation(String information)
    {
        this.information = information;
    }

}

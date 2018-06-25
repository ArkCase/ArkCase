package com.armedia.acm.plugins.admin.model;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.QueryType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 25, 2017
 *
 */

@JsonIdentityInfo(generator = JSOGGenerator.class)
public class CorrespondenceQueryResponse
{

    private String beanId;

    private QueryType queryType;

    private String sqlQuery;

    private List<String> fieldNames;

    /**
     *
     */
    public CorrespondenceQueryResponse()
    {
    }

    public CorrespondenceQueryResponse(String beanId, CorrespondenceQuery queryBean)
    {
        this.beanId = beanId;
        queryType = queryBean.getType();
        sqlQuery = queryBean.getSqlQuery();
        fieldNames = queryBean.getFieldNames();
    }

    /**
     * @return the beanId
     */
    public String getBeanId()
    {
        return beanId;
    }

    /**
     * @param beanId
     *            the beanId to set
     */
    public void setBeanId(String beanId)
    {
        this.beanId = beanId;
    }

    /**
     * @return the queryType
     */
    public QueryType getQueryType()
    {
        return queryType;
    }

    /**
     * @param queryType
     *            the queryType to set
     */
    public void setQueryType(QueryType queryType)
    {
        this.queryType = queryType;
    }

    /**
     * @return the sqlQuery
     */
    public String getSqlQuery()
    {
        return sqlQuery;
    }

    /**
     * @param sqlQuery
     *            the sqlQuery to set
     */
    public void setSqlQuery(String sqlQuery)
    {
        this.sqlQuery = sqlQuery;
    }

    /**
     * @return the fieldNames
     */
    public List<String> getFieldNames()
    {
        return fieldNames;
    }

    /**
     * @param fieldNames
     *            the fieldNames to set
     */
    public void setFieldNames(List<String> fieldNames)
    {
        this.fieldNames = fieldNames;
    }

}

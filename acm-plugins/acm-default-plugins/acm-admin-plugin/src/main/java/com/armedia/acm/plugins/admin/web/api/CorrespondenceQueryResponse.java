package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.QueryType;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 25, 2017
 *
 */
public class CorrespondenceQueryResponse
{

    private String beanId;

    private QueryType queryType;

    private String jpaQuery;

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
        jpaQuery = queryBean.getJpaQuery();
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
     * @param beanId the beanId to set
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
     * @param queryType the queryType to set
     */
    public void setQueryType(QueryType queryType)
    {
        this.queryType = queryType;
    }

    /**
     * @return the jpaQuery
     */
    public String getJpaQuery()
    {
        return jpaQuery;
    }

    /**
     * @param jpaQuery the jpaQuery to set
     */
    public void setJpaQuery(String jpaQuery)
    {
        this.jpaQuery = jpaQuery;
    }

    /**
     * @return the fieldNames
     */
    public List<String> getFieldNames()
    {
        return fieldNames;
    }

    /**
     * @param fieldNames the fieldNames to set
     */
    public void setFieldNames(List<String> fieldNames)
    {
        this.fieldNames = fieldNames;
    }

}

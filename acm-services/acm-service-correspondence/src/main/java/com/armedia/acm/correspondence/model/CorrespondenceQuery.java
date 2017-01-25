package com.armedia.acm.correspondence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 1/25/2017.
 */
public class CorrespondenceQuery
{
    private QueryType queryType;

    private String jpaQuery;

    private List<String> fieldNames = new ArrayList<>();

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

    public String getJpaQuery()
    {
        return jpaQuery;
    }

    public void setJpaQuery(String jpaQuery)
    {
        this.jpaQuery = jpaQuery;
    }

    public List<String> getFieldNames()
    {
        return new ArrayList<>(fieldNames);
    }

    public void setFieldNames(List<String> fieldNames)
    {
        if (fieldNames != null && !fieldNames.isEmpty())
        {
            this.fieldNames.clear();
            this.fieldNames.addAll(fieldNames);
        }
    }
}

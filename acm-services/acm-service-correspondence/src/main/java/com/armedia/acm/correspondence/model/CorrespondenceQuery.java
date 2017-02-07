package com.armedia.acm.correspondence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 1/25/2017.
 */
public class CorrespondenceQuery
{
    private QueryType type;

    private String jpaQuery;

    private List<String> fieldNames = new ArrayList<>();

    /**
     * @return the type
     */
    public QueryType getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(QueryType type)
    {
        this.type = type;
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

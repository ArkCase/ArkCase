package com.armedia.acm.correspondence.model;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 1/25/2017.
 */
public class CorrespondenceQuery
{
    private String type;
    private String jpaQuery;
    private List<String> fieldNames;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJpaQuery() {
        return jpaQuery;
    }

    public void setJpaQuery(String jpaQuery) {
        this.jpaQuery = jpaQuery;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }
}

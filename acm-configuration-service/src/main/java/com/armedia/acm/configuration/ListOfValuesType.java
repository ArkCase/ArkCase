package com.armedia.acm.configuration;


public enum ListOfValuesType
{
    COMPLAINT_PRIORITY("acm_complaint_priority_lu"),
    COMPLAINT_TYPE("acm_complaint_type_lu");

    private String tableName;

    private ListOfValuesType(String tableName)
    {
        this.tableName = tableName;
    }

    public String getTableName()
    {
        return tableName;
    }
}

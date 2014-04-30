package com.armedia.acm.configuration;

/**
 * Allow plugins to define their own lookup tables, but still prevent SQL injection.  We don't want to just send
 * a raw String table name to query a lookup table. Plugin authors have to write a LookupTableDescriptor to
 * encapsulate the table name.
 */
public class LookupTableDescriptor
{
    private String tableName;
    private String description;

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}

package com.armedia.acm.plugins.admin.model;

public class PrivilegeItem implements Comparable<PrivilegeItem>
{
    private String key;
    private String value;

    public PrivilegeItem()
    {
    }

    public PrivilegeItem(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public int compareTo(PrivilegeItem privilegeItem)
    {
        return this.value.toLowerCase().compareTo(privilegeItem.value.toLowerCase());
    }
}
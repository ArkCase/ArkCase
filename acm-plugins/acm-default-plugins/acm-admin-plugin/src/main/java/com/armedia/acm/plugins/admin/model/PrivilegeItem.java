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

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof PrivilegeItem))
        {
            return false;
        }

        PrivilegeItem privilegeItem = (PrivilegeItem) obj;

        return this.key.equalsIgnoreCase(privilegeItem.getKey()) && this.value.equalsIgnoreCase(privilegeItem.getValue());
    }
}
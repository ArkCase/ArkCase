package com.armedia.acm.plugins.onlyoffice.model.callback;

public class Action
{
    private Integer type;
    private String userid;

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    @Override
    public String toString()
    {
        return "Action{" +
                "type=" + type +
                ", userid='" + userid + '\'' +
                '}';
    }
}

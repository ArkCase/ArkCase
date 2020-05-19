package com.armedia.acm.plugins.consultation.model;

public class ConsultationsByStatusDto
{
    private String status;
    private int count;

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
}

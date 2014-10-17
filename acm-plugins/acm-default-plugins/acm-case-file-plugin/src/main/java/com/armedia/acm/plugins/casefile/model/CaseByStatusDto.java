package com.armedia.acm.plugins.casefile.model;

/**
 * Created by marjan.stefanoski on 9/3/2014.
 */
public class CaseByStatusDto
{
    private String status;
    private int count;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

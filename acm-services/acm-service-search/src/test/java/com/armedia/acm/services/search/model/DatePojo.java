package com.armedia.acm.services.search.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by armdev on 5/5/15.
 */
public class DatePojo
{

    @JsonFormat(pattern = SearchConstants.ISO_DATE_FORMAT)
    private Date date;

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
}

package com.armedia.acm.plugins.admin.model;

import java.util.List;

public class HolidayConfiguration
{

    private Boolean includeWeekends;
    private List<HolidayItem> holidays;

    public Boolean getIncludeWeekends()
    {
        return includeWeekends;
    }

    public void setIncludeWeekends(Boolean includeWeekends)
    {
        this.includeWeekends = includeWeekends;
    }

    public List<HolidayItem> getHolidays()
    {
        return holidays;
    }

    public void setHolidays(List<HolidayItem> holidays)
    {
        this.holidays = holidays;
    }
}

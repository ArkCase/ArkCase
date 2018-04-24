package com.armedia.acm.plugins.admin.model;

import java.util.List;

public class HolidayConfiguration
{

    private Boolean weekends;
    private List<HolidayItem> holidays;

    public Boolean getWeekends()
    {
        return weekends;
    }

    public void setWeekends(Boolean weekends)
    {
        this.weekends = weekends;
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

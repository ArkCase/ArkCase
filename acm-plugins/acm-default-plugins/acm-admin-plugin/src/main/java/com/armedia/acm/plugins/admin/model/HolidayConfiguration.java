package com.armedia.acm.plugins.admin.model;

import java.util.List;

public class HolidayConfiguration
{

    private List<HolidayItem> holidays;

    public List<HolidayItem> getHolidays()
    {
        return holidays;
    }

    public void setHolidays(List<HolidayItem> holidays)
    {
        this.holidays = holidays;
    }
}

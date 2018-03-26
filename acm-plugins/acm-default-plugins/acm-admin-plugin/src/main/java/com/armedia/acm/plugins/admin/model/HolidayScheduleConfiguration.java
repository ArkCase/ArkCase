package com.armedia.acm.plugins.admin.model;

import java.util.List;

public class HolidayScheduleConfiguration
{

    private List<HolidayScheduleItems> schedule;

    public List<HolidayScheduleItems> getSchedule()
    {
        return schedule;
    }

    public void setSchedule(List<HolidayScheduleItems> schedule)
    {
        this.schedule = schedule;
    }
}

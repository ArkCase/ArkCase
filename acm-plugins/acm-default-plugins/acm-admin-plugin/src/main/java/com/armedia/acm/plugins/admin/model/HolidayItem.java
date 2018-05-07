package com.armedia.acm.plugins.admin.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

public class HolidayItem
{
    private String holidayName;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate holidayDate;

    public String getHolidayName()
    {
        return holidayName;
    }

    public void setHolidayName(String holidayName)
    {
        this.holidayName = holidayName;
    }

    public LocalDate getHolidayDate()
    {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate)
    {
        this.holidayDate = holidayDate;
    }
}

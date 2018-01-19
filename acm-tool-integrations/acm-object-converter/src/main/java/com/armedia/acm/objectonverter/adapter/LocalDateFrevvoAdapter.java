package com.armedia.acm.objectonverter.adapter;

import com.armedia.acm.objectonverter.DateFormats;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateFrevvoAdapter extends XmlAdapter<String, LocalDate>
{

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DateFormats.FREVVO_DATE_FORMAT_MARSHAL_UNMARSHAL);

    @Override
    public String marshal(LocalDate date) throws Exception
    {
        return date == null ? null : date.format(dateFormatter);
    }

    @Override
    public LocalDate unmarshal(String strDate) throws Exception
    {
        return strDate == null ? null : LocalDate.parse(strDate, dateFormatter);
    }
}

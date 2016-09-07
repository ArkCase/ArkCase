package com.armedia.acm.objectonverter.adapter;

import com.armedia.acm.objectonverter.DateFormats;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateMillisecondAdapter extends XmlAdapter<Long, Date> {
	
	@Override
	public Long marshal(Date date) throws Exception
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		return calendar.getTimeInMillis();
	}

	@Override
	public Date unmarshal(Long date) throws Exception
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);

		return calendar.getTime();
	}

}

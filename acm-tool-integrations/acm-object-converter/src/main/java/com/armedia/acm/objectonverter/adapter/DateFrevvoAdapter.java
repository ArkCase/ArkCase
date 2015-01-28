package com.armedia.acm.objectonverter.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.armedia.acm.objectonverter.DateFormats;

public class DateFrevvoAdapter extends XmlAdapter<String, Date> {

	private SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormats.FREVVO_DATE_FORMAT_MARSHAL_UNMARSHAL);
	
	@Override
	public String marshal(Date date) throws Exception 
	{
		return getDateFormat().format(date);
	}

	@Override
	public Date unmarshal(String date) throws Exception 
	{
		return getDateFormat().parse(date);
	}

	public SimpleDateFormat getDateFormat() 
	{
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) 
	{
		this.dateFormat = dateFormat;
	}

}

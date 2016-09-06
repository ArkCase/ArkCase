package com.armedia.acm.objectonverter.adapter;

import com.armedia.acm.objectonverter.DateFormats;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSolrAdapter extends XmlAdapter<String, Date> {

	private SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormats.SOLR_DATE_FORMAT);
	
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

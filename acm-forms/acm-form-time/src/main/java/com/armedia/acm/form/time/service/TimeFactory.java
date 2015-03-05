/**
 * 
 */
package com.armedia.acm.form.time.service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.form.time.model.TimeForm;
import com.armedia.acm.form.time.model.TimeFormConstants;
import com.armedia.acm.form.time.model.TimeItem;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;

/**
 * @author riste.tutureski
 *
 */
public class TimeFactory {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	/**
	 * Converting Frevvo TimeForm to AcmTimesheet
	 * 
	 * @param form
	 * @return
	 */
	public AcmTimesheet asAcmTimesheet(TimeForm form)
	{
		LOG.debug("Start converting Frevvo Time Form to Acm Timesheet ...");
		
		AcmTimesheet retval = new AcmTimesheet();
		
		if (form != null)
		{
			retval.setId(form.getId());
			retval.setUserId(form.getUser());
			retval.setTimes(asAcmTimes(form));
			retval.setStartDate(getStartDate(form.getPeriod()));
			retval.setEndDate(getEndDate(form.getPeriod()));
		}
		else
		{
			LOG.debug("The conversion process is not executed. Form is null.");
		}
		
		LOG.debug("End converting Frevvo Time Form to Acm Timesheet.");
		
		return retval;
	}
	
	/**
	 * Converting AcmTimesheet to Frevvo TimeForm
	 * 
	 * @param timesheet
	 * @return
	 */
	public TimeForm asFrevvoTimeForm(AcmTimesheet timesheet)
	{
		LOG.debug("Start converting Acm Timesheet to Frevvo Time Form ...");
		
		TimeForm form = null;
		
		if (timesheet != null)
		{
			form = new TimeForm();
			
			form.setId(timesheet.getId());
			form.setUser(timesheet.getUserId());
			
			// Doesn't matter which date - it should be one date between start and end date ... I am taking "startDate"
			form.setPeriod(timesheet.getStartDate());
			
			form.setItems(asFrevvoTimeItems(timesheet));
		}
		else
		{
			LOG.debug("The conversion process is not executed. Timesheet is null.");
		}
		
		LOG.debug("End converting Acm Timesheet to Frevvo Time Form.");
		
		return form;
	}
	
	/**
	 * Converting Frevvo TimeItems to AcmTimes
	 * 
	 * @param form
	 * @return
	 */
	private List<AcmTime> asAcmTimes(TimeForm form)
	{
		LOG.debug("Converting Frevvo Time Items to Acm Times.");
		
		List<AcmTime> retval = new ArrayList<>();
		
		if (form != null && form.getItems() != null)
		{
			for (TimeItem item : form.getItems())
			{
				// Create AcmTime for each day from Sunday to Saturday for TimeItem (one row in the form)
				List<AcmTime> times = getTimesFromItem(item, form);
				retval.addAll(times);					
			}
		}
		
		return retval;
	}
	
	/**
	 * Item has 7 fields for each day of the week. This method create a list of AcmTimes for each day from item.
	 * 
	 * @param item
	 * @param form
	 * @return
	 */
	private List<AcmTime> getTimesFromItem(TimeItem item, TimeForm form)
	{
		LOG.debug("Taking Times from Frevvo Time Item.");
		
		List<AcmTime> times = new ArrayList<>();
		
		try 
		{
			// Create calendar for given date and set the current date to first day of the week
			// (first day of the week is Sunday)
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(form.getPeriod());
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			
			// Get TimeItem class information
			BeanInfo info = Introspector.getBeanInfo(TimeItem.class);
			
			// Go to all properties for TimeItem
			for (PropertyDescriptor descriptor : info.getPropertyDescriptors())
			{
				// Get the name of the property and his value
				String name = descriptor.getName();
				Object value = descriptor.getReadMethod().invoke(item);
				
				// If the property is "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
				// create the offset from first day of the week
				int offset = -1;
				if (TimeFormConstants.SUNDAY.equals(name.toUpperCase()))  offset = 0;
				if (TimeFormConstants.MONDAY.equals(name.toUpperCase()))  offset = 1;
				if (TimeFormConstants.TUESDAY.equals(name.toUpperCase()))  offset = 2;
				if (TimeFormConstants.WEDNESDAY.equals(name.toUpperCase()))  offset = 3;
				if (TimeFormConstants.THURSDAY.equals(name.toUpperCase()))  offset = 4;
				if (TimeFormConstants.FRIDAY.equals(name.toUpperCase()))  offset = 5;
				if (TimeFormConstants.SATURDAY.equals(name.toUpperCase()))  offset = 6;

				// If the offset is different than -1 (this means that the property is one of days provided above)
				// then create AcmTime object for adding/updating in the database
				if (offset > -1 && value != null)
				{
					AcmTime time = new AcmTime();
						
					time.setId(item.getId());
					time.setCode(item.getCode());
					time.setType(item.getType());
					
					calendar.add(Calendar.DATE, offset);
					time.setDate(calendar.getTime());
					calendar.add(Calendar.DATE, -offset);
					
					time.setValue((Long) value);
					
					times.add(time);
				}
			}
		} 
		catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			LOG.error("Cannot get times from TimeItem.", e);
		}
		
		return times;
	}
	
	/**
	 * The method converst all AcmTime objects to list of Frevvo TimeItem
	 * 
	 * @param timesheet
	 * @return
	 */
	private List<TimeItem> asFrevvoTimeItems(AcmTimesheet timesheet)
	{
		LOG.debug("Converting Acm Times to Frevvo Time Items.");
		
		List<TimeItem> retval = new ArrayList<>();
		
		if (timesheet != null && timesheet.getTimes() != null)
		{
			Map<String, TimeItem> itemsMap = new HashMap<>();
			List<AcmTime> times = timesheet.getTimes();
			for (AcmTime time : times)
			{
				TimeItem item = null;
				
				if (itemsMap.containsKey(time.getCode()))
				{
					item = itemsMap.get(time.getCode());
				}
				else
				{
					item = new TimeItem();
				}
				
				item.setId(time.getId());
				item.setCode(time.getCode());
				item.setType(time.getType());

				item = setTimeFromAcmTime(item, time);
				
				itemsMap.put(time.getCode(), item);
			}
			
			for (Entry<String, TimeItem> entry : itemsMap.entrySet())
			{
				retval.addAll(Arrays.asList(entry.getValue()));
			}
		}
		
		return retval;
	}
	
	/**
	 * Set time in hours for given AcmTime. First need to find the offset (days between start date and the date where the 
	 * hours are recorded) and then set the hours in appropriate day (depends on offset)
	 * 
	 * @param item
	 * @param time
	 * @return
	 */
	private TimeItem setTimeFromAcmTime(TimeItem item, AcmTime time)
	{
		LOG.debug("Setting time to Frevvo Time Item from Acm Time.");
		Calendar calendar = Calendar.getInstance();
		Date date = time.getDate();
		
		calendar.setTime(date);
		
		long offset = calendar.get(Calendar.DAY_OF_WEEK);
		
		if (offset == 0) item.setSunday(time.getValue());
		if (offset == 1) item.setMonday(time.getValue());
		if (offset == 2) item.setTuesday(time.getValue());
		if (offset == 3) item.setWednesday(time.getValue());
		if (offset == 4) item.setThursday(time.getValue());
		if (offset == 5) item.setFriday(time.getValue());
		if (offset == 6) item.setSaturday(time.getValue());
		
		return item;
	}
	
	public Date getStartDate(Date period)
	{
		if (period != null)
		{
			// Create calendar for given date and set the current date to first day of the week
			// (first day of the week is Sunday)
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(period);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			
			// This will return the first date of the week for given date
			return calendar.getTime();
		}
		
		return null;
	}
	
	public Date getEndDate(Date period)
	{
		if (period != null)
		{
			// Create calendar for given date and set the current date to first day of the week
			// (first day of the week is Sunday)
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(period);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			
			// Change calendar to end date of the week and set it to the timesheet
			calendar.add(Calendar.DATE, 6);
			
			// This will return the last date of the week for given date
			return calendar.getTime();
		}
		
		return null;
	}
	
}

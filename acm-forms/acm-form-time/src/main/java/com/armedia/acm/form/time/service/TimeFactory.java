/**
 *
 */
package com.armedia.acm.form.time.service;

/*-
 * #%L
 * ACM Forms: Time
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.AcmTitleEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.form.time.model.TimeForm;
import com.armedia.acm.form.time.model.TimeItem;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.services.timesheet.dao.AcmTimeDao;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author riste.tutureski
 *
 */
public class TimeFactory extends FrevvoFormFactory
{

    private Logger LOG = LogManager.getLogger(getClass());

    private AcmTimeDao acmTimeDao;
    private AcmTimesheetDao acmTimesheetDao;
    private SpringContextHolder springContextHolder;

    /**
     * Converting Frevvo TimeForm to AcmTimesheet
     *
     * @param form
     * @return
     */
    public AcmTimesheet asAcmTimesheet(TimeForm form)
    {
        LOG.debug("Start converting Frevvo Time Form to Acm Timesheet ...");

        AcmTimesheet retval = null;

        if (form != null && form.getId() != null)
        {
            retval = getAcmTimesheetDao().find(form.getId());
        }

        if (retval == null)
        {
            retval = new AcmTimesheet();
        }

        if (form != null)
        {
            retval.setId(form.getId());
            retval.setUser(getUser(form.getUser()));
            retval.setTimes(asAcmTimes(form));
            retval.setStartDate(getStartDate(form.getPeriod()));
            retval.setEndDate(getEndDate(form.getPeriod()));
            retval.setStatus(form.getStatus());
            retval.setDetails(form.getDetails());
            retval.setParticipants(asAcmParticipants(form.getApprovers()));
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

            if (timesheet.getUser() != null)
            {
                form.setUser(timesheet.getUser().getUserId());
            }

            // Doesn't matter which date - it should be one date between start and end date ... I am taking "startDate"
            form.setPeriod(timesheet.getStartDate());

            form.setItems(asFrevvoTimeItems(timesheet));
            form.setStatus(timesheet.getStatus());
            form.setDetails(timesheet.getDetails());
            form.setApprovers(asFrevvoApprovers(timesheet.getParticipants()));
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
    protected List<AcmTime> asAcmTimes(TimeForm form)
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
    protected List<AcmTime> getTimesFromItem(TimeItem item, TimeForm form)
    {
        LOG.debug("Taking Times from Frevvo Time Item.");

        List<AcmTime> times = new ArrayList<>();
        if (item != null)
        {
            if (item.getSunday() != null)
            {
                AcmTime time = createAcmTime(item.getSundayId(), item.getSunday(), item, form, 0);
                times.add(time);
            }

            if (item.getMonday() != null)
            {
                AcmTime time = createAcmTime(item.getMondayId(), item.getMonday(), item, form, 1);
                times.add(time);
            }

            if (item.getTuesday() != null)
            {
                AcmTime time = createAcmTime(item.getTuesdayId(), item.getTuesday(), item, form, 2);
                times.add(time);
            }

            if (item.getWednesday() != null)
            {
                AcmTime time = createAcmTime(item.getWednesdayId(), item.getWednesday(), item, form, 3);
                times.add(time);
            }

            if (item.getThursday() != null)
            {
                AcmTime time = createAcmTime(item.getThursdayId(), item.getThursday(), item, form, 4);
                times.add(time);
            }

            if (item.getFriday() != null)
            {
                AcmTime time = createAcmTime(item.getFridayId(), item.getFriday(), item, form, 5);
                times.add(time);
            }

            if (item.getSaturday() != null)
            {
                AcmTime time = createAcmTime(item.getSaturdayId(), item.getSaturday(), item, form, 6);
                times.add(time);
            }
        }

        return times;
    }

    /**
     * Create AcmTime object for given Frevvo TimeItem
     *
     * @param id
     * @param value
     * @param item
     * @param form
     * @param offset
     * @return
     */
    protected AcmTime createAcmTime(Long id, Double value, TimeItem item, TimeForm form, int offset)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(form.getPeriod());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        Double totalWeekHours = 0.0;
        totalWeekHours += Objects.nonNull(item.getSunday()) ? item.getSunday() : 0.0;
        totalWeekHours += Objects.nonNull(item.getMonday()) ? item.getMonday() : 0.0;
        totalWeekHours += Objects.nonNull(item.getTuesday()) ? item.getTuesday() : 0.0;
        totalWeekHours += Objects.nonNull(item.getWednesday()) ? item.getWednesday() : 0.0;
        totalWeekHours += Objects.nonNull(item.getThursday()) ? item.getThursday() : 0.0;
        totalWeekHours += Objects.nonNull(item.getFriday()) ? item.getFriday() : 0.0;
        totalWeekHours += Objects.nonNull(item.getSaturday()) ? item.getSaturday() : 0.0;

        Double costPerHour = item.getTotalCost() / totalWeekHours;

        AcmTime time = null;

        if (id != null)
        {
            time = getAcmTimeDao().find(id);
        }

        if (time == null)
        {
            time = new AcmTime();
        }

        time.setObjectId(item.getObjectId());
        time.setCode(item.getCode());
        time.setType(item.getType());
        time.setChargeRole(item.getChargeRole());
        time.setTotalCost(value * costPerHour);

        calendar.add(Calendar.DATE, offset);
        time.setDate(calendar.getTime());
        calendar.add(Calendar.DATE, -offset);

        time.setValue(value);

        return time;
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
            Map<List<String>, Double> totalCostsPerObject = timesheet.getTimes()
                    .stream()
                    .collect(Collectors.groupingBy(o -> Arrays.asList(o.getType(), o.getCode()),
                            Collectors.summingDouble(AcmTime::getTotalCost)));

            Map<String, TimeItem> itemsMap = new HashMap<>();
            timesheet.getTimes().forEach(time -> {
                TimeItem item = itemsMap.computeIfAbsent(time.getCode() + ":" + time.getType(), key -> new TimeItem());

                List<String> timeItemKey = Arrays.asList(time.getType(), time.getCode());
                Double timeItemTotalCost = totalCostsPerObject.getOrDefault(timeItemKey, 0.0);

                item.setObjectId(time.getObjectId());
                item.setCode(time.getCode());
                item.setType(time.getType());
                item.setTitle(getObjectTitleByObjectCode(item.getObjectId(), item.getType()));
                item.setChargeRole(time.getChargeRole());
                item.setTotalCost(timeItemTotalCost);

                item = setTimeFromAcmTime(item, time);
            });

            retval.addAll(itemsMap.values());
        }

        return retval;
    }

    /**
     * Set time in hours for given AcmTime. First need to find the offset (days between start date and the date where
     * the hours are recorded) and then set the hours in appropriate day (depends on offset)
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

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (Calendar.SUNDAY == dayOfWeek)
        {
            item.setSundayId(time.getId());
            item.setSunday(time.getValue());
        }

        if (Calendar.MONDAY == dayOfWeek)
        {
            item.setMondayId(time.getId());
            item.setMonday(time.getValue());
        }

        if (Calendar.TUESDAY == dayOfWeek)
        {
            item.setTuesdayId(time.getId());
            item.setTuesday(time.getValue());
        }

        if (Calendar.WEDNESDAY == dayOfWeek)
        {
            item.setWednesdayId(time.getId());
            item.setWednesday(time.getValue());
        }

        if (Calendar.THURSDAY == dayOfWeek)
        {
            item.setThursdayId(time.getId());
            item.setThursday(time.getValue());
        }

        if (Calendar.FRIDAY == dayOfWeek)
        {
            item.setFridayId(time.getId());
            item.setFriday(time.getValue());
        }

        if (Calendar.SATURDAY == dayOfWeek)
        {
            item.setSaturdayId(time.getId());
            item.setSaturday(time.getValue());
        }

        return item;
    }

    public String getObjectTitleByObjectCode(Long objectId, String objectType)
    {

        String title = "";

        Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);

        AcmAbstractDao<AcmTitleEntity> dao = daos.values()
                .stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getSupportedObjectType() != null)
                .filter(item -> item.getSupportedObjectType().equals(objectType))
                .findFirst()
                .orElse(null);

        if (dao != null)
        {
            AcmTitleEntity entityObject = dao.find(objectId);
            title = entityObject.getTitle();
        }

        return title;
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

    public AcmTimeDao getAcmTimeDao()
    {
        return acmTimeDao;
    }

    public void setAcmTimeDao(AcmTimeDao acmTimeDao)
    {
        this.acmTimeDao = acmTimeDao;
    }

    public AcmTimesheetDao getAcmTimesheetDao()
    {
        return acmTimesheetDao;
    }

    public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao)
    {
        this.acmTimesheetDao = acmTimesheetDao;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }
}

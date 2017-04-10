/**
 *
 */
package com.armedia.acm.calendar.service;

import com.armedia.acm.calendar.service.RecurrenceDetails.Daily;
import com.armedia.acm.calendar.service.RecurrenceDetails.Monthly;
import com.armedia.acm.calendar.service.RecurrenceDetails.OnlyOnce;
import com.armedia.acm.calendar.service.RecurrenceDetails.WeekOfMonth;
import com.armedia.acm.calendar.service.RecurrenceDetails.Weekly;
import com.armedia.acm.calendar.service.RecurrenceDetails.Yearly;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.EnumSet;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 3, 2017
 *
 */
public class AcmCalendarEventTest
{

    private void populateCommonValues(AcmCalendarEvent event)
    {
        event.setSubject("test");
        event.setLocation("Armedia");
        event.setStart(ZonedDateTime.now());
        event.setDetails("details");
        event.setRemindIn(30);
        // List<String> invitees = new ArrayList<>();
        // invitees.add("aron@armedia.com");
        // invitees.add("bob@armedia.com");
        // invitees.add("charlie@armedia.com");
        // event.setInvitees(invitees);
    }

    private void printJSON(AcmCalendarEvent event, String test) throws Exception
    {
        System.out.println("== " + test + " ==");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String serializedEvent = mapper.writeValueAsString(event);
        System.out.println(serializedEvent);
        System.out.println();
    }

    @Test
    public void testOnlyOnceStartEnd() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        OnlyOnce once = new OnlyOnce();
        event.setRecurrenceDetails(once);
        printJSON(event, "Only once, start date, end date.");
    }

    @Test
    public void testOnlyOnceAllDay() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        OnlyOnce once = new OnlyOnce();
        event.setAllDayEvent(true);
        event.setRecurrenceDetails(once);
        printJSON(event, "Only once, start date, all day.");
    }

    @Test
    public void testDailyInterval() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Daily daily = new Daily();
        daily.setInterval(2);
        event.setRecurrenceDetails(daily);
        printJSON(event, "Daily, no end date.");
    }

    @Test
    public void testDailyIntervalEndAfter() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Daily daily = new Daily();
        daily.setInterval(2);
        daily.setEndAfterOccurrances(5);
        event.setRecurrenceDetails(daily);
        printJSON(event, "Daily, ends after 5 occurrances.");
    }

    @Test
    public void testDailyIntervalEndBy() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Daily daily = new Daily();
        daily.setInterval(2);
        daily.setEndBy(ZonedDateTime.now().plusYears(5));
        event.setRecurrenceDetails(daily);
        printJSON(event, "Daily, ends after 5 years.");
    }

    @Test
    public void testDailyEveryWeekday() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Daily daily = new Daily();
        daily.setEveryWeekDay(true);
        event.setRecurrenceDetails(daily);
        printJSON(event, "Daily, every weekday.");
    }

    @Test
    public void testDailyEveryWeekdayEndAfter() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Daily daily = new Daily();
        daily.setEveryWeekDay(true);
        daily.setEndAfterOccurrances(5);
        event.setRecurrenceDetails(daily);
        printJSON(event, "Daily, every weekday, ends after 5 occurrances.");
    }

    @Test
    public void testDailyEveryWeekdayEndBy() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Daily daily = new Daily();
        daily.setEveryWeekDay(true);
        daily.setEndBy(ZonedDateTime.now().plusYears(3));
        event.setRecurrenceDetails(daily);
        printJSON(event, "Daily, every weekday, ends after 3 years.");
    }

    @Test
    public void testWeekly() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Weekly weekly = new Weekly();
        weekly.setInterval(2);
        weekly.setDays(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
        event.setRecurrenceDetails(weekly);
        printJSON(event, "Weekly, no end date.");
    }

    @Test
    public void testWeeklyEndAfter() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Weekly weekly = new Weekly();
        weekly.setInterval(2);
        weekly.setDays(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
        weekly.setEndAfterOccurrances(5);
        event.setRecurrenceDetails(weekly);
        printJSON(event, "Weekly, end after 5 occurrances.");
    }

    @Test
    public void testWeeklyEndBy() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Weekly weekly = new Weekly();
        weekly.setInterval(2);
        weekly.setDays(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
        weekly.setEndBy(ZonedDateTime.now().plusYears(5));
        event.setRecurrenceDetails(weekly);
        printJSON(event, "Weekly, end after 5 years.");
    }

    @Test
    public void testMonthlyDay() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Monthly monthly = new Monthly();
        monthly.setDay(5);
        monthly.setInterval(2);
        event.setRecurrenceDetails(monthly);
        printJSON(event, "Monthly, no end date.");
    }

    @Test
    public void testMonthlyDayEndAfter() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Monthly monthly = new Monthly();
        monthly.setDay(5);
        monthly.setInterval(2);
        monthly.setEndAfterOccurrances(5);
        event.setRecurrenceDetails(monthly);
        printJSON(event, "Monthly, end after 5 occurrances.");
    }

    @Test
    public void testMonthlyDayEndBy() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Monthly monthly = new Monthly();
        monthly.setDay(5);
        monthly.setInterval(2);
        monthly.setEndBy(ZonedDateTime.now().plusYears(5));
        event.setRecurrenceDetails(monthly);
        printJSON(event, "Monthly, end after 5 years.");
    }

    @Test
    public void testMonthlyThe() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Monthly monthly = new Monthly();
        monthly.setWeekOfMonth(WeekOfMonth.SECOND);
        monthly.setDayOfWeek(DayOfWeek.THURSDAY);
        monthly.setInterval(2);
        event.setRecurrenceDetails(monthly);
        printJSON(event, "Monthly, weeok of month, day of week, no end date.");
    }

    @Test
    public void testMonthlyTheEndAfter() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Monthly monthly = new Monthly();
        monthly.setWeekOfMonth(WeekOfMonth.SECOND);
        monthly.setDayOfWeek(DayOfWeek.THURSDAY);
        monthly.setInterval(2);
        monthly.setEndAfterOccurrances(5);
        event.setRecurrenceDetails(monthly);
        printJSON(event, "Monthly, weeok of month, day of week, end after 5 occurrances.");
    }

    @Test
    public void testMonthlyTheEndBy() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Monthly monthly = new Monthly();
        monthly.setWeekOfMonth(WeekOfMonth.SECOND);
        monthly.setDayOfWeek(DayOfWeek.THURSDAY);
        monthly.setInterval(2);
        monthly.setEndBy(ZonedDateTime.now().plusYears(5));
        event.setRecurrenceDetails(monthly);
        printJSON(event, "Monthly, weeok of month, day of week, end after 5 years.");
    }

    @Test
    public void testYearlyOn() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Yearly yearly = new Yearly();
        yearly.setInterval(2);
        yearly.setDayOfMonth(14);
        yearly.setMonth(Month.APRIL);
        event.setRecurrenceDetails(yearly);
        printJSON(event, "Yearly, no end date.");
    }

    @Test
    public void testYearlyOnEndAfter() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Yearly yearly = new Yearly();
        yearly.setInterval(2);
        yearly.setDayOfMonth(14);
        yearly.setMonth(Month.APRIL);
        yearly.setEndAfterOccurrances(5);
        event.setRecurrenceDetails(yearly);
        printJSON(event, "Yearly, end after 5 occurrances.");
    }

    @Test
    public void testYearlyOnEndBy() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Yearly yearly = new Yearly();
        yearly.setInterval(2);
        yearly.setDayOfMonth(14);
        yearly.setMonth(Month.APRIL);
        yearly.setEndBy(ZonedDateTime.now().plusYears(5));
        event.setRecurrenceDetails(yearly);
        printJSON(event, "Yearly, end after 5 years.");
    }

    @Test
    public void testYearlyOnThe() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Yearly yearly = new Yearly();
        yearly.setInterval(2);
        yearly.setWeekOfMonth(WeekOfMonth.LAST);
        yearly.setDayOfWeek(DayOfWeek.WEDNESDAY);
        yearly.setMonth(Month.JUNE);
        event.setRecurrenceDetails(yearly);
        printJSON(event, "Yearly, weeok of month, day of week, no end date.");
    }

    @Test
    public void testYearlyOnTheEndAfter() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Yearly yearly = new Yearly();
        yearly.setInterval(2);
        yearly.setWeekOfMonth(WeekOfMonth.LAST);
        yearly.setDayOfWeek(DayOfWeek.WEDNESDAY);
        yearly.setMonth(Month.JUNE);
        yearly.setEndAfterOccurrances(5);
        event.setRecurrenceDetails(yearly);
        printJSON(event, "Yearly, weeok of month, day of week, end after 5 occurrances.");
    }

    @Test
    public void testYearlyOnTheEndBy() throws Exception
    {
        AcmCalendarEvent event = new AcmCalendarEvent();
        populateCommonValues(event);
        event.setEnd(ZonedDateTime.now().plusHours(2));
        Yearly yearly = new Yearly();
        yearly.setInterval(2);
        yearly.setWeekOfMonth(WeekOfMonth.LAST);
        yearly.setDayOfWeek(DayOfWeek.WEDNESDAY);
        yearly.setMonth(Month.JUNE);
        yearly.setEndBy(ZonedDateTime.now().plusYears(5));
        event.setRecurrenceDetails(yearly);
        printJSON(event, "Yearly, weeok of month, day of week, end after 5 years.");
    }

}

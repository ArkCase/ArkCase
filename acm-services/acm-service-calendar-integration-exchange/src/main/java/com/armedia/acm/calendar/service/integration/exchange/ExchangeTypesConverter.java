package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEvent.Priority;
import com.armedia.acm.calendar.service.Attendee;
import com.armedia.acm.calendar.service.Attendee.AttendeeType;
import com.armedia.acm.calendar.service.RecurrenceDetails;
import com.armedia.acm.calendar.service.RecurrenceDetails.Daily;
import com.armedia.acm.calendar.service.RecurrenceDetails.Monthly;
import com.armedia.acm.calendar.service.RecurrenceDetails.WeekOfMonth;
import com.armedia.acm.calendar.service.RecurrenceDetails.Weekly;
import com.armedia.acm.calendar.service.RecurrenceDetails.Yearly;

import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.TimeZone;

import microsoft.exchange.webservices.data.core.XmlElementNames;
import microsoft.exchange.webservices.data.core.enumeration.property.Importance;
import microsoft.exchange.webservices.data.core.enumeration.property.Sensitivity;
import microsoft.exchange.webservices.data.core.enumeration.property.time.DayOfTheWeek;
import microsoft.exchange.webservices.data.core.enumeration.property.time.DayOfTheWeekIndex;
import microsoft.exchange.webservices.data.core.enumeration.property.time.Month;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.recurrence.DayOfTheWeekCollection;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.DailyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.MonthlyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.RelativeMonthlyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.RelativeYearlyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.WeeklyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.YearlyPattern;
import microsoft.exchange.webservices.data.property.complex.time.OlsonTimeZoneDefinition;
import microsoft.exchange.webservices.data.property.complex.time.TimeZoneDefinition;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 26, 2017
 *
 */
public class ExchangeTypesConverter
{

    /**
     * @param appointment
     * @param exchange
     * @param folder
     * @param calendarEvent
     * @throws Exception
     */
    static void setAppointmentProperties(Appointment appointment, AcmCalendarEvent calendarEvent, MultipartFile[] attachments)
            throws Exception
    {
        appointment.setSubject(calendarEvent.getSubject());
        appointment.setLocation(calendarEvent.getLocation());
        OlsonTimeZoneDefinition startTimeZoneDefinition = new OlsonTimeZoneDefinition(
                TimeZone.getTimeZone(calendarEvent.getStart().getZone().toString()));
        appointment.setStartTimeZone(startTimeZoneDefinition);
        Date startDate = Date.from(calendarEvent.getStart().toInstant());
        appointment.setStart(startDate);
        OlsonTimeZoneDefinition endTimeZoneDefinition = new OlsonTimeZoneDefinition(
                TimeZone.getTimeZone(calendarEvent.getEnd().getZone().toString()));
        appointment.setEndTimeZone(endTimeZoneDefinition);
        appointment.setEnd(Date.from(calendarEvent.getEnd().toInstant()));
        appointment.setIsAllDayEvent(calendarEvent.isAllDayEvent());
        RecurrenceDetails rc = calendarEvent.getRecurrenceDetails();
        Recurrence recurrence = null;
        if (rc != null)
        {
            switch (rc.getRecurrenceType())
            {
            case ONLY_ONCE:

                break;
            case DAILY:
                Daily daily = (Daily) rc;
                if (daily.getEveryWeekDay() != null && daily.getEveryWeekDay())
                {
                    recurrence = new DailyPattern(startDate, 1);
                } else
                {
                    recurrence = new DailyPattern(startDate, daily.getInterval());
                }
                break;
            case WEEKLY:
                Weekly weekly = (Weekly) rc;
                recurrence = new WeeklyPattern(startDate, weekly.getInterval(), convertDaysOfWeek(weekly));
                break;
            case MONTHLY:
                Monthly monthly = (Monthly) rc;
                if (monthly.getDay() != null)
                {
                    recurrence = new MonthlyPattern(startDate, monthly.getInterval(), monthly.getDay());
                } else
                {
                    recurrence = new RelativeMonthlyPattern(startDate, monthly.getInterval(), convertDayOfWeek(monthly.getDayOfWeek()),
                            convertWeekOfMonth(monthly.getWeekOfMonth()));
                }
                break;
            case YEARLY:
                Yearly yearly = (Yearly) rc;
                if (yearly.getDayOfMonth() != null)
                {
                    recurrence = new YearlyPattern(startDate, convertMonth(yearly.getMonth()), yearly.getDayOfMonth());
                } else
                {
                    recurrence = new RelativeYearlyPattern(startDate, convertMonth(yearly.getMonth()),
                            convertDayOfWeek(yearly.getDayOfWeek()), convertWeekOfMonth(yearly.getWeekOfMonth()));
                }
                break;
            }
            appointment.setRecurrence(recurrence);
        }
        appointment.setBody(MessageBody.getMessageBodyFromText(calendarEvent.getDetails()));
        if (calendarEvent.getRemindIn() != -1)
        {
            appointment.setIsReminderSet(true);
            appointment.setReminderMinutesBeforeStart(calendarEvent.getRemindIn());
            // appointment.setReminderDueBy(Date.from(calendarEvent.getEnd().minusMinutes(calendarEvent.getRemindIn()).toInstant()));
        } else
        {
            appointment.setIsReminderSet(false);
        }
        appointment.setSensitivity(convertSensitivity(calendarEvent.getSensitivity()));
        appointment.setImportance(convertImportance(calendarEvent.getPriority()));

        if (calendarEvent.getAttendees() != null)
        {
            for (Attendee attendee : calendarEvent.getAttendees())
            {
                switch (attendee.getType())
                {
                case REQUIRED:
                    appointment.getRequiredAttendees().add(attendee.getEmail());
                case OPTIONAL:
                    appointment.getOptionalAttendees().add(attendee.getEmail());
                case RESOURCE:
                    appointment.getResources().add(attendee.getEmail());
                }
            }
        }

        if (attachments != null)
        {
            for (MultipartFile attachment : attachments)
            {
                appointment.getAttachments().addFileAttachment(attachment.getName(), attachment.getInputStream());
            }
        }

    }

    /**
     * @param rc
     * @return
     */
    private static DayOfTheWeek[] convertDaysOfWeek(Weekly rc)
    {
        List<DayOfTheWeek> daysOfWeek = new ArrayList<>();
        for (DayOfWeek dof : rc.getDays())
        {
            daysOfWeek.add(convertDayOfWeek(dof));
        }
        return daysOfWeek.toArray(new DayOfTheWeek[daysOfWeek.size()]);
    }

    private static DayOfTheWeek convertDayOfWeek(DayOfWeek dof)
    {
        switch (dof)
        {
        case MONDAY:
            return DayOfTheWeek.Monday;
        case TUESDAY:
            return DayOfTheWeek.Tuesday;
        case WEDNESDAY:
            return DayOfTheWeek.Wednesday;
        case THURSDAY:
            return DayOfTheWeek.Thursday;
        case FRIDAY:
            return DayOfTheWeek.Friday;
        case SATURDAY:
            return DayOfTheWeek.Saturday;
        case SUNDAY:
            return DayOfTheWeek.Sunday;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param weekOfMonth
     * @return
     */
    private static DayOfTheWeekIndex convertWeekOfMonth(WeekOfMonth weekOfMonth)
    {
        switch (weekOfMonth)
        {
        case FIRST:
            return DayOfTheWeekIndex.First;
        case SECOND:
            return DayOfTheWeekIndex.Second;
        case THIRD:
            return DayOfTheWeekIndex.Third;
        case FOURTH:
            return DayOfTheWeekIndex.Fourth;
        case LAST:
            return DayOfTheWeekIndex.Last;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param month
     * @return
     */
    private static Month convertMonth(java.time.Month month)
    {
        switch (month)
        {
        case JANUARY:
            return Month.January;
        case FEBRUARY:
            return Month.February;
        case MARCH:
            return Month.March;
        case APRIL:
            return Month.April;
        case MAY:
            return Month.May;
        case JUNE:
            return Month.June;
        case JULY:
            return Month.July;
        case AUGUST:
            return Month.August;
        case SEPTEMBER:
            return Month.September;
        case OCTOBER:
            return Month.October;
        case NOVEMBER:
            return Month.November;
        case DECEMBER:
            return Month.December;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param priority
     * @return
     */
    private static Importance convertImportance(Priority priority)
    {
        switch (priority)
        {
        case LOW:
            return Importance.Low;
        case NORMAL:
            return Importance.Normal;
        case HIGH:
            return Importance.High;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param sensitivity
     * @return
     */
    private static Sensitivity convertSensitivity(com.armedia.acm.calendar.service.AcmCalendarEvent.Sensitivity sensitivity)
    {
        switch (sensitivity)
        {
        case CONFIDENTIAL:
            return Sensitivity.Confidential;
        case PRIVATE:
            return Sensitivity.Private;
        case PERSONAL:
            return Sensitivity.Personal;
        case NORMAL:
            return Sensitivity.Normal;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param event
     * @param appointment
     * @throws Exception
     */
    static void setEventProperties(AcmCalendarEvent event, Appointment appointment) throws Exception
    {
        event.setSubject(appointment.getSubject());
        event.setLocation(appointment.getLocation());

        TimeZoneDefinition startTimeZoneDefinititon = appointment.getStartTimeZone();
        TimeZone startTimeZone = TimeZone.getTimeZone(startTimeZoneDefinititon.getId());
        event.setStart(ZonedDateTime.ofInstant(appointment.getStart().toInstant(), ZoneId.of(startTimeZone.getID())));

        TimeZoneDefinition endTimeZoneDefinition = appointment.getEndTimeZone();
        TimeZone endTimeZone = TimeZone.getTimeZone(endTimeZoneDefinition.getId());
        event.setEnd(ZonedDateTime.ofInstant(appointment.getEnd().toInstant(), ZoneId.of(endTimeZone.getID())));

        event.setAllDayEvent(appointment.getIsAllDayEvent());
        Recurrence recurrence = appointment.getRecurrence();
        RecurrenceDetails recurrenceDetails = null;
        if (recurrence != null)
        {
            switch (recurrence.getXmlElementName())
            {
            case XmlElementNames.DailyRecurrence:
                DailyPattern dp = (DailyPattern) recurrence;
                Daily daily = new Daily();
                daily.setInterval(dp.getInterval());
                recurrenceDetails = daily;
                break;
            case XmlElementNames.WeeklyRecurrence:
                WeeklyPattern wp = (WeeklyPattern) recurrence;
                Weekly weekly = new Weekly();
                weekly.setInterval(wp.getInterval());
                weekly.setDays(convertDaysOfWeek(wp.getDaysOfTheWeek()));
                recurrenceDetails = weekly;
                break;
            case XmlElementNames.AbsoluteMonthlyRecurrence:
                MonthlyPattern mp = (MonthlyPattern) recurrence;
                Monthly monthly = new Monthly();
                monthly.setInterval(mp.getInterval());
                monthly.setDay(mp.getDayOfMonth());
                recurrenceDetails = monthly;
                break;
            case XmlElementNames.RelativeMonthlyRecurrence:
                RelativeMonthlyPattern rmp = (RelativeMonthlyPattern) recurrence;
                Monthly relativeMonthly = new Monthly();
                relativeMonthly.setInterval(rmp.getInterval());
                relativeMonthly.setDayOfWeek(convertDayOfWeek(rmp.getDayOfTheWeek()));
                relativeMonthly.setWeekOfMonth(convertWeekOfTheMonth(rmp.getDayOfTheWeekIndex()));
                recurrenceDetails = relativeMonthly;
                break;
            case XmlElementNames.AbsoluteYearlyRecurrence:
                YearlyPattern yp = (YearlyPattern) recurrence;
                Yearly yearly = new Yearly();
                yearly.setMonth(convertMonth(yp.getMonth()));
                yearly.setDayOfMonth(yp.getDayOfMonth());
                recurrenceDetails = yearly;
                break;
            case XmlElementNames.RelativeYearlyRecurrence:
                RelativeYearlyPattern ryp = (RelativeYearlyPattern) recurrence;
                Yearly relativeYearly = new Yearly();
                relativeYearly.setMonth(convertMonth(ryp.getMonth()));
                relativeYearly.setDayOfWeek(convertDayOfWeek(ryp.getDayOfTheWeek()));
                relativeYearly.setWeekOfMonth(convertWeekOfTheMonth(ryp.getDayOfTheWeekIndex()));
                recurrenceDetails = relativeYearly;
                break;
            }
            if (recurrence.getNumberOfOccurrences() != null)
            {
                recurrenceDetails.setEndAfterOccurrances(recurrence.getNumberOfOccurrences());
            }
            if (recurrence.getEndDate() != null)
            {
                recurrenceDetails.setEndBy(ZonedDateTime.ofInstant(recurrence.getEndDate().toInstant(), ZoneId.of(endTimeZone.getID())));
            }
            event.setRecurrenceDetails(recurrenceDetails);

            event.setDetails(MessageBody.getStringFromMessageBody(appointment.getBody()));

            if (appointment.getIsReminderSet())
            {
                event.setRemindIn(appointment.getReminderMinutesBeforeStart());
            }

            event.setSensitivity(convertSensitivity(appointment.getSensitivity()));

            event.setPriority(convertImportance(appointment.getImportance()));

            List<Attendee> attendees = new ArrayList<>();
            appointment.getRequiredAttendees().forEach(a -> {
                Attendee attendee = new Attendee();
                attendee.setEmail(a.getAddress());
                attendee.setType(AttendeeType.REQUIRED);
                attendees.add(attendee);
            });
            appointment.getOptionalAttendees().forEach(a -> {
                Attendee attendee = new Attendee();
                attendee.setEmail(a.getAddress());
                attendee.setType(AttendeeType.OPTIONAL);
                attendees.add(attendee);
            });
            appointment.getResources().forEach(a -> {
                Attendee attendee = new Attendee();
                attendee.setEmail(a.getAddress());
                attendee.setType(AttendeeType.RESOURCE);
                attendees.add(attendee);
            });
            event.setAttendees(attendees);

            List<String> fileNames = new ArrayList<>();
            appointment.getAttachments().forEach(att -> fileNames.add(att.getName()));
            event.setFileNames(fileNames);
        }
    }

    /**
     * @param daysOfTheWeek
     * @return
     */
    private static EnumSet<DayOfWeek> convertDaysOfWeek(DayOfTheWeekCollection daysOfTheWeek)
    {
        List<DayOfWeek> dow = new ArrayList<>();
        daysOfTheWeek.forEach(day -> dow.add(convertDayOfWeek(day)));
        EnumSet<DayOfWeek> daysOfWeek = EnumSet.copyOf(dow);
        return daysOfWeek;
    }

    /**
     * @param day
     * @return
     */
    private static DayOfWeek convertDayOfWeek(DayOfTheWeek day)
    {
        switch (day)
        {
        case Monday:
            return DayOfWeek.MONDAY;
        case Tuesday:
            return DayOfWeek.TUESDAY;
        case Wednesday:
            return DayOfWeek.WEDNESDAY;
        case Thursday:
            return DayOfWeek.THURSDAY;
        case Friday:
            return DayOfWeek.FRIDAY;
        case Saturday:
            return DayOfWeek.SATURDAY;
        case Sunday:
            return DayOfWeek.SUNDAY;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param dayOfTheWeekIndex
     * @return
     */
    private static WeekOfMonth convertWeekOfTheMonth(DayOfTheWeekIndex dayOfTheWeekIndex)
    {
        switch (dayOfTheWeekIndex)
        {
        case First:
            return WeekOfMonth.FIRST;
        case Second:
            return WeekOfMonth.SECOND;
        case Third:
            return WeekOfMonth.THIRD;
        case Fourth:
            return WeekOfMonth.FOURTH;
        case Last:
            return WeekOfMonth.LAST;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param month
     * @return
     */
    private static java.time.Month convertMonth(Month month)
    {
        switch (month)
        {
        case January:
            return java.time.Month.JANUARY;
        case February:
            return java.time.Month.FEBRUARY;
        case March:
            return java.time.Month.MARCH;
        case April:
            return java.time.Month.APRIL;
        case May:
            return java.time.Month.MAY;
        case June:
            return java.time.Month.JUNE;
        case July:
            return java.time.Month.JULY;
        case August:
            return java.time.Month.AUGUST;
        case September:
            return java.time.Month.SEPTEMBER;
        case October:
            return java.time.Month.OCTOBER;
        case November:
            return java.time.Month.NOVEMBER;
        case December:
            return java.time.Month.DECEMBER;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param sensitivity
     * @return
     */
    private static AcmCalendarEvent.Sensitivity convertSensitivity(Sensitivity sensitivity)
    {
        switch (sensitivity)
        {
        case Normal:
            return AcmCalendarEvent.Sensitivity.NORMAL;
        case Personal:
            return AcmCalendarEvent.Sensitivity.PERSONAL;
        case Private:
            return AcmCalendarEvent.Sensitivity.PRIVATE;
        case Confidential:
            return AcmCalendarEvent.Sensitivity.CONFIDENTIAL;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param importance
     * @return
     */
    private static Priority convertImportance(Importance importance)
    {
        switch (importance)
        {
        case Low:
            return Priority.LOW;
        case Normal:
            return Priority.NORMAL;
        case High:
            return Priority.HIGH;
        default:
            throw new IllegalArgumentException();
        }
    }

}

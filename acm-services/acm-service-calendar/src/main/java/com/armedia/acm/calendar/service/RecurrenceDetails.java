package com.armedia.acm.calendar.service;

import com.armedia.acm.calendar.service.RecurrenceDetails.Daily;
import com.armedia.acm.calendar.service.RecurrenceDetails.Monthly;
import com.armedia.acm.calendar.service.RecurrenceDetails.OnlyOnce;
import com.armedia.acm.calendar.service.RecurrenceDetails.Weekly;
import com.armedia.acm.calendar.service.RecurrenceDetails.Yearly;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.EnumSet;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 30, 2017
 * @param <EventRecurrence>
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY, property = "recurrenceType")
@JsonSubTypes({ @Type(value = OnlyOnce.class, name = "ONLY_ONCE"), @Type(value = Daily.class, name = "DAILY"),
        @Type(value = Weekly.class, name = "WEEKLY"), @Type(value = Monthly.class, name = "MONTHLY"),
        @Type(value = Yearly.class, name = "YEARLY") })
public abstract class RecurrenceDetails
{
    /**
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 30, 2017
     *
     */
    public static enum EventRecurrence
    {
        ONLY_ONCE, DAILY, WEEKLY, MONTHLY, YEARLY;
    }

    public static enum WeekOfMonth
    {
        FIRST, SECOND, THIRD, FOURTH, LAST;
    }

    private EventRecurrence recurrenceType;

    private Integer interval;

    private Integer endAfterOccurrances;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssXXX")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime endBy;

    public EventRecurrence getRecurrenceType()
    {
        return recurrenceType;
    }

    /**
     * @param recurrenceType
     *            the recurrenceType to set
     */
    public void setRecurrenceType(EventRecurrence recurrenceType)
    {
        this.recurrenceType = recurrenceType;
    }

    /**
     * @return the daysInterval
     */
    public Integer getInterval()
    {
        return interval;
    }

    /**
     * @param interval
     *            the interval to set
     */
    public void setInterval(Integer interval)
    {
        this.interval = interval;
    }

    /**
     * @return the endAfterOccurrances
     */
    public Integer getEndAfterOccurrances()
    {
        return endAfterOccurrances;
    }

    /**
     * @param endAfterOccurrances
     *            the endAfterOccurrances to set
     */
    public void setEndAfterOccurrances(Integer endAfterOccurrances)
    {
        this.endAfterOccurrances = endAfterOccurrances;
    }

    /**
     * @return the endBy
     */
    public ZonedDateTime getEndBy()
    {
        return endBy;
    }

    /**
     * @param endBy
     *            the endBy to set
     */
    public void setEndBy(ZonedDateTime endBy)
    {
        this.endBy = endBy;
    }

    public static final class OnlyOnce extends RecurrenceDetails
    {

        public OnlyOnce()
        {
            setRecurrenceType(EventRecurrence.ONLY_ONCE);
        }

    }

    public static final class Daily extends RecurrenceDetails
    {

        private Boolean everyWeekDay;

        public Daily()
        {
            setRecurrenceType(EventRecurrence.DAILY);
        }

        /**
         * @return the everyWeekDay
         */
        public Boolean getEveryWeekDay()
        {
            return everyWeekDay;
        }

        /**
         * @param everyWeekDay
         *            the everyWeekDay to set
         */
        public void setEveryWeekDay(Boolean everyWeekDay)
        {
            this.everyWeekDay = everyWeekDay;
        }

    }

    public static final class Weekly extends RecurrenceDetails
    {

        private EnumSet<DayOfWeek> days;

        public Weekly()
        {
            setRecurrenceType(EventRecurrence.WEEKLY);
        }

        /**
         * @return the days
         */
        public EnumSet<DayOfWeek> getDays()
        {
            return days;
        }

        /**
         * @param days
         *            the days to set
         */
        public void setDays(EnumSet<DayOfWeek> days)
        {
            this.days = days;
        }

    }

    public static final class Monthly extends RecurrenceDetails
    {

        private Integer day;

        private WeekOfMonth weekOfMonth;

        private DayOfWeek dayOfWeek;

        public Monthly()
        {
            setRecurrenceType(EventRecurrence.MONTHLY);
        }

        /**
         * @return the day
         */
        public Integer getDay()
        {
            return day;
        }

        /**
         * @param day
         *            the day to set
         */
        public void setDay(Integer day)
        {
            this.day = day;
        }

        /**
         * @return the weekOfMonth
         */
        public WeekOfMonth getWeekOfMonth()
        {
            return weekOfMonth;
        }

        /**
         * @param weekOfMonth
         *            the weekOfMonth to set
         */
        public void setWeekOfMonth(WeekOfMonth weekOfMonth)
        {
            this.weekOfMonth = weekOfMonth;
        }

        /**
         * @return the dayOfWeek
         */
        public DayOfWeek getDayOfWeek()
        {
            return dayOfWeek;
        }

        /**
         * @param dayOfWeek
         *            the dayOfWeek to set
         */
        public void setDayOfWeek(DayOfWeek dayOfWeek)
        {
            this.dayOfWeek = dayOfWeek;
        }

    }

    public static final class Yearly extends RecurrenceDetails
    {

        private Month month;

        private Integer dayOfMonth;

        private DayOfWeek dayOfWeek;

        private WeekOfMonth weekOfMonth;

        public Yearly()
        {
            setRecurrenceType(EventRecurrence.YEARLY);
        }

        /**
         * @return the month
         */
        public Month getMonth()
        {
            return month;
        }

        /**
         * @param month
         *            the month to set
         */
        public void setMonth(Month month)
        {
            this.month = month;
        }

        /**
         * @return the dayOfMonth
         */
        public Integer getDayOfMonth()
        {
            return dayOfMonth;
        }

        /**
         * @param dayOfMonth
         *            the dayOfMonth to set
         */
        public void setDayOfMonth(Integer dayOfMonth)
        {
            this.dayOfMonth = dayOfMonth;
        }

        /**
         * @return the dayOfWeek
         */
        public DayOfWeek getDayOfWeek()
        {
            return dayOfWeek;
        }

        /**
         * @param dayOfWeek
         *            the dayOfWeek to set
         */
        public void setDayOfWeek(DayOfWeek dayOfWeek)
        {
            this.dayOfWeek = dayOfWeek;
        }

        /**
         * @return the weekOfMonth
         */
        public WeekOfMonth getWeekOfMonth()
        {
            return weekOfMonth;
        }

        /**
         * @param weekOfMonth
         *            the weekOfMonth to set
         */
        public void setWeekOfMonth(WeekOfMonth weekOfMonth)
        {
            this.weekOfMonth = weekOfMonth;
        }

    }

}

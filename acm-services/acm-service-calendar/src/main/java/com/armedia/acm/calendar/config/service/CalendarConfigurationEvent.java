package com.armedia.acm.calendar.config.service;

import com.armedia.acm.core.model.AcmEvent;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 13, 2017
 *
 */
public class CalendarConfigurationEvent extends AcmEvent
{

    private static final long serialVersionUID = -54593860004283408L;

    private static final String EVENT_TYPE = "com.armedia.acm.calendar.config.event.updated";

    /**
     * @param source
     * @param source
     */
    public CalendarConfigurationEvent(CalendarConfigurationsByObjectType source, String user)
    {
        super(source);
        setUserId(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.armedia.acm.core.model.AcmEvent#getEventType()
     */
    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}

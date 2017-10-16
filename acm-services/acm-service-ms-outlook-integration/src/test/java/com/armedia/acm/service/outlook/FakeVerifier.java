package com.armedia.acm.service.outlook;

import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.EmailCredentials;
import com.armedia.acm.calendar.config.service.EmailCredentialsVerifierService;
import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarExceptionMapper;
import com.armedia.acm.calendar.service.CalendarService;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 20, 2017
 *
 */
public class FakeVerifier implements CalendarService, EmailCredentialsVerifierService
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.armedia.acm.calendar.config.service.EmailCredentialsVerifierService#verifyEmailCredentials(java.lang.String,
     * com.armedia.acm.calendar.config.service.EmailCredentials)
     */
    @Override
    public boolean verifyEmailCredentials(String userId, EmailCredentials emailCredentials)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.armedia.acm.calendar.service.CalendarService#retrieveCalendar(com.armedia.acm.services.users.model.AcmUser,
     * org.springframework.security.core.Authentication, java.lang.String, java.lang.String)
     */
    @Override
    public Optional<AcmCalendar> retrieveCalendar(AcmUser user, Authentication auth, String objectType, String objectId)
            throws CalendarServiceException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.armedia.acm.calendar.service.CalendarService#listCalendars(com.armedia.acm.services.users.model.AcmUser,
     * org.springframework.security.core.Authentication, java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    @Override
    public List<AcmCalendarInfo> listCalendars(AcmUser user, Authentication auth, String objectType, String sort, String sortDirection,
            int start, int maxItems) throws CalendarServiceException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.armedia.acm.calendar.service.CalendarService#addCalendarEvent(com.armedia.acm.services.users.model.AcmUser,
     * org.springframework.security.core.Authentication, com.armedia.acm.calendar.service.AcmCalendarEvent,
     * org.springframework.web.multipart.MultipartFile[])
     */
    @Override
    public void addCalendarEvent(AcmUser user, Authentication auth, AcmCalendarEvent calendarEvent, MultipartFile[] attachments)
            throws CalendarServiceException
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.armedia.acm.calendar.service.CalendarService#updateCalendarEvent(com.armedia.acm.services.users.model.
     * AcmUser, org.springframework.security.core.Authentication, boolean,
     * com.armedia.acm.calendar.service.AcmCalendarEvent, org.springframework.web.multipart.MultipartFile[])
     */
    @Override
    public void updateCalendarEvent(AcmUser user, Authentication auth, boolean updateMaster, AcmCalendarEvent calendarEvent,
            MultipartFile[] attachments) throws CalendarServiceException
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.armedia.acm.calendar.service.CalendarService#deleteCalendarEvent(com.armedia.acm.services.users.model.
     * AcmUser, org.springframework.security.core.Authentication, java.lang.String, java.lang.String, java.lang.String,
     * boolean)
     */
    @Override
    public void deleteCalendarEvent(AcmUser user, Authentication auth, String objectType, String objectId, String calendarEventId,
            boolean deleteRecurring) throws CalendarServiceException
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.armedia.acm.calendar.service.CalendarService#purgeEvents(java.lang.String,
     * com.armedia.acm.calendar.config.service.CalendarConfiguration)
     */
    @Override
    public void purgeEvents(String objectType, CalendarConfiguration config) throws CalendarServiceException
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.armedia.acm.calendar.service.CalendarService#getExceptionMapper(com.armedia.acm.calendar.service.
     * CalendarServiceException)
     */
    @Override
    public <CSE extends CalendarServiceException> CalendarExceptionMapper<CSE> getExceptionMapper(CalendarServiceException e)
    {
        // TODO Auto-generated method stub
        return null;
    }

}

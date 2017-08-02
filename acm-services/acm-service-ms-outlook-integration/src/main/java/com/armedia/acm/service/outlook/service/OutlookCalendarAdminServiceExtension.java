package com.armedia.acm.service.outlook.service;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 1, 2017
 *
 */
public interface OutlookCalendarAdminServiceExtension extends CalendarAdminService
{

    AcmOutlookUser getEventListenerOutlookUser(String objectType) throws AcmOutlookItemNotFoundException;

    AcmOutlookUser getHandlerOutlookUser(String userName, String objectType) throws PipelineProcessException;
}

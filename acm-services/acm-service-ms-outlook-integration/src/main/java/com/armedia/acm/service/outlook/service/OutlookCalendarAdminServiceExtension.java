package com.armedia.acm.service.outlook.service;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import java.util.List;
import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 1, 2017
 *
 */
public interface OutlookCalendarAdminServiceExtension extends CalendarAdminService
{

    Optional<AcmOutlookUser> getEventListenerOutlookUser(String objectType) throws AcmOutlookItemNotFoundException;

    Optional<AcmOutlookUser> getHandlerOutlookUser(String userName, String objectType) throws PipelineProcessException;

    List<AcmOutlookFolderCreator> findFolderCreatorsWithInvalidCredentials();
}

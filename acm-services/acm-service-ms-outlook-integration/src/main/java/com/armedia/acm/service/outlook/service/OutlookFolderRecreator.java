package com.armedia.acm.service.outlook.service;

import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 26, 2017
 *
 */
public interface OutlookFolderRecreator
{

    void recreateFolder(String objectType, Long objectId, AcmOutlookUser outlookUser) throws CalendarServiceException;

}

package com.armedia.acm.service.outlook.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2017
 *
 */
public interface OutlookRecreateableCalendarService
{

    AcmContainer clearFolderRecreatedFlag(String objectType, Long objectId) throws AcmObjectNotFoundException;

}

package com.armedia.acm.service.outlook.service;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

    /**
     * @param updatedCreator
     */
    void updateFolderCreatorAndRecreateFoldersIfNecessary(AcmOutlookFolderCreator updatedCreator, String user);

    List<AcmOutlookFolderCreator> findFolderCreatorsWithInvalidCredentials();

}

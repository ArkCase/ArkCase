package com.armedia.acm.calendar.service.integration.exchange.web.api;

/*-
 * #%L
 * ACM Service: Exchange Integration Calendar Service
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

import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 15, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/calendar/exchange/configure", "/api/latest/service/calendar/exchange/configure" })
public class AcmExchangeCalendarManagementAPIController
{

    private OutlookCalendarAdminServiceExtension calendarAdminService;

    @RequestMapping(path = "/credentials/invalid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<AcmOutlookFolderCreator> findFolderCreatorsWithInvalidCredentials()
    {
        List<AcmOutlookFolderCreator> invalidUsers = calendarAdminService.findFolderCreatorsWithInvalidCredentials();
        return invalidUsers;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<AcmOutlookFolderCreator> updateConfiguration(@RequestBody AcmOutlookFolderCreator updatedCreator,
            Authentication authentication) throws AcmOutlookFolderCreatorDaoException
    {
        AcmOutlookFolderCreator updatedCreatorCopy = new AcmOutlookFolderCreator(updatedCreator.getSystemEmailAddress(),
                updatedCreator.getSystemPassword());
        updatedCreatorCopy.setId(updatedCreator.getId());

        calendarAdminService.updateFolderCreatorAndRecreateFoldersIfNecessary(updatedCreatorCopy, authentication.getName());
        updatedCreator.setSystemPassword(null);

        return ResponseEntity.status(HttpStatus.OK).body(updatedCreator);
    }

    @ExceptionHandler(AcmOutlookFolderCreatorDaoException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(AcmOutlookFolderCreatorDaoException ce)
    {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error_cause", "INTERNAL_SERVER_ERROR");
        errorDetails.put("error_message", ce.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    /**
     * @param calendarAdminService
     *            the calendarAdminService to set
     */
    public void setCalendarAdminService(OutlookCalendarAdminServiceExtension calendarAdminService)
    {
        this.calendarAdminService = calendarAdminService;
    }

}

package com.armedia.acm.calendar.service.integration.exchange.web.api;

import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private AcmOutlookFolderCreatorDao folderCreatorDao;

    private OutlookCalendarAdminServiceExtension outlookCalendarAdminService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<AcmOutlookFolderCreator> checkFolderCreatorCredentials()
    {
        List<AcmOutlookFolderCreator> invalidUsers = outlookCalendarAdminService.getFolderCreatorsWithInvalidCredentials();
        return invalidUsers;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<AcmOutlookFolderCreator> updateConfiguration(@RequestBody AcmOutlookFolderCreator updatedCreator)
            throws AcmOutlookFolderCreatorDaoException
    {
        folderCreatorDao.updateFolderCreator(updatedCreator);
        updatedCreator.setSystemPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCreator);
    }

    @ExceptionHandler(CalendarServiceException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(CalendarServiceException ce)
    {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error_cause", "INTERNAL_SERVER_ERROR");
        errorDetails.put("error_message", ce.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    /**
     * @param folderCreatorDao
     *            the folderCreatorDao to set
     */
    public void setFolderCreatorDao(AcmOutlookFolderCreatorDao folderCreatorDao)
    {
        this.folderCreatorDao = folderCreatorDao;
    }

    /**
     * @param outlookCalendarAdminService
     *            the outlookCalendarAdminService to set
     */
    public void setOutlookCalendarAdminService(OutlookCalendarAdminServiceExtension outlookCalendarAdminService)
    {
        this.outlookCalendarAdminService = outlookCalendarAdminService;
    }

}

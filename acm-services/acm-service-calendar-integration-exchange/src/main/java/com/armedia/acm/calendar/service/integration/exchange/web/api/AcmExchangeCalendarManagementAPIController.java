package com.armedia.acm.calendar.service.integration.exchange.web.api;

import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 15, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/calendar/exchange/configure", "/api/latest/service/calendar/exchange/configure" })
public class AcmExchangeCalendarManagementAPIController
{
    private AcmOutlookFolderCreatorDao folderCreatorDao;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<AcmOutlookFolderCreator> checkFolderCreatorCredentials()
    {
        List<AcmOutlookFolderCreator> invalidUsers = folderCreatorDao.checkFolderCreatorCredentials();
        return invalidUsers;
    }

    /**
     * @param folderCreatorDao
     *            the folderCreatorDao to set
     */
    public void setFolderCreatorDao(AcmOutlookFolderCreatorDao folderCreatorDao)
    {
        this.folderCreatorDao = folderCreatorDao;
    }

}

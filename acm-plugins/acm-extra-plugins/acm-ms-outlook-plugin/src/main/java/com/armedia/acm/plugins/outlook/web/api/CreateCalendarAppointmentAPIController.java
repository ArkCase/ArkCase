package com.armedia.acm.plugins.outlook.web.api;

import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({"/api/v1/plugin/outlook/calendar", "/api/latest/plugin/outlook/calendar"})
public class CreateCalendarAppointmentAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());
    private OutlookService outlookService;
    private UserOrgDao userOrgDao;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OutlookCalendarItem createCalendarAppointment(
            @RequestBody OutlookCalendarItem in,
            Authentication authentication,
            HttpSession session) {

        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        OutlookDTO outlookDTO = getUserOrgDao().retrieveOutlookPassword(authentication);

        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());
        in = outlookService.createOutlookAppointment(outlookUser, WellKnownFolderName.Calendar, in);

        return in;
    }

    public UserOrgDao getUserOrgDao() {
        return userOrgDao;
    }

    public void setUserOrgDao(UserOrgDao userOrgDao) {
        this.userOrgDao = userOrgDao;
    }

    public OutlookService getOutlookService() {
        return outlookService;
    }

    public void setOutlookService(OutlookService outlookService) {
        this.outlookService = outlookService;
    }
}

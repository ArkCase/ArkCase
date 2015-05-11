package com.armedia.acm.plugins.outlook.web.api;

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
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
@RequestMapping({"/api/v1/plugin/outlook/contacts", "/api/latest/plugin/outlook/contacts"})
public class CreateContactItemAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());
    private OutlookService outlookService;
    private UserOrgService userOrgService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OutlookContactItem createContactItem(
            @RequestBody OutlookContactItem in,
            Authentication authentication,
            HttpSession session) throws AcmEncryptionException {

        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        OutlookDTO outlookDTO = getUserOrgService().retrieveOutlookPassword(authentication);

        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());
        in = outlookService.createOutlookContactItem(outlookUser, WellKnownFolderName.Contacts, in);

        return in;
    }

    public UserOrgService getUserOrgService() {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService) {
        this.userOrgService = userOrgService;
    }

    public OutlookService getOutlookService() {
        return outlookService;
    }

    public void setOutlookService(OutlookService outlookService) {
        this.outlookService = outlookService;
    }
}

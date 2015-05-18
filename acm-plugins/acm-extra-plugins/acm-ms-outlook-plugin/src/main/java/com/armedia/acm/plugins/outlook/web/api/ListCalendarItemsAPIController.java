package com.armedia.acm.plugins.outlook.web.api;

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookResults;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/outlook", "/api/latest/plugin/outlook" })
public class ListCalendarItemsAPIController
{
    private OutlookService outlookService;
    private UserOrgService userOrgService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/calendar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OutlookResults<OutlookCalendarItem> inbox(
            @RequestParam(value = "folderId", required = false) String folderId,
            @RequestParam(value = "s", required = false, defaultValue = "dateTimeStart") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            Authentication authentication,
            HttpSession session
    ) throws AcmEncryptionException {
        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        OutlookDTO outlookDTO = getUserOrgService().retrieveOutlookPassword(authentication);

        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());

        boolean ascendingSort = "ASC".equals(sortDirection);

        OutlookResults<OutlookCalendarItem> results = getOutlookService().findCalendarItems(folderId, outlookUser, startRow, maxRows, sort, ascendingSort);

        return results;

    }

    public UserOrgService getUserOrgService() {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService) {
        this.userOrgService = userOrgService;
    }

    public OutlookService getOutlookService()
    {
        return outlookService;
    }

    public void setOutlookService(OutlookService outlookService)
    {
        this.outlookService = outlookService;
    }
}

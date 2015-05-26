package com.armedia.acm.plugins.outlook.web.api;

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookResults;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RequestMapping({ "/api/v1/plugin/outlook", "/api/latest/plugin/outlook" })
public class ListCalendarItemsAPIController
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    DateFormat searchDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private OutlookService outlookService;
    private UserOrgService userOrgService;

    @RequestMapping(value = "/calendar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OutlookResults<OutlookCalendarItem> inbox(
            @RequestParam(value = "folderId", required = false) String folderId,
            @RequestParam(value = "s", required = false, defaultValue = "dateTimeStart") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "startSearchStartDate", required = false) String startSearchStartDate,
            @RequestParam(value = "endSearchStartDate", required = false) String endSearchStartDate,
            Authentication authentication,
            HttpSession session
    ) throws AcmEncryptionException, ParseException {
        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        OutlookDTO outlookDTO = getUserOrgService().retrieveOutlookPassword(authentication);

        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());

        boolean ascendingSort = "ASC".equals(sortDirection);

        //Append all filters for searching in filterCollection
        SearchFilter.SearchFilterCollection filterCollection = new SearchFilter.SearchFilterCollection();
        if (!StringUtils.isEmpty(startSearchStartDate)) {
            SearchFilter.IsGreaterThan isGreaterThanFilter = new SearchFilter.IsGreaterThan(AppointmentSchema.Start, searchDateFormat.parse(startSearchStartDate));
            filterCollection.add(isGreaterThanFilter);
        }
        if (!StringUtils.isEmpty(endSearchStartDate)) {
            SearchFilter.IsLessThan isLessThanFilter = new SearchFilter.IsLessThan(AppointmentSchema.Start,  searchDateFormat.parse(endSearchStartDate));
            filterCollection.add(isLessThanFilter);
        }

        //if folderId is null than items are retrieved from own calendar folder
        OutlookResults<OutlookCalendarItem> results = getOutlookService().findCalendarItems(folderId, outlookUser, startRow, maxRows, sort, ascendingSort, filterCollection);

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

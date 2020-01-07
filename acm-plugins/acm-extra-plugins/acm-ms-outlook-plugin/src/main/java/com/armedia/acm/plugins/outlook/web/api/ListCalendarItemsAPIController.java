package com.armedia.acm.plugins.outlook.web.api;

/*-
 * #%L
 * ACM Extra Plugin: MS Outlook Integration
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

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.model.OutlookResults;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

@RequestMapping({ "/api/v1/plugin/outlook", "/api/latest/plugin/outlook" })
public class ListCalendarItemsAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private DateTimeFormatter searchDateFormat = DateTimeFormatter.ofPattern(DateFormats.DEFAULT_DATE_FORMAT);
    private OutlookService outlookService;
    private UserOrgService userOrgService;

    @RequestMapping(value = "/calendar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OutlookResults<OutlookCalendarItem> inbox(@RequestParam(value = "folderId", required = false) String folderId,
            @RequestParam(value = "s", required = false, defaultValue = "dateTimeStart") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "startSearchStartDate", required = false) String startSearchStartDate,
            @RequestParam(value = "endSearchStartDate", required = false) String endSearchStartDate, Authentication authentication,
            HttpSession session) throws AcmEncryptionException, ParseException
    {
        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        OutlookDTO outlookDTO = getOutlookService().retrieveOutlookPassword(authentication);

        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());

        boolean ascendingSort = "ASC".equals(sortDirection);

        // Append all filters for searching in filterCollection
        SearchFilter.SearchFilterCollection filterCollection = null;

        if (!StringUtils.isEmpty(startSearchStartDate) || !StringUtils.isEmpty(endSearchStartDate))
        {
            filterCollection = new SearchFilter.SearchFilterCollection();
            if (!StringUtils.isEmpty(startSearchStartDate))
            {
                Date start = getDate(startSearchStartDate);
                SearchFilter.IsGreaterThan isGreaterThanFilter = new SearchFilter.IsGreaterThan(AppointmentSchema.Start, start);
                filterCollection.add(isGreaterThanFilter);
            }
            if (!StringUtils.isEmpty(endSearchStartDate))
            {
                Date end = getDate(endSearchStartDate);
                SearchFilter.IsLessThan isLessThanFilter = new SearchFilter.IsLessThan(AppointmentSchema.Start, end);
                filterCollection.add(isLessThanFilter);
            }
        }

        // if folderId is null than items are retrieved from own calendar folder
        OutlookResults<OutlookCalendarItem> results = getOutlookService().findCalendarItems(folderId, outlookUser, startRow, maxRows, sort,
                ascendingSort, filterCollection);

        return results;

    }

    private Date getDate(String dateString)
    {
        LocalDateTime ldt = searchDateFormat.parse(dateString, LocalDateTime::from);
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public UserOrgService getUserOrgService()
    {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService)
    {
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

/**
 *
 */
package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.holiday.service.HolidayConfigurationService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIARequest;
import gov.foia.model.FoiaConfig;

/**
 * @author riste.tutureski
 */
public class HoldedAndAppealedRequestsDueDateUpdate
{

    private FOIARequestDao requestDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private FoiaConfig foiaConfig;
    private HolidayConfigurationService holidayConfigurationService;

    public void updateDueDate()
    {
        if (!getFoiaConfig().getHoldedAndAppealedRequestsDueDateUpdateEnabled())
        {
            return;
        }

        auditPropertyEntityAdapter.setUserId("DUE_DATE_UPDATER");
        List<FOIARequest> result = requestDao.findAllHeldAndAppealedRequests();
        for (FOIARequest request : result)
        {
            Date dueDate = request.getDueDate();
            if (dueDate != null)
            {
                request.setDueDate(getHolidayConfigurationService().addWorkingDaysToDateAndSetTimeToBusinessHours(dueDate, 1));
            }

            LocalDateTime perfectedDate = request.getPerfectedDate();
            if (perfectedDate != null)
            {
                request.setPerfectedDate(getHolidayConfigurationService().addWorkingDaysToDate(perfectedDate.toLocalDate(), 1)
                        .atTime(perfectedDate.toLocalTime()));
            }

            if (request.getRequestType().equals(FOIAConstants.NEW_REQUEST_TYPE))
            {
                LocalDateTime redirectedDate = request.getRedirectedDate();
                if (redirectedDate != null)
                {
                    request.setRedirectedDate(getHolidayConfigurationService().addWorkingDaysToDate(redirectedDate.toLocalDate(), 1)
                            .atTime(perfectedDate.toLocalTime()));
                }
            }

            requestDao.save(request);
        }
    }

    /**
     * @param requestDao
     *            the requestDao to set
     */
    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }

    /**
     * @param auditPropertyEntityAdapter
     *            the auditPropertyEntityAdapter to set
     */
    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public FoiaConfig getFoiaConfig()
    {
        return foiaConfig;
    }

    public void setFoiaConfig(FoiaConfig foiaConfig)
    {
        this.foiaConfig = foiaConfig;
    }

    public HolidayConfigurationService getHolidayConfigurationService() {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService) {
        this.holidayConfigurationService = holidayConfigurationService;
    }
}

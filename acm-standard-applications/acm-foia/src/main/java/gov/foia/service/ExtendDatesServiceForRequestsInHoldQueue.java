package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.services.holiday.service.HolidayConfigurationService;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

import java.time.LocalDate;

public class ExtendDatesServiceForRequestsInHoldQueue
{
    private HolidayConfigurationService holidayConfigurationService;
    private FOIARequestDao requestDao;

    public void extendDatesForRequestsLeavingHoldQueue(Long objectId)
    {
       FOIARequest request = requestDao.find(objectId);

       if (request != null)
       {

           request.setPerfectedDate(getHolidayConfigurationService()
                   .addWorkingDaysToDate(request.getPerfectedDate(), getHolidayConfigurationService()
                           .calculateAmountOfWorkingDays(request.getHoldEnterDate().toLocalDate(), LocalDate.now())));
           request.setDueDate(getHolidayConfigurationService()
                   .addWorkingDaysToDate(request.getDueDate(), getHolidayConfigurationService()
                           .calculateAmountOfWorkingDays(request.getHoldEnterDate().toLocalDate(), LocalDate.now())));

           requestDao.save(request);
       }
    }

    public HolidayConfigurationService getHolidayConfigurationService()
    {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService)
    {
        this.holidayConfigurationService = holidayConfigurationService;
    }

    public FOIARequestDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }
}

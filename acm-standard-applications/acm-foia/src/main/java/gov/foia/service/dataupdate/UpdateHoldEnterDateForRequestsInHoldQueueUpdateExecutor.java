package gov.foia.service.dataupdate;

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

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateHoldEnterDateForRequestsInHoldQueueUpdateExecutor implements AcmDataUpdateExecutor
{

    private FOIARequestDao requestDao;

    @Override
    public String getUpdateId()
    {
        return "update_hold_enter_date_for_requests_in_hold_queue_v2";
    }

    @Override
    public void execute()
    {
        List<FOIARequest> requestList = getRequestDao().findAllHoldRequestsBefore(LocalDateTime.now());

        for (FOIARequest request : requestList)
        {
            request.setHoldEnterDate(LocalDateTime.now());
            requestDao.save(request);
        }
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

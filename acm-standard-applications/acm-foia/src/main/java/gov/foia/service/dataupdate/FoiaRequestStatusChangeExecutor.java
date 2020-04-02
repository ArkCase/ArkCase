package gov.foia.service.dataupdate;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

/**
 * @author aleksandar.acevski
 *
 */
public class FoiaRequestStatusChangeExecutor implements AcmDataUpdateExecutor
{
    private final Logger log = LogManager.getLogger(getClass());

    private FOIARequestDao requestDao;

    @Override
    public String getUpdateId()
    {
        return "foia-request-status-changes-v1";
    }

    @Override
    public void execute()
    {
        List<FOIARequest> allFoiaRequest = getRequestDao().findAll();

        for (FOIARequest request : allFoiaRequest)
        {
            if (isQueueAndStatusEqualTo("Intake", "In Review", request))
            {
                request.setStatus("Perfected");
            }
            else if (isQueueAndStatusEqualTo("Appeal", "Appealed", request))
            {
                request.setStatus("Perfected");
            }
            else if (isQueueAndStatusEqualTo("Hold", "Hold", request))
            {
                request.setStatus("Unperfected");
            }
            else if (isQueueAndStatusEqualTo("Fulfill", "In Fulfillment", request))
            {
                request.setStatus("Perfected");
            }
            else if (isQueueAndStatusEqualTo("Approve", "In Approval", request) && !request.getDeniedFlag())
            {
                request.setStatus("Perfected");
            }
            else if (isQueueAndStatusEqualTo("Approve", "In Approval", request) && request.getDeniedFlag())
            {
                request.setStatus("Denied");
            }
            else if (isQueueAndStatusEqualTo("General Counsel", "GC Review", request))
            {
                request.setStatus("Perfected");
            }
            else if (isQueueAndStatusEqualTo("Billing", "Billing", request))
            {
                request.setStatus("Perfected");
            }
            else if (isQueueAndStatusEqualTo("Release", "Released", request) && !request.getDeniedFlag())
            {
                request.setStatus("Closed");
                if (request.getDispositionClosedDate() == null && request.getReleasedDate() != null)
                {
                    request.setDispositionClosedDate(request.getReleasedDate());
                }
            }
            else if (isQueueAndStatusEqualTo("Release", "Released", request) && request.getDeniedFlag())
            {
                request.setStatus("Denied");
                if (request.getDispositionClosedDate() == null && request.getReleasedDate() != null)
                {
                    request.setDispositionClosedDate(request.getReleasedDate());
                }
            }
            else
            {
                continue;
            }

            getRequestDao().save(request);
        }
    }

    private boolean isQueueAndStatusEqualTo(String queueName, String statusName, FOIARequest request)
    {
        return request.getQueue().getName().equalsIgnoreCase(queueName)
                && request.getStatus().equalsIgnoreCase(statusName);
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

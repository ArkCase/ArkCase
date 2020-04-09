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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import gov.foia.dao.FOIARequestDao;
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
            LocalDateTime dueDateUpdated = dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1);
            request.setDueDate(Date.from(dueDateUpdated.atZone(ZoneId.systemDefault()).toInstant()));
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

}

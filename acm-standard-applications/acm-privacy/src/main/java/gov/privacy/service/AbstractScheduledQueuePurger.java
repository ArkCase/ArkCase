/**
 *
 */
package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.plugins.businessprocess.service.StartBusinessProcessService;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.privacy.dao.SARDao;
import gov.privacy.model.SubjectAccessRequest;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public abstract class AbstractScheduledQueuePurger
{

    private SARDao requestDao;

    private StartBusinessProcessService startBusinessProcessService;

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private SARConfigurationService SARConfigurationService;


    /**
     * @return the log
     */
    protected abstract Logger getLog();

    /**
     * @return
     */
    protected abstract String getClassName();

    /**
     * @return
     */
    protected abstract Integer getMaxDaysInQueueProperty();

    /**
     * @return
     */
    protected abstract Boolean getPurgeRequestEnabled();

    public void executeTask()
    {
        if (getPurgeRequestEnabled())
        {
            if (getMaxDaysInQueueProperty() == 0)
            {
                return;
            }
            try
            {
                List<SubjectAccessRequest> requestsForPurging = getAllRequestsInQueueBefore(LocalDate.now().minusDays(getMaxDaysInQueueProperty()));

                auditPropertyEntityAdapter.setUserId(getProcessUser());
                MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, getProcessUser());

                for (SubjectAccessRequest request : requestsForPurging)
                {
                    Map<String, Object> processVariables = createProcessVariables(request);
                    startBusinessProcessService.startBusinessProcess(getBusinessProcessName(), processVariables);
                }
            }
            catch (Exception e)
            {
                getLog().error("Error while executing task from {} bean.", getClassName(), e);
            }
        }
    }

    protected abstract List<SubjectAccessRequest> getAllRequestsInQueueBefore(LocalDate date);

    protected abstract String getProcessUser();

    private Map<String, Object> createProcessVariables(SubjectAccessRequest request)
    {
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("OBJECT_TYPE", "CASE_FILE");
        processVariables.put("OBJECT_ID", request.getId());
        return processVariables;
    }

    /**
     * @return
     */
    protected abstract String getBusinessProcessName();

    /**
     * @return the maxDaysInQueue
     */
    protected int getMaxDaysInQueue()
    {
        return getMaxDaysInQueueProperty();
    }

    /**
     * @return the requestDao
     */
    protected SARDao getRequestDao()
    {
        return requestDao;
    }

    /**
     * @param requestDao
     *            the requestDao to set
     */
    public void setRequestDao(SARDao requestDao)
    {
        this.requestDao = requestDao;
    }

    /**
     * @param startBusinessProcessService
     *            the startBusinessProcessService to set
     */
    public void setStartBusinessProcessService(StartBusinessProcessService startBusinessProcessService)
    {
        this.startBusinessProcessService = startBusinessProcessService;
    }

    /**
     * @param auditPropertyEntityAdapter
     *            the auditPropertyEntityAdapter to set
     */
    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public SARConfigurationService getSARConfigurationService()
    {
        return SARConfigurationService;
    }

    public void setSARConfigurationService(SARConfigurationService SARConfigurationService)
    {
        this.SARConfigurationService = SARConfigurationService;
    }
}

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
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.plugins.businessprocess.service.StartBusinessProcessService;
import com.armedia.acm.scheduler.AcmSchedulableBean;

import org.slf4j.Logger;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 5, 2016
 */
public abstract class AbstractScheduledQueuePurger implements AcmSchedulableBean, ApplicationListener<AbstractConfigurationFileEvent>
{
    private int maxDaysInQueue;

    private String purgeEnabled;

    private FOIARequestDao requestDao;

    private StartBusinessProcessService startBusinessProcessService;

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {
        if (isPropertyFileChange(event))
        {
            File configFile = event.getConfigFile();
            try (FileInputStream fis = new FileInputStream(configFile))
            {
                getLog().debug("Loading configaration for {} from {} file.", getClassName(), configFile.getName());

                Properties billingQueuePurgerProperties = new Properties();
                billingQueuePurgerProperties.load(fis);

                maxDaysInQueue = Integer.parseInt(billingQueuePurgerProperties.getProperty(getMaxDaysInQueueProperty()));
                purgeEnabled = billingQueuePurgerProperties.getProperty(getPurgeRequestWhenInHoldEnabled());

            }
            catch (IOException e)
            {
                getLog().error("Could not load configuration for {} from {} file.", getClassName(), configFile.getName(), e);
            }
        }
    }

    protected boolean isPropertyFileChange(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        return (abstractConfigurationFileEvent instanceof ConfigurationFileAddedEvent
                || abstractConfigurationFileEvent instanceof ConfigurationFileChangedEvent)
                && abstractConfigurationFileEvent.getConfigFile().getName().equals("foia.properties");
    }

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
    protected abstract String getMaxDaysInQueueProperty();

    /**
     * @return
     */
    protected abstract String getPurgeRequestWhenInHoldEnabled();

    @Override
    public void executeTask()
    {
        if (Boolean.parseBoolean(purgeEnabled))
        {
            if (maxDaysInQueue == 0)
            {
                return;
            }
            try
            {
                List<FOIARequest> requestsForPurging = getAllRequestsInQueueBefore(LocalDate.now().minusDays(maxDaysInQueue));

                auditPropertyEntityAdapter.setUserId(getProcessUser());

                for (FOIARequest request : requestsForPurging)
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

    protected abstract List<FOIARequest> getAllRequestsInQueueBefore(LocalDate date);

    protected abstract String getProcessUser();

    private Map<String, Object> createProcessVariables(FOIARequest request)
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
        return maxDaysInQueue;
    }

    /**
     * @return the requestDao
     */
    protected FOIARequestDao getRequestDao()
    {
        return requestDao;
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
}

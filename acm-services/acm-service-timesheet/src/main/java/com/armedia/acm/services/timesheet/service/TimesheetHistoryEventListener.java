package com.armedia.acm.services.timesheet.service;

/*-
 * #%L
 * ACM Service: Timesheet
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataServiceImpl;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.AcmTimesheetAssociatedEvent;
import com.armedia.acm.services.timesheet.model.AcmTimesheetEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.util.List;

public class TimesheetHistoryEventListener implements ApplicationListener<AcmTimesheetEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private AcmDataServiceImpl acmDataService;
    private TimesheetAssociatedEventPublisher timesheetAssociatedEventPublisher;

    @Override
    public void onApplicationEvent(AcmTimesheetEvent event)
    {
        if (event != null && checkExecution(event.getEventType()))
        {
            LOG.debug("TimesheetHistoryEventListener: Trying to add timesheet associated event to the object history");

            AcmTimesheet timesheet = (AcmTimesheet) event.getSource();
            List<AcmTime> times = timesheet.getTimes();

            times.forEach(time -> {
                if (!time.getType().equals("OTHER"))
                {
                    Long parentObjectId = time.getObjectId();
                    Long objectId = timesheet.getId();
                    String parentObjectType = time.getType();
                    String objectType = timesheet.getObjectType();
                    String eventType = "com.armedia.acm." + parentObjectType.replace("_", "").toLowerCase() + ".timesheet.associated";

                    AcmAbstractDao<AcmObject> dao = getAcmDataService().getDaoByObjectType(parentObjectType);
                    AcmStatefulEntity entity = (AcmStatefulEntity) dao.find(parentObjectId);

                    if (entity != null)
                    {
                        AcmTimesheetAssociatedEvent acmTimesheetAssociatedEvent = new AcmTimesheetAssociatedEvent(entity, parentObjectId, objectId, parentObjectType, objectType, eventType, event.getUserId(), event.getIpAddress(), event.getEventDate(), true);
                        getTimesheetAssociatedEventPublisher().publishEvent(acmTimesheetAssociatedEvent);
                    }
                }
            });
        }
    }

    private boolean checkExecution(String eventType)
    {
        return eventType.equals("com.armedia.acm.timesheet.save");
    }

    public AcmDataServiceImpl getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataServiceImpl acmDataService)
    {
        this.acmDataService = acmDataService;
    }

    public TimesheetAssociatedEventPublisher getTimesheetAssociatedEventPublisher()
    {
        return timesheetAssociatedEventPublisher;
    }

    public void setTimesheetAssociatedEventPublisher(TimesheetAssociatedEventPublisher timesheetAssociatedEventPublisher)
    {
        this.timesheetAssociatedEventPublisher = timesheetAssociatedEventPublisher;
    }
}

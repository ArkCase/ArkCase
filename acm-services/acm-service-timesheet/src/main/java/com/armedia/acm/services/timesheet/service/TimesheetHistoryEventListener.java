package com.armedia.acm.services.timesheet.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataServiceImpl;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.AcmTimesheetAssociatedEvent;
import com.armedia.acm.services.timesheet.model.AcmTimesheetEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.List;

public class TimesheetHistoryEventListener implements ApplicationListener<AcmTimesheetEvent>
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());
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
                    Long objectId = time.getObjectId();
                    String objectType = time.getType();
                    String eventType = "com.armedia.acm." + objectType.replace("_", "").toLowerCase() + ".timesheet.associated";

                    AcmAbstractDao<AcmObject> dao = getAcmDataService().getDaoByObjectType(objectType);
                    AcmStatefulEntity entity = (AcmStatefulEntity) dao.find(objectId);

                    if (entity != null)
                    {
                        AcmTimesheetAssociatedEvent acmTimesheetAssociatedEvent = new AcmTimesheetAssociatedEvent(entity, objectId,
                                objectType, eventType, event.getUserId(), event.getIpAddress(), event.getEventDate(), true);
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

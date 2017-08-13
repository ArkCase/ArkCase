package com.armedia.acm.services.costsheet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataServiceImpl;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.AcmCostsheetAssociatedEvent;
import com.armedia.acm.services.costsheet.model.AcmCostsheetEvent;

public class CostsheetHistoryEventListener implements ApplicationListener<AcmCostsheetEvent>
{
    
    private final Logger LOG = LoggerFactory.getLogger(getClass());    
    private AcmDataServiceImpl acmDataService;
    private CostsheetAssociatedEventPublisher costsheetAssociatedEventPublisher;    

    @Override
    public void onApplicationEvent(AcmCostsheetEvent event)
    {   
        if(event != null && checkExecution(event.getEventType()))
        {
            LOG.debug("CostsheetHistoryEventListener: Trying to add costsheet associated event to the object history");
            
            AcmCostsheet costsheet = (AcmCostsheet)event.getSource();
            
            String objectType = costsheet.getParentType();
            Long objectId = costsheet.getParentId();
            String eventType = "com.armedia.acm." + objectType.replace("_", "").toLowerCase() + ".costsheet.associated";
            
            AcmAbstractDao<AcmStatefulEntity> dao = getAcmDataService().getDaoByObjectType(objectType);
            AcmStatefulEntity entity = dao.find(objectId);

            if(entity != null)
            {
                AcmCostsheetAssociatedEvent acmCostsheetAssociatedEvent 
                        = new AcmCostsheetAssociatedEvent(entity, objectId, objectType, eventType, event.getUserId(), event.getIpAddress(), event.getEventDate(), true);            
                getCostsheetAssociatedEventPublisher().publishEvent(acmCostsheetAssociatedEvent);
            }
        }
    }
    
    private boolean checkExecution(String eventType) 
    {        
        return eventType.equals("com.armedia.acm.costsheet.save");
    }

    public AcmDataServiceImpl getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataServiceImpl acmDataService)
    {
        this.acmDataService = acmDataService;
    }

    public CostsheetAssociatedEventPublisher getCostsheetAssociatedEventPublisher()
    {
        return costsheetAssociatedEventPublisher;
    }

    public void setCostsheetAssociatedEventPublisher(CostsheetAssociatedEventPublisher costsheetAssociatedEventPublisher)
    {
        this.costsheetAssociatedEventPublisher = costsheetAssociatedEventPublisher;
    }
}

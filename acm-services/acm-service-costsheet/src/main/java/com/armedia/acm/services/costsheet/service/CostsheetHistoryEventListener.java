package com.armedia.acm.services.costsheet.service;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.core.AcmTitleEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.AcmCostsheetAssociatedEvent;
import com.armedia.acm.services.costsheet.model.AcmCostsheetEvent;
import com.armedia.acm.spring.SpringContextHolder;

public class CostsheetHistoryEventListener implements ApplicationListener<AcmCostsheetEvent>
{
    
    private final Logger LOG = LoggerFactory.getLogger(getClass());    
    private SpringContextHolder springContextHolder;
    private CostsheetAssociatedEventPublisher costsheetAssociatedEventPublisher;    

    @Override
    public void onApplicationEvent(AcmCostsheetEvent event)
    {   
        if(event != null && checkExecution(event.getEventType()))
        {
            LOG.debug("CostsheetHistoryEventListener: Trying to add costsheet associated event to the object history");
            
            AcmCostsheet costsheet = (AcmCostsheet)event.getSource();            
            AcmEntity acmEntity = getCostsheetParentObject(costsheet.getParentType(), costsheet.getParentId());
            
            String objectType = costsheet.getParentType().replace("_", "").toLowerCase();
            String eventType = "com.armedia.acm." + objectType + ".costsheet.associated";
            
            AcmCostsheetAssociatedEvent acmCostsheetAssociatedEvent 
                        = new AcmCostsheetAssociatedEvent(acmEntity, costsheet.getParentId(), costsheet.getParentType(), eventType, event.getUserId(), event.getIpAddress(), event.getEventDate(), true);            
            getCostsheetAssociatedEventPublisher().publishEvent(acmCostsheetAssociatedEvent);
        }
    }
    
    private AcmEntity getCostsheetParentObject(String parentObjectType, Long parentId) 
    {
        AcmEntity acmEntity = null;        
        Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);
        
        AcmAbstractDao<AcmTitleEntity> dao = daos.values()
            .stream()
            .filter(Objects::nonNull)
            .filter(item -> item.getSupportedObjectType() != null)
            .filter(item -> item.getSupportedObjectType().equals(parentObjectType))
            .findFirst()
            .orElse(null);
        
        if(dao != null) {
            acmEntity = (AcmEntity) dao.find(parentId);            
        }
        return acmEntity;
    }
    
    private boolean checkExecution(String eventType) 
    {        
        return eventType.equals("com.armedia.acm.costsheet.save");
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
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

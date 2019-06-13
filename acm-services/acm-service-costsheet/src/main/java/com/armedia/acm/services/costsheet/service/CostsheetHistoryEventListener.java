package com.armedia.acm.services.costsheet.service;

/*-
 * #%L
 * ACM Service: Costsheet
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
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.AcmCostsheetAssociatedEvent;
import com.armedia.acm.services.costsheet.model.AcmCostsheetEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

public class CostsheetHistoryEventListener implements ApplicationListener<AcmCostsheetEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private AcmDataServiceImpl acmDataService;
    private CostsheetAssociatedEventPublisher costsheetAssociatedEventPublisher;

    @Override
    public void onApplicationEvent(AcmCostsheetEvent event)
    {
        if (event != null && checkExecution(event.getEventType()))
        {
            LOG.debug("CostsheetHistoryEventListener: Trying to add costsheet associated event to the object history");

            AcmCostsheet costsheet = (AcmCostsheet) event.getSource();

            String objectType = costsheet.getParentType();
            Long objectId = costsheet.getParentId();
            String eventType = "com.armedia.acm." + objectType.replace("_", "").toLowerCase() + ".costsheet.associated";

            AcmAbstractDao<AcmObject> dao = getAcmDataService().getDaoByObjectType(objectType);
            AcmStatefulEntity entity = (AcmStatefulEntity) dao.find(objectId);

            if (entity != null)
            {
                AcmCostsheetAssociatedEvent acmCostsheetAssociatedEvent = new AcmCostsheetAssociatedEvent(entity, objectId, objectType,
                        eventType, event.getUserId(), event.getIpAddress(), event.getEventDate(), true);
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

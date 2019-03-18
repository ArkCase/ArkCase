package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.model.Notification;

public class AcmEntityTemplateModelProvider implements TemplateModelProvider
{
    private AcmDataService dataService;

    @Override
    public Object getModel(Notification notification)
    {
        AcmAbstractDao<AcmObject> dao = getDataService().getDaoByObjectType(notification.getParentType());
        return dao.find(notification.getParentId());
    }

    public AcmDataService getDataService()
    {
        return dataService;
    }

    public void setDataService(AcmDataService dataService)
    {
        this.dataService = dataService;
    }
}

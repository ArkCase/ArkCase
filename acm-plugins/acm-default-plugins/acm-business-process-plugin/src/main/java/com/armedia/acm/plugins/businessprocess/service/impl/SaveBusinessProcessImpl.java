package com.armedia.acm.plugins.businessprocess.service.impl;

import com.armedia.acm.plugins.businessprocess.dao.BusinessProcessDao;
import com.armedia.acm.plugins.businessprocess.model.BusinessProcess;
import com.armedia.acm.plugins.businessprocess.service.SaveBusinessProcess;

public class SaveBusinessProcessImpl implements SaveBusinessProcess {
    
    private BusinessProcessDao businessProcessDao;
    
    @Override
    public BusinessProcess save(BusinessProcess businessProcess) {
        return businessProcessDao.save(businessProcess);
    }

    public BusinessProcessDao getBusinessProcessDao() {
        return businessProcessDao;
    }

    public void setBusinessProcessDao(BusinessProcessDao businessProcessDao) {
        this.businessProcessDao = businessProcessDao;
    }
}

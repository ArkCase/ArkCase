package com.armedia.acm.services.zylab.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.services.zylab.jms.ZylabProductionSyncStatusToJmsSender;
import com.armedia.acm.services.zylab.model.ZylabProductionSyncEvent;
import com.armedia.acm.services.zylab.model.ZylabProductionSyncStatus;
import com.armedia.acm.tool.zylab.model.ZylabProductionSyncDTO;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on March, 2021
 */
public class ZylabProductionSyncListener implements ApplicationListener<ZylabProductionSyncEvent>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ZylabProductionSyncStatusToJmsSender zylabProductionSyncStatusToJmsSender;

    @Override
    public void onApplicationEvent(ZylabProductionSyncEvent zylabProductionSyncEvent)
    {
        zylabProductionSyncStatusToJmsSender.sendProductionSyncStatus(zylabProductionSyncEvent.getZylabProductionSyncDTO());
    }

    public ZylabProductionSyncStatusToJmsSender getZylabProductionSyncStatusToJmsSender()
    {
        return zylabProductionSyncStatusToJmsSender;
    }

    public void setZylabProductionSyncStatusToJmsSender(ZylabProductionSyncStatusToJmsSender zylabProductionSyncStatusToJmsSender)
    {
        this.zylabProductionSyncStatusToJmsSender = zylabProductionSyncStatusToJmsSender;
    }
}

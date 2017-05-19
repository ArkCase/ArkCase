package com.armedia.acm.plugins.ecm.service.sync;

import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoSyncScheduledBean;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoSyncService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Created by dmiller on 5/15/17.
 */
public class AlfrescoSyncScheduledBeanTest
{
    private AlfrescoSyncService service = EasyMock.createMock(AlfrescoSyncService.class);

    private AlfrescoSyncScheduledBean unit;

    @Before
    public void setUp() throws Exception
    {
        unit = new AlfrescoSyncScheduledBean();

        unit.setAlfrescoSyncService(service);
    }

    @Test
    public void executeTask_ifDisabled_thenReturnImmediately() throws Exception
    {
        unit.setEnabled(false);

        replay(service);

        unit.executeTask();

        verify(service);
    }

    @Test
    public void executeTask_ifEnabled_thenCallServiceOncePerAlfrescoAuditApplication() throws Exception
    {
        unit.setEnabled(true);

        service.queryAlfrescoAuditApplications();

        replay(service);

        unit.executeTask();

        verify(service);
    }
}

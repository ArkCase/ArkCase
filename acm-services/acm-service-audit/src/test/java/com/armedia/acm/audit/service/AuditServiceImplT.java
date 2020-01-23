package com.armedia.acm.audit.service;

/*-
 * #%L
 * ACM Service: Audit Library
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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditConfig;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.systemlogger.ISystemLogger;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created by Bojan Milenkoski on 11.1.2016.
 */
public class AuditServiceImplT extends EasyMockSupport
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    private AuditServiceImpl auditService;
    private AuditEvent event;
    private AuditDao mockAuditDao;
    private AuditConfig auditConfig;
    private ISystemLogger mockSyslogLogger;

    @Before
    public void setUp() throws Exception
    {
        auditService = new AuditServiceImpl();
        auditConfig = new AuditConfig();
        auditService.setAuditConfig(auditConfig);
        event = new AuditEvent();
        event.setEventDate(new Date());
    }

    @Test
    public void auditLogsToDatabaseOnly()
    {
        // given
        auditConfig.setDatabaseChangesLoggingEnabled(true);
        auditConfig.setSystemLogEnabled(false);
        auditConfig.setDatabaseEnabled(true);
        mockAuditDao = createMock(AuditDao.class);
        expect(mockAuditDao.save(event)).andReturn(event);
        auditService.setAuditDao(mockAuditDao);

        // when
        replay(mockAuditDao);
        auditService.audit(event);

        // then
        verify(mockAuditDao);
    }

    @Test
    public void auditLogsToSystemLogOnly()
    {
        // given
        auditConfig.setDatabaseChangesLoggingEnabled(false);
        auditConfig.setSystemLogEnabled(true);
        auditConfig.setDatabaseEnabled(false);
        mockSyslogLogger = createMock(ISystemLogger.class);
        mockSyslogLogger.log(event.toString());
        expectLastCall();
        auditService.setSystemLogger(mockSyslogLogger);

        // when
        replay(mockSyslogLogger);
        auditService.audit(event);

        // then
        verify(mockSyslogLogger);
    }

    @Test
    public void auditLogsBothToDatabaseLogAndSystemLog()
    {
        // given
        auditConfig.setDatabaseChangesLoggingEnabled(true);
        auditConfig.setSystemLogEnabled(true);
        auditConfig.setDatabaseEnabled(true);
        mockSyslogLogger = createMock(ISystemLogger.class);
        mockSyslogLogger.log(event.toString());
        expectLastCall();
        auditService.setSystemLogger(mockSyslogLogger);
        mockAuditDao = createMock(AuditDao.class);
        expect(mockAuditDao.save(event)).andReturn(event);
        auditService.setAuditDao(mockAuditDao);

        // when
        replay(mockSyslogLogger, mockAuditDao);
        auditService.audit(event);

        // then
        verify(mockSyslogLogger, mockAuditDao);
    }
}

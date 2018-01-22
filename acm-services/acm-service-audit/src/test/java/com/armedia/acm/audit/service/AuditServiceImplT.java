package com.armedia.acm.audit.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.armedia.acm.audit.dao.AuditDao;
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
    private AuditServiceImpl auditService;
    private AuditEvent event;
    private AuditDao mockAuditDao;
    private ISystemLogger mockSyslogLogger;

    @Before
    public void setUp() throws Exception
    {
        auditService = new AuditServiceImpl();
        event = new AuditEvent();
        event.setEventDate(new Date());
    }

    @Test
    public void auditLogsToDatabaseOnly()
    {
        // given
        auditService.setDatabaseLoggerEnabled(true);
        auditService.setSystemLogLoggerEnabled(false);
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
        auditService.setDatabaseLoggerEnabled(false);
        auditService.setSystemLogLoggerEnabled(true);
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
        auditService.setDatabaseLoggerEnabled(true);
        auditService.setSystemLogLoggerEnabled(true);
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

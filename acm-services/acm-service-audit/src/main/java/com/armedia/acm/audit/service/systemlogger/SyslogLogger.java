package com.armedia.acm.audit.service.systemlogger;

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

import com.armedia.acm.audit.model.AuditConfig;
import com.armedia.acm.core.model.ApplicationConfig;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.SyslogAppender;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.Rfc5424Layout;
import org.apache.logging.log4j.core.net.Facility;

/**
 * Syslog implementation of {@link ISystemLogger}.
 * Add the following lines to rsyslog.conf file to enable UDP protocol:
 * # $ModLoad imudp
 * # $UDPServerRun 514
 * <p>
 * Created by Bojan Milenkoski on 25.12.2015.
 */
public class SyslogLogger implements ISystemLogger
{
    private static final String SYSLOG_LOGGER_APPENDER_NAME = SyslogLogger.class.getName();
    private static final String SYSLOG_LOGGER_NAME = SyslogLogger.class.getName();
    private static Logger logger;

    private AuditConfig auditConfig;
    private ApplicationConfig applicationConfig;

    @Override
    public void log(String message)
    {
        if (logger == null)
        {
            initLogger();
        }

        logger.info(message);
    }

    private synchronized void initLogger()
    {
        if (logger != null)
        {
            return;
        }

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        AbstractConfiguration config = (AbstractConfiguration) ctx.getConfiguration();

        SyslogAppender syslogAppender = SyslogAppender.createAppender(auditConfig.getSystemLogSysLogHost(),
                auditConfig.getSystemLogSysLogPort(), auditConfig.getSystemLogSysLogProtocol(), null,
                0, 0, true, SYSLOG_LOGGER_APPENDER_NAME, true, true,
                Facility.LOG_AUDIT, "App", Rfc5424Layout.DEFAULT_ENTERPRISE_NUMBER, true, Rfc5424Layout.DEFAULT_MDCID, null, "acm", true,
                null,
                applicationConfig.getApplicationName(), "ACMAudit", null, null, null, "RFC5424", null, new DefaultConfiguration(),
                Charsets.UTF_8,
                null, null, false);

        syslogAppender.start();
        config.addAppender(syslogAppender);
        AppenderRef[] refs = new AppenderRef[] { AppenderRef.createAppenderRef(SYSLOG_LOGGER_APPENDER_NAME, null, null) };
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.ALL, SYSLOG_LOGGER_NAME, "true", refs, null, config, null);
        loggerConfig.addAppender(syslogAppender, Level.ALL, null);
        config.addLogger(SYSLOG_LOGGER_NAME, loggerConfig);
        ctx.updateLoggers();

        logger = LogManager.getLogger(SYSLOG_LOGGER_NAME);
    }

    public AuditConfig getAuditConfig()
    {
        return auditConfig;
    }

    public void setAuditConfig(AuditConfig auditConfig)
    {
        this.auditConfig = auditConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }
}

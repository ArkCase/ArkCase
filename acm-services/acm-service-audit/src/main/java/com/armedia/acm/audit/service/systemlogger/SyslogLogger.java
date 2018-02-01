package com.armedia.acm.audit.service.systemlogger;

import com.armedia.acm.core.AcmApplication;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.SyslogAppender;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.Rfc5424Layout;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.util.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private String host;
    private int port;
    private String protocol;
    private AcmApplication acmApplication;

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

        SyslogAppender syslogAppender = SyslogAppender.createAppender(getHost(), getPort(), getProtocol(), null, 0, 0, true,
                SYSLOG_LOGGER_APPENDER_NAME, true, true,
                Facility.LOG_AUDIT, "App", Rfc5424Layout.DEFAULT_ENTERPRISE_NUMBER, true, Rfc5424Layout.DEFAULT_MDCID, null, "acm", true,
                null,
                getAcmApplication().getApplicationName(), "ACMAudit", null, null, null, "RFC5424", null, new DefaultConfiguration(),
                Charsets.UTF_8,
                null, null, false);

        syslogAppender.start();
        config.addAppender(syslogAppender);
        AppenderRef[] refs = new AppenderRef[] { AppenderRef.createAppenderRef(SYSLOG_LOGGER_APPENDER_NAME, null, null) };
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.ALL, SYSLOG_LOGGER_NAME, "true", refs, null, config, null);
        loggerConfig.addAppender(syslogAppender, Level.ALL, null);
        config.addLogger(SYSLOG_LOGGER_NAME, loggerConfig);
        ctx.updateLoggers();

        logger = LoggerFactory.getLogger(SYSLOG_LOGGER_NAME);
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public AcmApplication getAcmApplication()
    {
        return acmApplication;
    }

    public void setAcmApplication(AcmApplication acmApplication)
    {
        this.acmApplication = acmApplication;
    }
}

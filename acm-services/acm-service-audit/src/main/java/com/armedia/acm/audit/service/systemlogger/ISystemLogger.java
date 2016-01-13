package com.armedia.acm.audit.service.systemlogger;

import org.springframework.scheduling.annotation.Async;

/**
 * System logger interface. All implementations should just log to INFO level to the system log.
 * <p>
 * Created by Bojan Milenkoski on 28.12.2015.
 */
public interface ISystemLogger
{
    @Async("auditorExecutor")
    void log(String message);
}

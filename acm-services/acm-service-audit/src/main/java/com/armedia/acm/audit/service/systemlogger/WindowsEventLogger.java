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
import com.armedia.acm.core.AcmApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Windows event logger implementation of {@link ISystemLogger}.
 * To create the app source in event log open terminal as Administrator and run:
 * eventcreate /l APPLICATION /so "applicationName defined in app-config.xml" /t Information /id 1 /d "Creating app
 * source"
 * EventID must be in the range of 1 - 1000.
 * <p>
 * Created by Bojan Milenkoski on 28.12.2015.
 */
public class WindowsEventLogger implements ISystemLogger
{
    private static final String level = "Information";
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmApplication acmApplication;
    private AuditConfig auditConfig;

    @Override
    public void log(String message)
    {
        String command = "eventcreate "
                + " /l APPLICATION"
                + " /so \"" + getAcmApplication().getApplicationName() + "\""
                + " /t " + level
                + " /id " + auditConfig.getSystemLogWindowsEventLogEventId()
                + " /d \"" + message + "\"";

        try
        {
            Runtime.getRuntime().exec(command);
        }
        catch (IOException e)
        {
            log.error("Error writing to Windows Event Log!", e);
        }
    }

    public AcmApplication getAcmApplication()
    {
        return acmApplication;
    }

    public void setAcmApplication(AcmApplication acmApplication)
    {
        this.acmApplication = acmApplication;
    }

    public AuditConfig getAuditConfig()
    {
        return auditConfig;
    }

    public void setAuditConfig(AuditConfig auditConfig)
    {
        this.auditConfig = auditConfig;
    }
}

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

/**
 * Created by Bojan Milenkoski on 29.12.2015.
 */
public class SystemLoggerFactory
{
    private ISystemLogger syslogLogger;
    private ISystemLogger windowsEventLogger;

    public ISystemLogger getSystemLogger()
    {
        if (System.getProperty("os.name").startsWith("Windows"))
        {
            return windowsEventLogger;
        }
        else
        {
            return syslogLogger;
        }
    }

    public ISystemLogger getWindowsEventLogger()
    {
        return windowsEventLogger;
    }

    public void setWindowsEventLogger(ISystemLogger windowsEventLogger)
    {
        this.windowsEventLogger = windowsEventLogger;
    }

    public ISystemLogger getSyslogLogger()
    {
        return syslogLogger;
    }

    public void setSyslogLogger(ISystemLogger syslogLogger)
    {
        this.syslogLogger = syslogLogger;
    }
}

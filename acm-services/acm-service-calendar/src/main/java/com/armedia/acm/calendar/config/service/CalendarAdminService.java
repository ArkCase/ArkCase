package com.armedia.acm.calendar.config.service;

/*-
 * #%L
 * ACM Service: Calendar Service
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

import com.armedia.acm.calendar.service.CalendarService;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
public interface CalendarAdminService
{

    CalendarConfigurationsByObjectType readConfiguration(boolean includePassword) throws CalendarConfigurationException;

    /**
     * Upon successful storage of the configuration, the implementation is required to raise
     * <code>CalendarConfigurationEvent</code> to inform the implementations of <code>CalendarService</code> that the
     * configuration has been updated.
     *
     * @param configuration
     * @throws CalendarConfigurationException
     *
     * @see {@link CalendarService}
     * @see CalendarConfigurationEvent
     */
    void writeConfiguration(CalendarConfigurationsByObjectType configuration) throws CalendarConfigurationException;

    <CCE extends CalendarConfigurationException> CalendarConfigurationExceptionMapper<CCE> getExceptionMapper(CCE e);

}

package com.armedia.acm.plugins.dashboard.model.widget;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 10/7/2014.
 */
public class SetAuthorizedWidgetRolesEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.dashboard.widget.setAuthorizedRoles";

    private static final String OBJECT_TYPE = "AUTHORIZED_ROLES_BY_WIDGET_LIST";

    public SetAuthorizedWidgetRolesEvent(RolesGroupByWidgetDto source)
    {

        super(source);
        setEventType(EVENT_TYPE);
        setEventDate(new Date());
        setObjectType(OBJECT_TYPE);
    }
}

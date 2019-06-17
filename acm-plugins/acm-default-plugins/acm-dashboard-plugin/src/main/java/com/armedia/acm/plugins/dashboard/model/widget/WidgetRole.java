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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * Created by marjan.stefanoski on 9/12/2014.
 */

@Entity
@Table(name = "acm_widget_role")
@IdClass(WidgetRolePrimaryKey.class)
public class WidgetRole
{

    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LogManager.getLogger(getClass());

    @Id
    @Column(name = "cm_widget_id")
    private Long widgetId;

    @Id
    @Column(name = "cm_role_name")
    private String roleName;

    public Long getWidgetId()
    {
        return widgetId;
    }

    public void setWidgetId(Long widgetId)
    {
        this.widgetId = widgetId;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

}

package com.armedia.acm.plugins.dashboard.model;

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

import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.io.Serializable;

/**
 * Created by marst on 7/29/14.
 */

@Entity
@Table(name = "acm_dashboard")
public class Dashboard implements Serializable
{

    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LogManager.getLogger(getClass());

    @Id
    @TableGenerator(name = "acm_dashboard_gen", table = "acm_dashboard_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_dashboard", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_dashboard_gen")
    @Column(name = "cm_dashboard_id")
    private Long dashboardId;

    @Column(name = "cm_dashboard_config")
    private String dashboardConfig;

    @Column(name = "cm_module_name")
    private String moduleName;

    @Column(name = "cm_module_config_collapsed")
    private Boolean collapsed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cm_dashboard_owner")
    private AcmUser dashboardOwner;

    public AcmUser getDashboardOwner()
    {
        return dashboardOwner;
    }

    public void setDashboardOwner(AcmUser dashboardOwner)
    {
        this.dashboardOwner = dashboardOwner;
    }

    public Long getDashboardId()
    {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId)
    {
        this.dashboardId = dashboardId;
    }

    public String getDashboardConfig()
    {
        return dashboardConfig;
    }

    public void setDashboardConfig(String dashboardConfig)
    {
        this.dashboardConfig = dashboardConfig;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public Boolean getCollapsed()
    {
        return collapsed;
    }

    public void setCollapsed(Boolean collapsed)
    {
        this.collapsed = collapsed;
    }
}

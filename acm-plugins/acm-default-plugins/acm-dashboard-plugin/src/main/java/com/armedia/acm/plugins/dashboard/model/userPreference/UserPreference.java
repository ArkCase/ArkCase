package com.armedia.acm.plugins.dashboard.model.userPreference;

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

import com.armedia.acm.plugins.dashboard.model.module.Module;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.services.users.model.AcmUser;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Created by marjan.stefanoski on 14.01.2016.
 */

@Entity
@Table(name = "acm_user_preference")
public class UserPreference
{
    private static final long serialVersionUID = -1554137631123345851L;

    @Id
    @TableGenerator(name = "acm_user_preference_gen", table = "acm_user_preference_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_user_preference", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_user_preference_gen")
    @Column(name = "cm_user_preference_id")
    private Long userPreferenceId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_user")
    private AcmUser user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_widget")
    private Widget widget;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_module")
    private Module module;

    public Long getUserPreferenceId()
    {
        return userPreferenceId;
    }

    public void setUserPreferenceId(Long userPreferenceId)
    {
        this.userPreferenceId = userPreferenceId;
    }

    public AcmUser getUser()
    {
        return user;
    }

    public void setUser(AcmUser user)
    {
        this.user = user;
    }

    public Widget getWidget()
    {
        return widget;
    }

    public void setWidget(Widget widget)
    {
        this.widget = widget;
    }

    public Module getModule()
    {
        return module;
    }

    public void setModule(Module module)
    {
        this.module = module;
    }
}

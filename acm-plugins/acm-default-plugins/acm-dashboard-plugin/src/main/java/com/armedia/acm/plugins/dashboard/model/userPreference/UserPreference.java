package com.armedia.acm.plugins.dashboard.model.userPreference;

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

package com.armedia.acm.plugins.dashboard.model;

import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by marst on 7/29/14.
 */

@Entity
@Table(name = "acm_dashboard")
public class Dashboard implements Serializable{

    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @TableGenerator(name = "acm_dashboard_gen",
            table = "acm_dashboard_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_dashboard",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_dashboard_gen")
    @Column(name = "cm_dashboard_id")
    private Long dashboardId;

    @Column(name = "cm_dashboard_config")
    private String dashobardConfig;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cm_dashboard_owner")
    private AcmUser dashboardOwner;

    public AcmUser getDashboardOwner() {
        return dashboardOwner;
    }

    public void setDashboardOwner(AcmUser dashboardOwner) {
        this.dashboardOwner = dashboardOwner;
    }

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getDashobardConfig() {
        return dashobardConfig;
    }

    public void setDashobardConfig(String dashobardConfig) {
        this.dashobardConfig = dashobardConfig;
    }

}

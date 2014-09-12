//package com.armedia.acm.plugins.dashboard.model;
//
//import com.armedia.acm.services.users.model.AcmUserRolePrimaryKey;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.persistence.*;
//
///**
// * Created by marjan.stefanoski on 9/12/2014.
// */
//
//@Entity
//@Table(name = "acm_widget_role")
//@IdClass(WidgetRolePrimaryKey.class)
//public class WidgetRole {
//
//    private static final long serialVersionUID = -1154137631399833851L;
//    private transient final Logger log = LoggerFactory.getLogger(getClass());
//
//    @Id
//    @Column(name="cm_widget_id")
//    private Long widgetId;
//
//    @Id
//    @Column(name="cm_role_name")
//    private String roleName;
//
//
//    public Long getWidgetId() {
//        return widgetId;
//    }
//
//    public void setWidgetId(Long widgetId) {
//        this.widgetId = widgetId;
//    }
//
//    public String getRoleName() {
//        return roleName;
//    }
//
//    public void setRoleName(String roleName) {
//        this.roleName = roleName;
//    }
//
//}

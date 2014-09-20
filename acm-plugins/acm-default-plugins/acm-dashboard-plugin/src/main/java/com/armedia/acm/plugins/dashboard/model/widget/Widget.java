package com.armedia.acm.plugins.dashboard.model.widget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

/**
* Created by marjan.stefanoski on 9/12/2014.
*/
@Entity
@Table(name = "acm_widget")
public class Widget {
    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_widget_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long widgetId;

    @Column(name = "cm_widget_name")
    private String widgetName;

    public Long getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(Long widgetId) {
        this.widgetId = widgetId;
    }

    public String getWidgetName() {
        return widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }
}

package com.armedia.acm.plugins.dashboard.model.widget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Created by marjan.stefanoski on 9/12/2014.
 */
@Entity
@Table(name = "acm_widget")
public class Widget
{
    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @TableGenerator(name = "acm_widget_gen",
            table = "acm_widget_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_widget",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_widget_gen")
    @Column(name = "cm_widget_id")
    private Long widgetId;

    @Column(name = "cm_widget_name")
    private String widgetName;

    public Long getWidgetId()
    {
        return widgetId;
    }

    public void setWidgetId(Long widgetId)
    {
        this.widgetId = widgetId;
    }

    public String getWidgetName()
    {
        return widgetName;
    }

    public void setWidgetName(String widgetName)
    {
        this.widgetName = widgetName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Widget widget = (Widget) o;

        if (widgetName != null ? !widgetName.equals(widget.widgetName) : widget.widgetName != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = 31 * 1 + (widgetName != null ? widgetName.hashCode() : 0);
        return result;
    }
}

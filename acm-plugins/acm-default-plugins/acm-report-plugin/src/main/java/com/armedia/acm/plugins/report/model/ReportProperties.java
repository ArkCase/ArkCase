/**
 * 
 */
package com.armedia.acm.plugins.report.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportProperties
{

    private String locale;

    @XmlElement(name = "properties")
    private List<ReportProperty> value;

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public List<ReportProperty> getValue()
    {
        return value;
    }

    public void setValue(List<ReportProperty> value)
    {
        this.value = value;
    }

}

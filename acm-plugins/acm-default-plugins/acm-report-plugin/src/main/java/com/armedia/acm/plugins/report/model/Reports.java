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
public class Reports
{

    @XmlElement(name = "repositoryFileDto")
    private List<Report> value;

    public List<Report> getValue()
    {
        return value;
    }

    public void setValue(List<Report> value)
    {
        this.value = value;
    }

}

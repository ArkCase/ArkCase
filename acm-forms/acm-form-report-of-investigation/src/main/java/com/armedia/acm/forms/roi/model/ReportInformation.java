/**
 * 
 */
package com.armedia.acm.forms.roi.model;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class ReportInformation
{

    private String title;
    private Date date;
    private String firstName;
    private String lastName;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @XmlJavaTypeAdapter(value = DateFrevvoAdapter.class)
    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

}

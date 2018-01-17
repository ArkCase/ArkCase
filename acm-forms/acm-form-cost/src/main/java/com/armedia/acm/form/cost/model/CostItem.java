/**
 * 
 */
package com.armedia.acm.form.cost.model;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class CostItem
{

    private Long id;
    private Date date;
    private String title;
    private List<String> titleOptions;
    private String description;
    private Double amount;

    @XmlElement(name = "id")
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @XmlElement(name = "date")
    @XmlJavaTypeAdapter(value = DateFrevvoAdapter.class)
    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    @XmlElement(name = "title")
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @XmlTransient
    public List<String> getTitleOptions()
    {
        return titleOptions;
    }

    public void setTitleOptions(List<String> titleOptions)
    {
        this.titleOptions = titleOptions;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @XmlElement(name = "amount")
    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

}

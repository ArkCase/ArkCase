package com.armedia.acm.form.time.model;

/*-
 * #%L
 * ACM Forms: Time
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class TimeItem
{

    private String type;
    private List<String> typeOptions;

    private Long objectId;

    private String code;

    private String chargeRole;

    private List<String> chargeRoles;

    private Double totalCost;

    private String title;

    private Long sundayId;
    private Double sunday;

    private Long mondayId;
    private Double monday;

    private Long tuesdayId;
    private Double tuesday;

    private Long wednesdayId;
    private Double wednesday;

    private Long thursdayId;
    private Double thursday;

    private Long fridayId;
    private Double friday;

    private Long saturdayId;
    private Double saturday;

    @XmlElement(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlTransient
    public List<String> getTypeOptions()
    {
        return typeOptions;
    }

    public void setTypeOptions(List<String> typeOptions)
    {
        this.typeOptions = typeOptions;
    }

    @XmlElement(name = "objectId")
    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    @XmlElement(name = "objectNumber")
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @XmlElement(name = "chargeRole")
    public String getChargeRole()
    {
        return chargeRole;
    }

    public void setChargeRole(String chargeRole)
    {
        this.chargeRole = chargeRole;
    }

    @XmlTransient
    public List<String> getChargeRoles()
    {
        return chargeRoles;
    }

    public void setChargeRoles(List<String> chargeRoles)
    {
        this.chargeRoles = chargeRoles;
    }

    @XmlElement(name = "totalCost")
    public Double getTotalCost()
    {
        return totalCost;
    }

    public void setTotalCost(Double totalCost)
    {
        this.totalCost = totalCost;
    }

    @XmlElement(name = "objectTitle")
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @XmlElement(name = "sundayId")
    public Long getSundayId()
    {
        return sundayId;
    }

    public void setSundayId(Long sundayId)
    {
        this.sundayId = sundayId;
    }

    @XmlElement(name = "sunday")
    public Double getSunday()
    {
        return sunday;
    }

    public void setSunday(Double sunday)
    {
        this.sunday = sunday;
    }

    @XmlElement(name = "mondayId")
    public Long getMondayId()
    {
        return mondayId;
    }

    public void setMondayId(Long mondayId)
    {
        this.mondayId = mondayId;
    }

    @XmlElement(name = "monday")
    public Double getMonday()
    {
        return monday;
    }

    public void setMonday(Double monday)
    {
        this.monday = monday;
    }

    @XmlElement(name = "tuesdayId")
    public Long getTuesdayId()
    {
        return tuesdayId;
    }

    public void setTuesdayId(Long tuesdayId)
    {
        this.tuesdayId = tuesdayId;
    }

    @XmlElement(name = "tuesday")
    public Double getTuesday()
    {
        return tuesday;
    }

    public void setTuesday(Double tuesday)
    {
        this.tuesday = tuesday;
    }

    @XmlElement(name = "wednesdayId")
    public Long getWednesdayId()
    {
        return wednesdayId;
    }

    public void setWednesdayId(Long wednesdayId)
    {
        this.wednesdayId = wednesdayId;
    }

    @XmlElement(name = "wednesday")
    public Double getWednesday()
    {
        return wednesday;
    }

    public void setWednesday(Double wednesday)
    {
        this.wednesday = wednesday;
    }

    @XmlElement(name = "thursdayId")
    public Long getThursdayId()
    {
        return thursdayId;
    }

    public void setThursdayId(Long thursdayId)
    {
        this.thursdayId = thursdayId;
    }

    @XmlElement(name = "thursday")
    public Double getThursday()
    {
        return thursday;
    }

    public void setThursday(Double thursday)
    {
        this.thursday = thursday;
    }

    @XmlElement(name = "fridayId")
    public Long getFridayId()
    {
        return fridayId;
    }

    public void setFridayId(Long fridayId)
    {
        this.fridayId = fridayId;
    }

    @XmlElement(name = "friday")
    public Double getFriday()
    {
        return friday;
    }

    public void setFriday(Double friday)
    {
        this.friday = friday;
    }

    @XmlElement(name = "saturdayId")
    public Long getSaturdayId()
    {
        return saturdayId;
    }

    public void setSaturdayId(Long saturdayId)
    {
        this.saturdayId = saturdayId;
    }

    @XmlElement(name = "saturday")
    public Double getSaturday()
    {
        return saturday;
    }

    public void setSaturday(Double saturday)
    {
        this.saturday = saturday;
    }

}

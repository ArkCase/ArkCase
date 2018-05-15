package com.armedia.acm.plugins.complaint.model.complaint;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.form.config.xml.ParticipantItem;
import com.armedia.acm.form.config.xml.PersonItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.xml.GeneralPostalAddress;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name = "form_" + FrevvoFormName.COMPLAINT, namespace = FrevvoFormNamespace.COMPLAINT_NAMESPACE)
public class ComplaintForm
{

    private Long complaintId;
    private String complaintNumber;
    private String complaintTitle;
    private String category;
    private List<String> categories;
    private String complaintDescription;
    private String priority;
    private List<String> priorities;
    private Date date;
    private String complaintTag;
    private String frequency;
    private List<String> frequencies;
    private PostalAddress location;
    private Long initiatorId;
    private String initiatorFullName;
    private String initiatorType;
    private List<PersonItem> people;
    private String cmisFolderId;
    private List<ParticipantItem> participants;
    private List<String> participantsTypeOptions;
    private Map<String, String> participantsPrivilegeTypes;
    private OwningGroupItem owningGroup;
    private List<String> owningGroupOptions;

    public Long getComplaintId()
    {
        return complaintId;
    }

    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
    }

    public String getComplaintNumber()
    {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber)
    {
        this.complaintNumber = complaintNumber;
    }

    public String getComplaintTitle()
    {
        return complaintTitle;
    }

    public void setComplaintTitle(String complaintTitle)
    {
        this.complaintTitle = complaintTitle;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    @XmlTransient
    public List<String> getCategories()
    {
        return categories;
    }

    public void setCategories(List<String> categories)
    {
        this.categories = categories;
    }

    public String getComplaintDescription()
    {
        return complaintDescription;
    }

    public void setComplaintDescription(String complaintDescription)
    {
        this.complaintDescription = complaintDescription;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    @XmlTransient
    public List<String> getPriorities()
    {
        return priorities;
    }

    public void setPriorities(List<String> priorities)
    {
        this.priorities = priorities;
    }

    @XmlElement(name = "incidentDate")
    @XmlJavaTypeAdapter(value = DateFrevvoAdapter.class)
    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getComplaintTag()
    {
        return complaintTag;
    }

    public void setComplaintTag(String complaintTag)
    {
        this.complaintTag = complaintTag;
    }

    public String getFrequency()
    {
        return frequency;
    }

    public void setFrequency(String frequency)
    {
        this.frequency = frequency;
    }

    @XmlTransient
    public List<String> getFrequencies()
    {
        return frequencies;
    }

    public void setFrequencies(List<String> frequencies)
    {
        this.frequencies = frequencies;
    }

    @XmlElement(name = "location", type = GeneralPostalAddress.class)
    public PostalAddress getLocation()
    {
        return location;
    }

    public void setLocation(PostalAddress location)
    {
        this.location = location;
    }

    @XmlElement(name = "initiatorId")
    public Long getInitiatorId()
    {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId)
    {
        this.initiatorId = initiatorId;
    }

    @XmlElement(name = "initiatorFullName")
    public String getInitiatorFullName()
    {
        return initiatorFullName;
    }

    public void setInitiatorFullName(String initiatorFullName)
    {
        this.initiatorFullName = initiatorFullName;
    }

    @XmlElement(name = "initiatorType")
    public String getInitiatorType()
    {
        return initiatorType;
    }

    public void setInitiatorType(String initiatorType)
    {
        this.initiatorType = initiatorType;
    }

    @XmlElement(name = "peopleItem", type = PersonItem.class)
    public List<PersonItem> getPeople()
    {
        return people;
    }

    public void setPeople(List<PersonItem> people)
    {
        this.people = people;
    }

    @XmlTransient
    public String getCmisFolderId()
    {
        return cmisFolderId;
    }

    public void setCmisFolderId(String cmisFolderId)
    {
        this.cmisFolderId = cmisFolderId;
    }

    @XmlElement(name = "participantsItem", type = ParticipantItem.class)
    public List<ParticipantItem> getParticipants()
    {
        return participants;
    }

    public void setParticipants(List<ParticipantItem> participants)
    {
        this.participants = participants;
    }

    @XmlTransient
    public List<String> getParticipantsTypeOptions()
    {
        return participantsTypeOptions;
    }

    public void setParticipantsTypeOptions(List<String> participantsTypeOptions)
    {
        this.participantsTypeOptions = participantsTypeOptions;
    }

    @XmlTransient
    public Map<String, String> getParticipantsPrivilegeTypes()
    {
        return participantsPrivilegeTypes;
    }

    public void setParticipantsPrivilegeTypes(Map<String, String> participantsPrivilegeTypes)
    {
        this.participantsPrivilegeTypes = participantsPrivilegeTypes;
    }

    @XmlElement(name = "owningGroup")
    public OwningGroupItem getOwningGroup()
    {
        return owningGroup;
    }

    public void setOwningGroup(OwningGroupItem owningGroup)
    {
        this.owningGroup = owningGroup;
    }

    @XmlTransient
    public List<String> getOwningGroupOptions()
    {
        return owningGroupOptions;
    }

    public void setOwningGroupOptions(List<String> owningGroupOptions)
    {
        this.owningGroupOptions = owningGroupOptions;
    }

}

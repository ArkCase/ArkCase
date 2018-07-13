/**
 * 
 */
package com.armedia.acm.form.cost.model;

/*-
 * #%L
 * ACM Forms: Cost
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

import com.armedia.acm.form.config.xml.ApproverItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.frevvo.model.Details;
import com.armedia.acm.frevvo.model.FrevvoForm;
import com.armedia.acm.frevvo.model.Options;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name = "form_" + FrevvoFormName.COSTSHEET, namespace = FrevvoFormNamespace.COSTSHEET_NAMESPACE)
public class CostForm extends FrevvoForm
{

    private Long id;
    private String user;
    private List<String> userOptions;
    private Long objectId;
    private Map<String, Options> codeOptions;
    private Map<String, Map<String, Details>> codeDetails;
    private String objectType;
    private List<String> objectTypeOptions;
    private String objectNumber;
    private String objectTitle;
    private List<CostItem> items;
    private String status;
    private List<String> statusOptions;
    private String details;
    private List<ApproverItem> approvers;
    private List<String> balanceTable;

    @XmlElement(name = "id")
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @XmlElement(name = "user")
    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    @XmlTransient
    public List<String> getUserOptions()
    {
        return userOptions;
    }

    public void setUserOptions(List<String> userOptions)
    {
        this.userOptions = userOptions;
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

    @XmlTransient
    public Map<String, Options> getCodeOptions()
    {
        return codeOptions;
    }

    public void setCodeOptions(Map<String, Options> codeOptions)
    {
        this.codeOptions = codeOptions;
    }

    @XmlTransient
    public Map<String, Map<String, Details>> getCodeDetails()
    {
        return codeDetails;
    }

    public void setCodeDetails(Map<String, Map<String, Details>> codeDetails)
    {
        this.codeDetails = codeDetails;
    }

    @XmlElement(name = "type")
    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    @XmlTransient
    public List<String> getObjectTypeOptions()
    {
        return objectTypeOptions;
    }

    public void setObjectTypeOptions(List<String> objectTypeOptions)
    {
        this.objectTypeOptions = objectTypeOptions;
    }

    @XmlElement(name = "objectNumber")
    public String getObjectNumber()
    {
        return objectNumber;
    }

    public void setObjectNumber(String objectNumber)
    {
        this.objectNumber = objectNumber;
    }

    @XmlElement(name = "objectTitle")
    public String getObjectTitle()
    {
        return objectTitle;
    }

    public void setObjectTitle(String objectTitle)
    {
        this.objectTitle = objectTitle;
    }

    @XmlElement(name = "costTableItem")
    public List<CostItem> getItems()
    {
        return items;
    }

    public void setItems(List<CostItem> items)
    {
        this.items = items;
    }

    @XmlElement(name = "status")
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @XmlTransient
    public List<String> getStatusOptions()
    {
        return statusOptions;
    }

    public void setStatusOptions(List<String> statusOptions)
    {
        this.statusOptions = statusOptions;
    }

    @XmlElement(name = "details")
    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    @XmlElement(name = "approverItem")
    public List<ApproverItem> getApprovers()
    {
        return approvers;
    }

    public void setApprovers(List<ApproverItem> approvers)
    {
        this.approvers = approvers;
    }

    @XmlElement(name = "balanceTableItem")
    public List<String> getBalanceTable()
    {
        return balanceTable;
    }

    public void setBalanceTable(List<String> balanceTable)
    {
        this.balanceTable = balanceTable;
    }
}

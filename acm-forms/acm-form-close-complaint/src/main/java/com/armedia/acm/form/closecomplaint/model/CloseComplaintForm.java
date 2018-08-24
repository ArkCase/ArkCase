/**
 * 
 */
package com.armedia.acm.form.closecomplaint.model;

/*-
 * #%L
 * ACM Forms: Close Complaint
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

import com.armedia.acm.form.config.Item;
import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.form.config.xml.ApproverItem;
import com.armedia.acm.form.config.xml.ComplaintResolveInformation;

import javax.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class CloseComplaintForm
{

    private ResolveInformation information;
    private ReferExternal referExternal;
    private ExistingCase existingCase;
    private List<Item> approvers;
    private String description;

    @XmlElement(name = "information", type = ComplaintResolveInformation.class)
    public ResolveInformation getInformation()
    {
        return information;
    }

    public void setInformation(ResolveInformation information)
    {
        this.information = information;
    }

    public ReferExternal getReferExternal()
    {
        return referExternal;
    }

    public void setReferExternal(ReferExternal referExternal)
    {
        this.referExternal = referExternal;
    }

    public ExistingCase getExistingCase()
    {
        return existingCase;
    }

    public void setExistingCase(ExistingCase existingCase)
    {
        this.existingCase = existingCase;
    }

    @XmlElement(name = "approverItem", type = ApproverItem.class)
    public List<Item> getApprovers()
    {
        return approvers;
    }

    public void setApprovers(List<Item> approvers)
    {
        this.approvers = approvers;
    }

    @XmlElement(name = "description")
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

}

package com.armedia.acm.forms.roi.model;

/*-
 * #%L
 * ACM Forms: Report of Investigation
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
import com.armedia.acm.form.config.xml.ApproverItem;

import javax.xml.bind.annotation.XmlElement;

import java.util.List;

public class ROIForm
{

    private ReportInformation reportInformation;
    private ReportDetails reportDetails;
    private List<Item> approvers;

    /**
     * @return the reportInformation
     */
    public ReportInformation getReportInformation()
    {
        return reportInformation;
    }

    /**
     * @param reportInformation
     *            the reportInformation to set
     */
    public void setReportInformation(ReportInformation reportInformation)
    {
        this.reportInformation = reportInformation;
    }

    /**
     * @return the reportDetails
     */
    public ReportDetails getReportDetails()
    {
        return reportDetails;
    }

    /**
     * @param reportDetails
     *            the reportDetails to set
     */
    public void setReportDetails(ReportDetails reportDetails)
    {
        this.reportDetails = reportDetails;
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
}

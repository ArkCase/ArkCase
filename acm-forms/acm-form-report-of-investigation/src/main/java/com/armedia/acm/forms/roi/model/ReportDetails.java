/**
 * 
 */
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

/**
 * @author riste.tutureski
 *
 */
public class ReportDetails
{

    private String type;
    private Long complaintId;
    private String complaintNumber;
    private String complaintTitle;
    private String complaintPriority;
    private Long caseId;
    private String caseNumber;
    private String caseTitle;
    private String summary;

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the complaintId
     */
    public Long getComplaintId()
    {
        return complaintId;
    }

    /**
     * @param complaintId
     *            the complaintId to set
     */
    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
    }

    /**
     * @return the complaintNumber
     */
    public String getComplaintNumber()
    {
        return complaintNumber;
    }

    /**
     * @param complaintNumber
     *            the complaintNumber to set
     */
    public void setComplaintNumber(String complaintNumber)
    {
        this.complaintNumber = complaintNumber;
    }

    /**
     * @return the complaintTitle
     */
    public String getComplaintTitle()
    {
        return complaintTitle;
    }

    /**
     * @param complaintTitle
     *            the complaintTitle to set
     */
    public void setComplaintTitle(String complaintTitle)
    {
        this.complaintTitle = complaintTitle;
    }

    /**
     * @return the complaintPriority
     */
    public String getComplaintPriority()
    {
        return complaintPriority;
    }

    /**
     * @param complaintPriority
     *            the complaintPriority to set
     */
    public void setComplaintPriority(String complaintPriority)
    {
        this.complaintPriority = complaintPriority;
    }

    /**
     * @return the caseId
     */
    public Long getCaseId()
    {
        return caseId;
    }

    /**
     * @param caseId
     *            the caseId to set
     */
    public void setCaseId(Long caseId)
    {
        this.caseId = caseId;
    }

    /**
     * @return the caseNumber
     */
    public String getCaseNumber()
    {
        return caseNumber;
    }

    /**
     * @param caseNumber
     *            the caseNumber to set
     */
    public void setCaseNumber(String caseNumber)
    {
        this.caseNumber = caseNumber;
    }

    /**
     * @return the caseTitle
     */
    public String getCaseTitle()
    {
        return caseTitle;
    }

    /**
     * @param caseTitle
     *            the caseTitle to set
     */
    public void setCaseTitle(String caseTitle)
    {
        this.caseTitle = caseTitle;
    }

    /**
     * @return the summary
     */
    public String getSummary()
    {
        return summary;
    }

    /**
     * @param summary
     *            the summary to set
     */
    public void setSummary(String summary)
    {
        this.summary = summary;
    }

}

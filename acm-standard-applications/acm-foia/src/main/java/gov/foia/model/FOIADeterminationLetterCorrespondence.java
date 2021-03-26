package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.plugins.person.model.PersonAssociation;

import java.time.LocalDateTime;
import java.util.List;

public class FOIADeterminationLetterCorrespondence
{

    private List<PersonAssociation> personAssociations;

    private String exemptionCodeSummary;
    private String exemptionCodesAndDescription;
    private String assigneeName;
    private String assigneeTitle;
    private String assigneeEmail;
    private double invoiceAmount;
    private String requestCaseNumber;
    private LocalDateTime receivedDate;
    private LocalDateTime perfectedDate;

    public List<PersonAssociation> getPersonAssociations()
    {
        return personAssociations;
    }

    public void setPersonAssociations(List<PersonAssociation> personAssociations)
    {
        this.personAssociations = personAssociations;
    }

    public String getExemptionCodeSummary()
    {
        return exemptionCodeSummary;
    }

    public void setExemptionCodeSummary(String exemptionCodeSummary)
    {
        this.exemptionCodeSummary = exemptionCodeSummary;
    }

    public String getExemptionCodesAndDescription()
    {
        return exemptionCodesAndDescription;
    }

    public void setExemptionCodesAndDescription(String exemptionCodesAndDescription)
    {
        this.exemptionCodesAndDescription = exemptionCodesAndDescription;
    }

    public String getAssigneeName()
    {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName)
    {
        this.assigneeName = assigneeName;
    }

    public String getAssigneeTitle()
    {
        return assigneeTitle;
    }

    public void setAssigneeTitle(String assigneeTitle)
    {
        this.assigneeTitle = assigneeTitle;
    }

    public String getAssigneeEmail()
    {
        return assigneeEmail;
    }

    public void setAssigneeEmail(String assigneeEmail)
    {
        this.assigneeEmail = assigneeEmail;
    }

    public double getInvoiceAmount()
    {
        return invoiceAmount;
    }

    public void setInvoiceAmount(double invoiceAmount)
    {
        this.invoiceAmount = invoiceAmount;
    }

    public String getRequestCaseNumber()
    {
        return requestCaseNumber;
    }

    public void setRequestCaseNumber(String requestCaseNumber)
    {
        this.requestCaseNumber = requestCaseNumber;
    }

    public LocalDateTime getReceivedDate()
    {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate)
    {
        this.receivedDate = receivedDate;
    }

    public LocalDateTime getPerfectedDate()
    {
        return perfectedDate;
    }

    public void setPerfectedDate(LocalDateTime perfectedDate)
    {
        this.perfectedDate = perfectedDate;
    }
}

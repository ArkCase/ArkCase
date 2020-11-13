package gov.foia.model;

import com.armedia.acm.plugins.person.model.PersonAssociation;

import java.time.LocalDateTime;
import java.util.List;

public class FOIADeterminationLetterCorrespondence
{

    private List<PersonAssociation> personAssociations;

    private String exemptionCodeSummary;
    private String exemptionCodesAndDescription;
    private String requestAssigneeName;
    private String requestAssigneeTitle;
    private String requestAssigneeEmail;
    private double invoiceAmount;
    private String caseNumber;
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

    public String getRequestAssigneeName()
    {
        return requestAssigneeName;
    }

    public void setRequestAssigneeName(String requestAssigneeName)
    {
        this.requestAssigneeName = requestAssigneeName;
    }

    public String getRequestAssigneeTitle()
    {
        return requestAssigneeTitle;
    }

    public void setRequestAssigneeTitle(String requestAssigneeTitle)
    {
        this.requestAssigneeTitle = requestAssigneeTitle;
    }

    public String getRequestAssigneeEmail()
    {
        return requestAssigneeEmail;
    }

    public void setRequestAssigneeEmail(String requestAssigneeEmail)
    {
        this.requestAssigneeEmail = requestAssigneeEmail;
    }

    public double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(double invoiceAmount)
    {
        this.invoiceAmount = invoiceAmount;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber)
    {
        this.caseNumber = caseNumber;
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

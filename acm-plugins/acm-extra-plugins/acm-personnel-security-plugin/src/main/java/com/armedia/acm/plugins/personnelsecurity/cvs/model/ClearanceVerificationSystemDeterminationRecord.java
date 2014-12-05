package com.armedia.acm.plugins.personnelsecurity.cvs.model;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents OPM's record format for reporting clearance determinations; this flat file format is defined in
 * the fin11-02.pdf file.  This record format is used for reporting clearance events including: interim clearance
 * granted; clearance denied; and final clearance granted. After the clearance is in place, this format is also
 * used to report status changes, including: Revoked, Suspended, Revalidated, Administratively Withdrawn.
 */
public class ClearanceVerificationSystemDeterminationRecord
{
    /** position 1 - 9 */
    private String ssn;

    /** position 10-13 */
    private String grantingAuthoritySoi;

    /** position 14-14, C, L, Q, S, T */
    private String clearanceLevel;

    /** position 15-15, "C" indicates this is a determination record (vs a polygraph) */
    private String recordType = "C";

    /** position 16-23, YYYYMMDD */
    private Date grantedOrDeniedDate = new Date();

    /** position 24-43, max 20 chars */
    private String subjectLastName;

    /** position 44-51, YYYYMMDD */
    private Date subjectDateOfBirth;

    /** position 52-53, required if born in United States */
    private String subjectUSPlaceOfBirth;

    /** position 54-73, required if not born in US, max 20 chars */
    private String subjectNonUSPlaceOfBirth;

    /** position 74-74, Y, N, or blank */
    private String clearanceException;

    /** position 75-75, D=denied, I=interim, F=final */
    private String clearanceType;

    /** position 76-81, SCI, SAP, SAPSCI, N, blank */
    private String accessReported;

    /** position 82-82, A=E.O.12968, B=DCID, C=ICD704, blank */
    private String standardUsedToGrantClearance;

    /** position 83-83, Y, blank.  Must be Y if clearanceException is Y. */
    private String contactGrantingAuthority;

    /** position 84-84, Y, N, blank */
    private String nonUSImmediateFamilyMembers;

    /** position 85-85, C, L, Q, S, T, blank */
    private String eligibilityLevel;

    /** position 86-93, YYYYMMDD, required if eligibilityLevel is non-blank */
    private Date eligibilityDate;

    /** position 94-97, B = Bond Amendments, C = Conditions, D = Deviations, W = Waivers */
    private String exceptionType;

    /** position 98-105, YYYYMMDD, optional */
    private Date adjudicationStartDate;

    /** position 106-113, YYYYMMDD, required if status is revoked, suspended, or withdrawn */
    private Date statusDate;

    // note, must leave blanks for characters 114-167

    /** position 168-169.  03 = current version of record format */
    private String recordFormatVersion = "03";

    /** position 170-170.  Must be D for new records for Denied clearances.  Must be N
      * for new records for Interim or Final clearances.  Must be R (revoked), S (suspended), V (revalidated),
      * or "W" for status updates records (CVS already has this record).  For all other updates (family member info,
      * special access, exceptions, etc) must be U.
    */
    private String statusUpdate;

    public ClearanceVerificationSystemDeterminationRecord(
            String subjectLastName,
            Date subjectDateOfBirth,
            boolean clearanceGranted,
            Date adjudicationFinishedDate)
    {

        this.subjectDateOfBirth = subjectDateOfBirth;

        this.statusUpdate = clearanceGranted ? "N" : "D";

        this.clearanceType = clearanceGranted ? "F" : "D";

        this.grantedOrDeniedDate = adjudicationFinishedDate;

        this.subjectLastName = subjectLastName;

    }

    @Override
    public String toString()
    {

        if ( getClearanceType() == null )
        {
            throw new IllegalStateException("Clearance Type must be D, I, or F");
        }

        SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");

        return StringUtils.defaultIfEmpty(getSsn(), StringUtils.repeat("0", 9)) +
                StringUtils.rightPad(StringUtils.defaultIfEmpty(getGrantingAuthoritySoi(), "ABC"), 4).substring(0, 4) +
                StringUtils.defaultIfEmpty(getClearanceLevel(), "S") +
                getRecordType() +
                yyyymmdd.format(getGrantedOrDeniedDate()) +
                StringUtils.rightPad(StringUtils.defaultIfEmpty(getSubjectLastName(), StringUtils.repeat(" ", 20)), 20).substring(0, 20) +
                ( getSubjectDateOfBirth() == null ? StringUtils.repeat(" ", 8) : yyyymmdd.format(getSubjectDateOfBirth()) ) +
                StringUtils.defaultIfEmpty(getSubjectUSPlaceOfBirth(), StringUtils.repeat(" ", 2)).substring(0, 2) +
                StringUtils.rightPad(StringUtils.defaultIfEmpty(getSubjectNonUSPlaceOfBirth(), StringUtils.repeat(" ", 20)), 20).substring(0, 20) +
                StringUtils.defaultIfEmpty(getClearanceException(), " ") +
                getClearanceType() +
                StringUtils.rightPad(StringUtils.defaultIfEmpty(getAccessReported(), StringUtils.repeat(" ", 6)), 6).substring(0, 6) +
                StringUtils.defaultIfEmpty(getStandardUsedToGrantClearance(), " ") +
                StringUtils.defaultIfEmpty(getContactGrantingAuthority(), " ") +
                StringUtils.defaultIfEmpty(getNonUSImmediateFamilyMembers(), " ") +
                StringUtils.defaultIfEmpty(getEligibilityLevel(), " ") +
                ( getEligibilityDate() == null ? StringUtils.repeat(" ", 8) : yyyymmdd.format(getEligibilityDate()) ) +
                StringUtils.rightPad(StringUtils.defaultIfEmpty(getExceptionType(), StringUtils.repeat(" ", 4)), 4).substring(0, 4) +
                ( getAdjudicationStartDate() == null ? StringUtils.repeat(" ", 8) : yyyymmdd.format(getAdjudicationStartDate()) ) +
                ( getStatusDate() == null ? StringUtils.repeat(" ", 8) : yyyymmdd.format(getStatusDate()) ) +
                StringUtils.repeat(" ", 54) +
                getRecordFormatVersion() +
                StringUtils.defaultIfEmpty(getStatusUpdate(), " ");


    }


    public String getSsn()
    {
        return ssn;
    }

    public void setSsn(String ssn)
    {
        this.ssn = ssn;
    }

    public String getGrantingAuthoritySoi()
    {
        return grantingAuthoritySoi;
    }

    public void setGrantingAuthoritySoi(String grantingAuthoritySoi)
    {
        this.grantingAuthoritySoi = grantingAuthoritySoi;
    }

    public String getClearanceLevel()
    {
        return clearanceLevel;
    }

    public void setClearanceLevel(String clearanceLevel)
    {
        this.clearanceLevel = clearanceLevel;
    }

    public String getRecordType()
    {
        return recordType;
    }

    public void setRecordType(String recordType)
    {
        this.recordType = recordType;
    }

    public Date getGrantedOrDeniedDate()
    {
        return grantedOrDeniedDate;
    }

    public void setGrantedOrDeniedDate(Date grantedOrDeniedDate)
    {
        this.grantedOrDeniedDate = grantedOrDeniedDate;
    }

    public String getSubjectLastName()
    {
        return subjectLastName;
    }

    public void setSubjectLastName(String subjectLastName)
    {
        this.subjectLastName = subjectLastName;
    }

    public Date getSubjectDateOfBirth()
    {
        return subjectDateOfBirth;
    }

    public void setSubjectDateOfBirth(Date subjectDateOfBirth)
    {
        this.subjectDateOfBirth = subjectDateOfBirth;
    }

    public String getSubjectUSPlaceOfBirth()
    {
        return subjectUSPlaceOfBirth;
    }

    public void setSubjectUSPlaceOfBirth(String subjectUSPlaceOfBirth)
    {
        this.subjectUSPlaceOfBirth = subjectUSPlaceOfBirth;
    }

    public String getSubjectNonUSPlaceOfBirth()
    {
        return subjectNonUSPlaceOfBirth;
    }

    public void setSubjectNonUSPlaceOfBirth(String subjectNonUSPlaceOfBirth)
    {
        this.subjectNonUSPlaceOfBirth = subjectNonUSPlaceOfBirth;
    }

    public String getClearanceException()
    {
        return clearanceException;
    }

    public void setClearanceException(String clearanceException)
    {
        this.clearanceException = clearanceException;
    }

    public String getClearanceType()
    {
        return clearanceType;
    }

    public void setClearanceType(String clearanceType)
    {
        this.clearanceType = clearanceType;
    }

    public String getAccessReported()
    {
        return accessReported;
    }

    public void setAccessReported(String accessReported)
    {
        this.accessReported = accessReported;
    }

    public String getStandardUsedToGrantClearance()
    {
        return standardUsedToGrantClearance;
    }

    public void setStandardUsedToGrantClearance(String standardUsedToGrantClearance)
    {
        this.standardUsedToGrantClearance = standardUsedToGrantClearance;
    }

    public String getContactGrantingAuthority()
    {
        return contactGrantingAuthority;
    }

    public void setContactGrantingAuthority(String contactGrantingAuthority)
    {
        this.contactGrantingAuthority = contactGrantingAuthority;
    }

    public String getNonUSImmediateFamilyMembers()
    {
        return nonUSImmediateFamilyMembers;
    }

    public void setNonUSImmediateFamilyMembers(String nonUSImmediateFamilyMembers)
    {
        this.nonUSImmediateFamilyMembers = nonUSImmediateFamilyMembers;
    }

    public String getEligibilityLevel()
    {
        return eligibilityLevel;
    }

    public void setEligibilityLevel(String eligibilityLevel)
    {
        this.eligibilityLevel = eligibilityLevel;
    }

    public Date getEligibilityDate()
    {
        return eligibilityDate;
    }

    public void setEligibilityDate(Date eligibilityDate)
    {
        this.eligibilityDate = eligibilityDate;
    }

    public String getExceptionType()
    {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType)
    {
        this.exceptionType = exceptionType;
    }

    public Date getAdjudicationStartDate()
    {
        return adjudicationStartDate;
    }

    public void setAdjudicationStartDate(Date adjudicationStartDate)
    {
        this.adjudicationStartDate = adjudicationStartDate;
    }

    public Date getStatusDate()
    {
        return statusDate;
    }

    public void setStatusDate(Date statusDate)
    {
        this.statusDate = statusDate;
    }

    public String getRecordFormatVersion()
    {
        return recordFormatVersion;
    }

    public void setRecordFormatVersion(String recordFormatVersion)
    {
        this.recordFormatVersion = recordFormatVersion;
    }

    public String getStatusUpdate()
    {
        return statusUpdate;
    }

    public void setStatusUpdate(String statusUpdate)
    {
        this.statusUpdate = statusUpdate;
    }
}

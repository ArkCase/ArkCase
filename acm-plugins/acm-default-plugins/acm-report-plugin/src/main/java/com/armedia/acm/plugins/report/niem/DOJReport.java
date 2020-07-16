package com.armedia.acm.plugins.report.niem;

public enum DOJReport
{
    OLDEST_PENDING_APPEALS("OPA", "foia:OldestPendingAppealSection"),
    OLDEST_PENDING_REQUESTS("OPR", "foia:OldestPendingAppealSection"),
    OLDEST_PENDING_CONSULTATIONS("OPC", "foia:OldestPendingAppealSection"),
    APPEAL_REQUEST_OTHER_REASON("CODR", "foia:RequestDenialOtherReasonSection"),
    APPEAL_DENIAL_OTHER_REASON("ADOR", "foia:AppealDenialOtherReasonSection"),
    SIMPLE_RESPONSE_TIME_INCREMENTS("SRT", "foia:SimpleResponseTimeIncrementsSection"),
    COMPLEX_RESPONSE_TIME_INCREMENTS("CRT", "foia:ComplexResponseTimeIncrementsSection"),
    EXPEDITED_RESPONSE_TIME_INCREMENTS("ERT", "foia:ExpeditedResponseTimeIncrementsSection"),
    APPEAL_DISPOSITION("AD", "foia:AppealDispositionSection"),
    REQUEST_DISPOSITION("RD", "foia:RequestDispositionSection"),
    APPEAL_DISPOSITION_APPLIED_EXEMPTIONS("ADE", "foia:AppealDispositionAppliedExemptionsSection"),
    REQUEST_DISPOSITION_APPLIED_EXEMPTIONS("RDE", "foia:RequestDispositionAppliedExemptionsSection"),
    APPEAL_PROCESSED_COMPARISON("APC", "foia:ProcessedAppealComparisonSection"),
    REQUEST_PROCESSED_COMPARISON("RPC", "foia:ProcessedRequestComparisonSection"),
    PROCESSED_APPEALS("PA", "foia:ProcessedAppealSection"),
    PROCESSED_REQUESTS("PS", "foia:ProcessedRequestSection"),
    PROCESSED_CONSULTATIONS("PCN", "foia:ProcessedConsultationSection"),
    APPEAL_NON_EXEMPTION_DENIAL("ANE", "foia:AppealNonExemptionDenialSection"),
    APPEAL_RESPONSE_TIME("ART", "foia:AppealResponseTimeSection"),
    EXPEDITED_PROCESSING("EP", "foia:ExpeditedProcessingSection"),
    FEE_WAIVER("FW", "foia:FeeWaiverSection"),
    PERSONNEL_AND_COST("PC", "foia:PersonnelAndCostSection"),
    FEES_COLLECTED("FC", "foia:FeesCollectedSection"),
    SUBSECTION_USED("SU", "foia:SubsectionUsedSection"),
    SUBSECTION_POST("SP", "foia:SubsectionPostSection"),
    BACKLOG("BK", "foia:Backlog"),
    BACKLOG_REQUEST_COMPARISON("RBC", "foia:BackloggedRequestComparisonSection"),
    BACKLOG_APPEAL_COMPARISON("ABC", "foia:BackloggedAppealComparisonSection");

    private final String idPrefix;
    private final String sectionName;

    DOJReport(String idPrefix, String sectionName)
    {
        this.idPrefix = idPrefix;
        this.sectionName = sectionName;
    }

    public String getIdPrefix()
    {
        return idPrefix;
    }

    public String getSectionName()
    {
        return sectionName;
    }
}

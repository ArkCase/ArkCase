package com.armedia.acm.plugins.report.niem;

public enum DOJReport
{
    PROCESSED_REQUESTS("PS", "foia:ProcessedRequestSection"),
    REQUEST_DISPOSITION("RD", "foia:RequestDispositionSection"),
    REQUEST_DENIAL_OTHER_REASON("CODR", "foia:RequestDenialOtherReasonSection"),
    REQUEST_DISPOSITION_APPLIED_EXEMPTIONS("RDE", "foia:RequestDispositionAppliedExemptionsSection"),
    PROCESSED_APPEALS("PA", "foia:ProcessedAppealSection"),
    APPEAL_DISPOSITION("AD", "foia:AppealDispositionSection"),
    APPEAL_DISPOSITION_APPLIED_EXEMPTIONS("ADE", "foia:AppealDispositionAppliedExemptionsSection"),
    APPEAL_NON_EXEMPTION_DENIAL("ANE", "foia:AppealNonExemptionDenialSection"),
    APPEAL_DENIAL_OTHER_REASON("ADOR", "foia:AppealDenialOtherReasonSection"),
    APPEAL_RESPONSE_TIME("ART", "foia:AppealResponseTimeSection"),
    OLDEST_PENDING_APPEALS("OPA", "foia:OldestPendingAppealSection"),
    SIMPLE_RESPONSE_TIME_INCREMENTS("SRT", "foia:SimpleResponseTimeIncrementsSection"),
    COMPLEX_RESPONSE_TIME_INCREMENTS("CRT", "foia:ComplexResponseTimeIncrementsSection"),
    EXPEDITED_RESPONSE_TIME_INCREMENTS("ERT", "foia:ExpeditedResponseTimeIncrementsSection"),
    OLDEST_PENDING_REQUESTS("OPR", "foia:OldestPendingAppealSection"),
    EXPEDITED_PROCESSING("EP", "foia:ExpeditedProcessingSection"),
    FEE_WAIVER("FW", "foia:FeeWaiverSection"),
    PERSONNEL_AND_COST("PC", "foia:PersonnelAndCostSection"),
    FEES_COLLECTED("FC", "foia:FeesCollectedSection"),
    SUBSECTION_USED("SU", "foia:SubsectionUsedSection"),
    SUBSECTION_POST("SP", "foia:SubsectionPostSection"),
    BACKLOG("BK", "foia:Backlog"),
    PROCESSED_CONSULTATIONS("PCN", "foia:ProcessedConsultationSection"),
    OLDEST_PENDING_CONSULTATIONS("OPC", "foia:OldestPendingAppealSection"),
    REQUEST_PROCESSED_COMPARISON("RPC", "foia:ProcessedRequestComparisonSection"),
    BACKLOG_REQUEST_COMPARISON("RBC", "foia:BackloggedRequestComparisonSection"),
    APPEAL_PROCESSED_COMPARISON("APC", "foia:ProcessedAppealComparisonSection"),
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

package com.armedia.acm.plugins.report.niem;

/*-
 * #%L
 * ACM Default Plugin: report
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
    PROCESSED_PERFECTED_REQUESTS_RESPONSE_TIME("PRT", "foia:ProcessedResponseTimeSection"),
    INFORMATION_GRANTED_REQUESTS_RESPONSE_TIME("IGR", "foia:InformationGrantedResponseTimeSection"),
    // ALL_PENDING_PERFECTED_REQUESTS("PPR", "foia:PendingPerfectedRequestsSection"),
    SIMPLE_RESPONSE_TIME_INCREMENTS("SRT", "foia:SimpleResponseTimeIncrementsSAection"),
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

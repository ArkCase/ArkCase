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

import static java.util.stream.Collectors.groupingBy;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NiemExportService
{

    public static final String AGENCY_IDENTIFIER_COLUMN = "Agency / Component";
    public static final String LOWER_THAN_ONE_IDENTIFIER = "LT1";

    private NiemExportUtils niemExportUtils;
    private Map<String, String> componentMap = new HashMap<>();

    public void appendReportSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report) throws ParseException
    {
        switch (report)
        {
        case PROCESSED_REQUESTS:
            generateProcessedRequestSection(data, parent, agencyIdentifiers);
            break;
        case REQUEST_DISPOSITION:
            generateRequestDispositionSection(data, parent, agencyIdentifiers);
            break;
        case REQUEST_DENIAL_OTHER_REASON:
            generateRequestDenialOtherReasonSection(data, parent, agencyIdentifiers);
            break;
        case REQUEST_DISPOSITION_APPLIED_EXEMPTIONS:
            generateRequestDispositionAppliedExemptionsSection(data, parent, agencyIdentifiers);
            break;
        case PROCESSED_APPEALS:
            generateProcessedAppealsSection(data, parent, agencyIdentifiers);
            break;
        case APPEAL_DISPOSITION:
            generateAppealDispositionSection(data, parent, agencyIdentifiers);
            break;
        case APPEAL_DISPOSITION_APPLIED_EXEMPTIONS:
            generateAppealDispositionAppliedExemptionsSection(data, parent, agencyIdentifiers);
            break;
        case APPEAL_NON_EXEMPTION_DENIAL:
            generateAppealNonExemptionDenialSection(data, parent, agencyIdentifiers);
            break;
        case APPEAL_RESPONSE_TIME:
            generateAppealResponseTimeSection(data, parent, agencyIdentifiers);
            break;
        case OLDEST_PENDING_APPEALS:
            generateOldestPendingAppealsSection(data, parent, agencyIdentifiers);
            break;
        case PROCESSED_PERFECTED_REQUESTS_RESPONSE_TIME:
            generateProcessedRequestResponseTimeSection(data, parent, agencyIdentifiers);
            break;
        case INFORMATION_GRANTED_REQUESTS_RESPONSE_TIME:
            generateInformationGrantedResponseTimeSection(data, parent, agencyIdentifiers);
            break;
        case SIMPLE_RESPONSE_TIME_INCREMENTS:
            generateSimpleResponseTimeIncrementsSection(data, parent, agencyIdentifiers);
            break;
        case COMPLEX_RESPONSE_TIME_INCREMENTS:
            generateComplexResponseTimeIncrementsSection(data, parent, agencyIdentifiers);
            break;
        case EXPEDITED_RESPONSE_TIME_INCREMENTS:
            generateExpeditedResponseTimeIncrementsSection(data, parent, agencyIdentifiers);
            break;
        case OLDEST_PENDING_REQUESTS:
            generateOldestPendingRequestsSection(data, parent, agencyIdentifiers);
            break;
        case EXPEDITED_PROCESSING:
            generateExpeditedProcessingSection(data, parent, agencyIdentifiers);
            break;
        case FEE_WAIVER:
            generateFeeWaiverSection(data, parent, agencyIdentifiers);
            break;
        case PERSONNEL_AND_COST:
            generatePersonnelAndCostSection(data, parent, agencyIdentifiers);
            break;
        case FEES_COLLECTED:
            generateFeesCollectedSection(data, parent, agencyIdentifiers);
            break;
        case SUBSECTION_USED:
            generateSubsectionUsedSection(data, parent, agencyIdentifiers);
            break;
        case SUBSECTION_POST:
            generateSubsectionPostSection(data, parent, agencyIdentifiers);
            break;
        case BACKLOG:
            generateBacklogSection(data, parent, agencyIdentifiers);
            break;
        case PROCESSED_CONSULTATIONS:
            generateProcessedConsultationsSection(data, parent, agencyIdentifiers);
            break;
        case OLDEST_PENDING_CONSULTATIONS:
            generateOldestPendingConsultationsSection(data, parent, agencyIdentifiers);
            break;
        case REQUEST_PROCESSED_COMPARISON:
            generateProcessedRequestComparisonSection(data, parent, agencyIdentifiers);
            break;
        case BACKLOG_REQUEST_COMPARISON:
            generateBackloggedRequestComparisonSection(data, parent, agencyIdentifiers);
            break;
        case APPEAL_PROCESSED_COMPARISON:
            generateProcessedAppealsComparisonSection(data, parent, agencyIdentifiers);
            break;
        case BACKLOG_APPEAL_COMPARISON:
            generateBackloggedAppealsComparisonSection(data, parent, agencyIdentifiers);
            break;
        }
    }

    // TODO rethink organization parent
    public void appendOrganizationSection(Element parent, Map<String, String> agencyIdentifiers) throws ParseException
    {
        Element organizationElement = parent.getOwnerDocument().createElement("nc:Organization");
        organizationElement.setAttribute("s:id", "ORG0");

        addElement(organizationElement, "nc:OrganizationAbbreviationText", "EEOC");
        addElement(organizationElement, "nc:OrganizationName", "Equal Employment Opportunity Commission (EEOC)");

        for (Map.Entry<String, String> subUnit : agencyIdentifiers.entrySet())
        {
            appendSubUnit(organizationElement, subUnit.getKey(), subUnit.getValue());
        }

        parent.appendChild(organizationElement);
    }

    private void appendSubUnit(Element organizationElement, String subUnitName, String subUnitAbbrivation)
    {

        Element orgSubUnitElement = organizationElement.getOwnerDocument().createElement("nc:OrganizationSubUnit");
        orgSubUnitElement.setAttribute("s:id", subUnitAbbrivation);

        addElement(orgSubUnitElement, "nc:OrganizationAbbreviationText", subUnitName);
        addElement(orgSubUnitElement, "nc:OrganizationName", subUnitName);

        organizationElement.appendChild(orgSubUnitElement);
    }

    public void generateOldestPendingAppealsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateOldestPendingSection(data, parent, agencyIdentifiers, DOJReport.OLDEST_PENDING_APPEALS);
    }

    public void generateOldestPendingRequestsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateOldestPendingSection(data, parent, agencyIdentifiers, DOJReport.OLDEST_PENDING_REQUESTS);
    }

    public void generateOldestPendingConsultationsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateOldestPendingSection(data, parent, agencyIdentifiers, DOJReport.OLDEST_PENDING_CONSULTATIONS);
    }

    private void generateOldestPendingSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report) throws ParseException
    {
        int idSuffix = 1;

        Element oldestPendingSectionElement = parent.getOwnerDocument().createElement(report.getSectionName());

        parent.appendChild(oldestPendingSectionElement);

        Collection<List<Map<String, String>>> dataByComponents = groupByComponentAndFilter(data).values();

        // Look into erasing the id counter
        Map<String, String> associationData = new LinkedHashMap<>();

        List<String> agencies = data.stream().map(it -> it.get(AGENCY_IDENTIFIER_COLUMN)).distinct().collect(Collectors.toList());

        for (int index = 0; index < dataByComponents.size(); index++)
        {
            String id = report.getIdPrefix().concat(String.valueOf(index + 1));
            String agency = agencies.get(index);

            associationData.put(agency, id);
        }
        // Look into erasing the id counter

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            appendOldestPendingItems(oldestPendingSectionElement, componentData, id);

            appendProcessingAssociations(oldestPendingSectionElement,
                    "foia:OldestPendingItemsOrganizationAssociation", id, agency);
        }
    }

    private void appendOldestPendingItems(Element parent, List<Map<String, String>> componentData, String id) throws ParseException
    {
        Element oldestPendingItemsElement = parent.getOwnerDocument().createElement("foia:OldestPendingItems");
        oldestPendingItemsElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String dateOfReceipt = record.get("Date of Receipt");
            String daysPending = record.get("Number of Days Pending");

            String formattedDate = niemExportUtils.formatDateToNiemExpectedDate(dateOfReceipt);

            Element oldItem = parent.getOwnerDocument().createElement("foia:OldItem");

            addElement(oldItem, "foia:OldItemReceiptDate", formattedDate);
            addElement(oldItem, "foia:OldItemPendingDaysQuantity", daysPending);

            oldestPendingItemsElement.appendChild(oldItem);
        }
        parent.appendChild(oldestPendingItemsElement);
    }

    public void generateRequestDenialOtherReasonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateDenialOtherReasonSection(data, parent, agencyIdentifiers, DOJReport.REQUEST_DENIAL_OTHER_REASON);
    }

    public void generateAppealDenialOtherReasonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateDenialOtherReasonSection(data, parent, agencyIdentifiers, DOJReport.APPEAL_DENIAL_OTHER_REASON);
    }

    private void generateDenialOtherReasonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report) throws ParseException
    {
        Element denialOtherReasonSectionElement = parent.getOwnerDocument().createElement(report.getSectionName());

        String descriptionColumn = "Description of \"Other\" Reasons for Denial on Appeal from Chart C(2)";
        String quantityColumn = "Number of Times \"Other\" Reason Was Relied Upon";

        List<Map<String, String>> rearrangedData = rearrangeMultiLineData(data,
                descriptionColumn, quantityColumn);
        List<Map<String, String>> filteredData = getDataWithComponentReferences(rearrangedData, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendOtherDenialReasonItem(denialOtherReasonSectionElement, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(denialOtherReasonSectionElement,
                componentData, "foia:OtherDenialReasonOrganizationAssociation"));

        parent.appendChild(denialOtherReasonSectionElement);
    }

    private void appendOtherDenialReasonItem(Element parent, Map<String, String> record)
    {
        Element componentOtherDenialReason = parent.getOwnerDocument().createElement("foia:ComponentOtherDenialReason");
        componentOtherDenialReason.setAttribute("s:id", record.get("ComponentDataReference"));

        String total = record.remove("Total");

        for (Map.Entry<String, String> otherDenialReasonEntry : record.entrySet())
        {
            String otherReasonDescription = otherDenialReasonEntry.getKey();
            String numberOfTimesOtherReasonWasUsed = otherDenialReasonEntry.getValue();

            if (NumberUtils.isParsable(numberOfTimesOtherReasonWasUsed))
            {
                Element otherDenialReason = parent.getOwnerDocument().createElement("foia:OtherDenialReason");

                addElement(otherDenialReason, "foia:OtherDenialReasonDescriptionText", otherReasonDescription);
                addElement(otherDenialReason, "foia:OtherDenialReasonQuantity", numberOfTimesOtherReasonWasUsed);

                componentOtherDenialReason.appendChild(otherDenialReason);
            }
        }
        addElement(componentOtherDenialReason, "foia:ComponentOtherDenialReasonQuantity", total);

        parent.appendChild(componentOtherDenialReason);
    }

    private List<Map<String, String>> rearrangeMultiLineData(List<Map<String, String>> data, String descriptionColumn,
            String quantityColumn)
    {
        List<Map<String, String>> mainData = new ArrayList<>();

        Map<String, List<Map<String, String>>> stringListMap = groupByComponentAndFilter(data);
        Collection<List<Map<String, String>>> dataByComponents = stringListMap.values();
        for (List<Map<String, String>> componentData : dataByComponents)
        {
            Map<String, String> dataPair = new LinkedHashMap<>();

            int total = 0;

            dataPair.put(AGENCY_IDENTIFIER_COLUMN, componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN));

            for (Map<String, String> record : componentData)
            {
                record.remove(AGENCY_IDENTIFIER_COLUMN);
                String description = record.get(descriptionColumn);
                String quantity = record.get(quantityColumn);

                if (description != null && quantity != null)
                {
                    if (!description.isEmpty())
                    {
                        dataPair.put(description, quantity);
                    }
                    total += Integer.parseInt(quantity);
                }
            }
            dataPair.put("Total", String.valueOf(total));

            mainData.add(dataPair);
        }
        return mainData;
    }

    public void generateProcessedRequestResponseTimeSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.PROCESSED_PERFECTED_REQUESTS_RESPONSE_TIME;
        appendProcessedRequestResponseTimeSection(data, parent, agencyIdentifiers, report);
    }

    public void generateInformationGrantedResponseTimeSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.INFORMATION_GRANTED_REQUESTS_RESPONSE_TIME;
        appendProcessedRequestResponseTimeSection(data, parent, agencyIdentifiers, report);
    }

    private void appendProcessedRequestResponseTimeSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)
    {
        Element processedRequestResponseTimeElement = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendProcessedResponseTimeItem(processedRequestResponseTimeElement, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(processedRequestResponseTimeElement,
                componentData, "foia:ProcessedResponseTimeOrganizationAssociation"));

        parent.appendChild(processedRequestResponseTimeElement);
    }

    private void appendProcessedResponseTimeItem(Element parent, Map<String, String> record)
    {
        Element processedResponseTimeElement = parent.getOwnerDocument().createElement("foia:ProcessedResponseTime");
        processedResponseTimeElement.setAttribute("s:id", record.get("ComponentDataReference"));

        Element simpleResponseTimeElement = parent.getOwnerDocument().createElement("foia:SimpleResponseTime");
        appendResponseTimeValuesPerTrack(simpleResponseTimeElement, record, "simple");
        processedResponseTimeElement.appendChild(simpleResponseTimeElement);

        Element complexResponseTimeElement = parent.getOwnerDocument().createElement("foia:ComplexResponseTime");
        appendResponseTimeValuesPerTrack(complexResponseTimeElement, record, "complex");
        processedResponseTimeElement.appendChild(complexResponseTimeElement);

        Element expeditedResponseTimeElement = parent.getOwnerDocument().createElement("foia:ExpeditedResponseTime");
        appendResponseTimeValuesPerTrack(expeditedResponseTimeElement, record, "expedited");
        processedResponseTimeElement.appendChild(expeditedResponseTimeElement);

        parent.appendChild(processedResponseTimeElement);
    }

    private void appendResponseTimeValuesPerTrack(Element parent, Map<String, String> record, String track)
    {
        String medianNumberOfDays = record.get(track + "~Median Number of Days");
        String averageNumberOfDays = record.get(track + "~Average Number of Days");
        String lowestNumberOfDays = record.get(track + "~Lowest Number of Days");
        String highestNumberOfDays = record.get(track + "~Highest Number of Days");

        addResponseTimeElementIfValid(parent, medianNumberOfDays, "foia:ResponseTimeMedianDaysCode", "foia:ResponseTimeMedianDaysValue");
        addResponseTimeElementIfValid(parent, averageNumberOfDays, "foia:ResponseTimeAverageDaysCode", "foia:ResponseTimeAverageDaysValue");
        addResponseTimeElementIfValid(parent, lowestNumberOfDays, "foia:ResponseTimeLowestDaysCode", "foia:ResponseTimeLowestDaysValue");
        addResponseTimeElementIfValid(parent, highestNumberOfDays, "foia:ResponseTimeHighestDaysCode", "foia:ResponseTimeHighestDaysValue");
    }

    private void addResponseTimeElementIfValid(Element parent, String numberOfDays, String codeName, String valueName)
    {
        if (NumberUtils.isParsable(numberOfDays) && Double.parseDouble(numberOfDays) > 0
                && Double.parseDouble(numberOfDays) < 1)
        {
            addElement(parent, codeName, LOWER_THAN_ONE_IDENTIFIER);
        }
        else if (NumberUtils.isParsable(numberOfDays) && Double.parseDouble(numberOfDays) >= 1)
        {
            addElement(parent, valueName, numberOfDays);
        }
    }

    public void generateSimpleResponseTimeIncrementsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateResponseTimeIncrementsSection(data, parent, agencyIdentifiers, DOJReport.SIMPLE_RESPONSE_TIME_INCREMENTS);
    }

    public void generateComplexResponseTimeIncrementsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateResponseTimeIncrementsSection(data, parent, agencyIdentifiers, DOJReport.COMPLEX_RESPONSE_TIME_INCREMENTS);
    }

    public void generateExpeditedResponseTimeIncrementsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateResponseTimeIncrementsSection(data, parent, agencyIdentifiers, DOJReport.EXPEDITED_RESPONSE_TIME_INCREMENTS);
    }

    private void generateResponseTimeIncrementsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)
            throws ParseException
    {
        Element responseTimeIncrementsSectionElement = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendTimeIncrementItem(responseTimeIncrementsSectionElement, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(responseTimeIncrementsSectionElement,
                componentData, "foia:ResponseTimeIncrementsOrganizationAssociation"));

        parent.appendChild(responseTimeIncrementsSectionElement);
    }

    private void appendTimeIncrementItem(Element parent, Map<String, String> record)
    {
        Element componentResponseTimeIncrements = parent.getOwnerDocument().createElement("foia:ComponentResponseTimeIncrements");
        componentResponseTimeIncrements.setAttribute("s:id", record.get("ComponentDataReference"));

        int totalTimeIncrementQuantity = 0;

        for (Map.Entry<String, String> timeIncrementEntry : record.entrySet())
        {
            String timeIncrementCode = timeIncrementEntry.getKey();
            String timeIncrementQuantity = timeIncrementEntry.getValue();

            timeIncrementCode = timeIncrementCode.replace("Days", "").replace("<", "").trim();

            if (NumberUtils.isParsable(timeIncrementQuantity))
            {
                Element timeIncrementElement = parent.getOwnerDocument().createElement("foia:TimeIncrement");

                addElement(timeIncrementElement, "foia:TimeIncrementCode", timeIncrementCode);
                addElement(timeIncrementElement, "foia:TimeIncrementProcessedQuantity", timeIncrementQuantity);

                componentResponseTimeIncrements.appendChild(timeIncrementElement);

                totalTimeIncrementQuantity += Integer.parseInt(timeIncrementQuantity);
            }
        }
        addElement(componentResponseTimeIncrements, "foia:TimeIncrementTotalQuantity",
                String.valueOf(totalTimeIncrementQuantity));

        parent.appendChild(componentResponseTimeIncrements);
    }

    public void generateAppealDispositionSection(List<Map<String, String>> data, Element parent, Map<String, String> agencyIdentifiers)
            throws ParseException
    {
        DOJReport report = DOJReport.APPEAL_DISPOSITION;

        Element appealDispositionSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendAppealDispositionItem(appealDispositionSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(appealDispositionSection,
                componentData, "foia:AppealDispositionOrganizationAssociation"));

        parent.appendChild(appealDispositionSection);
    }

    private void appendAppealDispositionItem(Element parent, Map<String, String> record)
    {
        Element appealDispositionElement = parent.getOwnerDocument().createElement("foia:AppealDisposition");
        appealDispositionElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String affirmedAppealCountString = record.get("Number Affirmed on Appeal");
        String partialAppealCountString = record.get("Number Partially Affirmed & Partially Reversed/Remanded on Appeal");
        String reversedAppealCountString = record.get("Number Completely Reversed/Remanded on Appeal");
        String closedAppealForOtherReasonCountString = record.get("Number of Appeals Closed for Other Reasons");

        int totalCount = record.values().stream()
                .filter(NumberUtils::isParsable)
                .map(Integer::parseInt)
                .mapToInt(Integer::intValue)
                .sum();

        addElement(appealDispositionElement, "foia:AppealDispositionAffirmedQuantity", affirmedAppealCountString);
        addElement(appealDispositionElement, "foia:AppealDispositionPartialQuantity", partialAppealCountString);
        addElement(appealDispositionElement, "foia:AppealDispositionReversedQuantity", reversedAppealCountString);
        addElement(appealDispositionElement, "foia:AppealDispositionOtherQuantity",
                closedAppealForOtherReasonCountString);
        addElement(appealDispositionElement, "foia:AppealDispositionTotalQuantity", String.valueOf(totalCount));

        parent.appendChild(appealDispositionElement);
    }

    public void generateRequestDispositionSection(List<Map<String, String>> data, Element parent, Map<String, String> agencyIdentifiers)
            throws ParseException
    {
        DOJReport report = DOJReport.REQUEST_DISPOSITION;

        Element requestDispositionSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendRequestDispositionItem(requestDispositionSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(requestDispositionSection,
                componentData, "foia:RequestDispositionOrganizationAssociation"));

        parent.appendChild(requestDispositionSection);
    }

    private void appendRequestDispositionItem(Element parent, Map<String, String> record)
    {
        Element requestDispositionElement = parent.getOwnerDocument().createElement("foia:RequestDisposition");
        requestDispositionElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String fullGrant = record.get("Number of Full Grants");
        String partialGrant = record.get("Number of Partial Grants/Partial Denials");
        String fullDenial = record.get("Number of Full Denials Based on Exemptions");

        addElement(requestDispositionElement, "foia:RequestDispositionFullGrantQuantity", fullGrant);
        addElement(requestDispositionElement, "foia:RequestDispositionPartialGrantQuantity", partialGrant);
        addElement(requestDispositionElement, "foia:RequestDispositionFullExemptionDenialQuantity", fullDenial);

        String noRecords = record.get("No Records");
        String referred = record.get("All Records Referred to Another Component or Agency");
        String withdrawn = record.get("Request Withdrawn");
        String feeRelated = record.get("Fee-Related Reason");
        String notDescribed = record.get("Records not Reasonably Described");
        String improperRequests = record.get("Improper FOIA Request for Other Reason");
        String notAgencyRecord = record.get("Not Agency Record");
        String duplicateRequest = record.get("Duplicate Request");
        String other = record.get("Other");

        appendNonExemptionDenialElementIfValid(requestDispositionElement, "NoRecords", noRecords);
        appendNonExemptionDenialElementIfValid(requestDispositionElement, "Referred", referred);
        appendNonExemptionDenialElementIfValid(requestDispositionElement, "Withdrawn", withdrawn);
        appendNonExemptionDenialElementIfValid(requestDispositionElement, "FeeRelated", feeRelated);
        appendNonExemptionDenialElementIfValid(requestDispositionElement, "NotDescribed", notDescribed);
        appendNonExemptionDenialElementIfValid(requestDispositionElement, "ImproperRequest", improperRequests);
        appendNonExemptionDenialElementIfValid(requestDispositionElement, "NotAgency", notAgencyRecord);
        appendNonExemptionDenialElementIfValid(requestDispositionElement, "Duplicate", duplicateRequest);
        appendNonExemptionDenialElementIfValid(requestDispositionElement, "Other", other);

        int totalCount = record.values().stream()
                .filter(NumberUtils::isParsable)
                .map(Integer::parseInt)
                .mapToInt(Integer::intValue)
                .sum();

        addElement(requestDispositionElement, "foia:RequestDispositionTotalQuantity", String.valueOf(totalCount));

        parent.appendChild(requestDispositionElement);
    }

    public void generateAppealNonExemptionDenialSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.APPEAL_NON_EXEMPTION_DENIAL;

        Element appealNonExemptionSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendAppealNonExemptionDenialItem(appealNonExemptionSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(appealNonExemptionSection,
                componentData, "foia:AppealNonExemptionDenialOrganizationAssociation"));

        parent.appendChild(appealNonExemptionSection);
    }

    private void appendAppealNonExemptionDenialItem(Element parent, Map<String, String> record)
    {
        Element appealDispositionElement = parent.getOwnerDocument().createElement("foia:AppealNonExemptionDenial");
        appealDispositionElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String noRecords = record.get("No Records");
        String referred = record.get("Records Referred at Initial Request Level");
        String withdrawn = record.get("Request Withdrawn");
        String feeRelated = record.get("Fee-Related Reason");
        String notDescribed = record.get("Records not Reasonably Described");
        String improperRequests = record.get("Improper Request for Other Reasons");
        String notAgencyRecord = record.get("Not Agency Record");
        String duplicateRequest = record.get("Duplicate Request or Appeal");
        String litigation = record.get("Request in Litigation");
        String expeditedDenial = record.get("Appeal Based Solely on Denial of Request for Expedited Processing");
        String other = record.get("Other");

        appendNonExemptionDenialElementIfValid(appealDispositionElement, "NoRecords", noRecords);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "Referred", referred);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "Withdrawn", withdrawn);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "FeeRelated", feeRelated);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "NotDescribed", notDescribed);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "ImproperRequest", improperRequests);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "NotAgency", notAgencyRecord);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "Duplicate", duplicateRequest);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "InLitigation", litigation);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "ExpeditedDenial", expeditedDenial);
        appendNonExemptionDenialElementIfValid(appealDispositionElement, "Other", other);

        parent.appendChild(appealDispositionElement);
    }

    private void appendNonExemptionDenialElementIfValid(Element parentElement, String nonExemptionCode, String nonExemptionQuantity)
    {
        if (NumberUtils.isParsable(nonExemptionQuantity) && Integer.parseInt(nonExemptionQuantity) > 0)
        {
            appendNonExemptionDenialElement(parentElement, nonExemptionCode, nonExemptionQuantity);
        }
    }

    private void appendNonExemptionDenialElement(Element parentElement, String nonExemptionCode, String nonExemptionQuantity)
    {
        Element nonExemptionDenialElement = parentElement.getOwnerDocument().createElement("foia:NonExemptionDenial");

        addElement(nonExemptionDenialElement, "foia:NonExemptionDenialReasonCode", nonExemptionCode);
        addElement(nonExemptionDenialElement, "foia:NonExemptionDenialQuantity", nonExemptionQuantity);

        parentElement.appendChild(nonExemptionDenialElement);
    }

    public void generateAppealDispositionAppliedExemptionsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        appendDispositionAppliedExemptionsSection(data, parent, agencyIdentifiers, DOJReport.REQUEST_DISPOSITION_APPLIED_EXEMPTIONS);

    }

    public void generateRequestDispositionAppliedExemptionsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        appendDispositionAppliedExemptionsSection(data, parent, agencyIdentifiers, DOJReport.APPEAL_DISPOSITION_APPLIED_EXEMPTIONS);

    }

    private void appendDispositionAppliedExemptionsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)
            throws ParseException
    {
        Element dispositionAppliedExemptionsSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendAppealDispositionAppliedExemptionsItem(dispositionAppliedExemptionsSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(dispositionAppliedExemptionsSection,
                componentData, "foia:ComponentAppliedExemptionsOrganizationAssociation"));

        parent.appendChild(dispositionAppliedExemptionsSection);
    }

    private void appendAppealDispositionAppliedExemptionsItem(Element parent, Map<String, String> record)
    {
        Element componentAppliedExemptionsElement = parent.getOwnerDocument().createElement("foia:ComponentAppliedExemptions");
        componentAppliedExemptionsElement.setAttribute("s:id", record.get("ComponentDataReference"));

        for (Map.Entry<String, String> exemptionEntry : record.entrySet())
        {
            String exemptionCode = exemptionEntry.getKey();
            String exemptionQuantity = exemptionEntry.getValue();

            if (NumberUtils.isParsable(exemptionQuantity) && Integer.parseInt(exemptionQuantity) > 0)
            {
                Element appliedExemptionElement = parent.getOwnerDocument().createElement("foia:AppliedExemption");

                addElement(appliedExemptionElement, "foia:AppliedExemptionCode", exemptionCode);
                addElement(appliedExemptionElement, "foia:AppliedExemptionQuantity", exemptionQuantity);

                componentAppliedExemptionsElement.appendChild(appliedExemptionElement);
            }
        }

        parent.appendChild(componentAppliedExemptionsElement);
    }

    public void generateProcessedRequestComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateProcessedComparisonSection(data, parent, agencyIdentifiers, DOJReport.REQUEST_PROCESSED_COMPARISON);
    }

    public void generateProcessedAppealsComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateProcessedComparisonSection(data, parent, agencyIdentifiers, DOJReport.APPEAL_PROCESSED_COMPARISON);
    }

    private void generateProcessedComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)
            throws ParseException
    {
        Element processedComparisonSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendProcessingComparisonItem(processedComparisonSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(processedComparisonSection,
                componentData, "foia:ProcessingComparisonOrganizationAssociation"));

        parent.appendChild(processedComparisonSection);
    }

    private void appendProcessingComparisonItem(Element parent, Map<String, String> record)
    {
        Element processingComparisonElement = parent.getOwnerDocument().createElement("foia:ProcessingComparison");
        processingComparisonElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String itemsReceivedLastYear = record.get("Number Received During Fiscal Year from Last Year's Annual Report");
        String itemsReceivedCurrentYear = record.get("Number Received During Fiscal Year from Current Annual Report");
        String itemsProcessedLastYear = record.get("Number Processed During Fiscal Year from Last Year's Annual Report");
        String itemsProcessedCurrentYear = record
                .get("Number Processed During Fiscal Year from Current Annual Report");

        addElement(processingComparisonElement, "foia:ItemsReceivedLastYearQuantity", itemsReceivedLastYear);
        addElement(processingComparisonElement, "foia:ItemsReceivedCurrentYearQuantity", itemsReceivedCurrentYear);
        addElement(processingComparisonElement, "foia:ItemsProcessedLastYearQuantity", itemsProcessedLastYear);
        addElement(processingComparisonElement, "foia:ItemsProcessedCurrentYearQuantity",
                itemsProcessedCurrentYear);

        parent.appendChild(processingComparisonElement);
    }

    public void generateProcessedRequestSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateProcessedSection(data, parent, agencyIdentifiers, DOJReport.PROCESSED_REQUESTS);
    }

    public void generateProcessedAppealsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateProcessedSection(data, parent, agencyIdentifiers, DOJReport.PROCESSED_APPEALS);
    }

    public void generateProcessedConsultationsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateProcessedSection(data, parent, agencyIdentifiers, DOJReport.PROCESSED_CONSULTATIONS);
    }

    private void generateProcessedSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)
            throws ParseException
    {
        Element processedSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendProcessingItem(processedSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(processedSection,
                componentData, "foia:ProcessingStatisticsOrganizationAssociation"));

        parent.appendChild(processedSection);
    }

    private void appendProcessingItem(Element parent, Map<String, String> record)
    {
        Element processingElement = parent.getOwnerDocument().createElement("foia:ProcessingStatistics");
        processingElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String itemsPendingAtStart = record.get("Number of Pending as of Start of Fiscal Year");
        String itemsReceived = record.get("Number of Received in Fiscal Year");
        String itemsProcessed = record.get("Number of Processed in Fiscal Year");

        int itemsPendingAtEnd = Integer.parseInt(itemsPendingAtStart) + Integer.parseInt(itemsReceived)
                - Integer.parseInt(itemsProcessed);

        addElement(processingElement, "foia:ProcessingStatisticsPendingAtStartQuantity", itemsPendingAtStart);
        addElement(processingElement, "foia:ProcessingStatisticsReceivedQuantity", itemsReceived);
        addElement(processingElement, "foia:ProcessingStatisticsProcessedQuantity", itemsProcessed);
        addElement(processingElement, "foia:ProcessingStatisticsPendingAtEndQuantity",
                String.valueOf(itemsPendingAtEnd));

        parent.appendChild(processingElement);
    }

    public void generateAppealResponseTimeSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        DOJReport report = DOJReport.APPEAL_RESPONSE_TIME;

        Element appealResponseTimeSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendResponseTimeItem(appealResponseTimeSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(appealResponseTimeSection,
                componentData, "foia:ResponseTimeOrganizationAssociation"));

        parent.appendChild(appealResponseTimeSection);
    }

    private void appendResponseTimeItem(Element parent, Map<String, String> record)
    {
        Element responseTimeElement = parent.getOwnerDocument().createElement("foia:ProcessingStatistics");
        responseTimeElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String medianNumberOfDays = record.get("Median number of Days");
        String averageNumberOfDays = record.get("Average Number of Days");
        String lowestNumberOfDays = record.get("Lowest Number of Days");
        String highestNumberOfDays = record.get("Highest Number of Days");

        addElement(responseTimeElement, "foia:ResponseTimeMedianDaysValue", medianNumberOfDays);
        addElement(responseTimeElement, "foia:ResponseTimeAverageDaysValue", averageNumberOfDays);
        if (Double.parseDouble(lowestNumberOfDays) < 1)
        {
            lowestNumberOfDays = "LT1";
        }
        addElement(responseTimeElement, "foia:ResponseTimeLowestDaysValue", lowestNumberOfDays);
        addElement(responseTimeElement, "foia:ResponseTimeHighestDaysValue",
                highestNumberOfDays);

        parent.appendChild(responseTimeElement);
    }

    public void generateExpeditedProcessingSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        DOJReport report = DOJReport.EXPEDITED_PROCESSING;

        Element expeditedProcessingSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendExpeditedProcessingItem(expeditedProcessingSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(expeditedProcessingSection,
                componentData, "foia:ExpeditedProcessingOrganizationAssociation"));

        parent.appendChild(expeditedProcessingSection);
    }

    private void appendExpeditedProcessingItem(Element parent, Map<String, String> record)
    {
        Element expeditedProcessingElement = parent.getOwnerDocument().createElement("foia:ExpeditedProcessing");
        expeditedProcessingElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String granted = record.get("Number Granted");
        String denied = record.get("Number Denied");
        String medianDaysToAdjudicate = record.get("Median Number of Days to Adjudicate");
        String averageDaysToAdjudicate = record.get("Average Number of Days to Adjudicate");
        String adjudicatedWithingTenDays = record.get("Number Adjudicated Within Ten Calendar Days");

        addElement(expeditedProcessingElement, "foia:RequestGrantedQuantity", granted);
        addElement(expeditedProcessingElement, "foia:RequestDeniedQuantity", denied);
        addElement(expeditedProcessingElement, "foia:AdjudicationMedianDaysValue", medianDaysToAdjudicate);
        addElement(expeditedProcessingElement, "foia:AdjudicationAverageDaysValue",
                averageDaysToAdjudicate);
        addElement(expeditedProcessingElement, "foia:AdjudicationWithinTenDaysQuantity",
                adjudicatedWithingTenDays);

        parent.appendChild(expeditedProcessingElement);
    }

    public void generateFeeWaiverSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        DOJReport report = DOJReport.FEE_WAIVER;

        Element feeWaiverSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendFeeWaiverItem(feeWaiverSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(feeWaiverSection,
                componentData, "foia:FeeWaiverOrganizationAssociation"));

        parent.appendChild(feeWaiverSection);
    }

    private void appendFeeWaiverItem(Element parent, Map<String, String> record)
    {
        Element feeWaiverElement = parent.getOwnerDocument().createElement("foia:FeeWaiver");
        feeWaiverElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String granted = record.get("Number Granted");
        String denied = record.get("Number Denied");
        String medianDaysToAdjudicate = record.get("Median Number of Days to Adjudicate");
        String averageDaysToAdjudicate = record.get("Average Number of Days to Adjudicate");

        addElement(feeWaiverElement, "foia:RequestGrantedQuantity", granted);
        addElement(feeWaiverElement, "foia:RequestDeniedQuantity", denied);
        addElement(feeWaiverElement, "foia:AdjudicationMedianDaysValue", medianDaysToAdjudicate);
        addElement(feeWaiverElement, "foia:AdjudicationAverageDaysValue",
                averageDaysToAdjudicate);

        parent.appendChild(feeWaiverElement);
    }

    public void generatePersonnelAndCostSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        DOJReport report = DOJReport.PERSONNEL_AND_COST;

        Element personnelAndCostSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> generatePersonnelAndCostItem(personnelAndCostSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(personnelAndCostSection,
                componentData, "foia:PersonnelAndCostOrganizationAssociation"));

        parent.appendChild(personnelAndCostSection);
    }

    private void generatePersonnelAndCostItem(Element parent, Map<String, String> record)
    {
        Element personelAndCostElement = parent.getOwnerDocument().createElement("foia:PersonnelAndCost");
        personelAndCostElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String fullTimeStaff = record.get("Number of \"Full-Time FOIA Employees\"");
        String equivalentStaff = record.get("Number of \"Equivalent Full-Time FOIA Employees\"");
        String totalStaff = record.get("Total Number of \"Full-Time FOIA Staff\"");
        String processingCosts = record.get("Processing Costs");
        String litigationCosts = record.get("Litigation-Related Costs");
        String totalCosts = record.get("Total Costs");

        addElement(personelAndCostElement, "foia:FullTimeEmployeeQuantity", fullTimeStaff);
        addElement(personelAndCostElement, "foia:EquivalentFullTimeEmployeeQuantity", equivalentStaff);
        addElement(personelAndCostElement, "foia:TotalFullTimeStaffQuantity", totalStaff);
        addElement(personelAndCostElement, "foia:ProcessingCostAmount", processingCosts);
        addElement(personelAndCostElement, "foia:LitigationCostAmount", litigationCosts);
        addElement(personelAndCostElement, "foia:TotalCostAmount", totalCosts);

        parent.appendChild(personelAndCostElement);
    }

    public void generateFeesCollectedSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        DOJReport report = DOJReport.FEES_COLLECTED;

        Element feesCollectedSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendFeesCollectedItem(feesCollectedSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(feesCollectedSection,
                componentData, "foia:FeesCollectedOrganizationAssociation"));

        parent.appendChild(feesCollectedSection);
    }

    private void appendFeesCollectedItem(Element parent, Map<String, String> record)
    {
        Element feesCollectedElement = parent.getOwnerDocument().createElement("foia:FeesCollected");
        feesCollectedElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String totalAmountOfFeesCollected = record.get("Total Amount of Fees Collected");
        String percentageOfTotalCosts = record.get("Percentage of Total Costs");

        addElement(feesCollectedElement, "foia:FeesCollectedAmount", totalAmountOfFeesCollected);
        addElement(feesCollectedElement, "foia:FeesCollectedCostPercent", percentageOfTotalCosts);

        parent.appendChild(feesCollectedElement);
    }

    public void generateSubsectionUsedSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        DOJReport report = DOJReport.SUBSECTION_USED;

        Element subsectionUsedSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendSubsectionUsedItem(subsectionUsedSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(subsectionUsedSection,
                componentData, "foia:SubsectionUsedOrganizationAssociation"));

        parent.appendChild(subsectionUsedSection);
    }

    private void appendSubsectionUsedItem(Element parent, Map<String, String> record)
    {
        Element subsectionUsedElement = parent.getOwnerDocument().createElement("foia:SubsectionUsed");
        subsectionUsedElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String timesUsed = record.get("Number of Times Subsection Used");

        addElement(subsectionUsedElement, "foia:TimesUsedQuantity", timesUsed);

        parent.appendChild(subsectionUsedElement);
    }

    public void generateSubsectionPostSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        DOJReport report = DOJReport.SUBSECTION_POST;

        Element subsectionPostSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendSubsectionPostItem(subsectionPostSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(subsectionPostSection,
                componentData, "foia:SubsectionPostOrganizationAssociation"));

        parent.appendChild(subsectionPostSection);
    }

    private void appendSubsectionPostItem(Element parent, Map<String, String> record)
    {
        Element subsectionPostElement = parent.getOwnerDocument().createElement("foia:FeesCollected");
        subsectionPostElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String postedByFOIAOffice = record.get("Number of Records Posted by the FOIA Office");
        String postedByProgramOffices = record.get("Number of Records Posted by Program Offices");

        addElement(subsectionPostElement, "foia:PostedbyFOIAQuantity", postedByFOIAOffice);
        addElement(subsectionPostElement, "foia:PostedbyProgramQuantity", postedByProgramOffices);

        parent.appendChild(subsectionPostElement);
    }

    public void generateBacklogSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        DOJReport report = DOJReport.BACKLOG;

        Element backlogSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendBacklogItem(backlogSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(backlogSection,
                componentData, "foia:BacklogOrganizationAssociation"));

        parent.appendChild(backlogSection);
    }

    private void appendBacklogItem(Element parent, Map<String, String> record)
    {
        Element backlogElement = parent.getOwnerDocument().createElement("foia:Backlog");
        backlogElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String backloggedRequestQuantity = record.get("Number of Backlogged Requests as of End of Fiscal Year");
        String backloggedAppealQuantity = record.get("Number of Backlogged Appeals as of End of Fiscal Year");

        addElement(backlogElement, "foia:BackloggedRequestQuantity", backloggedRequestQuantity);
        addElement(backlogElement, "foia:BackloggedAppealQuantity", backloggedAppealQuantity);

        parent.appendChild(backlogElement);
    }

    public void generateBackloggedRequestComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateBacklogComparisonSection(data, parent, agencyIdentifiers, DOJReport.BACKLOG_REQUEST_COMPARISON);
    }

    public void generateBackloggedAppealsComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers) throws ParseException
    {
        generateBacklogComparisonSection(data, parent, agencyIdentifiers, DOJReport.BACKLOG_APPEAL_COMPARISON);
    }

    private void generateBacklogComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report) throws ParseException
    {

        Element backlogComparisonSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendBacklogComparisonItem(backlogComparisonSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(backlogComparisonSection,
                componentData, "foia:BacklogComparisonOrganizationAssociation"));

        parent.appendChild(backlogComparisonSection);
    }

    private void appendBacklogComparisonItem(Element parent, Map<String, String> record)
    {
        Element backlogComparisonElement = parent.getOwnerDocument().createElement("foia:BacklogComparison");
        backlogComparisonElement.setAttribute("s:id", record.get("ComponentDataReference"));

        String backlogLastYearQuantity = record
                .get("Number of Backlogged Requests as of End of the Fiscal Year from Previous Annual Report");
        String backlogCurrentYearQuantity = record
                .get("Number of Backlogged Requests as of End of the Fiscal Year from Current Annual Report");

        addElement(backlogComparisonElement, "foia:BacklogLastYearQuantity", backlogLastYearQuantity);
        addElement(backlogComparisonElement, "foia:BacklogCurrentYearQuantity", backlogCurrentYearQuantity);

        parent.appendChild(backlogComparisonElement);
    }

    private Map<String, List<Map<String, String>>> groupByComponentAndFilter(List<Map<String, String>> data)
    {
        Map<String, List<Map<String, String>>> listMap = data.stream()
                .filter(it -> !it.get(AGENCY_IDENTIFIER_COLUMN).contains("Total")
                        || it.get(AGENCY_IDENTIFIER_COLUMN).equals("Grand Total"))
                .collect(groupingBy(it -> it.get(AGENCY_IDENTIFIER_COLUMN)));
        return listMap;
    }

    private void addElement(Element parent, String elemName, String elemValue)
    {
        Element elem = parent.getOwnerDocument().createElement(elemName);
        elem.appendChild(parent.getOwnerDocument().createTextNode(elemValue));
        parent.appendChild(elem);
    }

    private void appendProcessingAssociations(Element parentElement, String associationsElementName, String id,
            String agency)
    {
        Element organizationAssociationElement = parentElement.getOwnerDocument().createElement(associationsElementName);

        Element componentDataReference = parentElement.getOwnerDocument().createElement("foia:ComponentDataReference");
        componentDataReference.setAttribute("s:ref", id);
        organizationAssociationElement.appendChild(componentDataReference);

        Element organizationReference = parentElement.getOwnerDocument().createElement("nc:OrganizationReference");
        organizationReference.setAttribute("s:ref", componentMap.get(agency));
        organizationAssociationElement.appendChild(organizationReference);

        parentElement.appendChild(organizationAssociationElement);
    }

    private List<Map<String, String>> getDataWithComponentReferences(List<Map<String, String>> data, Map<String, String> agencyIdentifiers,
            DOJReport report)
    {
        List<Map<String, String>> filteredData = new ArrayList<>();

        for (int index = 0; index < data.size(); index++)
        {
            String agency = data.get(index).get(AGENCY_IDENTIFIER_COLUMN);

            if (agencyIdentifiers.get(agency) != null)
            {
                String componentDataReference = report.getIdPrefix().concat(String.valueOf(index + 1));
                Map<String, String> record = getComponentReferencedRecord(data.get(index), componentDataReference,
                        agencyIdentifiers.get(agency));
                filteredData.add(record);
            }
            else if (agency.equals("Grand Total"))
            {
                String componentDataReference = report.getIdPrefix().concat(String.valueOf(0));
                Map<String, String> record = getComponentReferencedRecord(data.get(index), componentDataReference, "ORG0");
                filteredData.add(record);
            }
        }
        filteredData = filteredData.stream()
                .filter(entry -> entry.get("ComponentDataReference") != null)
                .collect(Collectors.toList());

        return filteredData;
    }

    private Map<String, String> getComponentReferencedRecord(Map<String, String> record, String componentDataReference,
            String organizationReference)
    {
        record.put("ComponentDataReference", componentDataReference);
        record.put("OrganizationReference", organizationReference);
        return record;
    }

    private void appendProcessingAssociations2(Element parentElement, Map<String, String> data, String associationsElementName)
    {
        Element organizationAssociationElement = parentElement.getOwnerDocument().createElement(associationsElementName);

        Element componentDataReference = parentElement.getOwnerDocument().createElement("foia:ComponentDataReference");
        componentDataReference.setAttribute("s:ref", data.get("ComponentDataReference"));
        organizationAssociationElement.appendChild(componentDataReference);

        Element organizationReference = parentElement.getOwnerDocument().createElement("nc:OrganizationReference");
        organizationReference.setAttribute("s:ref", data.get("OrganizationReference"));
        organizationAssociationElement.appendChild(organizationReference);

        parentElement.appendChild(organizationAssociationElement);
    }

    public NiemExportUtils getNiemExportUtils()
    {
        return niemExportUtils;
    }

    public void setNiemExportUtils(NiemExportUtils niemExportUtils)
    {
        this.niemExportUtils = niemExportUtils;
    }

    public Map<String, String> getComponentMap()
    {
        return componentMap;
    }

    public void setComponentMap(Map<String, String> componentMap)
    {
        this.componentMap = componentMap;
    }
}

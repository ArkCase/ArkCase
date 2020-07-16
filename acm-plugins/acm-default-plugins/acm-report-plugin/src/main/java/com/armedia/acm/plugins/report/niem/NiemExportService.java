package com.armedia.acm.plugins.report.niem;

import static java.util.stream.Collectors.groupingBy;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private NiemExportUtils niemExportUtils;
    private Map<String, String> componentMap = new HashMap<>();

    public void generateOldestPendingAppealsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateOldestPendingSection(data, document, DOJReport.OLDEST_PENDING_APPEALS);
    }

    public void generateOldestPendingRequestsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateOldestPendingSection(data, document, DOJReport.OLDEST_PENDING_REQUESTS);
    }

    public void generateOldestPendingConsultationsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateOldestPendingSection(data, document, DOJReport.OLDEST_PENDING_CONSULTATIONS);
    }

    private void generateOldestPendingSection(List<Map<String, String>> data, Document document, DOJReport report) throws ParseException
    {
        int idSuffix = 1;

        Element oldestPendingSectionElement = document.createElement(report.getSectionName());

        document.appendChild(oldestPendingSectionElement);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element oldestPendingItemsElement = generateOldestPendingItems(document, componentData, id);
            oldestPendingSectionElement.appendChild(oldestPendingItemsElement);

            appendProcessingAssociations(oldestPendingSectionElement,
                    "foia:OldestPendingItemsOrganizationAssociation", id, agency);
        }
    }

    private Element generateOldestPendingItems(Document document, List<Map<String, String>> componentData, String id) throws ParseException
    {
        Element oldestPendingItemsElement = document.createElement("foia:OldestPendingItems");
        oldestPendingItemsElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String dateOfReceipt = record.get("Date of Receipt");
            String daysPending = record.get("Number of Days Pending");

            String formattedDate = niemExportUtils.formatDateToNiemExpectedDate(dateOfReceipt);

            if (isValidOldItem(formattedDate, daysPending))
            {
                addOldItem(document, oldestPendingItemsElement, daysPending, formattedDate);
            }
        }
        return oldestPendingItemsElement;
    }

    private void addOldItem(Document document, Element oldestPendingItemsElement, String daysPending, String date)
    {
        Element oldItem = document.createElement("foia:OldItem");

        addElement(oldItem, "foia:OldItemReceiptDate", date);
        addElement(oldItem, "foia:OldItemPendingDaysQuantity", daysPending);

        oldestPendingItemsElement.appendChild(oldItem);
    }

    private boolean isValidOldItem(String date, String daysPending)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            dateFormat.parse(date);
            Integer.parseInt(daysPending);
            return true;
        }
        catch (ParseException | NumberFormatException e)
        {
            return false;
        }
    }

    public void generateRequestDenialOtherReasonSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateDenialOtherReasonSection(data, document, DOJReport.APPEAL_REQUEST_OTHER_REASON);
    }

    public void generateAppealDenialOtherReasonSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateDenialOtherReasonSection(data, document, DOJReport.APPEAL_DENIAL_OTHER_REASON);
    }

    private void generateDenialOtherReasonSection(List<Map<String, String>> data, Document document, DOJReport report) throws ParseException
    {
        int idSuffix = 1;

        Element denialOtherReasonSectionElement = document.createElement(report.getSectionName());

        document.appendChild(denialOtherReasonSectionElement);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element componentOtherDenialReasonElement = generateOtherDenialReasonItems(document, componentData, id);
            denialOtherReasonSectionElement.appendChild(componentOtherDenialReasonElement);

            appendProcessingAssociations(denialOtherReasonSectionElement,
                    "foia:OtherDenialReasonOrganizationAssociation", id, agency);
        }
    }

    private Element generateOtherDenialReasonItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element componentOtherDenialReason = document.createElement("foia:ComponentOtherDenialReason");
        componentOtherDenialReason.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String otherReasonDescription = record
                    .get("Description of \"Other\" Reasons for Denial on Appeal from Chart C(2)");
            String numberOfTimesOtherReasonWasUsed = record.get("Number of Times \"Other\" Reason Was Relied Upon");

            if (otherReasonDescription != null && numberOfTimesOtherReasonWasUsed != null)
            {
                Element otherDenialReason = document.createElement("foia:OtherDenialReason");

                addElement(otherDenialReason, "foia:OtherDenialReasonDescriptionText", otherReasonDescription);
                addElement(otherDenialReason, "foia:OtherDenialReasonQuantity", numberOfTimesOtherReasonWasUsed);

                componentOtherDenialReason.appendChild(otherDenialReason);
            }
        }
        return componentOtherDenialReason;
    }

    public void generateSimpleResponseTimeIncrementsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateResponseTimeIncrementsSection(data, document, DOJReport.SIMPLE_RESPONSE_TIME_INCREMENTS);
    }

    public void generateComplexResponseTimeIncrementsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateResponseTimeIncrementsSection(data, document, DOJReport.COMPLEX_RESPONSE_TIME_INCREMENTS);
    }

    public void generateExpeditedResponseTimeIncrementsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateResponseTimeIncrementsSection(data, document, DOJReport.EXPEDITED_RESPONSE_TIME_INCREMENTS);
    }

    private void generateResponseTimeIncrementsSection(List<Map<String, String>> data, Document document, DOJReport report)
            throws ParseException
    {
        int idSuffix = 1;

        Element responseTimeIncrementsSectionElement = document.createElement(report.getSectionName());

        document.appendChild(responseTimeIncrementsSectionElement);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element componentResponseTimeIncrementsElement = generateTimeIncrementItems(document, componentData, id);
            responseTimeIncrementsSectionElement.appendChild(componentResponseTimeIncrementsElement);

            appendProcessingAssociations(responseTimeIncrementsSectionElement,
                    "foia:ResponseTimeIncrementsOrganizationAssociation", id, agency);
        }
    }

    private Element generateTimeIncrementItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element componentResponseTimeIncrements = document.createElement("foia:ComponentResponseTimeIncrements");
        componentResponseTimeIncrements.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            int totalTimeIncrementQuantity = 0;

            for (Map.Entry<String, String> timeIncrementEntry : record.entrySet())
            {
                String timeIncrementCode = timeIncrementEntry.getKey();
                String timeIncrementQuantity = timeIncrementEntry.getValue();

                timeIncrementCode = timeIncrementCode.replace("Days", "").replace("<", "").trim();

                if (NumberUtils.isParsable(timeIncrementQuantity))
                {
                    Element timeIncrementElement = document.createElement("foia:TimeIncrement");

                    addElement(timeIncrementElement, "foia:TimeIncrementCode", timeIncrementCode);
                    addElement(timeIncrementElement, "foia:TimeIncrementQuantity", timeIncrementQuantity);

                    componentResponseTimeIncrements.appendChild(timeIncrementElement);

                    totalTimeIncrementQuantity += Integer.parseInt(timeIncrementQuantity);
                }
            }

            addElement(componentResponseTimeIncrements, "foia:TimeIncrementTotalQuantity",
                    String.valueOf(totalTimeIncrementQuantity));

        }
        return componentResponseTimeIncrements;
    }

    public void generateAppealDispositionSection(List<Map<String, String>> data, Document document, Map<String, String> agencyIdentifiers)
            throws ParseException
    {
        DOJReport report = DOJReport.APPEAL_DISPOSITION;

        Element appealDispositionSection = document.createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendAppealDispositionItems(appealDispositionSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations2(appealDispositionSection,
                componentData, "foia:AppealDispositionOrganizationAssociation"));

        document.appendChild(appealDispositionSection);
    }

    private void appendAppealDispositionItems(Element parent, Map<String, String> record)
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

    public void generateRequestDispositionSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.REQUEST_DISPOSITION;

        int idSuffix = 1;

        Element requestDispositionSection = document.createElement(report.getSectionName());

        document.appendChild(requestDispositionSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element componentRequestDispositionElement = generateRequestDispositionItems(document, componentData, id);
            requestDispositionSection.appendChild(componentRequestDispositionElement);

            appendProcessingAssociations(requestDispositionSection,
                    "foia:RequestDispositionOrganizationAssociation", id, agency);
        }

    }

    private Element generateRequestDispositionItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element requestDispositionElement = document.createElement("foia:RequestDisposition");
        requestDispositionElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
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

        }
        return requestDispositionElement;
    }

    public void generateAppealNonExemptionDenialSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.APPEAL_NON_EXEMPTION_DENIAL;

        int idSuffix = 1;

        Element requestDispositionSection = document.createElement(report.getSectionName());

        document.appendChild(requestDispositionSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element componentRequestDispositionElement = generateAppealNonExemptionDenialItems(document, componentData, id);
            requestDispositionSection.appendChild(componentRequestDispositionElement);

            appendProcessingAssociations(requestDispositionSection,
                    "foia:AppealNonExemptionDenialOrganizationAssociation", id, agency);
        }

    }

    private Element generateAppealNonExemptionDenialItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element appealDispositionElement = document.createElement("foia:AppealNonExemptionDenial");
        appealDispositionElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
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
        }
        return appealDispositionElement;
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

    public void generateAppealDispositionAppliedExemptionsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateDispositionAppliedExemptionsSection(data, document, DOJReport.REQUEST_DISPOSITION_APPLIED_EXEMPTIONS);

    }

    public void generateRequestDispositionAppliedExemptionsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateDispositionAppliedExemptionsSection(data, document, DOJReport.APPEAL_DISPOSITION_APPLIED_EXEMPTIONS);

    }

    private void generateDispositionAppliedExemptionsSection(List<Map<String, String>> data, Document document, DOJReport report)
            throws ParseException
    {
        int idSuffix = 1;

        Element appealDispositionAppliedExemptionsSection = document.createElement(report.getSectionName());

        document.appendChild(appealDispositionAppliedExemptionsSection);

        // Look into erasing the id counter
        Map<String, String> associationData = new LinkedHashMap<>();

        List<String> agencies = data.stream().map(it -> it.get(AGENCY_IDENTIFIER_COLUMN)).distinct().collect(Collectors.toList());

        for (int index = 0; index < data.size(); index++)
        {
            String id = report.getIdPrefix().concat(String.valueOf(index + 1));
            String agency = agencies.get(index);

            associationData.put(agency, id);
        }
        // Look into erasing the id counter

        for (Map<String, String> componentData : data)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(AGENCY_IDENTIFIER_COLUMN);

            Element componentAppealDispositionAppliedExemptionsElement = generateAppealDispositionAppliedExemptionsItems(document,
                    componentData, id);
            appealDispositionAppliedExemptionsSection.appendChild(componentAppealDispositionAppliedExemptionsElement);

            appendProcessingAssociations(appealDispositionAppliedExemptionsSection,
                    "foia:ComponentAppliedExemptionsOrganizationAssociation", id, agency);
        }
    }

    private Element generateAppealDispositionAppliedExemptionsItems(Document document, Map<String, String> record, String id)
            throws ParseException
    {
        Element componentAppliedExemptionsElement = document.createElement("foia:ComponentAppliedExemptions");
        componentAppliedExemptionsElement.setAttribute("s:id", id);

        for (Map.Entry<String, String> exemptionEntry : record.entrySet())
            {
                String exemptionCode = exemptionEntry.getKey();
                String exemptionQuantity = exemptionEntry.getValue();

                if (NumberUtils.isParsable(exemptionQuantity) && Integer.parseInt(exemptionQuantity) > 0)
                {
                    Element appliedExemptionElement = document.createElement("foia:AppliedExemption");

                    addElement(appliedExemptionElement, "foia:AppliedExemptionCode", exemptionCode);
                    addElement(appliedExemptionElement, "foia:AppliedExemptionQuantity", exemptionQuantity);

                    componentAppliedExemptionsElement.appendChild(appliedExemptionElement);
                }
        }
        return componentAppliedExemptionsElement;
    }

    public void generateProcessedRequestComparisonSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateProcessedComparisonSection(data, document, DOJReport.REQUEST_PROCESSED_COMPARISON);
    }

    public void generateProcessedAppealsComparisonSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateProcessedComparisonSection(data, document, DOJReport.APPEAL_PROCESSED_COMPARISON);
    }

    private void generateProcessedComparisonSection(List<Map<String, String>> data, Document document, DOJReport report)
            throws ParseException
    {
        int idSuffix = 1;

        Element processedComparisonSection = document.createElement(report.getSectionName());

        document.appendChild(processedComparisonSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element processingComparisonElement = generateProcessingComparisonItems(document, componentData, id);
            processedComparisonSection.appendChild(processingComparisonElement);

            appendProcessingAssociations(processedComparisonSection,
                    "foia:ProcessingComparisonOrganizationAssociation", id, agency);
        }
    }

    private Element generateProcessingComparisonItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element processingComparisonElement = document.createElement("foia:ProcessingComparison");
        processingComparisonElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
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

        }
        return processingComparisonElement;
    }

    public void generateProcessedRequestSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateProcessedSection(data, document, DOJReport.PROCESSED_REQUESTS);
    }

    public void generateProcessedAppealsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateProcessedSection(data, document, DOJReport.PROCESSED_APPEALS);
    }

    public void generateProcessedConsultationsSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateProcessedSection(data, document, DOJReport.PROCESSED_CONSULTATIONS);
    }

    private void generateProcessedSection(List<Map<String, String>> data, Document document, DOJReport report)
            throws ParseException
    {
        int idSuffix = 1;

        Element processedComparisonSection = document.createElement(report.getSectionName());

        document.appendChild(processedComparisonSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element processingComparisonElement = generateProcessingItems(document, componentData, id);
            processedComparisonSection.appendChild(processingComparisonElement);

            appendProcessingAssociations(processedComparisonSection,
                    "foia:ProcessingStatisticsOrganizationAssociation", id, agency);
        }
    }

    private Element generateProcessingItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element processingElement = document.createElement("foia:ProcessingStatistics");
        processingElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String itemsPendingAtStart = record.get("Number of Pending as of Start of Fiscal Year");
            String itemsReceived = record.get("Number of Received in Fiscal Year");
            String itemsProcessed = record.get("Number of Processed in Fiscal Year");

            int itemsPendingAtEnd = Integer.parseInt("itemsPendingAtStart") + Integer.parseInt("itemsReceived")
                    - Integer.parseInt("itemsProcessed");

            addElement(processingElement, "foia:ProcessingStatisticsPendingAtStartQuantity", itemsPendingAtStart);
            addElement(processingElement, "foia:ProcessingStatisticsReceivedQuantity", itemsReceived);
            addElement(processingElement, "foia:ProcessingStatisticsProcessedQuantity", itemsProcessed);
            addElement(processingElement, "foia:ProcessingStatisticsPendingAtEndQuantity",
                    String.valueOf(itemsPendingAtEnd));
        }
        return processingElement;
    }

    public void generateAppealResponseTimeSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.APPEAL_RESPONSE_TIME;

        int idSuffix = 1;

        Element processedComparisonSection = document.createElement(report.getSectionName());

        document.appendChild(processedComparisonSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element responseTimeItemsElement = generateResponseTimeItems(document, componentData, id);
            processedComparisonSection.appendChild(responseTimeItemsElement);

            appendProcessingAssociations(processedComparisonSection,
                    "foia:ResponseTimeOrganizationAssociation", id, agency);
        }
    }

    private Element generateResponseTimeItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element responseTimeElement = document.createElement("foia:ResponseTime");
        responseTimeElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String medianNumberOfDays = record.get("Median number of Days");
            String averageNumberOfDays = record.get("Average Number of Days");
            String lowestNumberOfDays = record.get("Lowest Number of Days");
            String highestNumberOfDays = record.get("Highest Number of Days");

            addElement(responseTimeElement, "foia:ResponseTimeMedianDaysValue", medianNumberOfDays);
            addElement(responseTimeElement, "foia:ResponseTimeAverageDaysValue", averageNumberOfDays);
            addElement(responseTimeElement, "foia:ResponseTimeLowestDaysValue", lowestNumberOfDays);
            addElement(responseTimeElement, "foia:ResponseTimeHighestDaysValue",
                    highestNumberOfDays);
        }
        return responseTimeElement;
    }

    public void generateExpeditedProcessingSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.EXPEDITED_PROCESSING;

        int idSuffix = 1;

        Element expeditedProcessingSection = document.createElement(report.getSectionName());

        document.appendChild(expeditedProcessingSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element expeditedProcessingItemsElement = generateExpeditedProcessingItems(document, componentData, id);
            expeditedProcessingSection.appendChild(expeditedProcessingItemsElement);

            appendProcessingAssociations(expeditedProcessingSection,
                    "foia:ExpeditedProcessingOrganizationAssociation", id, agency);
        }
    }

    private Element generateExpeditedProcessingItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element expeditedProcessingElement = document.createElement("foia:ExpeditedProcessing");
        expeditedProcessingElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
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
        }
        return expeditedProcessingElement;
    }

    public void generateFeeWaiverSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.FEE_WAIVER;

        int idSuffix = 1;

        Element feeWaiverSection = document.createElement(report.getSectionName());

        document.appendChild(feeWaiverSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element feeWaiverItemsElement = generateFeeWaiverItems(document, componentData, id);
            feeWaiverSection.appendChild(feeWaiverItemsElement);

            appendProcessingAssociations(feeWaiverSection,
                    "foia:FeeWaiverOrganizationAssociation", id, agency);
        }
    }

    private Element generateFeeWaiverItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element feeWaiverElement = document.createElement("foia:FeeWaiver");
        feeWaiverElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String granted = record.get("Number Granted");
            String denied = record.get("Number Denied");
            String medianDaysToAdjudicate = record.get("Median Number of Days to Adjudicate");
            String averageDaysToAdjudicate = record.get("Average Number of Days to Adjudicate");

            addElement(feeWaiverElement, "foia:RequestGrantedQuantity", granted);
            addElement(feeWaiverElement, "foia:RequestDeniedQuantity", denied);
            addElement(feeWaiverElement, "foia:AdjudicationMedianDaysValue", medianDaysToAdjudicate);
            addElement(feeWaiverElement, "foia:AdjudicationAverageDaysValue",
                    averageDaysToAdjudicate);
        }
        return feeWaiverElement;
    }

    public void generatePersonnelAndCostSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.PERSONNEL_AND_COST;

        int idSuffix = 1;

        Element personnelAndCostSection = document.createElement(report.getSectionName());

        document.appendChild(personnelAndCostSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element personelAndCostElement = generatePersonnelAndCostItems(document, componentData, id);
            personnelAndCostSection.appendChild(personelAndCostElement);

            appendProcessingAssociations(personnelAndCostSection,
                    "foia:PersonnelAndCostOrganizationAssociation", id, agency);
        }
    }

    private Element generatePersonnelAndCostItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element personelAndCostElement = document.createElement("foia:PersonnelAndCost");
        personelAndCostElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
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
        }
        return personelAndCostElement;
    }

    public void generateFeesCollectedSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.FEES_COLLECTED;

        int idSuffix = 1;

        Element feesCollectedSection = document.createElement(report.getSectionName());

        document.appendChild(feesCollectedSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element feesCollectedItemsElement = generateFeesCollectedItems(document, componentData, id);
            feesCollectedSection.appendChild(feesCollectedItemsElement);

            appendProcessingAssociations(feesCollectedSection,
                    "foia:FeesCollectedOrganizationAssociation", id, agency);
        }
    }

    private Element generateFeesCollectedItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element feesCollectedElement = document.createElement("foia:FeesCollected");
        feesCollectedElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String totalAmountOfFeesCollected = record.get("Total Amount of Fees Collected");
            String percentageOfTotalCosts = record.get("Percentage of Total Costs");

            addElement(feesCollectedElement, "foia:FeesCollectedAmount", totalAmountOfFeesCollected);
            addElement(feesCollectedElement, "foia:FeesCollectedCostPercent", percentageOfTotalCosts);
        }
        return feesCollectedElement;
    }

    public void generateSubsectionUsedSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.SUBSECTION_USED;

        int idSuffix = 1;

        Element subsectionUsedSection = document.createElement(report.getSectionName());

        document.appendChild(subsectionUsedSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element subsectionUsedItemsElement = generateSubsectionUsedItems(document, componentData, id);
            subsectionUsedSection.appendChild(subsectionUsedItemsElement);

            appendProcessingAssociations(subsectionUsedSection,
                    "foia:SubsectionUsedOrganizationAssociation", id, agency);
        }
    }

    private Element generateSubsectionUsedItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element subsectionUsedElement = document.createElement("foia:SubsectionUsed");
        subsectionUsedElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String timesUsed = record.get("Number of Times Subsection Used");

            addElement(subsectionUsedElement, "foia:TimesUsedQuantity", timesUsed);
        }
        return subsectionUsedElement;
    }

    public void generateSubsectionPostSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.SUBSECTION_POST;

        int idSuffix = 1;

        Element subsectionPostSection = document.createElement(report.getSectionName());

        document.appendChild(subsectionPostSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element subsectionPostItemsElement = generateSubsectionPostItems(document, componentData, id);
            subsectionPostSection.appendChild(subsectionPostItemsElement);

            appendProcessingAssociations(subsectionPostSection,
                    "foia:SubsectionPostOrganizationAssociation", id, agency);
        }
    }

    private Element generateSubsectionPostItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element subsectionPostElement = document.createElement("foia:FeesCollected");
        subsectionPostElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String postedByFOIAOffice = record.get("Number of Records Posted by the FOIA Office");
            String postedByProgramOffices = record.get("Number of Records Posted by Program Offices");

            addElement(subsectionPostElement, "foia:PostedbyFOIAQuantity", postedByFOIAOffice);
            addElement(subsectionPostElement, "foia:PostedbyProgramQuantity", postedByProgramOffices);
        }
        return subsectionPostElement;
    }

    public void generateBacklogSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.BACKLOG;

        int idSuffix = 1;

        Element backlogSection = document.createElement(report.getSectionName());

        document.appendChild(backlogSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element backlogItemElement = generateBacklogItems(document, componentData, id);
            backlogSection.appendChild(backlogItemElement);

            appendProcessingAssociations(backlogSection,
                    "foia:BacklogOrganizationAssociation", id, agency);
        }
    }

    private Element generateBacklogItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element backlogElement = document.createElement("foia:Backlog");
        backlogElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String backloggedRequestQuantity = record.get("Number of Backlogged Requests as of End of Fiscal Year");
            String backloggedAppealQuantity = record.get("Number of Backlogged Appeals as of End of Fiscal Year");

            addElement(backlogElement, "foia:BackloggedRequestQuantity", backloggedRequestQuantity);
            addElement(backlogElement, "foia:BackloggedAppealQuantity", backloggedAppealQuantity);
        }
        return backlogElement;
    }

    public void generateBackloggedRequestComparisonSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateBacklogComparisonSection(data, document, DOJReport.BACKLOG_REQUEST_COMPARISON);
    }

    public void generateBackloggedAppealsComparisonSection(List<Map<String, String>> data, Document document) throws ParseException
    {
        generateBacklogComparisonSection(data, document, DOJReport.BACKLOG_APPEAL_COMPARISON);
    }

    private void generateBacklogComparisonSection(List<Map<String, String>> data, Document document, DOJReport report) throws ParseException
    {
        int idSuffix = 1;

        Element backlogComparisonSection = document.createElement(report.getSectionName());

        document.appendChild(backlogComparisonSection);

        Collection<List<Map<String, String>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, String>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = componentData.get(0).get(AGENCY_IDENTIFIER_COLUMN);

            Element backlogComparisonItemElement = generateBacklogComparisonItems(document, componentData, id);
            backlogComparisonSection.appendChild(backlogComparisonItemElement);

            appendProcessingAssociations(backlogComparisonSection,
                    "foia:BacklogComparisonOrganizationAssociation", id, agency);
        }
    }

    private Element generateBacklogComparisonItems(Document document, List<Map<String, String>> componentData, String id)
            throws ParseException
    {
        Element backlogComparisonElement = document.createElement("foia:BacklogComparison");
        backlogComparisonElement.setAttribute("s:id", id);

        for (Map<String, String> record : componentData)
        {
            String backlogLastYearQuantity = record
                    .get("Number of Backlogged Requests as of End of the Fiscal Year from Previous Annual Report");
            String backlogCurrentYearQuantity = record
                    .get("Number of Backlogged Requests as of End of the Fiscal Year from Current Annual Report");

            addElement(backlogComparisonElement, "foia:BacklogLastYearQuantity", backlogLastYearQuantity);
            addElement(backlogComparisonElement, "foia:BacklogCurrentYearQuantity", backlogCurrentYearQuantity);
        }
        return backlogComparisonElement;
    }

    private Collection<List<Map<String, String>>> filterDataAndGroupByComponent(List<Map<String, String>> data)
    {
        return data.stream()
                .filter(it -> !it.get(AGENCY_IDENTIFIER_COLUMN).contains("Total"))
                .collect(groupingBy(it -> it.get(AGENCY_IDENTIFIER_COLUMN)))
                .values();
    }

    private List<Map<String, String>> filterData(List<Map<String, String>> data)
    {
        return data.stream()
                .filter(it -> !it.get(AGENCY_IDENTIFIER_COLUMN).contains("Total"))
                .collect(Collectors.toList());
    }

    private void addElement(Element parent, String elemName,
            String elemValue)
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

                Map<String, String> record = data.get(index);
                record.put("ComponentDataReference", componentDataReference);
                record.put("OrganizationReference", agencyIdentifiers.get(agency));
                filteredData.add(record);
            }
            else if (agency.equals("Grand Total"))
            {
                String componentDataReference = report.getIdPrefix().concat(String.valueOf(0));

                Map<String, String> record = data.get(index);
                record.put("ComponentDataReference", componentDataReference);
                record.put("OrganizationReference", "ORG0");
                filteredData.add(record);
            }
        }
        filteredData = filteredData.stream()
                .filter(entry -> entry.get("ComponentDataReference") != null)
                .collect(Collectors.toList());

        return filteredData;
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

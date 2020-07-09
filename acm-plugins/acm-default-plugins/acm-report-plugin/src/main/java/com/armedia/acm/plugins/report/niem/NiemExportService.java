package com.armedia.acm.plugins.report.niem;

import static java.util.stream.Collectors.groupingBy;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NiemExportService
{

    private NiemExportUtils niemExportUtils;
    private Map<String, String> componentMap = new HashMap<>();

    public void generateOldestPendingAppealsSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateOldestPendingSection(data, document, DOJReport.OLDEST_PENDING_APPEALS);
    }

    public void generateOldestPendingRequestsSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateOldestPendingSection(data, document, DOJReport.OLDEST_PENDING_REQUESTS);
    }

    public void generateOldestPendingConsultationsSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateOldestPendingSection(data, document, DOJReport.OLDEST_PENDING_CONSULTATIONS);
    }

    private void generateOldestPendingSection(List<Map<String, Object>> data, Document document, DOJReport report) throws ParseException
    {
        int idSuffix = 1;

        Element oldestPendingSectionElement = document.createElement(report.getSectionName());

        document.appendChild(oldestPendingSectionElement);

        Collection<List<Map<String, Object>>> dataByComponents = filterDataAndGroupByComponent(data);

        // Look into erasing the id counter
        Map<String, String> associationData = new LinkedHashMap<>();

        List<String> agencies = data.stream().map(it -> (String) it.get("Agency / Component")).distinct().collect(Collectors.toList());

        for (int index = 0; index < dataByComponents.size(); index++)
        {
            String id = report.getIdPrefix().concat(String.valueOf(index + 1));
            String agency = agencies.get(index);

            associationData.put(agency, id);
        }
        // Look into erasing the id counter

        for (List<Map<String, Object>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = (String) componentData.get(0).get("Agency / Component");

            Element oldestPendingItemsElement = generateOldestPendingItems(document, componentData, id);
            oldestPendingSectionElement.appendChild(oldestPendingItemsElement);

            appendProcessingAssociationsToElement(document, oldestPendingSectionElement,
                    "foia:OldestPendingItemsOrganizationAssociation", id, agency);
        }
    }

    private Element generateOldestPendingItems(Document document, List<Map<String, Object>> componentData, String id) throws ParseException
    {
        Element oldestPendingItemsElement = document.createElement("foia:OldestPendingItems");
        oldestPendingItemsElement.setAttribute("s:id", id);

        for (Map<String, Object> record : componentData)
        {
            String dateOfReceipt = (String) record.get("Date of Receipt");
            String daysPending = (String) record.get("Number of Days Pending");

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

        addElement(document, oldItem, "foia:OldItemReceiptDate", date);
        addElement(document, oldItem, "foia:OldItemPendingDaysQuantity", daysPending);

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

    public void generateRequestDenialOtherReasonSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateDenialOtherReasonSection(data, document, DOJReport.APPEAL_REQUEST_OTHER_REASON);
    }

    public void generateAppealDenialOtherReasonSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateDenialOtherReasonSection(data, document, DOJReport.APPEAL_DENIAL_OTHER_REASON);
    }

    private void generateDenialOtherReasonSection(List<Map<String, Object>> data, Document document, DOJReport report) throws ParseException
    {
        int idSuffix = 1;

        Element denialOtherReasonSectionElement = document.createElement(report.getSectionName());

        document.appendChild(denialOtherReasonSectionElement);

        Collection<List<Map<String, Object>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, Object>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = (String) componentData.get(0).get("Agency / Component");

            Element componentOtherDenialReasonElement = generateOtherDenialReasonItems(document, componentData, id);
            denialOtherReasonSectionElement.appendChild(componentOtherDenialReasonElement);

            appendProcessingAssociationsToElement(document, denialOtherReasonSectionElement,
                    "foia:OtherDenialReasonOrganizationAssociation", id, agency);
        }
    }

    private Element generateOtherDenialReasonItems(Document document, List<Map<String, Object>> componentData, String id)
            throws ParseException
    {
        Element componentOtherDenialReason = document.createElement("foia:ComponentOtherDenialReason");
        componentOtherDenialReason.setAttribute("s:id", id);

        for (Map<String, Object> record : componentData)
        {
            String otherReasonDescription = (String) record
                    .get("Description of \"Other\" Reasons for Denial on Appeal from Chart C(2)");
            String numberOfTimesOtherReasonWasUsed = (String) record.get("Number of Times \"Other\" Reason Was Relied Upon");

            if (otherReasonDescription != null && numberOfTimesOtherReasonWasUsed != null)
            {
                Element otherDenialReason = document.createElement("foia:OtherDenialReason");

                addElement(document, otherDenialReason, "foia:OtherDenialReasonDescriptionText", otherReasonDescription);
                addElement(document, otherDenialReason, "foia:OtherDenialReasonQuantity", numberOfTimesOtherReasonWasUsed);

                componentOtherDenialReason.appendChild(otherDenialReason);
            }
        }
        return componentOtherDenialReason;
    }

    public void generateSimpleResponseTimeIncrementsSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateResponseTimeIncrementsSection(data, document, DOJReport.SIMPLE_RESPONSE_TIME_INCREMENTS);
    }

    public void generateComplexResponseTimeIncrementsSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateResponseTimeIncrementsSection(data, document, DOJReport.COMPLEX_RESPONSE_TIME_INCREMENTS);
    }

    public void generateExpeditedResponseTimeIncrementsSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateResponseTimeIncrementsSection(data, document, DOJReport.EXPEDITED_RESPONSE_TIME_INCREMENTS);
    }

    private void generateResponseTimeIncrementsSection(List<Map<String, Object>> data, Document document, DOJReport report)
            throws ParseException
    {
        int idSuffix = 1;

        Element responseTimeIncrementsSectionElement = document.createElement(report.getSectionName());

        document.appendChild(responseTimeIncrementsSectionElement);

        Collection<List<Map<String, Object>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, Object>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = (String) componentData.get(0).get("Agency / Component");

            Element componentResponseTimeIncrementsElement = generateTimeIncrementItems(document, componentData, id);
            responseTimeIncrementsSectionElement.appendChild(componentResponseTimeIncrementsElement);

            appendProcessingAssociationsToElement(document, responseTimeIncrementsSectionElement,
                    "foia:ResponseTimeIncrementsOrganizationAssociation", id, agency);
        }
    }

    private Element generateTimeIncrementItems(Document document, List<Map<String, Object>> componentData, String id)
            throws ParseException
    {
        Element componentResponseTimeIncrements = document.createElement("foia:ComponentResponseTimeIncrements");
        componentResponseTimeIncrements.setAttribute("s:id", id);

        for (Map<String, Object> record : componentData)
        {
            int totalTimeIncrementQuantity = 0;

            for (Map.Entry<String, Object> timeIncrementEntry : record.entrySet())
            {
                String timeIncrementCode = timeIncrementEntry.getKey();
                String timeIncrementQuantity = (String) timeIncrementEntry.getValue();

                timeIncrementCode = timeIncrementCode.replace("Days", "").replace("<", "").trim();

                if (NumberUtils.isParsable(timeIncrementQuantity))
                {
                    Element timeIncrementElement = document.createElement("foia:TimeIncrement");

                    addElement(document, timeIncrementElement, "foia:TimeIncrementCode", timeIncrementCode);
                    addElement(document, timeIncrementElement, "foia:TimeIncrementQuantity", timeIncrementQuantity);

                    componentResponseTimeIncrements.appendChild(timeIncrementElement);

                    totalTimeIncrementQuantity += Integer.parseInt(timeIncrementQuantity);
                }
            }

            addElement(document, componentResponseTimeIncrements, "foia:TimeIncrementTotalQuantity",
                    String.valueOf(totalTimeIncrementQuantity));

        }
        return componentResponseTimeIncrements;
    }

    public void generateAppealDispositionSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.APPEAL_DISPOSITION;

        int idSuffix = 1;

        Element appealDispositionSection = document.createElement(report.getSectionName());

        document.appendChild(appealDispositionSection);

        Collection<List<Map<String, Object>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, Object>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = (String) componentData.get(0).get("Agency / Component");

            Element componentAppealDispositionElement = generateAppealDispositionItems(document, componentData, id);
            appealDispositionSection.appendChild(componentAppealDispositionElement);

            appendProcessingAssociationsToElement(document, appealDispositionSection,
                    "foia:AppealDispositionOrganizationAssociation", id, agency);
        }

    }

    private Element generateAppealDispositionItems(Document document, List<Map<String, Object>> componentData, String id)
            throws ParseException
    {
        Element appealDispositionElement = document.createElement("foia:AppealDisposition");
        appealDispositionElement.setAttribute("s:id", id);

        for (Map<String, Object> record : componentData)
        {
            String affirmedAppealCountString = (String) record.get("Number Affirmed on Appeal");
            String partialAppealCountString = (String) record.get("Number Partially Affirmed & Partially Reversed/Remanded on Appeal");
            String reversedAppealCountString = (String) record.get("Number Completely Reversed/Remanded on Appeal");
            String closedAppealForOtherReasonCountString = (String) record.get("Number of Appeals Closed for Other Reasons");

            int affirmedAppealCount = Integer.parseInt(affirmedAppealCountString);
            int partialAppealCount = Integer.parseInt(partialAppealCountString);
            int reversedAppealCount = Integer.parseInt(reversedAppealCountString);
            int closedAppealForOtherReasonCount = Integer.parseInt(closedAppealForOtherReasonCountString);

            int totalCount = affirmedAppealCount + partialAppealCount + reversedAppealCount + closedAppealForOtherReasonCount;

            addElement(document, appealDispositionElement, "foia:AppealDispositionAffirmedQuantity", affirmedAppealCountString);
            addElement(document, appealDispositionElement, "foia:AppealDispositionPartialQuantity", partialAppealCountString);
            addElement(document, appealDispositionElement, "foia:AppealDispositionReversedQuantity", reversedAppealCountString);
            addElement(document, appealDispositionElement, "foia:AppealDispositionOtherQuantity",
                    closedAppealForOtherReasonCountString);
            addElement(document, appealDispositionElement, "foia:AppealDispositionTotalQuantity", String.valueOf(totalCount));

        }
        return appealDispositionElement;
    }

    public void generateRequestDispositionSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        DOJReport report = DOJReport.REQUEST_DISPOSITION;

        int idSuffix = 1;

        Element requestDispositionSection = document.createElement(report.getSectionName());

        document.appendChild(requestDispositionSection);

        Collection<List<Map<String, Object>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, Object>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = (String) componentData.get(0).get("Agency / Component");

            Element componentRequestDispositionElement = generateRequestDispositionItems(document, componentData, id);
            requestDispositionSection.appendChild(componentRequestDispositionElement);

            appendProcessingAssociationsToElement(document, requestDispositionSection,
                    "foia:RequestDispositionOrganizationAssociation", id, agency);
        }

    }

    private Element generateRequestDispositionItems(Document document, List<Map<String, Object>> componentData, String id)
            throws ParseException
    {
        Element appealDispositionElement = document.createElement("foia:RequestDisposition");
        appealDispositionElement.setAttribute("s:id", id);

        for (Map<String, Object> record : componentData)
        {
            String affirmedAppealCountString = (String) record.get("Number Affirmed on Appeal");
            String partialAppealCountString = (String) record.get("Number Partially Affirmed & Partially Reversed/Remanded on Appeal");
            String reversedAppealCountString = (String) record.get("Number Completely Reversed/Remanded on Appeal");
            String closedAppealForOtherReasonCountString = (String) record.get("Number of Appeals Closed for Other Reasons");

            String fullGrant = (String) record.get("Number of Full Grants");
            String partialGrant = (String) record.get("Number of Partial Grants/Partial Denials");
            String fullDenial = (String) record.get("Number of Full Denials Based on Exemptions");
            String noRecords = (String) record.get("No Records");
            String referred = (String) record.get("All Records Referred to Another Component or Agency");
            String withdrawn = (String) record.get("Request Withdrawn");
            String feeRelated = (String) record.get("Fee-Related Reason");
            String notDescribed = (String) record.get("Records not Reasonably Described");
            String improperRequests = (String) record.get("Improper FOIA Request for Other Reason");
            String notAgencyRecord = (String) record.get("Not Agency Record");
            String duplicateRequest = (String) record.get("Duplicate Request");
            String other = (String) record.get("Other");

            int totalCount = record.values().stream()
                    .filter(a -> NumberUtils.isParsable((String) a))
                    .map(a -> Integer.parseInt((String) a))
                    .mapToInt(Integer::intValue)
                    .sum();

            addElement(document, appealDispositionElement, "foia:AppealDispositionAffirmedQuantity", affirmedAppealCountString);
            addElement(document, appealDispositionElement, "foia:AppealDispositionPartialQuantity", partialAppealCountString);
            addElement(document, appealDispositionElement, "foia:AppealDispositionReversedQuantity", reversedAppealCountString);
            addElement(document, appealDispositionElement, "foia:AppealDispositionOtherQuantity",
                    closedAppealForOtherReasonCountString);
            addElement(document, appealDispositionElement, "foia:AppealDispositionTotalQuantity", String.valueOf(totalCount));

        }
        return appealDispositionElement;
    }

    public void generateAppealDispositionAppliedExemptionsSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateDispositionAppliedExemptionsSection(data, document, DOJReport.REQUEST_DISPOSITION_APPLIED_EXEMPTIONS);

    }

    public void generateRequestDispositionAppliedExemptionsSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateDispositionAppliedExemptionsSection(data, document, DOJReport.APPEAL_DISPOSITION_APPLIED_EXEMPTIONS);

    }

    private void generateDispositionAppliedExemptionsSection(List<Map<String, Object>> data, Document document, DOJReport report)
            throws ParseException
    {
        int idSuffix = 1;

        Element appealDispositionAppliedExemptionsSection = document.createElement(report.getSectionName());

        document.appendChild(appealDispositionAppliedExemptionsSection);

        Collection<List<Map<String, Object>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, Object>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = (String) componentData.get(0).get("Agency / Component");

            Element componentAppealDispositionAppliedExemptionsElement = generateAppealDispositionAppliedExemptionsItems(document,
                    componentData, id);
            appealDispositionAppliedExemptionsSection.appendChild(componentAppealDispositionAppliedExemptionsElement);

            appendProcessingAssociationsToElement(document, appealDispositionAppliedExemptionsSection,
                    "foia:ComponentAppliedExemptionsOrganizationAssociation", id, agency);
        }
    }

    private Element generateAppealDispositionAppliedExemptionsItems(Document document, List<Map<String, Object>> componentData, String id)
            throws ParseException
    {
        Element componentAppliedExemptionsElement = document.createElement("foia:ComponentAppliedExemptions");
        componentAppliedExemptionsElement.setAttribute("s:id", id);

        for (Map<String, Object> record : componentData)
        {
            for (Map.Entry<String, Object> exemptionEntry : record.entrySet())
            {
                String exemptionCode = exemptionEntry.getKey();
                String exemptionQuantity = (String) exemptionEntry.getValue();

                if (NumberUtils.isParsable(exemptionQuantity) && Integer.parseInt(exemptionQuantity) > 0)
                {
                    Element appliedExemptionElement = document.createElement("foia:AppliedExemption");

                    addElement(document, appliedExemptionElement, "foia:AppliedExemptionCode", exemptionCode);
                    addElement(document, appliedExemptionElement, "foia:AppliedExemptionQuantity", exemptionQuantity);

                    componentAppliedExemptionsElement.appendChild(appliedExemptionElement);
                }
            }

        }
        return componentAppliedExemptionsElement;
    }

    public void generateProcessedRequestComparisonSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateProcessedComparisonSection(data, document, DOJReport.REQUEST_PROCESSED_COMPARISON);
    }

    public void generateProcessedAppealsComparisonSection(List<Map<String, Object>> data, Document document) throws ParseException
    {
        generateProcessedComparisonSection(data, document, DOJReport.APPEAL_PROCESSED_COMPARISON);
    }

    private void generateProcessedComparisonSection(List<Map<String, Object>> data, Document document, DOJReport report)
            throws ParseException
    {
        int idSuffix = 1;

        Element processedComparisonSection = document.createElement(report.getSectionName());

        document.appendChild(processedComparisonSection);

        Collection<List<Map<String, Object>>> dataByComponents = filterDataAndGroupByComponent(data);

        for (List<Map<String, Object>> componentData : dataByComponents)
        {
            String id = report.getIdPrefix().concat(String.valueOf(idSuffix));
            idSuffix++;

            String agency = (String) componentData.get(0).get("Agency / Component");

            Element processingComparisonElement = generateProcessingComparisonItems(document, componentData, id);
            processedComparisonSection.appendChild(processingComparisonElement);

            appendProcessingAssociationsToElement(document, processedComparisonSection,
                    "foia:ProcessingComparisonOrganizationAssociation", id, agency);
        }
    }

    private Element generateProcessingComparisonItems(Document document, List<Map<String, Object>> componentData, String id)
            throws ParseException
    {
        Element processingComparisonElement = document.createElement("foia:ProcessingComparison");
        processingComparisonElement.setAttribute("s:id", id);

        for (Map<String, Object> record : componentData)
        {
            String itemsReceivedLastYear = (String) record.get("Number Received During Fiscal Year from Last Year's Annual Report");
            String itemsReceivedCurrentYear = (String) record.get("Number Received During Fiscal Year from Current Annual Report");
            String itemsProcessedLastYear = (String) record.get("Number Processed During Fiscal Year from Last Year's Annual Report");
            String itemsProcessedCurrentYear = (String) record
                    .get("Number Processed During Fiscal Year from Current Annual Report");

            addElement(document, processingComparisonElement, "foia:ItemsReceivedLastYearQuantity", itemsReceivedLastYear);
            addElement(document, processingComparisonElement, "foia:ItemsReceivedCurrentYearQuantity", itemsReceivedCurrentYear);
            addElement(document, processingComparisonElement, "foia:ItemsProcessedLastYearQuantity", itemsProcessedLastYear);
            addElement(document, processingComparisonElement, "foia:ItemsProcessedCurrentYearQuantity",
                    itemsProcessedCurrentYear);

        }
        return processingComparisonElement;
    }

    private Collection<List<Map<String, Object>>> filterDataAndGroupByComponent(List<Map<String, Object>> data)
    {
        return data.stream()
                .filter(it -> !((String) it.get("Agency / Component")).contains("Total"))
                .collect(groupingBy(it -> it.get("Agency / Component")))
                .values();
    }

    private void addElement(Document doc, Element parent, String elemName,
            String elemValue)
    {
        Element elem = doc.createElement(elemName);
        elem.appendChild(doc.createTextNode(elemValue));
        parent.appendChild(elem);
    }

    private void appendProcessingAssociationsToElement(Document document, Element parentElement, String associationsElementName, String id,
            String agency)
    {
        Element organizationAssociationElement = document.createElement(associationsElementName);

        addProcessingAssociations(document, organizationAssociationElement, agency, id);

        parentElement.appendChild(organizationAssociationElement);
    }

    private void addProcessingAssociations(Document document, Element parentElement, String agency, String id)
    {
        Element componentDataReference = document.createElement("foia:ComponentDataReference");
        componentDataReference.setAttribute("s:ref", id);
        parentElement.appendChild(componentDataReference);

        Element organizationReference = document.createElement("nc:OrganizationReference");
        organizationReference.setAttribute("s:ref", componentMap.get(agency));
        parentElement.appendChild(organizationReference);
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

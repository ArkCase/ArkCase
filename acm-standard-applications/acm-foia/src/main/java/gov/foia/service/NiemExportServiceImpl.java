package gov.foia.service;

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

import static java.util.stream.Collectors.toList;

import com.armedia.acm.pentaho.config.PentahoReportsConfig;
import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.service.ReportService;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.Year;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.foia.model.FoiaConfig;

public class NiemExportServiceImpl implements NiemExportService
{
    public static final String IEPD_FOIA_ANNUAL_REPORT_TAG = "iepd:FoiaAnnualReport";
    public static final String NIEM_REFERENCE_ATTRIBUTE = "s:ref";

    public static final String AGENCY_IDENTIFIER_COLUMN = "Agency / Component";
    public static final String LOWER_THAN_ONE_IDENTIFIER = "LT1";
    public static final String COMPONENT_DATA_REFERENCE = "ComponentDataReference";
    public static final String ORGANIZATION_REFERENCE = "OrganizationReference";
    public static final String GRAND_TOTAL_ROW = "Grand Total";

    private final Logger LOG = LogManager.getLogger(getClass());
    private LookupDao lookupDao;
    private ReportService reportService;
    private PentahoReportsConfig reportsConfig;
    private FoiaConfig foiaConfig;

    @Override
    public File exportSingleReport(DOJReport report) throws IOException, TransformerException, ParserConfigurationException
    {
        LOG.debug("Started exporting a single DOJ Yearly report section in NIEM XML Format");
        int fiscalYear = Year.now().getValue();

        List<Report> acmReports = reportService.getAcmReports();
        Map<String, String> dojYearlyReports = foiaConfig.getDojYearlyReports();
        Map<String, String> agencyIdentifiers = getComponentAgencies();

        File temp = File.createTempFile("reports-merged-file", ".xml");
        try (FileOutputStream fileOutputStream = new FileOutputStream(temp))
        {
            Document document = createYearlyReportDocumentWithBaseData(agencyIdentifiers, fiscalYear);
            Element foiaAnnualReport = (Element) document.getElementsByTagName(IEPD_FOIA_ANNUAL_REPORT_TAG).item(0);

            List<Map<String, String>> data = exportPentahoReportToDataList(acmReports, dojYearlyReports, report, fiscalYear);
            if (data != null)
            {
                appendReportSection(data, foiaAnnualReport, agencyIdentifiers, report);
            }
            else
            {
                Element emptyReportElement = document.createElement(report.getSectionName());
                foiaAnnualReport.appendChild(emptyReportElement);
                LOG.warn("Report {} won't be included and may not exists.", report.name());
            }

            checkDoc(document);

            transformXMLToFile(document, fileOutputStream);

        }

        LOG.debug("Finished exporting a single DOJ Yearly report section in NIEM XML Format");

        return temp;
    }

    @Override
    @Async
    public void exportYearlyReport(int fiscalYear, Authentication auth)
            throws IOException, TransformerException, ParserConfigurationException
    {
        File exportFile = exportYearlyReport(fiscalYear);
        reportService.sendReportsExport(exportFile.getPath(), fiscalYear, auth);
    }

    @Override
    public File exportYearlyReport(int fiscalYear) throws IOException, TransformerException, ParserConfigurationException
    {
        LOG.debug("Started exporting all DOJ Yearly reports in NIEM XML Format");

        List<Report> acmReports = reportService.getAcmReports();
        Map<String, String> dojYearlyReports = foiaConfig.getDojYearlyReports();
        Map<String, String> agencyIdentifiers = getComponentAgencies();

        File temp = File.createTempFile("reports-merged-file", ".xml");
        try (FileOutputStream fileOutputStream = new FileOutputStream(temp))
        {
            Document document = createYearlyReportDocumentWithBaseData(agencyIdentifiers, fiscalYear);
            Element foiaAnnualReport = (Element) document.getElementsByTagName(IEPD_FOIA_ANNUAL_REPORT_TAG).item(0);

            Map<String, List<Map<String, String>>> downloadedReportsData = Arrays.asList(DOJReport.values())
                    .parallelStream()
                    .map(report -> {

                        List<Map<String, String>> data = exportPentahoReportToDataList(acmReports, dojYearlyReports, report, fiscalYear);
                        return new AbstractMap.SimpleEntry<>(report.name(), data);

                    })
                    .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);

            for (DOJReport report : DOJReport.values())
            {
                List<Map<String, String>> data = downloadedReportsData.get(report.name());
                if (data != null)
                {
                    appendReportSection(data, foiaAnnualReport, agencyIdentifiers, report);
                }
                else
                {
                    Element emptyReportElement = document.createElement(report.getSectionName());
                    foiaAnnualReport.appendChild(emptyReportElement);
                    LOG.warn("Report {} won't be included and may not exists.", report.name());
                }
            }
            checkDoc(document);

            transformXMLToFile(document, fileOutputStream);
        }

        LOG.debug("Finished exporting all DOJ Yearly reports in NIEM XML Format");

        return temp;
    }

    private List<Map<String, String>> exportPentahoReportToDataList(List<Report> acmReports, Map<String, String> dojYearlyReports,
            DOJReport report, int fiscalYear)
    {
        String dojReportPropertyName = dojYearlyReports.get(report.name());
        Report acmReport = acmReports.stream().filter(rep -> rep.getPropertyName().equals(dojReportPropertyName)).findAny()
                .orElse(null);

        List<Map<String, String>> data = null;

        if (acmReport != null)
        {
            String reportPath = acmReport.getName();

            String path = reportsConfig.getServerUrl() + reportsConfig.getReportUrl() + reportPath
                    + "/service/export?FISCAL_YEAR=" + fiscalYear + "&format=CSV";

            ResponseEntity<Resource> response = exportPenathoReportInCSV(path);

            try
            {
                InputStream csvInputStream = response.getBody().getInputStream();
                data = NiemExportUtils.getDataMapFromCSVInputStream(csvInputStream);
            }
            catch (IOException e)
            {
                LOG.warn("Failed to export report {}.", reportPath);
            }

        }
        return data;
    }

    private void transformXMLToFile(Document document, FileOutputStream fileOutputStream) throws TransformerException
    {
        TransformerFactory factory = TransformerFactory.newInstance();
        /**
         * com.sun.org.apache.xalan.internal.xsltc.trax - JDK
         * org.apache.xalan.processor - Xalan
         * org.apache.xalan.xsltc.trax - Xalan
         * 
         * those are TransformerFactory implementation providers and not sure which implementation doesn't support below XMLConstants 
         * that's why suppressing IllegalArgumentException.
         */
     
        try
        {
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        }
        catch (IllegalArgumentException e)
        {
            // TODO: handle exception
        }

        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        transformer.transform(new DOMSource(document), new StreamResult(fileOutputStream));
    }

    @Override
    public List<String> getYearlyReportTitlesOrdered()
    {
        Map<String, String> dojYearlyReports = foiaConfig.getDojYearlyReports();
        List<Report> allPentahoReports = reportService.getAcmReports();

        List<String> reportTitles = new LinkedList<>();

        for (DOJReport report : DOJReport.values())
        {
            String dojReportPropertyName = dojYearlyReports.get(report.name());

            allPentahoReports.stream()
                    .filter(rep -> rep.getPropertyName().equals(dojReportPropertyName))
                    .findAny()
                    .ifPresent(acmReport -> reportTitles.add(acmReport.getName()));
        }
        return reportTitles;
    }

    /**
     * This method will check and report for null data in the generated XML document
     * If such data exists, the document cannot be exported
     */
    private void checkDoc(Node node)
    {
        if (node instanceof Text)
        {
            if (((Text) node).getData() == null)
            {
                LOG.warn("Null text data in [{}]", node.getParentNode());
            }
        }

        NodeList l = node.getChildNodes();
        for (int i = 0; i < l.getLength(); ++i)
        {
            checkDoc(l.item(i));
        }
    }

    private ResponseEntity<Resource> exportPenathoReportInCSV(String path)
    {
        RestTemplate restTemplate = getReportService().buildReportsRestTemplate();
        HttpEntity<Object> entity = getReportService().buildReportsRestEntity();

        return restTemplate.exchange(path, HttpMethod.GET, entity, Resource.class);
    }

    private Map<String, String> getComponentAgencies()
    {
        Map<String, String> agencyIdentifiers = new LinkedHashMap<>();

        List<StandardLookupEntry> componentAgenciesLookupEntries = getLookupDao()
                .getLookupByName("componentsAgencies")
                .getEntries()
                .stream()
                .map(obj -> (StandardLookupEntry) obj)
                .collect(Collectors.toList());

        for (int i = 0; i < componentAgenciesLookupEntries.size(); i++)
        {
            agencyIdentifiers.put(componentAgenciesLookupEntries.get(i).getKey(), String.format("ORG%d", i + 1));
        }

        return agencyIdentifiers;
    }

    @Override
    public Document createYearlyReportDocumentWithBaseData(Map<String, String> agencyIdentifiers, int fiscalYear)
            throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        Element foiaAnnualReport = document.createElement(IEPD_FOIA_ANNUAL_REPORT_TAG);

        foiaAnnualReport.setAttribute("xmlns:iepd", "http://leisp.usdoj.gov/niem/FoiaAnnualReport/exchange/1.03");
        foiaAnnualReport.setAttribute("xsi:schemaLocation",
                "http://leisp.usdoj.gov/niem/FoiaAnnualReport/exchange/1.03 ../schema/exchange/FoiaAnnualReport.xsd");
        foiaAnnualReport.setAttribute("xmlns:foia", "http://leisp.usdoj.gov/niem/FoiaAnnualReport/extension/1.03");
        foiaAnnualReport.setAttribute("xmlns:j", "http://niem.gov/niem/domains/jxdm/4.1");
        foiaAnnualReport.setAttribute("xmlns:nc", "http://niem.gov/niem/niem-core/2.0");
        foiaAnnualReport.setAttribute("xmlns:s", "http://niem.gov/niem/structures/2.0");
        foiaAnnualReport.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        document.appendChild(foiaAnnualReport);

        Element docApplicationName = document.createElement("nc:DocumentApplicationName");
        docApplicationName.setAttribute("nc:applicationVersionText", "1.1");
        docApplicationName.appendChild(document.createTextNode("FOIA Annual Report Workbook"));
        foiaAnnualReport.appendChild(docApplicationName);

        Element docCreationDate = document.createElement("nc:DocumentCreationDate");
        addElement(docCreationDate, "nc:Date", NiemExportUtils.currentDateInNiemFormat());
        foiaAnnualReport.appendChild(docCreationDate);

        addElement(foiaAnnualReport, "nc:DocumentDescriptionText", "FOIA Annual Report");

        appendOrganizationSection(foiaAnnualReport, agencyIdentifiers);

        addElement(foiaAnnualReport, "foia:DocumentFiscalYearDate", String.valueOf(fiscalYear));

        return document;
    }

    @Override
    public void appendReportSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)
    {
        switch (report)
        {
        case EXEMPTION_3_STATUTE:
            generateExemption3StatuteSection(data, parent, agencyIdentifiers);
            break;
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
        case APPEAL_DENIAL_OTHER_REASON:
            generateAppealDenialOtherReasonSection(data, parent, agencyIdentifiers);
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
        case ALL_PENDING_PERFECTED_REQUESTS:
            generatePendingPerfectedRequestsSection(data, parent, agencyIdentifiers);
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

    private void appendOrganizationSection(Element parent, Map<String, String> agencyIdentifiers)
    {
        Element organizationElement = parent.getOwnerDocument().createElement("nc:Organization");
        organizationElement.setAttribute("s:id", "ORG0");

        addElement(organizationElement, "nc:OrganizationAbbreviationText", foiaConfig.getDojNiemOrganizationName());
        addElement(organizationElement, "nc:OrganizationName", foiaConfig.getDojNiemAbbreviationText());

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

    /**
     *
     * Section: IV. EXEMPTION 3 STATUTES
     * 
     * Note: This sections organization associations elements differs from all other sections due to the fact that
     * it includes the quantity of exemptions used in the association elements as opposed to the main data
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateExemption3StatuteSection(List<Map<String, String>> data, Element parent, Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.EXEMPTION_3_STATUTE;

        Element exemption3StatuteSection = parent.getOwnerDocument().createElement(report.getSectionName());

        int id = 1;

        for (List<Map<String, String>> exemptionData : groupDataByExemption(data).values())
        {
            String componentDataReference = report.getIdPrefix() + id;
            id++;
            exemptionData = getDataWithPredifinedComponentReference(exemptionData, agencyIdentifiers, componentDataReference);

            appendReliedUponStatuteElement(exemption3StatuteSection, exemptionData, componentDataReference);
        }

        for (Map<String, String> componentData : data)
        {
            appendReliedUponStatuteOrganizationAssociation(exemption3StatuteSection, componentData);
        }

        parent.appendChild(exemption3StatuteSection);
    }

    private void appendReliedUponStatuteElement(Element exemption3StatuteSection, List<Map<String, String>> exemptionData,
            String componentDataReference)
    {
        String statuteDescriptionText = exemptionData.get(0).get("Statute");
        String infoWithheldText = exemptionData.get(0).get("Type of Information Withheld");
        String caseCitationText = exemptionData.get(0).get("Case Citation");

        Element reliedUponStatuteElement = exemption3StatuteSection.getOwnerDocument().createElement("foia:ReliedUponStatute");

        addElement(reliedUponStatuteElement, "j:StatuteDescriptionText", statuteDescriptionText);
        addElement(reliedUponStatuteElement, "foia:ReliedUponStatuteInformationWithheldText", infoWithheldText);

        Element caseElement = exemption3StatuteSection.getOwnerDocument().createElement("nc:Case");
        addElement(caseElement, "nc:CaseTitleText", caseCitationText);
        reliedUponStatuteElement.appendChild(caseElement);

        reliedUponStatuteElement.setAttribute("s:id", componentDataReference);
        exemption3StatuteSection.appendChild(reliedUponStatuteElement);
    }

    private void appendReliedUponStatuteOrganizationAssociation(Element parent, Map<String, String> record)
    {
        Element reliedUponStatuteElement = parent.getOwnerDocument().createElement("foia:ReliedUponStatuteOrganizationAssociation");

        Element componentDataReference = parent.getOwnerDocument().createElement("foia:ComponentDataReference");
        componentDataReference.setAttribute(NIEM_REFERENCE_ATTRIBUTE, record.get(COMPONENT_DATA_REFERENCE));
        reliedUponStatuteElement.appendChild(componentDataReference);

        Element organizationReference = parent.getOwnerDocument().createElement("nc:OrganizationReference");
        organizationReference.setAttribute(NIEM_REFERENCE_ATTRIBUTE, record.get(ORGANIZATION_REFERENCE));
        reliedUponStatuteElement.appendChild(organizationReference);

        String reliedUponStatute = record.get("Number of Times Relied upon by Agency Overall");
        addElement(reliedUponStatuteElement, "foia:ReliedUponStatuteQuantity", reliedUponStatute);

        parent.appendChild(reliedUponStatuteElement);
    }

    /**
     *
     * Section: VI.C.(5). TEN OLDEST PENDING ADMINISTRATIVE APPEALS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateOldestPendingAppealsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateOldestPendingSection(data, parent, agencyIdentifiers, DOJReport.OLDEST_PENDING_APPEALS);
    }

    /**
     *
     * Section: VII.E. PENDING REQUESTS -- TEN OLDEST PENDING PERFECTED REQUESTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateOldestPendingRequestsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateOldestPendingSection(data, parent, agencyIdentifiers, DOJReport.OLDEST_PENDING_REQUESTS);
    }

    /**
     *
     * Section: XII.C. CONSULTATIONS ON FOIA REQUESTS -- TEN OLDEST CONSULTATIONS RECEIVED FROM OTHER AGENCIES AND
     * PENDING AT THE AGENCY
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateOldestPendingConsultationsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateOldestPendingSection(data, parent, agencyIdentifiers, DOJReport.OLDEST_PENDING_CONSULTATIONS);
    }

    private void generateOldestPendingSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)
    {
        Element oldestPendingSectionElement = parent.getOwnerDocument().createElement(report.getSectionName());

        parent.appendChild(oldestPendingSectionElement);

        int id = 1;

        Collection<List<Map<String, String>>> dataByComponent = groupByComponentAndFilter(data).values();

        for (List<Map<String, String>> componentData : dataByComponent)
        {
            String componentDataReference = report.getIdPrefix() + id;
            id++;
            componentData = getDataWithPredifinedComponentReference(componentData, agencyIdentifiers, componentDataReference);

            appendOldestPendingItems(oldestPendingSectionElement, componentData, componentDataReference);
        }

        for (List<Map<String, String>> componentData : dataByComponent)
        {
            appendProcessingAssociations(oldestPendingSectionElement, componentData.get(0),
                    "foia:OldestPendingItemsOrganizationAssociation");
        }
    }

    private void appendOldestPendingItems(Element parent, List<Map<String, String>> componentData, String id)
    {
        Element oldestPendingItemsElement = parent.getOwnerDocument().createElement("foia:OldestPendingItems");
        oldestPendingItemsElement.setAttribute("s:id", id);

        // The data is expected to be in reversed order - the oldest element needs to be first
        Collections.reverse(componentData);

        for (Map<String, String> record : componentData)
        {
            String dateOfReceipt = record.get("Date of Receipt");
            String daysPending = record.get("Number of Days Pending");

            try
            {
                String formattedDate = NiemExportUtils.formatDateToNiemExpectedFormat(dateOfReceipt);

                if (NumberUtils.isParsable(daysPending) && Integer.parseInt(daysPending) > 0)
                {
                    Element oldItem = parent.getOwnerDocument().createElement("foia:OldItem");

                    addElement(oldItem, "foia:OldItemReceiptDate", formattedDate);
                    addElement(oldItem, "foia:OldItemPendingDaysQuantity", daysPending);

                    oldestPendingItemsElement.appendChild(oldItem);
                }
            }
            catch (ParseException e)
            {
                LOG.warn("Date: [{}] cannot be parsed!", dateOfReceipt);
            }

        }
        parent.appendChild(oldestPendingItemsElement);
    }

    /**
     *
     * Section: V.B.(2). DISPOSITION OF FOIA REQUESTS -- "OTHER" REASONS FOR "FULL DENIALS BASED ON REASONS OTHER THAN
     * EXEMPTIONS"
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateRequestDenialOtherReasonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateDenialOtherReasonSection(data, parent, agencyIdentifiers, DOJReport.REQUEST_DENIAL_OTHER_REASON);
    }

    /**
     *
     * Section: VI.C.(3). REASONS FOR DENIAL ON APPEAL -- "OTHER" REASONS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateAppealDenialOtherReasonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateDenialOtherReasonSection(data, parent, agencyIdentifiers, DOJReport.APPEAL_DENIAL_OTHER_REASON);
    }

    private void generateDenialOtherReasonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)
    {
        Element denialOtherReasonSectionElement = parent.getOwnerDocument().createElement(report.getSectionName());

        // The data needs to be rearranged in order to use the standard ways of generating elements
        String descriptionColumn;
        String quantityColumn;
        if (report == DOJReport.REQUEST_DENIAL_OTHER_REASON)
        {
            descriptionColumn = "Description of \"Other\" Reasons for Denials from Chart B(1)";
        }
        else
        {
            descriptionColumn = "Description of \"Other\" Reasons for Denials from Chart C(2)";
        }
        quantityColumn = "Number of Times \"Other\" Reason Was Relied Upon";
        List<Map<String, String>> rearrangedData = rearrangeMultiLineData(data, descriptionColumn, quantityColumn);
        List<Map<String, String>> filteredData = getDataWithComponentReferences(rearrangedData, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendOtherDenialReasonItem(denialOtherReasonSectionElement, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(denialOtherReasonSectionElement,
                componentData, "foia:OtherDenialReasonOrganizationAssociation"));

        parent.appendChild(denialOtherReasonSectionElement);
    }

    private void appendOtherDenialReasonItem(Element parent, Map<String, String> record)
    {
        Element componentOtherDenialReason = parent.getOwnerDocument().createElement("foia:ComponentOtherDenialReason");
        componentOtherDenialReason.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

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

    /**
     *
     * Section: VII.A. FOIA REQUESTS -- RESPONSE TIME FOR ALL PROCESSED PERFECTED REQUESTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateProcessedRequestResponseTimeSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        appendProcessedRequestResponseTimeSection(data, parent, agencyIdentifiers, DOJReport.PROCESSED_PERFECTED_REQUESTS_RESPONSE_TIME);
    }

    /**
     *
     * Section: VII.B. PROCESSED REQUESTS -- RESPONSE TIME FOR PERFECTED REQUESTS IN WHICH INFORMATION WAS GRANTED
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateInformationGrantedResponseTimeSection(List<Map<String, String>> data, Element parent,
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
        filteredData.forEach(componentData -> appendProcessingAssociations(processedRequestResponseTimeElement,
                componentData, "foia:ProcessedResponseTimeOrganizationAssociation"));

        parent.appendChild(processedRequestResponseTimeElement);
    }

    private void appendProcessedResponseTimeItem(Element parent, Map<String, String> record)
    {
        Element processedResponseTimeElement = parent.getOwnerDocument().createElement("foia:ProcessedResponseTime");
        processedResponseTimeElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

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

    /*
     * The xml uses different tag names for the response time data
     * If a value is lower than 1, the FOIA NIEM standard expects the use of a Code tag name and LT1 as value
     */
    private void addResponseTimeElementIfValid(Element parent, String numberOfDays, String codeName, String valueName)
    {
        if (NumberUtils.isParsable(numberOfDays) && Double.parseDouble(numberOfDays) >= 0
                && Double.parseDouble(numberOfDays) < 1)
        {
            addElement(parent, codeName, LOWER_THAN_ONE_IDENTIFIER);
        }
        else if (NumberUtils.isParsable(numberOfDays) && Double.parseDouble(numberOfDays) >= 1)
        {
            addElement(parent, valueName, numberOfDays);
        }
    }

    /**
     *
     * Section: VII.D. PENDING REQUESTS -- ALL PENDING PERFECTED REQUESTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generatePendingPerfectedRequestsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.ALL_PENDING_PERFECTED_REQUESTS;
        Element processedRequestResponseTimeElement = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendPendingPerfectedRequestsItem(processedRequestResponseTimeElement, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(processedRequestResponseTimeElement,
                componentData, "foia:PendingPerfectedRequestsOrganizationAssociation"));

        parent.appendChild(processedRequestResponseTimeElement);
    }

    private void appendPendingPerfectedRequestsItem(Element parent, Map<String, String> record)
    {
        Element pendingPerfectedRequestsElement = parent.getOwnerDocument().createElement("foia:PendingPerfectedRequests");
        pendingPerfectedRequestsElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        Element simplePendingRequestsElement = parent.getOwnerDocument().createElement("foia:SimplePendingRequestStatistics");
        appendPendingRequestStatisticsPerTrack(simplePendingRequestsElement, record, "simple");
        pendingPerfectedRequestsElement.appendChild(simplePendingRequestsElement);

        Element complexPendingRequestsElement = parent.getOwnerDocument().createElement("foia:ComplexPendingRequestStatistics");
        appendPendingRequestStatisticsPerTrack(complexPendingRequestsElement, record, "complex");
        pendingPerfectedRequestsElement.appendChild(complexPendingRequestsElement);

        Element expeditedPendingRequestsElement = parent.getOwnerDocument().createElement("foia:ExpeditedPendingRequestStatistics");
        appendPendingRequestStatisticsPerTrack(expeditedPendingRequestsElement, record, "expedited");
        pendingPerfectedRequestsElement.appendChild(expeditedPendingRequestsElement);

        parent.appendChild(pendingPerfectedRequestsElement);
    }

    private void appendPendingRequestStatisticsPerTrack(Element parent, Map<String, String> record, String track)
    {
        String numberPending = record.get(track + "~Number Pending");
        String medianNumberOfDays = record.get(track + "~Median Number of Days");
        String averageNumberOfDays = record.get(track + "~Average Number of Days");

        if (!NumberUtils.isParsable(numberPending))
        {
            numberPending = "0";
        }
        addElement(parent, "foia:PendingRequestQuantity", numberPending);
        if (NumberUtils.isParsable(numberPending) && Double.parseDouble(numberPending) > 0)
        {
            addElement(parent, "foia:PendingRequestMedianDaysValue", medianNumberOfDays);
            addElement(parent, "foia:PendingRequestAverageDaysValue", averageNumberOfDays);
        }

    }

    /**
     *
     * Section: VII.C.1 PROCESSED SIMPLE REQUESTS -- RESPONSE TIME IN DAY INCREMENTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateSimpleResponseTimeIncrementsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateResponseTimeIncrementsSection(data, parent, agencyIdentifiers, DOJReport.SIMPLE_RESPONSE_TIME_INCREMENTS);
    }

    /**
     *
     * Section: VII.C.2 PROCESSED COMPLEX REQUESTS -- RESPONSE TIME IN DAY INCREMENTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateExpeditedResponseTimeIncrementsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateResponseTimeIncrementsSection(data, parent, agencyIdentifiers, DOJReport.EXPEDITED_RESPONSE_TIME_INCREMENTS);
    }

    /**
     *
     * Section: VII.C.3 PROCESSED REQUESTS GRANTED EXPEDITED PROCESSING -- RESPONSE TIME IN DAY INCREMENTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateComplexResponseTimeIncrementsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateResponseTimeIncrementsSection(data, parent, agencyIdentifiers, DOJReport.COMPLEX_RESPONSE_TIME_INCREMENTS);
    }

    private void generateResponseTimeIncrementsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)

    {
        Element responseTimeIncrementsSectionElement = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendTimeIncrementItem(responseTimeIncrementsSectionElement, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(responseTimeIncrementsSectionElement,
                componentData, "foia:ResponseTimeIncrementsOrganizationAssociation"));

        parent.appendChild(responseTimeIncrementsSectionElement);
    }

    private void appendTimeIncrementItem(Element parent, Map<String, String> record)
    {
        Element componentResponseTimeIncrements = parent.getOwnerDocument().createElement("foia:ComponentResponseTimeIncrements");
        componentResponseTimeIncrements.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

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

    /**
     *
     * Section: VI.B. DISPOSITION OF ADMINISTRATIVE APPEALS -- ALL PROCESSED APPEALS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateAppealDispositionSection(List<Map<String, String>> data, Element parent, Map<String, String> agencyIdentifiers)

    {
        DOJReport report = DOJReport.APPEAL_DISPOSITION;

        Element appealDispositionSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendAppealDispositionItem(appealDispositionSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(appealDispositionSection,
                componentData, "foia:AppealDispositionOrganizationAssociation"));

        parent.appendChild(appealDispositionSection);
    }

    private void appendAppealDispositionItem(Element parent, Map<String, String> record)
    {
        Element appealDispositionElement = parent.getOwnerDocument().createElement("foia:AppealDisposition");
        appealDispositionElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

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

    /**
     *
     * Section: V.B.(1). DISPOSITION OF FOIA REQUESTS -- ALL PROCESSED REQUESTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateRequestDispositionSection(List<Map<String, String>> data, Element parent, Map<String, String> agencyIdentifiers)

    {
        DOJReport report = DOJReport.REQUEST_DISPOSITION;

        Element requestDispositionSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendRequestDispositionItem(requestDispositionSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(requestDispositionSection,
                componentData, "foia:RequestDispositionOrganizationAssociation"));

        parent.appendChild(requestDispositionSection);
    }

    private void appendRequestDispositionItem(Element parent, Map<String, String> record)
    {
        Element requestDispositionElement = parent.getOwnerDocument().createElement("foia:RequestDisposition");
        requestDispositionElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

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

    /**
     *
     * Section: VI.C.(2). REASONS FOR DENIAL ON APPEAL -- REASONS OTHER THAN EXEMPTIONS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateAppealNonExemptionDenialSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.APPEAL_NON_EXEMPTION_DENIAL;

        Element appealNonExemptionSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(componentData -> appendAppealNonExemptionDenialItem(appealNonExemptionSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(appealNonExemptionSection,
                componentData, "foia:AppealNonExemptionDenialOrganizationAssociation"));

        parent.appendChild(appealNonExemptionSection);
    }

    private void appendAppealNonExemptionDenialItem(Element parent, Map<String, String> record)
    {
        Element appealDispositionElement = parent.getOwnerDocument().createElement("foia:AppealNonExemptionDenial");
        appealDispositionElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

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

    /**
     *
     * Section: VI.C.(1). REASONS FOR DENIAL ON APPEAL -- NUMBER OF TIMES EXEMPTIONS APPLIED
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateAppealDispositionAppliedExemptionsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        appendDispositionAppliedExemptionsSection(data, parent, agencyIdentifiers, DOJReport.APPEAL_DISPOSITION_APPLIED_EXEMPTIONS);
    }

    /**
     *
     * Section: V.B.(3). DISPOSITION OF FOIA REQUESTS -- NUMBER OF TIMES EXEMPTIONS APPLIED
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateRequestDispositionAppliedExemptionsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        appendDispositionAppliedExemptionsSection(data, parent, agencyIdentifiers, DOJReport.REQUEST_DISPOSITION_APPLIED_EXEMPTIONS);
    }

    private void appendDispositionAppliedExemptionsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)

    {
        Element dispositionAppliedExemptionsSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendAppealDispositionAppliedExemptionsItem(dispositionAppliedExemptionsSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(dispositionAppliedExemptionsSection,
                componentData, "foia:ComponentAppliedExemptionsOrganizationAssociation"));

        parent.appendChild(dispositionAppliedExemptionsSection);
    }

    private void appendAppealDispositionAppliedExemptionsItem(Element parent, Map<String, String> record)
    {
        Element componentAppliedExemptionsElement = parent.getOwnerDocument().createElement("foia:ComponentAppliedExemptions");
        componentAppliedExemptionsElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

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

    /**
     *
     * Section: XII.D.(1). COMPARISON OF NUMBERS OF REQUESTS FROM PREVIOUS AND CURRENT ANNUAL REPORT -- REQUESTS
     * RECEIVED AND PROCESSED
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateProcessedRequestComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateProcessedComparisonSection(data, parent, agencyIdentifiers, DOJReport.REQUEST_PROCESSED_COMPARISON);
    }

    /**
     *
     * Section: XII.E.(1). COMPARISON OF NUMBERS OF ADMINISTRATIVE APPEALS FROM PREVIOUS AND CURRENT ANNUAL REPORT --
     * APPEALS RECEIVED AND PROCESSED
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateProcessedAppealsComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateProcessedComparisonSection(data, parent, agencyIdentifiers, DOJReport.APPEAL_PROCESSED_COMPARISON);
    }

    private void generateProcessedComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)

    {
        Element processedComparisonSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendProcessingComparisonItem(processedComparisonSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(processedComparisonSection,
                componentData, "foia:ProcessingComparisonOrganizationAssociation"));

        parent.appendChild(processedComparisonSection);
    }

    private void appendProcessingComparisonItem(Element parent, Map<String, String> record)
    {
        Element processingComparisonElement = parent.getOwnerDocument().createElement("foia:ProcessingComparison");
        processingComparisonElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

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

    /**
     *
     * Section: V.A. FOIA REQUESTS -- RECEIVED, PROCESSED AND PENDING FOIA REQUESTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateProcessedRequestSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateProcessedSection(data, parent, agencyIdentifiers, DOJReport.PROCESSED_REQUESTS);
    }

    /**
     *
     * Section: VI.B. DISPOSITION OF ADMINISTRATIVE APPEALS -- ALL PROCESSED APPEALS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateProcessedAppealsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateProcessedSection(data, parent, agencyIdentifiers, DOJReport.PROCESSED_APPEALS);
    }

    /**
     *
     * Section: XII.B. CONSULTATIONS ON FOIA REQUESTS -- RECEIVED, PROCESSED, AND PENDING CONSULTATIONS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateProcessedConsultationsSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateProcessedSection(data, parent, agencyIdentifiers, DOJReport.PROCESSED_CONSULTATIONS);
    }

    private void generateProcessedSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)

    {
        Element processedSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendProcessingItem(processedSection, componentData, report));
        filteredData.forEach(componentData -> appendProcessingAssociations(processedSection,
                componentData, "foia:ProcessingStatisticsOrganizationAssociation"));

        parent.appendChild(processedSection);
    }

    private void appendProcessingItem(Element parent, Map<String, String> record, DOJReport report)
    {
        Element processingElement = parent.getOwnerDocument().createElement("foia:ProcessingStatistics");
        processingElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String itemsPendingAtStart;
        String itemsReceived;
        String itemsProcessed;
        String itemsPendingAtEnd;

        if (report == DOJReport.PROCESSED_REQUESTS)
        {
            itemsPendingAtStart = record.get("Number of Requests Pending as of Start of  Fiscal Year");
            itemsReceived = record.get("Number of Requests Received in Fiscal Year");
            itemsProcessed = record.get("Number of Requests Processed in Fiscal Year");
            itemsPendingAtEnd = record.get("Number of Requests Pending as of End of Fiscal Year");
        }
        else if (report == DOJReport.PROCESSED_APPEALS)
        {
            itemsPendingAtStart = record.get("Number of Appeals Pending as of Start of Fiscal Year");
            itemsReceived = record.get("Number of Appeals Received in Fiscal Year");
            itemsProcessed = record.get("Number of Appeals Processed in Fiscal Year");
            itemsPendingAtEnd = record.get("Number of Appeals Pending as of End of Fiscal Year");
        }
        else
        {
            itemsPendingAtStart = record
                    .get("Number of Consultations Received from Other Agencies that were Pending at the Agency as of Start of the Fiscal Year");
            itemsReceived = record.get("Number of Consultations Received from Other Agencies During the Fiscal Year");
            itemsProcessed = record
                    .get("Number of Consultations Received from Other Agencies that were Processed by the Agency During the Fiscal Year");
            itemsPendingAtEnd = record
                    .get("Number of Consultations Received from Other Agencies that were Pending at the Agency as of End of the Fiscal Year");
        }

        addElement(processingElement, "foia:ProcessingStatisticsPendingAtStartQuantity", itemsPendingAtStart);
        addElement(processingElement, "foia:ProcessingStatisticsReceivedQuantity", itemsReceived);
        addElement(processingElement, "foia:ProcessingStatisticsProcessedQuantity", itemsProcessed);
        addElement(processingElement, "foia:ProcessingStatisticsPendingAtEndQuantity", itemsPendingAtEnd);

        parent.appendChild(processingElement);
    }

    /**
     *
     * Section: VI.C.(4). RESPONSE TIME FOR ADMINISTRATIVE APPEALS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateAppealResponseTimeSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.APPEAL_RESPONSE_TIME;

        Element appealResponseTimeSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendResponseTimeItem(appealResponseTimeSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(appealResponseTimeSection,
                componentData, "foia:ResponseTimeOrganizationAssociation"));

        parent.appendChild(appealResponseTimeSection);
    }

    private void appendResponseTimeItem(Element parent, Map<String, String> record)
    {
        Element responseTimeElement = parent.getOwnerDocument().createElement("foia:ResponseTime");
        responseTimeElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String medianNumberOfDays = record.get("Median number of Days");
        String averageNumberOfDays = record.get("Average Number of Days");
        String lowestNumberOfDays = record.get("Lowest Number of Days");
        String highestNumberOfDays = record.get("Highest Number of Days");

        addResponseTimeElementIfValid(responseTimeElement, medianNumberOfDays, "foia:ResponseTimeMedianDaysCode",
                "foia:ResponseTimeMedianDaysValue");
        addResponseTimeElementIfValid(responseTimeElement, averageNumberOfDays, "foia:ResponseTimeAverageDaysCode",
                "foia:ResponseTimeAverageDaysValue");
        addResponseTimeElementIfValid(responseTimeElement, lowestNumberOfDays, "foia:ResponseTimeLowestDaysCode",
                "foia:ResponseTimeLowestDaysValue");
        addResponseTimeElementIfValid(responseTimeElement, highestNumberOfDays, "foia:ResponseTimeHighestDaysCode",
                "foia:ResponseTimeHighestDaysValue");

        parent.appendChild(responseTimeElement);
    }

    /**
     *
     * Section: VIII.A. REQUESTS FOR EXPEDITED PROCESSING
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateExpeditedProcessingSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.EXPEDITED_PROCESSING;

        Element expeditedProcessingSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendExpeditedProcessingItem(expeditedProcessingSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(expeditedProcessingSection,
                componentData, "foia:ExpeditedProcessingOrganizationAssociation"));

        parent.appendChild(expeditedProcessingSection);
    }

    private void appendExpeditedProcessingItem(Element parent, Map<String, String> record)
    {
        Element expeditedProcessingElement = parent.getOwnerDocument().createElement("foia:ExpeditedProcessing");
        expeditedProcessingElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String granted = record.get("Number Granted");
        String denied = record.get("Number Denied");
        String medianDaysToAdjudicate = record.get("Median Number of Days to Adjudicate");
        String averageDaysToAdjudicate = record.get("Average Number of Days to Adjudicate");
        String adjudicatedWithingTenDays = record.get("Number Adjudicated Within Ten Calendar Days");

        addElement(expeditedProcessingElement, "foia:RequestGrantedQuantity", granted);
        addElement(expeditedProcessingElement, "foia:RequestDeniedQuantity", denied);

        if (NumberUtils.isParsable(averageDaysToAdjudicate) && Double.parseDouble(averageDaysToAdjudicate) > 0
                && NumberUtils.isParsable(medianDaysToAdjudicate) && Double.parseDouble(medianDaysToAdjudicate) > 0)
        {
            addElement(expeditedProcessingElement, "foia:AdjudicationMedianDaysValue", medianDaysToAdjudicate);
            addElement(expeditedProcessingElement, "foia:AdjudicationAverageDaysValue",
                    averageDaysToAdjudicate);
        }
        addElement(expeditedProcessingElement, "foia:AdjudicationWithinTenDaysQuantity",
                adjudicatedWithingTenDays);

        parent.appendChild(expeditedProcessingElement);
    }

    /**
     *
     * Section: VIII.B. REQUESTS FOR FEE WAIVER
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateFeeWaiverSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.FEE_WAIVER;

        Element feeWaiverSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendFeeWaiverItem(feeWaiverSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(feeWaiverSection,
                componentData, "foia:FeeWaiverOrganizationAssociation"));

        parent.appendChild(feeWaiverSection);
    }

    private void appendFeeWaiverItem(Element parent, Map<String, String> record)
    {
        Element feeWaiverElement = parent.getOwnerDocument().createElement("foia:FeeWaiver");
        feeWaiverElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String granted = record.get("Number Granted");
        String denied = record.get("Number Denied");
        String medianDaysToAdjudicate = record.get("Median Number of Days to Adjudicate");
        String averageDaysToAdjudicate = record.get("Average Number of Days to Adjudicate");

        addElement(feeWaiverElement, "foia:RequestGrantedQuantity", granted);
        addElement(feeWaiverElement, "foia:RequestDeniedQuantity", denied);
        if (NumberUtils.isParsable(averageDaysToAdjudicate) && Double.parseDouble(averageDaysToAdjudicate) > 0
                && NumberUtils.isParsable(medianDaysToAdjudicate) && Double.parseDouble(medianDaysToAdjudicate) > 0)
        {
            addElement(feeWaiverElement, "foia:AdjudicationMedianDaysValue", medianDaysToAdjudicate);
            addElement(feeWaiverElement, "foia:AdjudicationAverageDaysValue",
                    averageDaysToAdjudicate);
        }

        parent.appendChild(feeWaiverElement);
    }

    /**
     *
     * Section: IX. FOIA PERSONNEL AND COSTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generatePersonnelAndCostSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.PERSONNEL_AND_COST;

        Element personnelAndCostSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendPersonnelAndCostItem(personnelAndCostSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(personnelAndCostSection,
                componentData, "foia:PersonnelAndCostOrganizationAssociation"));

        parent.appendChild(personnelAndCostSection);
    }

    private void appendPersonnelAndCostItem(Element parent, Map<String, String> record)
    {
        Element personnelAndCostElement = parent.getOwnerDocument().createElement("foia:PersonnelAndCost");
        personnelAndCostElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String fullTimeStaff = record.get("Number of \"Full-Time FOIA Employees\"");
        String equivalentStaff = record.get("Number of \"Equivalent Full-Time FOIA Employees\"");
        String totalStaff = record.get("Total Number of \"Full- Time FOIA Staff\"");
        String processingCosts = record.get("Processing Costs");
        String litigationCosts = record.get("Litigation-Related Costs");
        String totalCosts = record.get("Total Costs");

        processingCosts = NiemExportUtils.formatCurrencyToNiemExpectedFormat(processingCosts);
        litigationCosts = NiemExportUtils.formatCurrencyToNiemExpectedFormat(litigationCosts);
        totalCosts = NiemExportUtils.formatCurrencyToNiemExpectedFormat(totalCosts);

        addElement(personnelAndCostElement, "foia:FullTimeEmployeeQuantity", fullTimeStaff);
        addElement(personnelAndCostElement, "foia:EquivalentFullTimeEmployeeQuantity", equivalentStaff);
        addElement(personnelAndCostElement, "foia:TotalFullTimeStaffQuantity", totalStaff);
        addElement(personnelAndCostElement, "foia:ProcessingCostAmount", processingCosts);
        addElement(personnelAndCostElement, "foia:LitigationCostAmount", litigationCosts);
        addElement(personnelAndCostElement, "foia:TotalCostAmount", totalCosts);

        parent.appendChild(personnelAndCostElement);
    }

    /**
     *
     * Section: X. FEES COLLECTED FOR PROCESSING REQUESTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateFeesCollectedSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.FEES_COLLECTED;

        Element feesCollectedSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendFeesCollectedItem(feesCollectedSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(feesCollectedSection,
                componentData, "foia:FeesCollectedOrganizationAssociation"));

        parent.appendChild(feesCollectedSection);
    }

    private void appendFeesCollectedItem(Element parent, Map<String, String> record)
    {
        Element feesCollectedElement = parent.getOwnerDocument().createElement("foia:FeesCollected");
        feesCollectedElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String totalAmountOfFeesCollected = record.get("Total Amount of Fees Collected");
        String percentageOfTotalCosts = record.get("Percentage of Total Costs");

        totalAmountOfFeesCollected = NiemExportUtils.formatCurrencyToNiemExpectedFormat(totalAmountOfFeesCollected);
        addElement(feesCollectedElement, "foia:FeesCollectedAmount", totalAmountOfFeesCollected);

        float feesCollectedCostPercent;
        if (NumberUtils.isParsable(percentageOfTotalCosts))
        {
            feesCollectedCostPercent = Float.parseFloat(percentageOfTotalCosts);
        }
        else
        {
            feesCollectedCostPercent = 0;
        }
        addElement(feesCollectedElement, "foia:FeesCollectedCostPercent", String.format("%.4f", feesCollectedCostPercent));

        parent.appendChild(feesCollectedElement);
    }

    /**
     *
     * Section: XI.A. NUMBER OF TIMES SUBSECTION (C) USED
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateSubsectionUsedSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.SUBSECTION_USED;

        Element subsectionUsedSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendSubsectionUsedItem(subsectionUsedSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(subsectionUsedSection,
                componentData, "foia:SubsectionUsedOrganizationAssociation"));

        parent.appendChild(subsectionUsedSection);
    }

    private void appendSubsectionUsedItem(Element parent, Map<String, String> record)
    {
        Element subsectionUsedElement = parent.getOwnerDocument().createElement("foia:SubsectionUsed");
        subsectionUsedElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String timesUsed = record.get("Number of Times Subsection Used");

        addElement(subsectionUsedElement, "foia:TimesUsedQuantity", timesUsed);

        parent.appendChild(subsectionUsedElement);
    }

    /**
     *
     * Section: XI.B. NUMBER OF SUBSECTION (A)(2) POSTINGS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateSubsectionPostSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.SUBSECTION_POST;

        Element subsectionPostSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendSubsectionPostItem(subsectionPostSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(subsectionPostSection,
                componentData, "foia:SubsectionPostOrganizationAssociation"));

        parent.appendChild(subsectionPostSection);
    }

    private void appendSubsectionPostItem(Element parent, Map<String, String> record)
    {
        Element subsectionPostElement = parent.getOwnerDocument().createElement("foia:Subsection");
        subsectionPostElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String postedByFOIAOffice = record.get("Number of Records Posted by the FOIA Office");
        String postedByProgramOffices = record.get("Number of Records Posted by the Program Office");

        addElement(subsectionPostElement, "foia:PostedbyFOIAQuantity", postedByFOIAOffice);
        addElement(subsectionPostElement, "foia:PostedbyProgramQuantity", postedByProgramOffices);

        parent.appendChild(subsectionPostElement);
    }

    /**
     *
     * Section: XII.A. BACKLOGS OF FOIA REQUESTS AND ADMINISTRATIVE APPEALS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateBacklogSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        DOJReport report = DOJReport.BACKLOG;

        Element backlogSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendBacklogItem(backlogSection, componentData));
        filteredData.forEach(componentData -> appendProcessingAssociations(backlogSection,
                componentData, "foia:BacklogOrganizationAssociation"));

        parent.appendChild(backlogSection);
    }

    private void appendBacklogItem(Element parent, Map<String, String> record)
    {
        Element backlogElement = parent.getOwnerDocument().createElement("foia:Backlog");
        backlogElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String backloggedRequestQuantity = record.get("Number of Backlogged Requests as of End of Fiscal Year");
        String backloggedAppealQuantity = record.get("Number of Backlogged Appeals as of End of Fiscal Year");

        addElement(backlogElement, "foia:BackloggedRequestQuantity", backloggedRequestQuantity);
        addElement(backlogElement, "foia:BackloggedAppealQuantity", backloggedAppealQuantity);

        parent.appendChild(backlogElement);
    }

    /**
     *
     * Section: XII.D.(2). COMPARISON OF NUMBERS OF REQUESTS FROM PREVIOUS AND CURRENT ANNUAL REPORT -- BACKLOGGED
     * REQUESTS
     *
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateBackloggedRequestComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateBacklogComparisonSection(data, parent, agencyIdentifiers, DOJReport.BACKLOG_REQUEST_COMPARISON);
    }

    /**
     *
     * Section: XII.E.(2). COMPARISON OF NUMBERS OF ADMINISTRATIVE APPEALS FROM PREVIOUS AND CURRENT ANNUAL REPORT --
     * BACKLOGGED APPEALS
     * 
     * @param data
     *            List of mapped values, where the table headers are the keys for each entry
     * @param parent
     *            The parent that we will append the section to
     * @param agencyIdentifiers
     *            All the agency components that can occur in the data
     */
    private void generateBackloggedAppealsComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers)
    {
        generateBacklogComparisonSection(data, parent, agencyIdentifiers, DOJReport.BACKLOG_APPEAL_COMPARISON);
    }

    private void generateBacklogComparisonSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report)
    {

        Element backlogComparisonSection = parent.getOwnerDocument().createElement(report.getSectionName());

        List<Map<String, String>> filteredData = getDataWithComponentReferences(data, agencyIdentifiers, report);

        filteredData.forEach(
                componentData -> appendBacklogComparisonItem(backlogComparisonSection, componentData, report));
        filteredData.forEach(componentData -> appendProcessingAssociations(backlogComparisonSection,
                componentData, "foia:BacklogComparisonOrganizationAssociation"));

        parent.appendChild(backlogComparisonSection);
    }

    private void appendBacklogComparisonItem(Element parent, Map<String, String> record, DOJReport report)
    {
        Element backlogComparisonElement = parent.getOwnerDocument().createElement("foia:BacklogComparison");
        backlogComparisonElement.setAttribute("s:id", record.get(COMPONENT_DATA_REFERENCE));

        String backlogLastYearQuantity;
        String backlogCurrentYearQuantity;

        if (report == DOJReport.BACKLOG_REQUEST_COMPARISON)
        {
            backlogLastYearQuantity = record
                    .get("Number of Backlogged Requests as of End of the Fiscal Year from Previous Annual Report");
            backlogCurrentYearQuantity = record
                    .get("Number of Backlogged Requests as of End of the Fiscal Year from Current Annual Report");
        }
        else
        {
            backlogLastYearQuantity = record
                    .get("Number of Backlogged Appeals as of End of the Fiscal Year from Previous Annual Report");
            backlogCurrentYearQuantity = record
                    .get("Number of Backlogged Appeals as of End of the Fiscal Year from Current Annual Report");

        }
        addElement(backlogComparisonElement, "foia:BacklogLastYearQuantity", backlogLastYearQuantity);
        addElement(backlogComparisonElement, "foia:BacklogCurrentYearQuantity", backlogCurrentYearQuantity);

        parent.appendChild(backlogComparisonElement);
    }

    private boolean isValidAgencyRow(Map<String, String> agency)
    {
        return !agency.get(AGENCY_IDENTIFIER_COLUMN).contains("Total")
                || agency.get(AGENCY_IDENTIFIER_COLUMN).equals(GRAND_TOTAL_ROW);
    }

    private Map<String, List<Map<String, String>>> groupByComponentAndFilter(List<Map<String, String>> data)
    {
        return data.stream()
                .filter(this::isValidAgencyRow)
                .collect(Collectors.groupingBy(
                        it -> it.get(AGENCY_IDENTIFIER_COLUMN),
                        LinkedHashMap::new,
                        toList()));
    }

    private Map<String, List<Map<String, String>>> groupDataByExemption(List<Map<String, String>> data)
    {
        return data.stream()
                .filter(record -> record.get("Agency / Component") != null && !record.get(AGENCY_IDENTIFIER_COLUMN).equals(""))
                .collect(Collectors.groupingBy(
                        it -> it.get("Statute"),
                        LinkedHashMap::new,
                        toList()));
    }

    private void addElement(Element parent, String elemName, String elemValue)
    {
        Element elem = parent.getOwnerDocument().createElement(elemName);
        elem.appendChild(parent.getOwnerDocument().createTextNode(elemValue));
        parent.appendChild(elem);
    }

    private List<Map<String, String>> getDataWithPredifinedComponentReference(List<Map<String, String>> data,
            Map<String, String> agencyIdentifiers, String componentDataReference)
    {
        List<Map<String, String>> filteredData = new ArrayList<>();

        for (Map<String, String> record : data)
        {
            String agency = record.get(AGENCY_IDENTIFIER_COLUMN);

            if (agencyIdentifiers.get(agency) != null)
            {
                Map<String, String> filteredRecord = getComponentReferencedRecord(record, componentDataReference,
                        agencyIdentifiers.get(agency));
                filteredData.add(filteredRecord);
            }
            else if (agency.equals(GRAND_TOTAL_ROW))
            {
                Map<String, String> filteredRecord = getComponentReferencedRecord(record, componentDataReference, "ORG0");
                filteredData.add(filteredRecord);
            }
        }
        filteredData = filteredData.stream()
                .filter(entry -> entry.get(COMPONENT_DATA_REFERENCE) != null)
                .collect(toList());

        return filteredData;
    }

    private List<Map<String, String>> getDataWithComponentReferences(List<Map<String, String>> data, Map<String, String> agencyIdentifiers,
            DOJReport report)
    {
        List<Map<String, String>> filteredData = new ArrayList<>();

        for (int index = 0; index < data.size(); index++)
        {
            Map<String, String> record = data.get(index);
            String agency = record.get(AGENCY_IDENTIFIER_COLUMN);

            if (agencyIdentifiers.get(agency) != null)
            {
                String componentDataReference = report.getIdPrefix().concat(String.valueOf(index + 1));
                Map<String, String> filteredRecord = getComponentReferencedRecord(record, componentDataReference,
                        agencyIdentifiers.get(agency));
                filteredData.add(filteredRecord);
            }
            else if (GRAND_TOTAL_ROW.equals(agency))
            {
                String componentDataReference = report.getIdPrefix().concat(String.valueOf(0));
                Map<String, String> filteredRecord = getComponentReferencedRecord(record, componentDataReference, "ORG0");
                filteredData.add(filteredRecord);
            }
        }
        filteredData = filteredData.stream()
                .filter(entry -> entry.get(COMPONENT_DATA_REFERENCE) != null)
                .collect(toList());

        return filteredData;
    }

    private Map<String, String> getComponentReferencedRecord(Map<String, String> record, String componentDataReference,
            String organizationReference)
    {
        record.put(COMPONENT_DATA_REFERENCE, componentDataReference);
        record.put(ORGANIZATION_REFERENCE, organizationReference);
        return record;
    }

    private void appendProcessingAssociations(Element parentElement, Map<String, String> data, String associationsElementName)
    {
        Element organizationAssociationElement = parentElement.getOwnerDocument().createElement(associationsElementName);

        Element componentDataReference = parentElement.getOwnerDocument().createElement("foia:ComponentDataReference");
        componentDataReference.setAttribute(NIEM_REFERENCE_ATTRIBUTE, data.get(COMPONENT_DATA_REFERENCE));
        organizationAssociationElement.appendChild(componentDataReference);

        Element organizationReference = parentElement.getOwnerDocument().createElement("nc:OrganizationReference");
        organizationReference.setAttribute(NIEM_REFERENCE_ATTRIBUTE, data.get(ORGANIZATION_REFERENCE));
        organizationAssociationElement.appendChild(organizationReference);

        parentElement.appendChild(organizationAssociationElement);
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    public ReportService getReportService()
    {
        return reportService;
    }

    public void setReportService(ReportService reportService)
    {
        this.reportService = reportService;
    }

    public PentahoReportsConfig getReportsConfig()
    {
        return reportsConfig;
    }

    public void setReportsConfig(PentahoReportsConfig reportsConfig)
    {
        this.reportsConfig = reportsConfig;
    }

    public FoiaConfig getFoiaConfig()
    {
        return foiaConfig;
    }

    public void setFoiaConfig(FoiaConfig foiaConfig)
    {
        this.foiaConfig = foiaConfig;
    }
}

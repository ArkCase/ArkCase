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

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.services.config.lookups.model.StandardLookup;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiComponentDOJReportNiemExportTest
{

    private NiemExportUtils niemExportUtils;
    private NiemExportService niemExportService;

    private CSVToMapReader csvToMapReader;

    private Map<String, String> componentMap = new HashMap<>();
    private Map<String, String> reportPath = new HashMap<>();

    private StandardLookup componentAgencies;
    Map<String, String> agencyIdentifiers = new LinkedHashMap<>();

    @Before
    public void setUp() throws Exception
    {

        niemExportUtils = new NiemExportUtils();
        csvToMapReader = new CSVToMapReader();

        componentMap.put("FOIA", "ORG1");
        componentMap.put("EEOC", "ORG2");

        niemExportService = new NiemExportService();
        niemExportService.setComponentMap(componentMap);
        niemExportService.setNiemExportUtils(niemExportUtils);

        setUpComponentAgencies();

        reportPath.put("PROCESSED_REQUESTS", "/csv/multiComponent/V.A.csv");
        reportPath.put("REQUEST_DISPOSITION", "/csv/multiComponent/V.B.1.csv");
        reportPath.put("REQUEST_DENIAL_OTHER_REASON", "/csv/multiComponent/V.B.2.csv");
        reportPath.put("REQUEST_DISPOSITION_APPLIED_EXEMPTIONS", "/csv/multiComponent/V.B.3.csv");
        reportPath.put("PROCESSED_APPEALS", "/csv/multiComponent/VI.A.csv");
        reportPath.put("APPEAL_DISPOSITION", "/csv/multiComponent/VI.B.csv");
        reportPath.put("APPEAL_DISPOSITION_APPLIED_EXEMPTIONS", "/csv/multiComponent/VI.C.1.csv");
        reportPath.put("APPEAL_NON_EXEMPTION_DENIAL", "/csv/multiComponent/VI.C.2.csv");
        reportPath.put("APPEAL_DENIAL_OTHER_REASON", "/csv/multiComponent/VI.C.3.csv");
        reportPath.put("APPEAL_RESPONSE_TIME", "/csv/multiComponent/VI.C.4.csv");
        reportPath.put("OLDEST_PENDING_APPEALS", "/csv/multiComponent/VI.C.5.csv");
        reportPath.put("PROCESSED_PERFECTED_REQUESTS_RESPONSE_TIME", "/csv/multiComponent/VII.A.csv");
        reportPath.put("INFORMATION_GRANTED_REQUESTS_RESPONSE_TIME", "/csv/multiComponent/VII.B.csv");
        reportPath.put("SIMPLE_RESPONSE_TIME_INCREMENTS", "/csv/multiComponent/VII.C.1.csv");
        reportPath.put("COMPLEX_RESPONSE_TIME_INCREMENTS", "/csv/multiComponent/VII.C.2.csv");
        reportPath.put("EXPEDITED_RESPONSE_TIME_INCREMENTS", "/csv/multiComponent/VII.C.3.csv");
        reportPath.put("ALL_PENDING_PERFECTED_REQUESTS", "/csv/multiComponent/VII.D.csv");
        reportPath.put("OLDEST_PENDING_REQUESTS", "/csv/multiComponent/VII.E.csv");
        reportPath.put("EXPEDITED_PROCESSING", "/csv/multiComponent/VIII.A.csv");
        reportPath.put("FEE_WAIVER", "/csv/multiComponent/VIII.B.csv");
        reportPath.put("PERSONNEL_AND_COST", "/csv/multiComponent/IX.csv");
        reportPath.put("FEES_COLLECTED", "/csv/multiComponent/X.csv");
        reportPath.put("SUBSECTION_USED", "/csv/multiComponent/XI.A.csv");
        reportPath.put("SUBSECTION_POST", "/csv/multiComponent/XI.B.csv");
        reportPath.put("BACKLOG", "/csv/multiComponent/XII.A.csv");
        reportPath.put("PROCESSED_CONSULTATIONS", "/csv/multiComponent/XII.B.csv");
        reportPath.put("OLDEST_PENDING_CONSULTATIONS", "/csv/multiComponent/XII.C.csv");
        reportPath.put("REQUEST_PROCESSED_COMPARISON", "/csv/multiComponent/XII.D.1.csv");
        reportPath.put("BACKLOG_REQUEST_COMPARISON", "/csv/multiComponent/XII.D.2.csv");
        reportPath.put("APPEAL_PROCESSED_COMPARISON", "/csv/multiComponent/XII.E.1.csv");
        reportPath.put("BACKLOG_APPEAL_COMPARISON", "/csv/multiComponent/XII.E.2.csv");

    }

    private void setUpComponentAgencies()
    {
        componentAgencies = new StandardLookup();
        List<StandardLookupEntry> componentAgenciesLookupEntries = new ArrayList<>();
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Atlanta", "Atlanta District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Birmingham", "Birmingham District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Charlotte", "Charlotte District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Chicago", "Chicago District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Dallas", "Dallas District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Houston", "Houston District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Indianapolis", "Indianapolis District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Los Angeles", "Los Angeles District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Memphis", "Memphis District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Miami", "Miami District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("New York", "New York District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Philadelphia", "Philadelphia District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Phoenix", "Phoenix District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("San Francisco", "San Francisco District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("St. Louis", "St. Louis District Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("WFO", "Washington Field Office"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("OLC/HDQ", "Office of Legal Counsel/Headquarters"));

        componentAgencies.setEntries(componentAgenciesLookupEntries);

        String[] componentAgencyNames = componentAgencies.getEntries().stream().map(StandardLookupEntry::getKey).toArray(String[]::new);

        for (int i = 0; i < componentAgencyNames.length; i++)
        {
            agencyIdentifiers.put(componentAgencyNames[i], String.format("ORG%d", i + 1));
        }
    }

    @Test
    public void testComponentAgencies()
    {

        String[] componentAgencyNames = componentAgencies.getEntries().stream().map(StandardLookupEntry::getKey).toArray(String[]::new);
        Map<String, String> agencyIdentifiers = new LinkedHashMap<>();

        for (int i = 0; i < componentAgencyNames.length; i++)
        {
            agencyIdentifiers.put(componentAgencyNames[i], String.format("ORG%d", i + 1));
        }

        assertNotNull(componentAgencies);
    }

    @Test
    public void generateYearlyReport() throws Exception
    {
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        foiaAnnualReport.setAttribute("xmlns:iepd", "http://leisp.usdoj.gov/niem/FoiaAnnualReport/exchange/1.03");
        foiaAnnualReport.setAttribute("xsi:schemaLocation", "http://leisp.usdoj.gov/niem/FoiaAnnualReport/extension/1.03");
        foiaAnnualReport.setAttribute("xmlns:foia", "http://leisp.usdoj.gov/niem/FoiaAnnualReport/extension/1.03");
        foiaAnnualReport.setAttribute("xmlns:j", "http://niem.gov/niem/domains/jxdm/4.1");
        foiaAnnualReport.setAttribute("xmlns:nc", "http://niem.gov/niem/niem-core/2.0");
        foiaAnnualReport.setAttribute("xmlns:s", "http://niem.gov/niem/structures/2.0");
        foiaAnnualReport.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        niemExportService.appendOrganizationSection(foiaAnnualReport, agencyIdentifiers);

        for (DOJReport report : DOJReport.values())
        {
            String path = reportPath.get(report.name());
            List<Map<String, String>> data = getDataFromPath(path);
            niemExportService.appendReportSection(data, foiaAnnualReport, agencyIdentifiers, report);
        }

        printXml(document);

    }

    @Test
    public void oldestPendingAppealSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath("/csv/singleComponent/Ten_Oldest_Pending_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateOldestPendingAppealsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void oldestPendingRequestSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Pending_Requests_--_Ten_Oldest_Pending_Perfected_Requests.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateOldestPendingRequestsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void oldestPendingConsultationSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath("/csv/singleComponent/Pending_Requests_--_Ten_Oldest_Consultations.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateOldestPendingConsultationsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void appealDenialOtherReasonSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath("/csv/multiComponent/VI.C.3.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateAppealDenialOtherReasonSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void requestDenialOtherReasonSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath("/csv/multiComponent/V.B.2.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateRequestDenialOtherReasonSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void simpleResponseTimeIncrementsSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Processed_Simple_Requests_--_Response_Time_in_Day_Increments.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateSimpleResponseTimeIncrementsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void complexResponseTimeIncrementsSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Processed_Simple_Requests_--_Response_Time_in_Day_Increments.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateComplexResponseTimeIncrementsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void expeditedResponseTimeIncrementsSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Processed_Simple_Requests_--_Response_Time_in_Day_Increments.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateExpeditedResponseTimeIncrementsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void appealDispositionSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/multiComponent/VI.B.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateAppealDispositionSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void requestDispositionSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Dispositions_of_FOIA_Requests_--_All_Processed_Requests.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateRequestDispositionSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void appealDispositionAppliedExemptionsSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Reasons_for_Denial_on_Appeal_--_Number_of_Times_Exemptions_Applied.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateAppealDispositionAppliedExemptionsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void requestDispositionAppliedExemptionsSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Dispositions_of_FOIA_Requests_--_Number_of_Times_Exemptions_Applied.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateRequestDispositionAppliedExemptionsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void processedRequestComparisonSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Comparison_of_Numbers_of_Requests_From_Previous_and_Current_Annual_Report_--_Request_Received_and_Processed.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateProcessedRequestComparisonSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void processedAppealsComparisonSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Comparison_of_Numbers_of_Administrative_Appeals_From_Previous_and_Current_Annual_Report.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateProcessedAppealsComparisonSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void processedRequestSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/multiComponent/V.A.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateProcessedRequestSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void processedAppealsSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Comparison_of_Numbers_of_Administrative_Appeals_From_Previous_and_Current_Annual_Report.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateProcessedAppealsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void processedConsultationsSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Comparison_of_Numbers_of_Administrative_Appeals_From_Previous_and_Current_Annual_Report.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateProcessedConsultationsSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void appealNonExemptionDenialSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Reasons_for_Denial_on_Appeal_--_Reasons_Other_Than_Exemptions.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateAppealNonExemptionDenialSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void appealResponseTimeSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateAppealResponseTimeSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    // NON CHECKED

    @Test
    public void expeditedProcessingSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateExpeditedProcessingSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void feeWaiverSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateFeeWaiverSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void personnelAndCostSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generatePersonnelAndCostSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void feesCollectedSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateFeesCollectedSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void subsectionUsedSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateSubsectionUsedSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void subsectionPostSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateSubsectionPostSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void backlogSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateBacklogSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void backloggedRequestComparisonSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateBackloggedRequestComparisonSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void backloggedAppealComparisonSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath(
                "/csv/singleComponent/Response_Time_for_Administrative_Appeals.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateBackloggedAppealsComparisonSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    private List<Map<String, String>> getDataFromPath(String csvPath) throws IOException
    {
        Resource csv = new ClassPathResource(csvPath);
        File csvFile = new File(csv.getURI());

        return csvToMapReader.getDataMapFromCSV(csvFile);
    }

    private Document createXmlDocument() throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();
        return document;
    }

    private void printXml(Document document) throws TransformerException, IOException, FileNotFoundException
    {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer output = new StringWriter();
        tf.transform(new DOMSource(document), new StreamResult(output));
        System.out.println(output.toString());

        String xmlFilePath = "/niem/YearlyFOIAReport.xml";
        Resource xmlOutput = new ClassPathResource(xmlFilePath);
        File xmlFile = null;
        xmlFile = new File(xmlOutput.getURI());

        tf.transform(new DOMSource(document),
                new StreamResult(new FileOutputStream(xmlFile)));

    }

}

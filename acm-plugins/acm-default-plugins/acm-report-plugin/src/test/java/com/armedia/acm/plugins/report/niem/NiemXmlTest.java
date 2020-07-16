package com.armedia.acm.plugins.report.niem;

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.services.config.lookups.model.StandardLookup;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;

import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NiemXmlTest
{

    private NiemExportUtils niemExportUtils;
    private NiemExportService niemExportService;

    private CSVToMapReader csvToMapReader;

    private Map<String, String> componentMap = new HashMap<>();

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

        componentAgencies = new StandardLookup();
        List<StandardLookupEntry> componentAgenciesLookupEntries = new ArrayList<>();
        componentAgenciesLookupEntries.add(new StandardLookupEntry("FOIA", "FOIA"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("EEOC", "EEOC"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("Test Team", "Test Team"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("B", "valueB"));
        componentAgenciesLookupEntries.add(new StandardLookupEntry("C", "valueC"));
        // componentAgenciesLookupEntries.add(new StandardLookupEntry("AGENCY OVERALL", "AGENCY OVERALL"));
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
    public void xsdTest() throws Exception
    {
        String xsdFilePath = "xsd/FoiaAnnualReportExtensions.xsd";

        Resource xsd = new ClassPathResource(xsdFilePath);
        File xsdFile = new File(xsd.getURI());

        SchemaTypeSystem sts = XmlBeans.compileXsd(new XmlObject[] {
                XmlObject.Factory.parse(xsdFile) }, XmlBeans.getBuiltinTypeSystem(), null);

        sts.toString();

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
        List<Map<String, String>> data = getDataFromPath("/csv/singleComponent/Reasons_for_Denial_on_Appeal__--__Other__Reasons.csv");
        Document document = createXmlDocument();
        Element foiaAnnualReport = document.createElement("iepd:FoiaAnnualReport");
        document.appendChild(foiaAnnualReport);

        niemExportService.generateAppealDenialOtherReasonSection(data, foiaAnnualReport, agencyIdentifiers);

        printXml(document);
    }

    @Test
    public void requestDenialOtherReasonSection() throws Exception
    {
        List<Map<String, String>> data = getDataFromPath("/csv/singleComponent/Reasons_for_Denial_on_Appeal__--__Other__Reasons.csv");
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
                "/csv/singleComponent/Disposition_of_Administrative_Appeals_--_All_Processed_Appeals.csv");
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
                "/csv/singleComponent/Comparison_of_Numbers_of_Administrative_Appeals_From_Previous_and_Current_Annual_Report_--_Appeals_Received_and_Processed.csv");
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
                "/csv/singleComponent/Comparison_of_Numbers_of_Requests_From_Previous_and_Current_Annual_Report_--_Request_Received_and_Processed.csv");
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
                "/csv/singleComponent/Comparison_of_Numbers_of_Administrative_Appeals_From_Previous_and_Current_Annual_Report_--_Appeals_Received_and_Processed.csv");
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
                "/csv/singleComponent/Comparison_of_Numbers_of_Administrative_Appeals_From_Previous_and_Current_Annual_Report_--_Appeals_Received_and_Processed.csv");
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

    private void printXml(Document document) throws TransformerException
    {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer output = new StringWriter();
        tf.transform(new DOMSource(document), new StreamResult(output));
        System.out.println(output.toString());
    }

}

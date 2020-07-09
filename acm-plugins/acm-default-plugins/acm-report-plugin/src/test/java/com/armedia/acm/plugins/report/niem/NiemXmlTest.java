package com.armedia.acm.plugins.report.niem;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NiemXmlTest
{

    private NiemExportUtils niemExportUtils;
    private NiemExportService niemExportService;

    private CSVToMapReader csvToMapReader;

    private Map<String, String> componentMap = new HashMap<>();

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

    }

    @Test
    public void oldestPendingAppealSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Ten_Oldest_Pending_Administrative_Appeals.csv");
        Document document = createXmlDocument();

        niemExportService.generateOldestPendingAppealsSection(data, document);

        printXml(document);
    }

    @Test
    public void oldestPendingRequestSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Pending_Requests_--_Ten_Oldest_Pending_Perfected_Requests.csv");
        Document document = createXmlDocument();

        niemExportService.generateOldestPendingRequestsSection(data, document);

        printXml(document);
    }

    @Test
    public void oldestPendingConsultationSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Pending_Requests_--_Ten_Oldest_Consultations.csv");
        Document document = createXmlDocument();

        niemExportService.generateOldestPendingConsultationsSection(data, document);

        printXml(document);
    }

    @Test
    public void appealDenialOtherReasonSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Reasons_for_Denial_on_Appeal__--__Other__Reasons.csv");
        Document document = createXmlDocument();

        niemExportService.generateAppealDenialOtherReasonSection(data, document);

        printXml(document);
    }

    @Test
    public void requestDenialOtherReasonSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Reasons_for_Denial_on_Appeal__--__Other__Reasons.csv");
        Document document = createXmlDocument();

        niemExportService.generateRequestDenialOtherReasonSection(data, document);

        printXml(document);
    }

    @Test
    public void simpleResponseTimeIncrementsSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Processed_Simple_Requests_--_Response_Time_in_Day_Increments.csv");
        Document document = createXmlDocument();

        niemExportService.generateSimpleResponseTimeIncrementsSection(data, document);

        printXml(document);
    }

    @Test
    public void complexResponseTimeIncrementsSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Processed_Simple_Requests_--_Response_Time_in_Day_Increments.csv");
        Document document = createXmlDocument();

        niemExportService.generateComplexResponseTimeIncrementsSection(data, document);

        printXml(document);
    }

    @Test
    public void expeditedResponseTimeIncrementsSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Processed_Simple_Requests_--_Response_Time_in_Day_Increments.csv");
        Document document = createXmlDocument();

        niemExportService.generateExpeditedResponseTimeIncrementsSection(data, document);

        printXml(document);
    }

    @Test
    public void appealDispositionSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Disposition_of_Administrative_Appeals_--_All_Processed_Appeals.csv");
        Document document = createXmlDocument();

        niemExportService.generateAppealDispositionSection(data, document);

        printXml(document);
    }

    @Test
    public void requestDispositionSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Dispositions_of_FOIA_Requests_--_All_Processed_Requests.csv");
        Document document = createXmlDocument();

        niemExportService.generateRequestDispositionSection(data, document);

        printXml(document);
    }

    @Test
    public void appealDispositionAppliedExemptionsSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Reasons_for_Denial_on_Appeal_--_Number_of_Times_Exemptions_Applied.csv");
        Document document = createXmlDocument();

        niemExportService.generateAppealDispositionAppliedExemptionsSection(data, document);

        printXml(document);
    }

    @Test
    public void requestDispositionAppliedExemptionsSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath("/csv/Dispositions_of_FOIA_Requests_--_Number_of_Times_Exemptions_Applied.csv");
        Document document = createXmlDocument();

        niemExportService.generateRequestDispositionAppliedExemptionsSection(data, document);

        printXml(document);
    }

    @Test
    public void processedRequestComparisonSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath(
                "/csv/Comparison_of_Numbers_of_Requests_From_Previous_and_Current_Annual_Report_--_Request_Received_and_Processed.csv");
        Document document = createXmlDocument();

        niemExportService.generateProcessedRequestComparisonSection(data, document);

        printXml(document);
    }

    @Test
    public void processedAppealsComparisonSection() throws Exception
    {
        List<Map<String, Object>> data = getDataFromPath(
                "/csv/Comparison_of_Numbers_of_Administrative_Appeals_From_Previous_and_Current_Annual_Report_--_Appeals_Received_and_Processed.csv");
        Document document = createXmlDocument();

        niemExportService.generateProcessedAppealsComparisonSection(data, document);

        printXml(document);
    }

    private List<Map<String, Object>> getDataFromPath(String csvPath) throws IOException
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

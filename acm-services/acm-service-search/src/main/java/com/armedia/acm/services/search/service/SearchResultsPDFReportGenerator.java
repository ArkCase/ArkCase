package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.configuration.model.ConfigurationClientConfig;
import com.armedia.acm.configuration.service.FileConfigurationService;
import com.armedia.acm.pdf.PdfServiceException;
import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.services.search.model.ReportGenerator;
import com.armedia.acm.services.search.model.SearchConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * PDF Report Generator
 */
public class SearchResultsPDFReportGenerator extends ReportGenerator
{

    private PdfService pdfService;

    private FileConfigurationService fileConfigurationService;

    private ConfigurationClientConfig configurationClientConfig;

    private final Logger log = LogManager.getLogger(getClass());

    /**
     * ISO 8601 Date/Time pattern used by Solr (yyyy-MM-ddTHH:mm:ssZ).
     */
    private static final String ISO8601_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$";

    /**
     * Formatter for parsing Solr *_tdt fields.
     */
    private static final DateTimeFormatter SOLR_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * Formatter for formatting dates.
     */
    private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");

    private static final String OBJECT_TYPE_CASE_FILE = "CASE_FILE";

    @Override public String generateReport(String[] requestedFields, String[] titles, String jsonData)
    {
        return generateReport(requestedFields, titles, jsonData, 0);
    }

    /**
     *
     * @param requestedFields
     * @param titles
     * @param jsonData
     * @param timeZone
     * @return
     */
    @Override public String generateReport(String[] requestedFields, String[] titles, String jsonData, int timeZone)
    {
        JSONObject jsonResult = new JSONObject(jsonData);
        JSONObject jsonResponse = jsonResult.getJSONObject("response");
        JSONArray jsonDocs = jsonResponse.getJSONArray("docs");
        String stylesheet = SearchConstants.PDFEXPORT_STYLESHEET;
        Document document = null;
        try
        {
            document = buildXmlForPdfDocument(jsonDocs, requestedFields, timeZone);
        }
        catch (ParserConfigurationException e)
        {
            log.warn("Unable to generate pdf document for Search Result");
        }
        Source source = new DOMSource(document);
        String filename = null;

        try
        {
            String pdfStylesheetsLocation = configurationClientConfig.getStylesheetsPath();
            InputStream xslStream = fileConfigurationService.getInputStreamFromConfiguration(pdfStylesheetsLocation + "/"
                    + stylesheet);
            URI baseURI = fileConfigurationService.getLocationUriFromConfiguration(pdfStylesheetsLocation);
            filename = getPdfService().generatePdf(xslStream, baseURI, source);
            log.debug("Created Search Result pdf document [{}]", filename);
        }catch(PdfServiceException | URISyntaxException | IOException ex){
            log.error("Unable to Create Search Result pdf document", ex);
        }

        return filename;
    }

    /**
     *
     * @param jsonDocs
     * @param requestedFields
     * @param timeZoneOffsetinMinutes
     * @return
     * @throws ParserConfigurationException
     */
    public Document buildXmlForPdfDocument(JSONArray jsonDocs, String[] requestedFields, int timeZoneOffsetinMinutes) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create <searchresult>, the root of the document
        Element rootElem = document.createElement("searchresult");
        document.appendChild(rootElem);

        Element resultsElement = document.createElement("results");
        rootElem.appendChild(resultsElement);

        for (int i = 0; i < jsonDocs.length(); i++)
        {
            Element resultElement = document.createElement("result");
            resultsElement.appendChild(resultElement);

            JSONObject data = jsonDocs.getJSONObject(i);

            if(data.has("object_type_s")){
                String typeValue = data.getString("object_type_s");
                typeValue = stringChangeLine(typeValue,14);
                if (typeValue.equals(OBJECT_TYPE_CASE_FILE) && data.has("object_sub_type_s"))
                {
                    typeValue = data.getString("object_sub_type_s");
                }
                addElement(document, resultElement, "type", typeValue, true);

            }
            if(data.has("name")){
                String nameValue = data.getString("name");
                nameValue = stringChangeLine(nameValue, 20);

                addElement(document, resultElement, "name", nameValue, true);
            }

            if(data.has("title_parseable")){
                String titleValue = data.getString("title_parseable");
                titleValue = stringChangeLine(titleValue, 17);

                addElement(document, resultElement, "title", titleValue, true);
            }

            if(data.has("parent_number_lcs")){
                String parentValue = data.getString("parent_number_lcs");
                parentValue = stringChangeLine(parentValue, 14);
                if(data.has("related_object_number_s")){
                    parentValue = data.getString("related_object_number_s");
                }
                addElement(document, resultElement, "parent", parentValue, true);
            }

            if(data.has("assignee_full_name_lcs")){
                String assigneeValue = data.getString("assignee_full_name_lcs");
                assigneeValue = stringChangeLine(assigneeValue, 11);
                addElement(document, resultElement, "assignee", assigneeValue.toString(), true);
            }

            if( data.has("modified_date_tdt")){
                String modifiedValue = data.getString("modified_date_tdt");
                if (modifiedValue.matches(ISO8601_PATTERN))
                {
                    // transform into Excel-recognizable format
                    try
                    {
                        LocalDateTime localDateTime = LocalDateTime.parse(modifiedValue, SOLR_DATE_TIME_PATTERN);
                        modifiedValue = timeZoneAdjust(localDateTime, timeZoneOffsetinMinutes);
                    }
                    catch (DateTimeException e)
                    {
                        log.warn("[{}] cannot be parsed as Solr date/time value, exporting as it is", modifiedValue);
                    }
                }
                addElement(document, resultElement, "modified", modifiedValue, true);
            }

        }

        return document;
    }


    @Override public String generateReportName(String name)
    {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return String.format("%s-%s.pdf", name, formatter.format(new Date()));
    }

    @Override public String getReportContentType()
    {
        return "application/pdf";
    }


    /**
     * A helper method that simplifies this class.
     *
     * @param doc
     *            the DOM Document, used as a factory for
     *            creating Elements.
     * @param parent
     *            the DOM Element to add the child to.
     * @param elemName
     *            the name of the XML element to create.
     * @param elemValue
     *            the text content of the new XML element.
     * @param required
     *            if true, insert 'required="true"' attribute.
     */
    public void addElement(Document doc, Element parent, String elemName,
            String elemValue, boolean required)
    {
        Element elem = doc.createElement(elemName);
        elem.appendChild(doc.createTextNode(elemValue));
        if (required)
        {
            elem.setAttribute("required", "true");
        }
        parent.appendChild(elem);
    }

    public PdfService getPdfService()
    {
        return pdfService;
    }

    public void setPdfService(PdfService pdfService)
    {
        this.pdfService = pdfService;
    }

    public FileConfigurationService getFileConfigurationService()
    {
        return fileConfigurationService;
    }

    public void setFileConfigurationService(FileConfigurationService fileConfigurationService)
    {
        this.fileConfigurationService = fileConfigurationService;
    }

    public ConfigurationClientConfig getConfigurationClientConfig()
    {
        return configurationClientConfig;
    }

    public void setConfigurationClientConfig(ConfigurationClientConfig configurationClientConfig)
    {
        this.configurationClientConfig = configurationClientConfig;
    }

    /**
     * Time zone process for AFDP-5769
     *
     * @param localDateTime           service time
     * @param timeZoneOffsetinMinutes timeZone received from client. Should be format like"240" "-480"
     */
    private String timeZoneAdjust(LocalDateTime localDateTime, int timeZoneOffsetinMinutes)
    {
        int adjTimeZone = ~(timeZoneOffsetinMinutes / 60) + 1;
        LocalDateTime adjDateTime = localDateTime.plusHours(adjTimeZone);

        return adjDateTime.format(DATE_TIME_PATTERN);
    }

    /**
     * Change Line once string is over length
     * @param value
     * @return
     */
    private String stringChangeLine(String value, int size){
        String tempString = "";
        if(value != null && value.length() > size){

            Integer startSize = 0;
            Integer endSize = size;
            for(Integer i = 0;i <= value.length()/size ;i++)
            {
                boolean isAddOne = false;
                if (endSize >= value.length())
                {
                    endSize = value.length();

                }
                // if next char of Enter is ',' or '.', it may cause faill to change line in pdf file. Get the next char of those symbol.
                if( (endSize < value.length()) && (value.charAt(endSize) == ',' || value.charAt(endSize) == '.')){
                    endSize += 1;
                    isAddOne = true;
                }
                tempString = tempString + System.getProperties().getProperty("line.separator") + value.substring(startSize, endSize);
                if(isAddOne){
                    startSize += 1;
                }
                startSize = startSize + size;
                endSize = endSize + size;
            }
        }else{
            tempString = value;
        }
        return tempString;
    }
}

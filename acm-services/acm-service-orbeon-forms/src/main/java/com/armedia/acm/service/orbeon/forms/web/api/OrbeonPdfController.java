package com.armedia.acm.service.orbeon.forms.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

@Controller
@RequestMapping({ "/api/v1/forms/pdf", "/api/latest/forms/pdf"})
public class OrbeonPdfController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.POST, consumes = "application/xml", produces = "application/xml")
    @ResponseBody
    public String savePdfRendition(
            Authentication auth,
            @RequestParam(value = "app", required = false) String orbeonApplication,
            @RequestParam(value = "form", required = false) String formType,
            @RequestParam(value = "document", required = false) String orbeonDocId,
            @RequestParam(value = "valid", required = false) boolean valid,
            @RequestParam(value = "language", required = false, defaultValue = "en") String language,
            HttpServletRequest request
    ) throws IOException, ParserConfigurationException, SAXException
    {
        log.debug("Form type: " + formType + "; doc id: " + orbeonDocId);

        BufferedReader br = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while ( line != null )
        {
            sb.append(line).append("\r\n");
            line = br.readLine();
        }

        String formXml = sb.toString();

        log.debug("Request: " + formXml);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(sb.toString())));
        log.debug("Doc XML Version: " + doc.getXmlVersion());

        return formXml;
    }
}

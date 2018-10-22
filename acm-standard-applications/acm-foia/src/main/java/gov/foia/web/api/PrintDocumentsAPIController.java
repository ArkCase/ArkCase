package gov.foia.web.api;

import com.armedia.acm.objectdataprocessing.BinaryDataProvider;
import com.armedia.acm.objectdataprocessing.ObjectDataExtractingProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import gov.foia.model.FOIARequest;

/**
 * Controller that accepts a list of <code>FOIARequest</code>s ids, and returns binary data of the documents merged and
 * extracted from those requests.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 16, 2016
 */
@Controller
@RequestMapping({ "/api/v1/service/casefile/print", "/api/latest/service/casefile/print" })
public class PrintDocumentsAPIController
{

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Printing service that does the extraction and merging of the documents into single PDF file.
     */
    private ObjectDataExtractingProcessor<FOIARequest, BinaryDataProvider<FOIARequest>> documentPrintingService;

    @RequestMapping(value = "/{caseFileIds}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> printDocuments(@PathVariable("caseFileIds") List<Long> caseFileIds, HttpSession session,
            Authentication authentication)
    {

        String ipAddress = (String) session.getAttribute("acm_ip_address");
        log.debug("User [{}@{}] requested printing of Case files with IDs [{}]", authentication.getName(), ipAddress, caseFileIds);

        BinaryDataProvider<FOIARequest> printDocument;
        try
        {
            printDocument = documentPrintingService.processObjects(caseFileIds);
        }
        catch (IOException e)
        {
            log.error("Error while printing documents for case files with IDs {}.", caseFileIds, e);
            throw new DocumentPrintingException();
        }

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.add("Content-Disposition", String.format("inline; %s", printDocument.getFileName()));

        ResponseEntity<InputStreamResource> response = ResponseEntity.ok().contentLength(printDocument.getContentLength())
                .contentType(MediaType.parseMediaType("application/pdf")).body(new InputStreamResource(printDocument.getContent()));

        documentPrintingService.postProcessDataProvider(printDocument);

        return response;
    }

    /**
     * @param documentPrintingService
     *            the documentPrintingService to set
     */
    public void setDocumentPrintingService(
            ObjectDataExtractingProcessor<FOIARequest, BinaryDataProvider<FOIARequest>> documentPrintingService)
    {
        this.documentPrintingService = documentPrintingService;
    }

}

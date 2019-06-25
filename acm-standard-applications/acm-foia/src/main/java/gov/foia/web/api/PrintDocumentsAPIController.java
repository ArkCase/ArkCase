package gov.foia.web.api;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.objectdataprocessing.BinaryDataProvider;
import com.armedia.acm.objectdataprocessing.ObjectDataExtractingProcessor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private Logger log = LogManager.getLogger(getClass());

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

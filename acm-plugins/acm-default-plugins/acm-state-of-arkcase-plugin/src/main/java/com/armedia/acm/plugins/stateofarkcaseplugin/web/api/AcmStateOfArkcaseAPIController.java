package com.armedia.acm.plugins.stateofarkcaseplugin.web.api;

import com.armedia.acm.plugins.stateofarkcaseplugin.service.AcmStateOfArkcaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;

/**
 * Created by nebojsha on 28.10.2015.
 */

@Controller
@RequestMapping(value = { "/api/v1/plugin/state-of-arkcase",
        "/api/latest/plugin/state-of-arkcase" })
public class AcmStateOfArkcaseAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmStateOfArkcaseService acmStateOfArkcaseService;

    /**
     *
     * @param date
     *            date in ISO-8601 format
     * @return generated file
     */
    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> generateStateOfArkcase(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "date", required = false) LocalDate date)
            throws FileNotFoundException
    {

        File file = acmStateOfArkcaseService.generateReportForDay(date != null ? date : LocalDate.now());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        log.debug("Generating state of arkcase report.");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).contentLength(file.length())
                .body(resource);
    }

    public void setAcmStateOfArkcaseService(AcmStateOfArkcaseService acmStateOfArkcaseService)
    {
        this.acmStateOfArkcaseService = acmStateOfArkcaseService;
    }
}

package com.armedia.acm.plugins.stateofarkcaseplugin.web.api;

/*-
 * #%L
 * ACM Plugins: Plugin State of Arkcase
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

import com.armedia.acm.core.exceptions.AcmStateOfArkcaseGenerateReportException;
import com.armedia.acm.plugins.stateofarkcaseplugin.service.AcmStateOfArkcaseService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private Logger log = LogManager.getLogger(getClass());

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
            throws FileNotFoundException, AcmStateOfArkcaseGenerateReportException
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

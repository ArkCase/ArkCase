package com.armedia.acm.plugins.stateofarkcaseplugin.web.api;

import com.armedia.acm.plugins.stateofarkcaseplugin.service.AcmStateOfArkcaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import java.io.File;
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
     * @param response
     *            injected instance of httpServletResponse
     * @return generated file
     */
    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    public @ResponseBody Resource generateStateOfArkcase(@RequestParam(value = "date", required = false) LocalDate date,
            HttpServletResponse response)
    {
        log.debug("Generating state of arkcase report.");
        File file = acmStateOfArkcaseService.generateReportForDay(date != null ? date : LocalDate.now());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        return new FileSystemResource(file);
    }

    public void setAcmStateOfArkcaseService(AcmStateOfArkcaseService acmStateOfArkcaseService)
    {
        this.acmStateOfArkcaseService = acmStateOfArkcaseService;
    }
}

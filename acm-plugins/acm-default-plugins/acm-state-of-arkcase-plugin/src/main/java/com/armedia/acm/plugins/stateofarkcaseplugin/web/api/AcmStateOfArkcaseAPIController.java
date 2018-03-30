package com.armedia.acm.plugins.stateofarkcaseplugin.web.api;

import com.armedia.acm.plugins.stateofarkcaseplugin.service.AcmStateOfArkcaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    @RequestMapping(value = "/{date}", method = RequestMethod.GET)
    @ResponseBody
    public void getStateOfArkcase(@PathVariable(value = "date") LocalDate date,
            Authentication authentication)
    {

    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void generateStateOfArkcase(Authentication authentication)
    {
        log.debug("Generating state of arkcase report.");
        acmStateOfArkcaseService.generateDailyReport();
    }

    public void setAcmStateOfArkcaseService(AcmStateOfArkcaseService acmStateOfArkcaseService)
    {
        this.acmStateOfArkcaseService = acmStateOfArkcaseService;
    }
}

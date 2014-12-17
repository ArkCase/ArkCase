package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Properties;

@Controller
@RequestMapping( { "/api/v1/plugin/casefile", "/api/latest/plugin/casefile"})
public class GetCaseTypesAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Properties caseFileProperties;

    @RequestMapping(value = "/caseTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String[] caseTypes(
            Authentication authentication,
            HttpSession session
    ) throws AcmListObjectsFailedException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Finding case types");
        }

        String commaSeparated = getCaseFileProperties().getProperty("casefile.case-types");

        String[] retval = commaSeparated.split(",");

        return retval;
    }

    public Properties getCaseFileProperties()
    {
        return caseFileProperties;
    }

    public void setCaseFileProperties(Properties caseFileProperties)
    {
        this.caseFileProperties = caseFileProperties;
    }
}

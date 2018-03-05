/**
 *
 */
package com.armedia.acm.services.costsheet.web;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.service.CostsheetService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({ "/api/v1/service/costsheet", "/api/latest/service/costsheet" })
public class GetCostsheetsAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private CostsheetService costsheetService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getCostsheets(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws AcmListObjectsFailedException
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Taking all costsheets.");
        }

        String jsonResponse = getCostsheetService().getObjectsFromSolr(CostsheetConstants.OBJECT_TYPE, auth, startRow, maxRows, sort, "*",
                null);

        if (jsonResponse == null)
        {
            throw new AcmListObjectsFailedException(CostsheetConstants.OBJECT_TYPE, "Could not retrieve list of Costsheets.",
                    new Throwable());
        }

        return jsonResponse;
    }

    public CostsheetService getCostsheetService()
    {
        return costsheetService;
    }

    public void setCostsheetService(CostsheetService costsheetService)
    {
        this.costsheetService = costsheetService;
    }
}

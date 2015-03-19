/**
 * 
 */
package com.armedia.acm.services.costsheet.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.service.CostsheetService;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/costsheet", "/api/latest/service/costsheet" })
public class SaveCostsheetAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private CostsheetService costsheetService;
	
	@RequestMapping(value="", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AcmCostsheet getCostsheet(@RequestBody AcmCostsheet costsheet,
            Authentication auth) throws AcmCreateObjectFailedException
	{
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Saving costsheet " + costsheet);
		}
		
		try
        {
			AcmCostsheet saved = getCostsheetService().save(costsheet);	
			
			return saved;
		}
	    catch (RuntimeException e)
	    {
	        throw new AcmCreateObjectFailedException(CostsheetConstants.OBJECT_TYPE, e.getMessage(), e);
	    }
	}

	public CostsheetService getCostsheetService() {
		return costsheetService;
	}

	public void setCostsheetService(CostsheetService costsheetService) {
		this.costsheetService = costsheetService;
	}
}

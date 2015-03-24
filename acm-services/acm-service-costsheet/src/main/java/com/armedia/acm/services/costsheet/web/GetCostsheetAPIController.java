/**
 * 
 */
package com.armedia.acm.services.costsheet.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.service.CostsheetService;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/costsheet", "/api/latest/service/costsheet" })
public class GetCostsheetAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private CostsheetService costsheetService;
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AcmCostsheet getCostsheet(@PathVariable("id") Long id,
            Authentication auth) throws AcmObjectNotFoundException
	{
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking costsheet id=" + id);
		}
		
		AcmCostsheet costsheet = getCostsheetService().get(id);
		
		if (costsheet == null)
		{
			throw new AcmObjectNotFoundException(CostsheetConstants.OBJECT_TYPE, id, "Could not retrieve Costsheet.", new Throwable());
		}
		
		return costsheet;
	}

	public CostsheetService getCostsheetService() {
		return costsheetService;
	}

	public void setCostsheetService(CostsheetService costsheetService) {
		this.costsheetService = costsheetService;
	}
}

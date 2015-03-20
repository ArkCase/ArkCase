/**
 * 
 */
package com.armedia.acm.services.costsheet.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.service.CostsheetService;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/costsheet", "/api/latest/service/costsheet" })
public class GetCostsheetsForObjectIdAndTypeAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private CostsheetService costsheetService;
	
	@RequestMapping(value="/objectId/{objectId}/objectType/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<AcmCostsheet> getAllCostsheetsForObject(@PathVariable("objectId") Long objectId,
			@PathVariable("objectType") String objectType,
			@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,			
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws AcmListObjectsFailedException
	{
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking all costsheets for objectId=" + objectId + " and objectType=" + objectType);
		}
		
		List<AcmCostsheet> costsheets = getCostsheetService().getByObjectIdAndType(objectId, objectType, startRow, maxRows, sort);
		
		if (costsheets == null)
		{
			throw new AcmListObjectsFailedException(CostsheetConstants.OBJECT_TYPE, "Could not retrieve list of Costsheets.", new Throwable());
		}
		
		return costsheets;
	}

	public CostsheetService getCostsheetService() {
		return costsheetService;
	}

	public void setCostsheetService(CostsheetService costsheetService) {
		this.costsheetService = costsheetService;
	}
}

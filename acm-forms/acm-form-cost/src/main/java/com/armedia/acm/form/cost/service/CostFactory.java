/**
 * 
 */
package com.armedia.acm.form.cost.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.form.cost.model.CostForm;
import com.armedia.acm.form.cost.model.CostItem;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.services.costsheet.dao.AcmCostDao;
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCost;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;

/**
 * @author riste.tutureski
 *
 */
public class CostFactory extends FrevvoFormFactory {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmCostDao acmCostDao;
	private AcmCostsheetDao acmCostsheetDao;
	
	/**
	 * Converting Frevvo CostForm to AcmCostsheet
	 * 
	 * @param form
	 * @return
	 */
	public AcmCostsheet asAcmCostsheet(CostForm form)
	{
		LOG.debug("Start converting Frevvo Cost Form to Acm Costsheet ...");
		
		AcmCostsheet retval = null;
		
		if (form != null && form.getId() != null)
		{
			retval = getAcmCostsheetDao().find(form.getId());
		}
		
		if (retval == null)
		{
			retval = new AcmCostsheet();
		}
		
		if (form != null)
		{
			retval.setId(form.getId());
			retval.setUser(getUser(form.getUser()));
			retval.setParentId(form.getObjectId());
			retval.setParentType(form.getObjectType());
			retval.setParentNumber(form.getObjectNumber());
			retval.setStatus(form.getStatus());
			retval.setCosts(asAcmCosts(form.getItems()));
			retval.setDetails(form.getDetails());
			retval.setParticipants(asAcmParticipants(form.getApprovers()));
		}
		else
		{
			LOG.debug("The conversion process is not executed. Form is null.");
		}
		
		LOG.debug("End converting Frevvo Cost Form to Acm Costsheet.");
		
		return retval;
	}
	
	/**
	 * Convert AcmCostsheet to Frevvo CostForm
	 * 
	 * @param costsheet
	 * @return
	 */
	public CostForm asFrevvoCostForm(AcmCostsheet costsheet)
	{
		LOG.debug("Start converting Acm Costsheet to Frevvo Cost Form ...");
		
		CostForm form = null;
		
		if (costsheet != null)
		{
			form = new CostForm();
			
			form.setId(costsheet.getId());
			
			if (costsheet.getUser() != null)
			{
				form.setUser(costsheet.getUser().getUserId());
			}

			form.setObjectId(costsheet.getParentId());
			form.setObjectType(costsheet.getParentType());
			form.setObjectNumber(costsheet.getParentNumber());
			form.setStatus(costsheet.getStatus());
			form.setItems(asFrevvoCostItems(costsheet.getCosts()));
			form.setApprovers(asFrevvoApprovers(costsheet.getParticipants()));
		}
		else
		{
			LOG.debug("The conversion process is not executed. Costsheet is null.");
		}
		
		LOG.debug("End converting Acm Costsheet to Frevvo Cost Form.");
		
		return form;
	}
	
	/**
	 * Convert Frevvo CostItems to AcmCosts
	 * 
	 * @param items
	 * @return
	 */
	private List<AcmCost> asAcmCosts(List<CostItem> items)
	{
		LOG.debug("Converting Frevvo Cost Items to Acm Costs.");
		
		List<AcmCost> retval = new ArrayList<>();
		
		if (items != null)
		{
			for (CostItem item : items)
			{
				AcmCost cost = null;
				
				if (item.getId() != null)
				{
					cost = getAcmCostDao().find(item.getId());
				}
				
				if (cost == null)
				{
					cost = new AcmCost();
				}
				
				cost.setId(item.getId());
				cost.setDate(item.getDate());
				cost.setTitle(item.getTitle());
				cost.setDescription(item.getDescription());
				cost.setValue(item.getAmount());
				
				retval.add(cost);
			}
		}
		
		return retval;
	}
	
	/**
	 * Convert AcmCosts to Frevvo CostItems
	 * 
	 * @param costs
	 * @return
	 */
	private List<CostItem> asFrevvoCostItems(List<AcmCost> costs)
	{
		LOG.debug("Converting Acm Costs to Frevvo Cost Items.");
		
		List<CostItem> retval = new ArrayList<>();
		
		if (costs != null)
		{
			for (AcmCost cost : costs)
			{
				CostItem item = new CostItem();
				
				item.setId(cost.getId());
				item.setDate(cost.getDate());
				item.setTitle(cost.getTitle());
				item.setDescription(cost.getDescription());
				item.setAmount(cost.getValue());
				
				retval.add(item);
			}
		}
		
		return retval;
	}

	public AcmCostsheetDao getAcmCostsheetDao() {
		return acmCostsheetDao;
	}

	public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao) {
		this.acmCostsheetDao = acmCostsheetDao;
	}

	public AcmCostDao getAcmCostDao() {
		return acmCostDao;
	}

	public void setAcmCostDao(AcmCostDao acmCostDao) {
		this.acmCostDao = acmCostDao;
	}	
	
}

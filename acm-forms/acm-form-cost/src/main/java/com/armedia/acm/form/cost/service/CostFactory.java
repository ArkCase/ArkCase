/**
 * 
 */
package com.armedia.acm.form.cost.service;

/*-
 * #%L
 * ACM Forms: Cost
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

import com.armedia.acm.core.AcmTitleEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.form.cost.model.CostForm;
import com.armedia.acm.form.cost.model.CostItem;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.services.costsheet.dao.AcmCostDao;
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCost;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author riste.tutureski
 *
 */
public class CostFactory extends FrevvoFormFactory
{

    private Logger LOG = LogManager.getLogger(getClass());

    private AcmCostDao acmCostDao;
    private AcmCostsheetDao acmCostsheetDao;
    private SpringContextHolder springContextHolder;

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
            form.setObjectTitle(getObjectTitleByObjectCode(costsheet.getParentId(), costsheet.getParentType()));
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

    public String getObjectTitleByObjectCode(Long objectId, String objectType)
    {

        String title = "";

        Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);

        AcmAbstractDao<AcmTitleEntity> dao = daos.values()
                .stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getSupportedObjectType() != null)
                .filter(item -> item.getSupportedObjectType().equals(objectType))
                .findFirst()
                .orElse(null);

        if (dao != null)
        {
            AcmTitleEntity entityObject = dao.find(objectId);
            title = entityObject.getTitle();
        }

        return title;
    }

    public AcmCostsheetDao getAcmCostsheetDao()
    {
        return acmCostsheetDao;
    }

    public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao)
    {
        this.acmCostsheetDao = acmCostsheetDao;
    }

    public AcmCostDao getAcmCostDao()
    {
        return acmCostDao;
    }

    public void setAcmCostDao(AcmCostDao acmCostDao)
    {
        this.acmCostDao = acmCostDao;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }
}

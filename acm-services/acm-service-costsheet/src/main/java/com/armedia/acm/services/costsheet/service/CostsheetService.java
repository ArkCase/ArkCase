/**
 *
 */
package com.armedia.acm.services.costsheet.service;

/*-
 * #%L
 * ACM Service: Costsheet
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

import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Properties;

/**
 * @author riste.tutureski
 */
public interface CostsheetService
{

    public Properties getProperties();

    @Transactional
    public AcmCostsheet save(AcmCostsheet costsheet, Authentication authentication, String submissionName) throws PipelineProcessException;

    @Transactional
    public AcmCostsheet save(AcmCostsheet costsheet, String submissionName) throws PipelineProcessException;

    public AcmCostsheet get(Long id);

    public List<AcmCostsheet> getByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams);

    public String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams,
            String searchQuery, String userId);

    public String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams,
            String userId);

    public boolean checkWorkflowStartup(String type);

    public String createName(AcmCostsheet costsheet);
}

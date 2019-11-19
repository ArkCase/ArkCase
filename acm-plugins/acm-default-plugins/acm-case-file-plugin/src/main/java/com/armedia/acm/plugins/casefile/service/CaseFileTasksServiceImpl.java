package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.services.search.service.ChildDocumentsSearchService;

import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * @author aleksandar.bujaroski
 */
public class CaseFileTasksServiceImpl implements CaseFileTasksService
{
    private ChildDocumentsSearchService childDocumentsSearchService;

    @Override
    public String getCaseFileTasks(Long caseId, String parentType, Long parentId, List<String> childTypes,
            String sort, Integer startRow, Integer maxRows, Authentication authentication) throws MuleException
    {
        return getChildDocumentsSearchService().searchForChildrenAndGrandchildrenDocuments(parentType, caseId, childTypes, sort, startRow,
                maxRows,
                authentication);
    }

    public ChildDocumentsSearchService getChildDocumentsSearchService()
    {
        return childDocumentsSearchService;
    }

    public void setChildDocumentsSearchService(ChildDocumentsSearchService childDocumentsSearchService)
    {
        this.childDocumentsSearchService = childDocumentsSearchService;
    }
}

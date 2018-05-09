package com.armedia.acm.plugins.casefile.listener;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatusConstants;
import com.armedia.acm.plugins.task.model.BuckslipProcessStateEvent;

import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.Objects;

public class ChangeCaseStatusOnBuckslipEventListener implements ApplicationListener<BuckslipProcessStateEvent>
{

    private CaseFileDao caseFileDao;

    @Override
    public void onApplicationEvent(BuckslipProcessStateEvent buckslipProcessStateEvent)
    {
        Map<String, Object> processVariables = (Map<String, Object>) buckslipProcessStateEvent.getSource();

        String parentObjectType = (String) processVariables.getOrDefault(CaseFileConstants.PARENT_OBJECT_TYPE, "");
        Long parentObjectId = (Long) processVariables.getOrDefault(CaseFileConstants.PARENT_OBJECT_ID, null);

        if (CaseFileConstants.OBJECT_TYPE.equals(parentObjectType) && Objects.nonNull(parentObjectId))
        {
            CaseFile caseFile = getCaseFileDao().find(parentObjectId);

            switch (buckslipProcessStateEvent.getBuckslipProcessState())
            {
            case INITIALIZED:
                caseFile.setStatus(ChangeCaseStatusConstants.STATUS_IN_APPROVAL);
                break;
            case WITHDRAWN:
                caseFile.setStatus(ChangeCaseStatusConstants.STATUS_DRAFT);
                break;
            case COMPLETED:
                caseFile.setStatus(ChangeCaseStatusConstants.STATUS_APPROVED);
                break;
            }

            getCaseFileDao().save(caseFile);
        }
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}

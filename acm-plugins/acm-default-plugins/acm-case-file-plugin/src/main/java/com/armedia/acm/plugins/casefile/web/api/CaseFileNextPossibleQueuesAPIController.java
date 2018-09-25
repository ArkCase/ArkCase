package com.armedia.acm.plugins.casefile.web.api;

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

import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueuesModel;
import com.armedia.acm.plugins.businessprocess.service.QueueService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.CaseFileNextPossibleQueuesBusinessRule;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class CaseFileNextPossibleQueuesAPIController
{

    private QueueService queueService;

    private CaseFileNextPossibleQueuesBusinessRule businessRule;

    private CaseFileDao caseFileDao;

    @RequestMapping(value = "/nextPossibleQueues/{caseFileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CaseFileNextPossibleQueuesResponse nextPossibleQueues(@PathVariable("caseFileId") Long caseFileId, HttpSession session,
            Authentication auth)
    {

        CaseFile caseFile = caseFileDao.find(caseFileId);

        if (caseFile == null)
        {
            return new CaseFileNextPossibleQueuesResponse("", "", "", new ArrayList<>());
        }

        CaseFilePipelineContext context = new CaseFilePipelineContext();
        context.setNewCase(caseFile.getId() == null);
        context.setAuthentication(auth);
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        context.setIpAddress(ipAddress);
        context.setQueueName(caseFile.getQueue().getName());

        NextPossibleQueuesModel<CaseFile, CaseFilePipelineContext> nextPossibleQueues = queueService.nextPossibleQueues(caseFile, context,
                businessRule);
        return new CaseFileNextPossibleQueuesResponse(nextPossibleQueues.getDefaultNextQueue(), nextPossibleQueues.getDefaultReturnQueue(),
                nextPossibleQueues.getDefaultDenyQueue(), nextPossibleQueues.getNextPossibleQueues());

    }

    public QueueService getQueueService()
    {
        return queueService;
    }

    public void setQueueService(QueueService queueService)
    {
        this.queueService = queueService;
    }

    public CaseFileNextPossibleQueuesBusinessRule getBusinessRule()
    {
        return businessRule;
    }

    public void setBusinessRule(CaseFileNextPossibleQueuesBusinessRule businessRule)
    {
        this.businessRule = businessRule;
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

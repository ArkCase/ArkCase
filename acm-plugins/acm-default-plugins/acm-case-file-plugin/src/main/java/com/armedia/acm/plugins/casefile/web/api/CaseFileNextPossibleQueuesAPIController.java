package com.armedia.acm.plugins.casefile.web.api;

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
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class CaseFileNextPossibleQueuesAPIController
{

    private QueueService queueService;

    private CaseFileNextPossibleQueuesBusinessRule businessRule;

    private CaseFileDao caseFileDao;

    @RequestMapping(value = "/nextPossibleQueues/{caseFileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> nextPossibleQueues(@PathVariable("caseFileId") Long caseFileId, HttpSession session, Authentication auth)
    {

        CaseFile caseFile = caseFileDao.find(caseFileId);

        if (caseFile == null)
        {
            return new ArrayList<String>();
        }

        CaseFilePipelineContext context = new CaseFilePipelineContext();
        context.setNewCase(caseFile.getId() == null);
        context.setAuthentication(auth);
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        context.setIpAddress(ipAddress);
        context.setQueueName(caseFile.getQueue().getName());

        NextPossibleQueuesModel<CaseFile, CaseFilePipelineContext> nextPossibleQueues = queueService.nextPossibleQueues(caseFile, context,
                businessRule);
        return nextPossibleQueues.getNextPossibleQueues();

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

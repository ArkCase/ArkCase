package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.businessprocess.model.EnterQueueModel;
import com.armedia.acm.plugins.businessprocess.model.LeaveCurrentQueueModel;
import com.armedia.acm.plugins.businessprocess.model.NextPossibleQueuesModel;
import com.armedia.acm.plugins.businessprocess.model.OnEnterQueueModel;
import com.armedia.acm.plugins.businessprocess.model.OnLeaveQueueModel;
import com.armedia.acm.plugins.businessprocess.service.QueueService;
import com.armedia.acm.plugins.businessprocess.service.StartBusinessProcessService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.web.api.CaseFileEnqueueResponse;
import com.armedia.acm.plugins.casefile.web.api.CaseFileEnqueueResponse.ErrorReason;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnqueueCaseFileServiceImpl implements EnqueueCaseFileService
{
    // private final Logger log = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;

    private LeaveCurrentQueueBusinessRule leaveCurrentQueueBusinessRule;

    private CaseFileNextPossibleQueuesBusinessRule caseFileNextPossibleQueuesBusinessRule;

    private EnterQueueBusinessRule enterQueueBusinessRule;

    private OnLeaveQueueBusinessRule onLeaveQueueBusinessRule;

    private OnEnterQueueBusinessRule onEnterQueueBusinessRule;

    private QueueService queueService;

    private StartBusinessProcessService startBusinessProcessService;

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public LeaveCurrentQueueBusinessRule getLeaveCurrentQueueBusinessRule()
    {
        return leaveCurrentQueueBusinessRule;
    }

    public void setLeaveCurrentQueueBusinessRule(LeaveCurrentQueueBusinessRule leaveCurrentQueueBusinessRule)
    {
        this.leaveCurrentQueueBusinessRule = leaveCurrentQueueBusinessRule;
    }

    public CaseFileNextPossibleQueuesBusinessRule getCaseFileNextPossibleQueuesBusinessRule()
    {
        return caseFileNextPossibleQueuesBusinessRule;
    }

    public void setCaseFileNextPossibleQueuesBusinessRule(CaseFileNextPossibleQueuesBusinessRule caseFileNextPossibleQueuesBusinessRule)
    {
        this.caseFileNextPossibleQueuesBusinessRule = caseFileNextPossibleQueuesBusinessRule;
    }

    public EnterQueueBusinessRule getEnterQueueBusinessRule()
    {
        return enterQueueBusinessRule;
    }

    public void setEnterQueueBusinessRule(EnterQueueBusinessRule enterQueueBusinessRule)
    {
        this.enterQueueBusinessRule = enterQueueBusinessRule;
    }

    public OnLeaveQueueBusinessRule getOnLeaveQueueBusinessRule()
    {
        return onLeaveQueueBusinessRule;
    }

    public void setOnLeaveQueueBusinessRule(OnLeaveQueueBusinessRule onLeaveQueueBusinessRule)
    {
        this.onLeaveQueueBusinessRule = onLeaveQueueBusinessRule;
    }

    public OnEnterQueueBusinessRule getOnEnterQueueBusinessRule()
    {
        return onEnterQueueBusinessRule;
    }

    public void setOnEnterQueueBusinessRule(OnEnterQueueBusinessRule onEnterQueueBusinessRule)
    {
        this.onEnterQueueBusinessRule = onEnterQueueBusinessRule;
    }

    public QueueService getQueueService()
    {
        return queueService;
    }

    public void setQueueService(QueueService queueService)
    {
        this.queueService = queueService;
    }

    public StartBusinessProcessService getStartBusinessProcessService()
    {
        return startBusinessProcessService;
    }

    public void setStartBusinessProcessService(StartBusinessProcessService startBusinessProcessService)
    {
        this.startBusinessProcessService = startBusinessProcessService;
    }

    @Override
    @Transactional
    public CaseFileEnqueueResponse enqueueCaseFile(Long caseId, String nextQueue, CaseFilePipelineContext context)
    {
        CaseFile caseFile = caseFileDao.find(caseId);
        context.setNewCase(false);

        LeaveCurrentQueueModel<CaseFile, CaseFilePipelineContext> leaveModel = new LeaveCurrentQueueModel<>();
        leaveModel.setBusinessObject(caseFile);
        leaveModel.setPipelineContext(context);
        leaveModel = leaveCurrentQueueBusinessRule.applyRules(leaveModel);

        if (!leaveModel.getCannotLeaveReasons().isEmpty())
        {
            return new CaseFileEnqueueResponse(ErrorReason.LEAVE, leaveModel.getCannotLeaveReasons(), nextQueue, caseFile);
        }

        NextPossibleQueuesModel<CaseFile, CaseFilePipelineContext> nextPossibleQueuesModel = queueService.nextPossibleQueues(caseFile,
                context, caseFileNextPossibleQueuesBusinessRule);
        List<String> nextPossibleQueues = nextPossibleQueuesModel.getNextPossibleQueues();
        if (nextPossibleQueues.isEmpty() || !nextPossibleQueues.contains(nextQueue))
        {
            return new CaseFileEnqueueResponse(ErrorReason.NEXT_POSSIBLE, nextQueue, caseFile);
        }

        OnLeaveQueueModel<CaseFile, CaseFilePipelineContext> onLeaveModel = new OnLeaveQueueModel<>();
        onLeaveModel.setBusinessObject(caseFile);
        onLeaveModel.setPipelineContext(context);
        onLeaveModel = onLeaveQueueBusinessRule.applyRules(onLeaveModel);

        String processName = onLeaveModel.getBusinessProcessName();
        if (processName != null && !processName.isEmpty())
        {
            Map<String, Object> processVariables = new HashMap<>();
            startBusinessProcessService.startBusinessProcess(processName, processVariables);
        }

        EnterQueueModel<CaseFile, CaseFilePipelineContext> enterModel = new EnterQueueModel<>();
        enterModel.setBusinessObject(caseFile);
        enterModel.setPipelineContext(context);
        enterModel = enterQueueBusinessRule.applyRules(enterModel);

        if (!enterModel.getCannotEnterReasons().isEmpty())
        {
            return new CaseFileEnqueueResponse(ErrorReason.ENTER, enterModel.getCannotEnterReasons(), nextQueue, caseFile);
        }

        OnEnterQueueModel<CaseFile, CaseFilePipelineContext> onEnterModel = new OnEnterQueueModel<>();
        onEnterModel.setBusinessObject(caseFile);
        onEnterModel.setPipelineContext(context);
        onEnterModel = onEnterQueueBusinessRule.applyRules(onEnterModel);

        processName = onEnterModel.getBusinessProcessName();
        if (processName != null && !processName.isEmpty())
        {
            Map<String, Object> processVariables = new HashMap<>();
            startBusinessProcessService.startBusinessProcess(processName, processVariables);
        }

        return new CaseFileEnqueueResponse(ErrorReason.NO_ERROR, nextQueue, caseFile);
    }

}

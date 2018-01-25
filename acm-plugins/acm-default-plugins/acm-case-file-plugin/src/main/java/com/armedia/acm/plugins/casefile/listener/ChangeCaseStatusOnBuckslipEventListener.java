package com.armedia.acm.plugins.casefile.listener;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
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

        String parentObjectType = (String) processVariables.getOrDefault("PARENT_OBJECT_TYPE", "");
        Long parentObjectId = (Long) processVariables.getOrDefault("PARENT_OBJECT_ID", null);

        if ("CASE_FILE".equals(parentObjectType) && Objects.nonNull(parentObjectId))
        {
            CaseFile caseFile = getCaseFileDao().find(parentObjectId);

            switch (buckslipProcessStateEvent.getBuckslipProcessState())
            {
            case initialized:
                caseFile.setStatus("IN APPROVAL");
                break;
            case withdrawn:
                caseFile.setStatus("DRAFT");
                break;
            case completed:
                caseFile.setStatus("APPROVED");
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

package gov.foia.listener;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.casefile.model.CaseFileModifiedEvent;

import gov.foia.model.FOIARequest;
import gov.foia.model.FoiaConfig;
import gov.foia.service.DeclareRequestAsRecordService;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on April, 2021
 */
public class RequestQueueChangedListener implements ApplicationListener<CaseFileModifiedEvent>
{
    private FoiaConfig foiaConfig;
    private DeclareRequestAsRecordService declareRequestAsRecordService;

    @Override
    public void onApplicationEvent(CaseFileModifiedEvent caseFileModifiedEvent)
    {
        if (isSuccessfulQueueChangeEvent(caseFileModifiedEvent))
        {
            FOIARequest request = (FOIARequest) caseFileModifiedEvent.getSource();
            if (isRequestInReleaseQueue(request) && shouldDeclareRequestAsRecordWithoutDelay())
            {
                declareRequestAsRecordService.declareRecords(request);
            }
        }
    }

    private boolean isSuccessfulQueueChangeEvent(CaseFileModifiedEvent caseFileModifiedEvent)
    {
        return caseFileModifiedEvent != null
                && caseFileModifiedEvent.isSucceeded()
                && caseFileModifiedEvent.getEventType().equals("com.armedia.acm.casefile.queue.changed")
                && caseFileModifiedEvent.getSource() != null;
    }

    private boolean isRequestInReleaseQueue(FOIARequest request)
    {
        return request.getQueue() != null && request.getQueue().getName().equals("Release");
    }

    private boolean shouldDeclareRequestAsRecordWithoutDelay()
    {
        return foiaConfig.getDeclareRequestAsRecordsEnabled() && foiaConfig.getDeclareRequestAsRecordsDaysDelay() == 0;
    }

    public FoiaConfig getFoiaConfig()
    {
        return foiaConfig;
    }

    public void setFoiaConfig(FoiaConfig foiaConfig)
    {
        this.foiaConfig = foiaConfig;
    }

    public DeclareRequestAsRecordService getDeclareRequestAsRecordService()
    {
        return declareRequestAsRecordService;
    }

    public void setDeclareRequestAsRecordService(DeclareRequestAsRecordService declareRequestAsRecordService)
    {
        this.declareRequestAsRecordService = declareRequestAsRecordService;
    }
}

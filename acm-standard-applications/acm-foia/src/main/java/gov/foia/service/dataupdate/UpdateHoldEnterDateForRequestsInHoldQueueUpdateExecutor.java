package gov.foia.service.dataupdate;

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.util.List;

public class UpdateHoldEnterDateForRequestsInHoldQueueUpdateExecutor implements AcmDataUpdateExecutor
{

    private FOIARequestDao requestDao;

    @Override
    public String getUpdateId()
    {
        return "update_hold_enter_date_for_requests_in_hold_queue_v2";
    }

    @Override
    public void execute()
    {
        List<FOIARequest> requestList = getRequestDao().findAllHoldRequestsBefore(LocalDate.now());

        for (FOIARequest request : requestList)
        {
            request.setHoldEnterDate(LocalDate.now());
            requestDao.save(request);
        }
    }

    public FOIARequestDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }
}

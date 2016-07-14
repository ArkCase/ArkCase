/**
 *
 */
package com.armedia.acm.objectchangestatus.service;

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author riste.tutureski
 *
 */
public class ChangeObjectStatusServiceImpl implements ChangeObjectStatusService
{

    private AcmDataService acmDataService;

    private UserTrackerService userTrackerService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void change(Long objectId, String objectType, String status)
    {

        log.debug("User from IP {}.", userTrackerService.getTrackedUser().getIpAddress());

        AcmAbstractDao<AcmStatefulEntity> dao = getAcmDataService().getDaoByObjectType(objectType);

        if (dao != null)
        {
            AcmStatefulEntity entity = dao.find(objectId);

            if (entity != null)
            {
                entity.setStatus(status);
                dao.save(entity);
            }
        }
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }
}

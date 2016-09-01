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

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

/**
 * @author riste.tutureski
 */
public class ChangeObjectStatusServiceImpl implements ChangeObjectStatusService
{

    private AcmDataService acmDataService;

    private UserTrackerService userTrackerService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void change(Long objectId, String objectType, String status)
    {
        log.debug("Changing object status: type [{}], id [{}], new status: [{}]",
                objectType, objectId, status);

        AcmAbstractDao<AcmStatefulEntity> dao = getAcmDataService().getDaoByObjectType(objectType);

        if (dao != null)
        {
            AcmStatefulEntity entity;

            try
            {
                entity = dao.find(objectId);
            } catch (EntityNotFoundException e)
            {
                // try and flush our SQL in case we are trying to operate on a brand new object
                entityManager.flush();
                entity = dao.find(objectId);
            }

            if (entity != null)
            {
                log.debug("Found object of type [{}], setting status to [{}]", entity.getClass().getName(), status);
                entity.setStatus(status);
                dao.save(entity);

                // now we have to flush our changes so other objects in a workflow will see our changes.
                entityManager.flush();
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

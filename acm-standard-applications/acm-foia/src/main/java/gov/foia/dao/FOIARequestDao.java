package gov.foia.dao;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import static gov.foia.model.FOIARequest.PURGE_BILLING_QUEUE;
import static gov.foia.model.FOIARequest.PURGE_HOLD_QUEUE;
import static gov.foia.model.FOIARequest.REQUESTS_BY_STATUS;

import com.armedia.acm.data.AcmAbstractDao;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.foia.model.FOIARequest;
import gov.foia.model.PortalFOIARequestStatus;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2016
 */
@Transactional
public class FOIARequestDao extends AcmAbstractDao<FOIARequest>
{

    @Override
    protected Class<FOIARequest> getPersistenceClass()
    {
        return FOIARequest.class;
    }

    public List<FOIARequest> getAllRequestsByStatus(List<String> statuses)
    {
        return getEm().createNamedQuery(REQUESTS_BY_STATUS, FOIARequest.class).setParameter("requestStatuses", statuses).getResultList();
    }

    public List<FOIARequest> getAllRequestsInBillingBefore(LocalDate billingEnterDate)
    {
        return getAllRequestsInQueueBefore(PURGE_BILLING_QUEUE, "Billing", "billingEnterDate", billingEnterDate);
    }

    public List<FOIARequest> getAllUnassignedRequestsInQueue(Long queueId)
    {
        String queryText = "SELECT cf FROM CaseFile cf WHERE cf.queue.id = :queueId AND cf.id in (SELECT p.objectId FROM AcmParticipant p WHERE p.objectType = 'CASE_FILE' AND p.participantType = 'assignee' AND p.participantLdapId = '')"
                + "ORDER BY cf.dueDate ASC";
        TypedQuery<FOIARequest> unassignedRequestsInQueue = getEm().createQuery(queryText, FOIARequest.class);
        unassignedRequestsInQueue.setParameter("queueId", queueId);
        return unassignedRequestsInQueue.getResultList();
    }

    public FOIARequest getOldestRequestInQueueAssignedToUser(Long queueId, String username)
    {
        String queryText = "SELECT cf FROM CaseFile cf WHERE cf.queue.id = :queueId AND cf.id in (SELECT p.objectId FROM AcmParticipant p WHERE p.objectType = 'CASE_FILE' AND p.participantType = 'assignee' AND p.participantLdapId = :username)"
                + "ORDER BY cf.dueDate ASC";
        TypedQuery<FOIARequest> requestsInQueueAssignedToUser = getEm().createQuery(queryText, FOIARequest.class);
        requestsInQueueAssignedToUser.setParameter("queueId", queueId);
        requestsInQueueAssignedToUser.setParameter("username", username);
        requestsInQueueAssignedToUser.setFirstResult(0);
        requestsInQueueAssignedToUser.setMaxResults(1);
        try
        {
            return requestsInQueueAssignedToUser.getSingleResult();
        }
        catch (NoResultException e)
        {
            return null;
        }
    }

    /**
     * @param minusDays
     * @return
     */
    public List<FOIARequest> getAllRequestsInHoldBefore(LocalDate holdEnterDate)
    {
        return getAllRequestsInQueueBefore(PURGE_HOLD_QUEUE, "Hold", "holdEnterDate", holdEnterDate);
    }

    private List<FOIARequest> getAllRequestsInQueueBefore(String queryName, String queueName, String enterDateFieldName,
            LocalDate enterDate)
    {

        TypedQuery<FOIARequest> query = getEm().createNamedQuery(queryName, FOIARequest.class);
        query.setParameter("queueName", queueName);
        query.setParameter(enterDateFieldName, enterDate);

        List<FOIARequest> result = query.getResultList();

        return result;

    }

    public List<PortalFOIARequestStatus> getExternalRequests(PortalFOIARequestStatus portalRequestStatus)
    {
        String queryText = "SELECT cf FROM FOIARequest cf JOIN PersonAssociation pa JOIN pa.person p"
                + " WHERE cf.id = pa.parentId"
                + " AND pa.parentType='CASE_FILE'"
                + " AND pa.personType = 'Requester'";

        if (portalRequestStatus.getRequestId() != null && !portalRequestStatus.getRequestId().equals("undefined"))
        {
            queryText += " AND cf.caseNumber = :caseNumber";
        }

        if (portalRequestStatus.getLastName() != null && !portalRequestStatus.getLastName().equals("undefined"))
        {
            queryText += " AND p.familyName = :lastName";
        }

        Query foiaRequests = getEm().createQuery(queryText);

        if (portalRequestStatus.getRequestId() != null && !portalRequestStatus.getRequestId().equals("undefined"))
        {
            foiaRequests.setParameter("caseNumber", portalRequestStatus.getRequestId());
        }

        if (portalRequestStatus.getLastName() != null && !portalRequestStatus.getLastName().equals("undefined"))
        {
            foiaRequests.setParameter("lastName", portalRequestStatus.getLastName().trim());
        }

        List<FOIARequest> resultList = foiaRequests.getResultList();

        return populateRequestStatusList(resultList);

    }

    public List<PortalFOIARequestStatus> getLoggedUserExternalRequests(Long personId)
    {
        String queryText = "SELECT cf FROM FOIARequest cf JOIN PersonAssociation pa JOIN pa.person p"
                + " WHERE cf.id = pa.parentId"
                + " AND pa.parentType='CASE_FILE'"
                + " AND pa.personType = 'Requester'"
                + " AND p.id = :personId";

        Query foiaRequests = getEm().createQuery(queryText);

        foiaRequests.setParameter("personId", personId);

        List<FOIARequest> resultList = foiaRequests.getResultList();

        return populateRequestStatusList(resultList);
    }

    private List<PortalFOIARequestStatus> populateRequestStatusList(List<FOIARequest> requests)
    {
        List<PortalFOIARequestStatus> requestStatusList = new ArrayList<>();
        for (FOIARequest request : requests)
        {
            PortalFOIARequestStatus requestStatus = populateRequestStatusFromRequest(request);
            requestStatusList.add(requestStatus);
        }

        return requestStatusList;
    }

    private PortalFOIARequestStatus populateRequestStatusFromRequest(FOIARequest request)
    {
        PortalFOIARequestStatus requestStatus = new PortalFOIARequestStatus();
        requestStatus.setRequestId(request.getCaseNumber());
        requestStatus.setRequestStatus(request.getStatus());
        requestStatus.setQueue(request.getQueue().getName());
        requestStatus.setUpdateDate(request.getModified());
        requestStatus.setIsDenied(request.getDeniedFlag());
        requestStatus.setWithdrawRequested(request.getWithdrawRequestedFlag());
        requestStatus.setIsPublic(request.getPublicFlag());
        requestStatus.setRequestType(request.getRequestType());
        requestStatus.setRequesterFirstName(request.getOriginator().getPerson().getGivenName());
        requestStatus.setRequesterLastName(request.getOriginator().getPerson().getFamilyName());
        requestStatus.setRequesterEmail(request.getOriginator().getPerson().getContactMethods()
                .stream().filter(cm -> cm.getType().equalsIgnoreCase("email")).findFirst().get().getValue());
        return requestStatus;
    }

    /**
     * @param portalUserId
     * @return
     */
    public List<PortalFOIARequestStatus> getExternalRequests(String portalUserId)
    {
        String queryText = "SELECT cf FROM FOIARequest cf JOIN PersonAssociation pa JOIN pa.person p"
                + " WHERE cf.id = pa.parentId"
                + " AND pa.parentType='CASE_FILE'"
                + " AND pa.personType = 'Requester'";

        if (portalUserId != null)
        {
            queryText += " AND p.id = :portalUserId";
        }

        TypedQuery<FOIARequest> foiaRequests = getEm().createQuery(queryText, FOIARequest.class);

        if (portalUserId != null)
        {
            foiaRequests.setParameter("portalUserId", Long.valueOf(portalUserId));
        }

        List<FOIARequest> resultList = foiaRequests.getResultList();

        return populateRequestStatusList(resultList);
    }

    /**
     * @param portalUserId
     * @param requestId
     * @return
     */
    public PortalFOIARequestStatus getExternalRequest(String portalUserId, String requestId)
    {
        String queryText = "SELECT cf FROM FOIARequest cf JOIN PersonAssociation pa JOIN pa.person p"
                + " WHERE cf.id = pa.parentId"
                + " AND pa.parentType='CASE_FILE'"
                + " AND pa.personType = 'Requester'";

        if (portalUserId != null)
        {
            queryText += " AND p.id = :portalUserId";
        }

        if (requestId != null)
        {
            queryText += " AND cf.caseNumber = :requestId";
        }

        TypedQuery<FOIARequest> foiaRequests = getEm().createQuery(queryText, FOIARequest.class);

        if (portalUserId != null)
        {
            foiaRequests.setParameter("portalUserId", Long.valueOf(portalUserId));
        }

        if (portalUserId != null)
        {
            foiaRequests.setParameter("requestId", requestId);
        }

        FOIARequest result = foiaRequests.getSingleResult();
        return populateRequestStatusFromRequest(result);
    }

    public List<FOIARequest> findAllNotReleasedRequests()
    {
        String queryText = "SELECT request FROM FOIARequest request"
                + " WHERE request.queue.name != 'Release'";
        TypedQuery<FOIARequest> allRecords = getEm().createQuery(queryText, FOIARequest.class);
        List<FOIARequest> requests = allRecords.getResultList();
        return requests;
    }

    public List<FOIARequest> findAllHeldAndAppealedRequests()
    {
        String queryText = "SELECT request FROM FOIARequest request"
                + " WHERE request.queue.name = 'Hold' OR request.requestType = 'Appeal'";
        TypedQuery<FOIARequest> allRecords = getEm().createQuery(queryText, FOIARequest.class);
        List<FOIARequest> requests = allRecords.getResultList();
        return requests;
    }

    public List<FOIARequest> getNextAvailableRequestInQueue(Long queueId, Date createdDate)
    {
        String queryText = "SELECT request FROM CaseFile request "
                + "WHERE request.queue.id = :queueId AND request.created < :createdDate "
                + "ORDER BY request.created DESC";
        TypedQuery<FOIARequest> nextRequestQuery = getEm().createQuery(queryText, FOIARequest.class);
        nextRequestQuery.setParameter("queueId", queueId);
        nextRequestQuery.setParameter("createdDate", createdDate);

        return nextRequestQuery.getResultList();
    }
}

package gov.privacy.dao;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import static gov.privacy.model.SubjectAccessRequest.PURGE_HOLD_QUEUE;
import static gov.privacy.model.SubjectAccessRequest.REQUESTS_BY_STATUS;

import com.armedia.acm.data.AcmAbstractDao;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.privacy.model.PortalSARStatus;
import gov.privacy.model.SubjectAccessRequest;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 */
@Transactional
public class SARDao extends AcmAbstractDao<SubjectAccessRequest>
{

    @Override
    protected Class<SubjectAccessRequest> getPersistenceClass()
    {
        return SubjectAccessRequest.class;
    }

    public List<SubjectAccessRequest> getAllRequestsByStatus(List<String> statuses)
    {
        return getEm().createNamedQuery(REQUESTS_BY_STATUS, SubjectAccessRequest.class).setParameter("requestStatuses", statuses).getResultList();
    }

    public List<SubjectAccessRequest> getAllUnassignedRequestsInQueue(Long queueId)
    {
        String queryText = "SELECT cf FROM CaseFile cf WHERE cf.queue.id = :queueId AND cf.id in (SELECT p.objectId FROM AcmParticipant p WHERE p.objectType = 'CASE_FILE' AND p.participantType = 'assignee' AND p.participantLdapId = '')"
                + "ORDER BY cf.dueDate ASC";
        TypedQuery<SubjectAccessRequest> unassignedRequestsInQueue = getEm().createQuery(queryText, SubjectAccessRequest.class);
        unassignedRequestsInQueue.setParameter("queueId", queueId);
        return unassignedRequestsInQueue.getResultList();
    }

    public SubjectAccessRequest getOldestRequestInQueueAssignedToUser(Long queueId, String username)
    {
        String queryText = "SELECT cf FROM CaseFile cf WHERE cf.queue.id = :queueId AND cf.id in (SELECT p.objectId FROM AcmParticipant p WHERE p.objectType = 'CASE_FILE' AND p.participantType = 'assignee' AND p.participantLdapId = :username)"
                + "ORDER BY cf.dueDate ASC";
        TypedQuery<SubjectAccessRequest> requestsInQueueAssignedToUser = getEm().createQuery(queryText, SubjectAccessRequest.class);
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
    public List<SubjectAccessRequest> getAllRequestsInHoldBefore(LocalDate holdEnterDate)
    {
        return getAllRequestsInQueueBefore(PURGE_HOLD_QUEUE, "Hold", "holdEnterDate", holdEnterDate);
    }

    private List<SubjectAccessRequest> getAllRequestsInQueueBefore(String queryName, String queueName, String enterDateFieldName,
                                                                   LocalDate enterDate)
    {

        TypedQuery<SubjectAccessRequest> query = getEm().createNamedQuery(queryName, SubjectAccessRequest.class);
        query.setParameter("queueName", queueName);
        query.setParameter(enterDateFieldName, enterDate);

        List<SubjectAccessRequest> result = query.getResultList();

        return result;

    }

    public List<PortalSARStatus> getExternalRequests(PortalSARStatus portalRequestStatus)
    {
        String queryText = "SELECT sar FROM SubjectAccessRequest sar JOIN PersonAssociation pa JOIN pa.person p"
                + " WHERE sar.id = pa.parentId"
                + " AND pa.parentType='CASE_FILE'"
                + " AND pa.personType = 'Requester'";

        if (portalRequestStatus.getRequestId() != null && !portalRequestStatus.getRequestId().equals("undefined"))
        {
            queryText += " AND sar.caseNumber = :caseNumber";
        }

        if (portalRequestStatus.getLastName() != null && !portalRequestStatus.getLastName().equals("undefined"))
        {
            queryText += " AND p.familyName = :lastName";
        }

        Query subjectAccessRequests = getEm().createQuery(queryText);

        if (portalRequestStatus.getRequestId() != null && !portalRequestStatus.getRequestId().equals("undefined"))
        {
            subjectAccessRequests.setParameter("caseNumber", portalRequestStatus.getRequestId());
        }

        if (portalRequestStatus.getLastName() != null && !portalRequestStatus.getLastName().equals("undefined"))
        {
            subjectAccessRequests.setParameter("lastName", portalRequestStatus.getLastName().trim());
        }

        List<SubjectAccessRequest> resultList = subjectAccessRequests.getResultList();

        return populateRequestStatusList(resultList);

    }

    public List<SubjectAccessRequest> getAllRequestsByRequester(Long personId)
    {
        String queryText = "SELECT sar FROM SubjectAccessRequest sar JOIN PersonAssociation pa JOIN pa.person p"
                + " WHERE sar.id = pa.parentId"
                + " AND pa.parentType='CASE_FILE'"
                + " AND pa.personType = 'Requester'"
                + " AND p.id = :personId";

        Query subjectAccessRequests = getEm().createQuery(queryText);

        subjectAccessRequests.setParameter("personId", personId);

        List<SubjectAccessRequest> resultList = subjectAccessRequests.getResultList();

        return resultList;
    }

    public List<PortalSARStatus> getLoggedUserExternalRequests(Long personId, String requestId)
    {
        String queryText = "SELECT sar FROM SubjectAccessRequest sar JOIN PersonAssociation pa JOIN pa.person p"
                + " WHERE sar.id = pa.parentId"
                + " AND pa.parentType='CASE_FILE'"
                + " AND pa.personType = 'Requester'"
                + " AND p.id = :personId";

        if (requestId != null && !requestId.equals("undefined"))
        {
            queryText += " AND sar.caseNumber = :caseNumber";
        }

        Query subjectAccessRequests = getEm().createQuery(queryText);

        subjectAccessRequests.setParameter("personId", personId);

        if (requestId != null && !requestId.equals("undefined"))
        {
            subjectAccessRequests.setParameter("caseNumber", requestId);
        }

        List<SubjectAccessRequest> resultList = subjectAccessRequests.getResultList();

        return populateRequestStatusList(resultList);
    }

    private List<PortalSARStatus> populateRequestStatusList(List<SubjectAccessRequest> requests)
    {
        List<PortalSARStatus> requestStatusList = new ArrayList<>();
        for (SubjectAccessRequest request : requests)
        {
            PortalSARStatus requestStatus = populateRequestStatusFromRequest(request);
            requestStatusList.add(requestStatus);
        }

        return requestStatusList;
    }

    private PortalSARStatus populateRequestStatusFromRequest(SubjectAccessRequest request)
    {
        PortalSARStatus requestStatus = new PortalSARStatus();
        requestStatus.setRequestId(request.getCaseNumber());
        requestStatus.setRequestStatus(request.getStatus());
        requestStatus.setQueue(request.getQueue().getName());
        requestStatus.setUpdateDate(request.getModified());
        requestStatus.setIsDenied(request.getDeniedFlag());
        requestStatus.setRequestTitle(request.getTitle());
        requestStatus.setRequestType(request.getRequestType());
        requestStatus.setRequesterFirstName(request.getOriginator().getPerson().getGivenName());
        requestStatus.setRequesterLastName(request.getOriginator().getPerson().getFamilyName());
        requestStatus.setRequesterEmail(request.getOriginator().getPerson().getContactMethods()
                .stream().filter(cm -> cm.getType().equalsIgnoreCase("email")).findFirst().get().getValue());
        requestStatus.setDispositionValue(request.getDisposition());

        return requestStatus;
    }

    /**
     * @param portalUserId
     * @return
     */
    public List<PortalSARStatus> getExternalRequests(String portalUserId)
    {
        String queryText = "SELECT sar FROM SubjectAccessRequest sar JOIN PersonAssociation pa JOIN pa.person p"
                + " WHERE sar.id = pa.parentId"
                + " AND pa.parentType='CASE_FILE'"
                + " AND pa.personType = 'Requester'";

        if (portalUserId != null)
        {
            queryText += " AND p.id = :portalUserId";
        }

        TypedQuery<SubjectAccessRequest> subjectAccessRequests = getEm().createQuery(queryText, SubjectAccessRequest.class);

        if (portalUserId != null)
        {
            subjectAccessRequests.setParameter("portalUserId", Long.valueOf(portalUserId));
        }

        List<SubjectAccessRequest> resultList = subjectAccessRequests.getResultList();

        return populateRequestStatusList(resultList);
    }

    /**
     * @param portalUserId
     * @param requestId
     * @return
     */
    public PortalSARStatus getExternalRequest(String portalUserId, String requestId)
    {
        String queryText = "SELECT sar FROM SubjectAccessRequest sar JOIN PersonAssociation pa JOIN pa.person p"
                + " WHERE sar.id = pa.parentId"
                + " AND pa.parentType='CASE_FILE'"
                + " AND pa.personType = 'Requester'";

        if (portalUserId != null)
        {
            queryText += " AND p.id = :portalUserId";
        }

        if (requestId != null)
        {
            queryText += " AND sar.caseNumber = :requestId";
        }

        TypedQuery<SubjectAccessRequest> subjectAccessRequests = getEm().createQuery(queryText, SubjectAccessRequest.class);

        if (portalUserId != null)
        {
            subjectAccessRequests.setParameter("portalUserId", Long.valueOf(portalUserId));
        }

        if (portalUserId != null)
        {
            subjectAccessRequests.setParameter("requestId", requestId);
        }

        SubjectAccessRequest result = subjectAccessRequests.getSingleResult();
        return populateRequestStatusFromRequest(result);
    }

    public List<SubjectAccessRequest> findAllNotReleasedRequests()
    {
        String queryText = "SELECT request FROM SubjectAccessRequest request"
                + " WHERE request.queue.name != 'Release'";
        TypedQuery<SubjectAccessRequest> allRecords = getEm().createQuery(queryText, SubjectAccessRequest.class);
        List<SubjectAccessRequest> requests = allRecords.getResultList();
        return requests;
    }

    public List<SubjectAccessRequest> getNextAvailableRequestInQueue(Long queueId, Date createdDate)
    {
        String queryText = "SELECT request FROM CaseFile request "
                + "WHERE request.queue.id = :queueId AND request.created < :createdDate "
                + "ORDER BY request.created DESC";
        TypedQuery<SubjectAccessRequest> nextRequestQuery = getEm().createQuery(queryText, SubjectAccessRequest.class);
        nextRequestQuery.setParameter("queueId", queueId);
        nextRequestQuery.setParameter("createdDate", createdDate);

        return nextRequestQuery.getResultList();
    }
}

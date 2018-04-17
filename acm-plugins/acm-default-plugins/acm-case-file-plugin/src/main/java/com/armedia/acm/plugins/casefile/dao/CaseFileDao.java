package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmNameDao;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.plugins.casefile.model.CaseByStatusDto;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.model.TimePeriod;
import com.armedia.acm.services.participants.model.ParticipantTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 8/26/14.
 */
@Transactional
public class CaseFileDao extends AcmAbstractDao<CaseFile> implements AcmNotificationDao, AcmNameDao
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CaseFile save(CaseFile toSave)
    {
        return super.save(toSave);
    }

    @Override
    protected Class<CaseFile> getPersistenceClass()
    {
        return CaseFile.class;
    }

    public List<CaseFile> findCaseFiles()
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<CaseFile> query = builder.createQuery(CaseFile.class);
        Root<CaseFile> cfRoot = query.from(CaseFile.class);
        query.select(cfRoot);

        // TODO: parameterized order by
        query.orderBy(builder.desc(cfRoot.get("created")));

        TypedQuery<CaseFile> dbQuery = getEm().createQuery(query);

        List<CaseFile> results = dbQuery.getResultList();

        return results;

    }

    public List<CaseByStatusDto> getAllCasesByStatus()
    {
        String queryText = "SELECT cf.status, COUNT(cf) as counted FROM CaseFile cf GROUP BY cf.status";
        Query caseGroupedByStatus = getEm().createQuery(queryText);

        List<Object[]> caseGroupedByS = caseGroupedByStatus.getResultList();

        List<CaseByStatusDto> result = new ArrayList<>();

        for (Object[] caseStatus : caseGroupedByS)
        {
            CaseByStatusDto caseByS = new CaseByStatusDto();
            caseByS.setStatus((String) caseStatus[0]);
            caseByS.setCount(((Number) caseStatus[1]).intValue());
            result.add(caseByS);
        }
        return result;
    }

    public List<CaseFile> getCaseFilesByUser(String user) throws AcmObjectNotFoundException
    {
        String queryText = "SELECT cf FROM CaseFile cf WHERE cf.creator = :user";
        Query casesByUser = getEm().createQuery(queryText);
        casesByUser.setParameter("user", user);
        List<CaseFile> retval = casesByUser.getResultList();
        if (retval.isEmpty())
        {
            throw new AcmObjectNotFoundException("Case File", null, "Cases not found for the user: " + user + "", null);
        }
        return retval;
    }

    public List<CaseFile> getNotClosedCaseFilesByUser(String user) throws AcmObjectNotFoundException
    {
        String queryText = "SELECT cf " + "FROM CaseFile cf, " + "     AcmParticipant ap " + "WHERE " + "     cf.id = ap.objectId "
                + "AND  ap.objectType = '" + CaseFileConstants.OBJECT_TYPE + "' " + "AND  ap.participantType = '"
                + ParticipantTypes.ASSIGNEE + "' " + "AND  ap.participantLdapId = :user " + "AND  cf.status <> :statusName " + "ORDER BY "
                + "     cf.dueDate ASC";
        Query casesByUser = getEm().createQuery(queryText);
        casesByUser.setParameter("user", user);
        casesByUser.setParameter("statusName", "CLOSED");
        List<CaseFile> retval = casesByUser.getResultList();
        if (retval.isEmpty())
        {
            throw new AcmObjectNotFoundException("Case File", null, "Cases not found for the user: " + user + "", null);
        }
        return retval;
    }

    public List<CaseByStatusDto> getCasesByStatusAndByTimePeriod(TimePeriod numberOfDaysFromToday)
    {
        String queryText = "SELECT cf.status, COUNT(cf) as counted FROM CaseFile cf WHERE cf.created >= :created GROUP BY cf.status";
        Query caseGroupedByStatus = getEm().createQuery(queryText);

        caseGroupedByStatus.setParameter("created", shiftDateFromToday(numberOfDaysFromToday.getNumOfDays()));

        List<Object[]> caseGroupedByS = caseGroupedByStatus.getResultList();

        List<CaseByStatusDto> result = new ArrayList<>();

        for (Object[] caseStatus : caseGroupedByS)
        {
            CaseByStatusDto caseByS = new CaseByStatusDto();
            caseByS.setStatus((String) caseStatus[0]);
            caseByS.setCount(((Number) caseStatus[1]).intValue());
            result.add(caseByS);
        }
        return result;
    }

    public CaseFile findByCaseNumber(String caseNumber)
    {
        CaseFile result = null;
        String queryText = "SELECT cf FROM CaseFile cf WHERE cf.caseNumber = :caseNumber";

        Query findByCaseNumber = getEm().createQuery(queryText);
        findByCaseNumber.setParameter("caseNumber", caseNumber);

        try
        {
            result = (CaseFile) findByCaseNumber.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.info("There are no any results.");
        }
        return result;
    }

    public List<CaseFile> findByCaseNumberKeyword(String expression)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<CaseFile> query = builder.createQuery(CaseFile.class);
        Root<CaseFile> cf = query.from(CaseFile.class);

        query.select(cf);

        query.where(builder.and(builder.like(builder.lower(cf.<String> get("caseNumber")), "%" + expression.toLowerCase() + "%")));

        query.orderBy(builder.asc(cf.get("caseNumber")));

        TypedQuery<CaseFile> dbQuery = getEm().createQuery(query);
        List<CaseFile> results = dbQuery.getResultList();

        return results;
    }

    public List<CaseFile> findByTitle(String title) throws AcmObjectNotFoundException
    {
        String queryText = "SELECT cf FROM CaseFile cf WHERE cf.title = :title";

        Query casesByTitle = getEm().createQuery(queryText);
        casesByTitle.setParameter("title", title);

        List<CaseFile> retval = casesByTitle.getResultList();
        if (retval.isEmpty())
        {
            throw new AcmObjectNotFoundException("Case File", null, "Cases not found for the title: " + title + "", null);
        }
        return retval;
    }

    @Transactional
    public int updateComplaintStatus(Long caseId, String newStatus, String modifier, Date date)
    {
        Query updateStatusQuery = getEm().createQuery("UPDATE CaseFile " + "SET status = :newStatus, " + "modified = :modified, "
                + "modifier = :modifier " + "WHERE caseId = :caseId");
        updateStatusQuery.setParameter("newStatus", newStatus);
        updateStatusQuery.setParameter("modified", date);
        updateStatusQuery.setParameter("modifier", modifier);
        updateStatusQuery.setParameter("caseId", caseId);

        return updateStatusQuery.executeUpdate();
    }

    private Date shiftDateFromToday(int daysFromToday)
    {
        Date nextDate;
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -daysFromToday);
        nextDate = cal.getTime();
        return nextDate;
    }

    @Override
    public String getSupportedObjectType()
    {
        return CaseFileConstants.OBJECT_TYPE;
    }

    @Override
    public AcmNotifiableEntity findEntity(Long id)
    {
        return find(id);
    }

    @Override
    public String getSupportedNotifiableObjectType()
    {
        return CaseFileConstants.OBJECT_TYPE;
    }

    @Override
    public AcmObject findByName(String name)
    {
        return findByCaseNumber(name);
    }
}

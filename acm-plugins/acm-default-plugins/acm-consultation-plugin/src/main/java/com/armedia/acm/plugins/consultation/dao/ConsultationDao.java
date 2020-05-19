package com.armedia.acm.plugins.consultation.dao;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmNameDao;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.plugins.casefile.model.TimePeriod;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.model.ConsultationsByStatusDto;
import com.armedia.acm.services.participants.model.ParticipantTypes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Transactional
public class ConsultationDao extends AcmAbstractDao<Consultation> implements AcmNotificationDao, AcmNameDao
{
    private Logger LOG = LogManager.getLogger(getClass());

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Consultation save(Consultation toSave)
    {
        return super.save(toSave);
    }

    @Override
    protected Class<Consultation> getPersistenceClass()
    {
        return Consultation.class;
    }

    @Override
    public String getSupportedObjectType()
    {
        return ConsultationConstants.OBJECT_TYPE;
    }

    @Override
    public AcmNotifiableEntity findEntity(Long id)
    {
        return find(id);
    }

    @Override
    public String getSupportedNotifiableObjectType()
    {
        return ConsultationConstants.OBJECT_TYPE;
    }

    @Override
    public AcmObject findByName(String name)
    {
        return findByConsultationNumber(name);
    }

    public Consultation findByConsultationNumber(String consultationNumber)
    {
        Consultation result = null;
        String queryText = "SELECT cf FROM Consultation cf WHERE cf.consultationNumber = :consultationNumber";

        Query findByConsultationNumber = getEm().createQuery(queryText);
        findByConsultationNumber.setParameter("consultationNumber", consultationNumber);

        try
        {
            result = (Consultation) findByConsultationNumber.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.warn("No consultation file has consultation number {}", consultationNumber);
        }
        return result;
    }

    public List<Consultation> findConsultations()
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<Consultation> query = builder.createQuery(Consultation.class);
        Root<Consultation> cfRoot = query.from(Consultation.class);
        query.select(cfRoot);

        query.orderBy(builder.desc(cfRoot.get("created")));

        TypedQuery<Consultation> dbQuery = getEm().createQuery(query);

        List<Consultation> results = dbQuery.getResultList();

        return results;
    }

    public List<ConsultationsByStatusDto> getAllConsultationsByStatus()
    {
        String queryText = "SELECT cf.status, COUNT(cf) as counted FROM Consultation cf GROUP BY cf.status";
        Query consultationsGroupedByStatus = getEm().createQuery(queryText);

        List<Object[]> consultationsGroupedByS = consultationsGroupedByStatus.getResultList();

        List<ConsultationsByStatusDto> result = new ArrayList<>();

        for (Object[] consultationStatus : consultationsGroupedByS)
        {
            ConsultationsByStatusDto consultationByS = new ConsultationsByStatusDto();
            consultationByS.setStatus((String) consultationStatus[0]);
            consultationByS.setCount(((Number) consultationStatus[1]).intValue());
            result.add(consultationByS);
        }
        return result;
    }

    public List<Consultation> getConsultationsByUser(String user) throws AcmObjectNotFoundException
    {
        String queryText = "SELECT cf FROM Consultation cf WHERE cf.creator = :user";
        Query consultationsByUser = getEm().createQuery(queryText);
        consultationsByUser.setParameter("user", user);
        List<Consultation> retval = consultationsByUser.getResultList();
        if (retval.isEmpty())
        {
            throw new AcmObjectNotFoundException("Consultation", null, "Consultations not found for the user: " + user + "", null);
        }
        return retval;
    }

    public List<Consultation> getNotClosedConsultationsByUser(String user) throws AcmObjectNotFoundException
    {
        String queryText = "SELECT cf " + "FROM Consultation cf, " + "     AcmParticipant ap " + "WHERE " + "     cf.id = ap.objectId "
                + "AND  ap.objectType = '" + ConsultationConstants.OBJECT_TYPE + "' " + "AND  ap.participantType = '"
                + ParticipantTypes.ASSIGNEE + "' " + "AND  ap.participantLdapId = :user " + "AND  cf.status <> :statusName " + "ORDER BY "
                + "     cf.dueDate ASC";
        Query consultatonsByUser = getEm().createQuery(queryText);
        consultatonsByUser.setParameter("user", user);
        consultatonsByUser.setParameter("statusName", "CLOSED");
        List<Consultation> retval = consultatonsByUser.getResultList();
        if (retval.isEmpty())
        {
            throw new AcmObjectNotFoundException("Consultation", null, "Consultations not found for the user: " + user + "", null);
        }
        return retval;
    }

    public List<ConsultationsByStatusDto> getConsultationsByStatusAndByTimePeriod(TimePeriod numberOfDaysFromToday)
    {
        String queryText = "SELECT cf.status, COUNT(cf) as counted FROM Consultation cf WHERE cf.created >= :created GROUP BY cf.status";
        Query consultatonGroupedByStatus = getEm().createQuery(queryText);

        consultatonGroupedByStatus.setParameter("created", shiftDateFromToday(numberOfDaysFromToday.getNumOfDays()));

        List<Object[]> consultatonGroupedByS = consultatonGroupedByStatus.getResultList();

        List<ConsultationsByStatusDto> result = new ArrayList<>();

        for (Object[] consultatonStatus : consultatonGroupedByS)
        {
            ConsultationsByStatusDto consultatonByS = new ConsultationsByStatusDto();
            consultatonByS.setStatus((String) consultatonStatus[0]);
            consultatonByS.setCount(((Number) consultatonStatus[1]).intValue());
            result.add(consultatonByS);
        }
        return result;
    }

    public List<Consultation> findByConsultationNumberKeyword(String expression)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<Consultation> query = builder.createQuery(Consultation.class);
        Root<Consultation> cf = query.from(Consultation.class);

        query.select(cf);

        query.where(builder.and(builder.like(builder.lower(cf.<String> get("consultatonNumber")), "%" + expression.toLowerCase() + "%")));

        query.orderBy(builder.asc(cf.get("consultatonNumber")));

        TypedQuery<Consultation> dbQuery = getEm().createQuery(query);
        List<Consultation> results = dbQuery.getResultList();

        return results;
    }

    public List<Consultation> findByTitle(String title) throws AcmObjectNotFoundException
    {
        String queryText = "SELECT cf FROM Consultation cf WHERE cf.title = :title";

        Query consultatonsByTitle = getEm().createQuery(queryText);
        consultatonsByTitle.setParameter("title", title);

        List<Consultation> retval = consultatonsByTitle.getResultList();
        if (retval.isEmpty())
        {
            throw new AcmObjectNotFoundException("Consultation", null, "Consultations not found for the title: " + title + "", null);
        }
        return retval;
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

    public Long getConsultationCount(LocalDateTime until)
    {
        String queryText = "SELECT COUNT(c) FROM Consultation c WHERE c.created <= :until";

        Query query = getEm().createQuery(queryText);
        query.setParameter("until", Date.from(ZonedDateTime.of(until, ZoneId.systemDefault()).toInstant()));
        return (Long) query.getSingleResult();
    }
}

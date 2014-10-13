package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.model.CaseByStatusDto;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.TimePeriod;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 8/26/14.
 */
public class CaseFileDao extends AcmAbstractDao<CaseFile>
{
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

    public List<CaseByStatusDto> getAllCasesByStatus(){
        String queryText = "SELECT cf.status, COUNT(cf) as counted FROM CaseFile cf GROUP BY cf.status";
        Query caseGroupedByStatus = getEm().createQuery(queryText);

        List<Object[]> caseGroupedByS = caseGroupedByStatus.getResultList();

        List<CaseByStatusDto> result = new ArrayList<CaseByStatusDto>();

        for(Object[] caseStatus : caseGroupedByS){
            CaseByStatusDto caseByS = new CaseByStatusDto();
            caseByS.setStatus((String) caseStatus[0]);
            caseByS.setCount(((Number)caseStatus[1]).intValue());
            result.add(caseByS);
        }
        return result;
    }

    public List<CaseFile> getCaseFilesByUser(String user) throws AcmObjectNotFoundException{
        String queryText = "SELECT cf FROM CaseFile cf " +
                "WHERE cf.creator = :user";
        Query casesByUser = getEm().createQuery(queryText);
        casesByUser.setParameter("user",user);
        List<CaseFile> retval = casesByUser.getResultList();
        if(retval.isEmpty()) {
            throw new AcmObjectNotFoundException("Case File",null, "Cases not found for the user: "+user+"",null);
        }
        return retval;
    }

    public List<CaseByStatusDto> getCasesByStatusAndByTimePeriod(TimePeriod numberOfDaysFromToday) {
        String queryText = "SELECT cf.status, COUNT(cf) as counted FROM CaseFile cf WHERE cf.created >= :created GROUP BY cf.status";
        Query caseGroupedByStatus = getEm().createQuery(queryText);

        caseGroupedByStatus.setParameter("created",shiftDateFromToday(numberOfDaysFromToday.getNumOfDays()));

        List<Object[]> caseGroupedByS = caseGroupedByStatus.getResultList();

        List<CaseByStatusDto> result = new ArrayList<CaseByStatusDto>();

        for(Object[] caseStatus : caseGroupedByS){
            CaseByStatusDto caseByS = new CaseByStatusDto();
            caseByS.setStatus((String) caseStatus[0]);
            caseByS.setCount(((Number)caseStatus[1]).intValue());
            result.add(caseByS);
        }
        return result;
    }


    public CaseFile findByCaseNumber(String caseNumber)
    {
        String queryText = "SELECT cf FROM CaseFile cf WHERE cf.caseNumber = :caseNumber";

        Query findByCaseNumber = getEm().createQuery(queryText);
        findByCaseNumber.setParameter("caseNumber", caseNumber);

        return (CaseFile) findByCaseNumber.getSingleResult();
    }
    private Date shiftDateFromToday(int daysFromToday){
        Date nextDate;
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE,-daysFromToday);
        nextDate = cal.getTime();
        return nextDate;
    }
}

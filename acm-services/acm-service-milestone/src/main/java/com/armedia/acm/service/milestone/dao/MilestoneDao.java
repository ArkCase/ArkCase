package com.armedia.acm.service.milestone.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.milestone.model.AcmMilestone;
import com.armedia.acm.service.milestone.model.MilestoneByNameDto;
import com.sun.org.apache.bcel.internal.generic.ObjectType;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 12/5/14.
 */

public class MilestoneDao extends AcmAbstractDao<AcmMilestone>
{
    @Override
    protected Class<AcmMilestone> getPersistenceClass()
    {
        return AcmMilestone.class;
    }

    public List<MilestoneByNameDto> getAllMilestonesForCaseFilesGroupedByName(String objectType){

        String queryText = "SELECT ms.milestoneName, COUNT(ms) as counted FROM AcmMilestone ms where ms.created >= :created AND ms.objectType=:objectType AND ms.milestoneName<>:mName GROUP BY ms.milestoneName";

        Query milestonesGroupedByName = getEm().createQuery(queryText);

        milestonesGroupedByName.setParameter("created",shiftDateFromToday(14));
        milestonesGroupedByName.setParameter("objectType",objectType); //"CASE_FILE");
        milestonesGroupedByName.setParameter("mName","Closed");

        List<Object[]> milestonesGroupedByN = milestonesGroupedByName.getResultList();

        List<MilestoneByNameDto> result = new ArrayList<>();

        for( Object[] milestoneName : milestonesGroupedByN ){
            MilestoneByNameDto milestoneByN = new MilestoneByNameDto();
            milestoneByN.setName((String) milestoneName[0]);
            milestoneByN.setCount(((Number) milestoneName[1]).intValue());
            result.add(milestoneByN);
        }
        return result;
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

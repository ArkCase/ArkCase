package com.armedia.acm.plugins.dashboard.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.services.users.model.AcmUser;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.swing.text.html.parser.Entity;
import java.util.Date;
import java.util.List;

/**
 * Created by marst on 7/29/14.
 */

public class DashboardDao extends AcmAbstractDao<Dashboard> {


    public Dashboard getDashboardConfigForUser(AcmUser user) throws AcmDashboardException, AcmObjectNotFoundException{
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<Dashboard> query = builder.createQuery(Dashboard.class);
        Root<Dashboard> d = query.from(Dashboard.class);

        query.select(d).where(builder.equal(d.get("dashboardOwner"), user));
        TypedQuery<Dashboard> dbQuery = getEm().createQuery(query);
        List<Dashboard> results = null;

            results = dbQuery.getResultList();

            if( results.isEmpty()){
                throw new AcmObjectNotFoundException("dashboard",null, "Object not found",null);
            }

        return results.get(0);
    }

    @Transactional
    public int setDasboardConfigForUser(AcmUser user, DashboardDto newDashboardDto){
        Query updateStatusQuery = getEm().createQuery(
                "UPDATE Dashboard " +
                        "SET dashobardConfig = :dashobardConfig " +
                        "WHERE dashboardOwner = :dashboardOwner");
        updateStatusQuery.setParameter("dashobardConfig", newDashboardDto.getDashboardConfig());
        updateStatusQuery.setParameter("dashboardOwner", user);

        return updateStatusQuery.executeUpdate();
    }

    @Override
    protected Class<Dashboard> getPersistenceClass()
    {
        return Dashboard.class;
    }
}

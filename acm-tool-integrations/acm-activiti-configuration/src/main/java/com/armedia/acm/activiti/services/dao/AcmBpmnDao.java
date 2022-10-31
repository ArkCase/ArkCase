package com.armedia.acm.activiti.services.dao;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
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

import com.armedia.acm.activiti.model.AcmProcessDefinition;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import java.util.List;

/**
 * Created by nebojsha on 14.04.2015.
 */
public class AcmBpmnDao
{

    @PersistenceContext
    private EntityManager em;

    @Value("${database.platform}")
    private String databasePlatform;

    @Transactional(propagation = Propagation.REQUIRED)
    public AcmProcessDefinition save(AcmProcessDefinition toSave)
    {
        AcmProcessDefinition saved = em.merge(toSave);
        return saved;
    }

    public long count()
    {
        String queryText = "SELECT COUNT(apd.id) FROM AcmProcessDefinition apd";
        Query query = getEm().createQuery(queryText);
        Long count = (Long) query.getSingleResult();
        return count;
    }

    public List<AcmProcessDefinition> list(String orderBy, boolean isAsc)
    {
        if (!databasePlatform.endsWith("PostgreSQLPlatform"))
        {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<AcmProcessDefinition> criteriaQuery = builder.createQuery(AcmProcessDefinition.class);
            Root<AcmProcessDefinition> processDefinition = criteriaQuery.from(AcmProcessDefinition.class);
            Subquery subquery = criteriaQuery.subquery(AcmProcessDefinition.class);
            Root<AcmProcessDefinition> processDefinitionSubquery = subquery.from(AcmProcessDefinition.class);

            criteriaQuery.select(processDefinition);
            subquery.select(builder.min(processDefinitionSubquery.<Long>get("id")));
            subquery.groupBy(processDefinitionSubquery.<String>get("key")).select(processDefinitionSubquery.<String>get("key"));

            criteriaQuery.where(processDefinition.<Long>get("id").in(subquery.getSelection()));
            if (isAsc)
            {
                criteriaQuery.orderBy(builder.asc(processDefinition.get(orderBy)));
            }
            else
            {
                criteriaQuery.orderBy(builder.desc(processDefinition.get(orderBy)));
            }
            TypedQuery<AcmProcessDefinition> query = getEm().createQuery(criteriaQuery);
            return query.getResultList();
        }
        else
        {
            String queryText =
                    "SELECT apd FROM AcmProcessDefinition apd WHERE apd.id in (SELECT MIN(apdid.id) FROM AcmProcessDefinition apdid GROUP BY apdid.key) ORDER BY apd."
                            + orderBy + (isAsc ? " ASC" : " DESC");
            TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);

            return query.getResultList();
        }
    }

    @Transactional
    public List<AcmProcessDefinition> listPage(String orderBy, boolean isAsc)
    {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<AcmProcessDefinition> criteriaQuery = builder.createQuery(AcmProcessDefinition.class);
        Root<AcmProcessDefinition> processDefinition = criteriaQuery.from(AcmProcessDefinition.class);

        criteriaQuery.select(processDefinition);
        criteriaQuery.where(builder.equal(processDefinition.get("active"), true));

        if (isAsc)
        {
            criteriaQuery.orderBy(builder.asc(processDefinition.get(orderBy)));
        }
        else
        {
            criteriaQuery.orderBy(builder.desc(processDefinition.get(orderBy)));
        }
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(criteriaQuery);
        return query.getResultList();

    }

    public List<AcmProcessDefinition> listAllVersions(AcmProcessDefinition processDefinition)
    {
        String queryText = "SELECT apd FROM AcmProcessDefinition apd WHERE apd.key = :key AND apd.version <> :version ORDER BY apd.version DESC";
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);
        query.setParameter("key", processDefinition.getKey());
        query.setParameter("version", processDefinition.getVersion());

        return query.getResultList();
    }

    public void remove(AcmProcessDefinition processDefinition)
    {
        getEm().remove(processDefinition);
    }

    public AcmProcessDefinition getActive(String processDefinitionKey)
    {
        String queryText = "SELECT apd FROM AcmProcessDefinition apd WHERE apd.key = :key and apd.active = true";
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);
        query.setParameter("key", processDefinitionKey);

        try
        {
            return query.getSingleResult();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public AcmProcessDefinition getByKeyAndVersion(String key, int version)
    {
        String queryText = "SELECT apd FROM AcmProcessDefinition apd WHERE apd.key = :key and apd.version =:version";
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);
        query.setParameter("key", key);
        query.setParameter("version", version);

        try
        {
            return query.getSingleResult();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public AcmProcessDefinition getByKeyAndDigest(String key, String digest)
    {
        String queryText = "SELECT apd FROM AcmProcessDefinition apd WHERE apd.key = :key and apd.sha256Hash =:digest";
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);
        query.setParameter("key", key);
        query.setParameter("digest", digest);

        try
        {
            return query.getSingleResult();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public List<AcmProcessDefinition> listAllDeactivatedVersions()
    {
        String queryText = "SELECT apd FROM AcmProcessDefinition apd WHERE apd.active = false";
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);

        return query.getResultList();
    }

    @Transactional
    public void activateLatestVersionBySystemUser()
    {
        String activateQueryText = "UPDATE AcmProcessDefinition apd SET apd.active = TRUE" +
                " WHERE apd.version IN (SELECT MAX(apd2.version)" +
                " FROM AcmProcessDefinition as apd2" +
                " WHERE apd2.creator = 'SYSTEM_USER'" +
                " AND apd2.key = apd.key)";

        TypedQuery<AcmProcessDefinition> activateLatestVersionBySystemUserQuery = getEm().createQuery(activateQueryText,
                AcmProcessDefinition.class);
        activateLatestVersionBySystemUserQuery.executeUpdate();

        String deactivateQueryText = "UPDATE AcmProcessDefinition apd SET apd.active = FALSE" +
                " WHERE apd.version NOT IN (SELECT MAX(apd2.version)" +
                " FROM AcmProcessDefinition as apd2" +
                " WHERE apd2.creator = 'SYSTEM_USER'" +
                " AND apd2.key = apd.key)";

        TypedQuery<AcmProcessDefinition> deactivateOtherVersionsQuery = getEm().createQuery(deactivateQueryText,
                AcmProcessDefinition.class);
        deactivateOtherVersionsQuery.executeUpdate();

    }

    public EntityManager getEm()
    {
        return em;
    }

    public void setEm(EntityManager em)
    {
        this.em = em;
    }
}

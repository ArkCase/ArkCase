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

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nebojsha on 14.04.2015.
 */
public class AcmBpmnDao
{

    @PersistenceContext
    private EntityManager em;

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
        String queryText = "SELECT apd FROM AcmProcessDefinition apd WHERE apd.id in (SELECT MIN(apdid.id) FROM AcmProcessDefinition apdid GROUP BY apdid.key)   ORDER BY apd."
                + orderBy + (isAsc ? " ASC" : " DESC");
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);
        return query.getResultList();
    }

    @Transactional
    public List<AcmProcessDefinition> listPage(int start, int length, String orderBy, boolean isAsc)
    {
        String queryMaxText = "SELECT apd FROM AcmProcessDefinition apd WHERE apd.id in (SELECT MAX(apdid.id) FROM AcmProcessDefinition apdid GROUP BY apdid.key)   ORDER BY apd."
                + orderBy + (isAsc ? " ASC" : " DESC");
        TypedQuery<AcmProcessDefinition> queryMax = getEm().createQuery(queryMaxText, AcmProcessDefinition.class).setFirstResult(start)
                .setMaxResults(length);
        List<AcmProcessDefinition> maxList = queryMax.getResultList();

        String queryActiveText = "SELECT apd FROM AcmProcessDefinition apd WHERE apd.id in (SELECT apdid.id FROM AcmProcessDefinition apdid WHERE apd.key = apdid.key AND apdid.active = 1)   ORDER BY apd."
                + orderBy + (isAsc ? " ASC" : " DESC");
        TypedQuery<AcmProcessDefinition> queryActive = getEm().createQuery(queryActiveText, AcmProcessDefinition.class)
                .setFirstResult(start).setMaxResults(length);
        List<AcmProcessDefinition> activeList = queryActive.getResultList();

        List<AcmProcessDefinition> acmProcessDefinitions = new ArrayList<>();
        maxList.forEach(mE -> {
            acmProcessDefinitions.add(activeList.stream()
                    .filter(aE -> aE.getKey().equals(mE.getKey()))
                    .findFirst()
                    .orElse(mE));
        });
        return acmProcessDefinitions;
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

    public EntityManager getEm()
    {
        return em;
    }

    public void setEm(EntityManager em)
    {
        this.em = em;
    }
}

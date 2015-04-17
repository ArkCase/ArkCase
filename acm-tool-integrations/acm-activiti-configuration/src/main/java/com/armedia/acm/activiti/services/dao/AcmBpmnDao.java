package com.armedia.acm.activiti.services.dao;

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.data.AcmAbstractDao;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by nebojsha on 14.04.2015.
 */
public class AcmBpmnDao extends AcmAbstractDao<AcmProcessDefinition> {

    @Override
    protected Class<AcmProcessDefinition> getPersistenceClass() {
        return AcmProcessDefinition.class;
    }


    public long count() {
        String queryText =
                "SELECT COUNT(apd.id) FROM AcmProcessDefinition apd";
        Query query = getEm().createQuery(queryText);
        Long count = (Long) query.getSingleResult();
        return count;
    }

    public List<AcmProcessDefinition> listPage(int start, int length, String orderBy, boolean isAsc) {
        String queryText =
                "SELECT apd FROM AcmProcessDefinition apd WHERE apd.id in (SELECT MIN(apdid.id) FROM AcmProcessDefinition apdid GROUP BY apdid.key)   ORDER BY apd." + orderBy + (isAsc ? " ASC" : " DESC");
        Query query = getEm().createQuery(queryText).setFirstResult(start).setMaxResults(length);
        return query.getResultList();
    }

    public List<AcmProcessDefinition> listAllVersions(AcmProcessDefinition processDefinition) {
        String queryText =
                "SELECT apd FROM AcmProcessDefinition apd WHERE apd.key = :key AND apd.version <> :version ORDER BY apd.version DESC";
        Query query = getEm().createQuery(queryText);
        query.setParameter("key", processDefinition.getKey());
        query.setParameter("version", processDefinition.getVersion());

        return query.getResultList();
    }

    public void remove(AcmProcessDefinition processDefinition) {
        getEm().remove(processDefinition);
    }


    public AcmProcessDefinition getActive(String processDefinitionKey) {
        String queryText =
                "SELECT apd FROM AcmProcessDefinition apd WHERE apd.key = :key and apd.active = true";
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);
        query.setParameter("key", processDefinitionKey);

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public AcmProcessDefinition getByKeyAndVersion(String key, int version) {
        String queryText =
                "SELECT apd FROM AcmProcessDefinition apd WHERE apd.key = :key and apd.version =:version";
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);
        query.setParameter("key", key);
        query.setParameter("version", version);

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public AcmProcessDefinition getByKeyAndDigest(String key, String digest) {
        String queryText =
                "SELECT apd FROM AcmProcessDefinition apd WHERE apd.key = :key and apd.md5Hash =:digest";
        TypedQuery<AcmProcessDefinition> query = getEm().createQuery(queryText, AcmProcessDefinition.class);
        query.setParameter("key", key);
        query.setParameter("digest", digest);

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}

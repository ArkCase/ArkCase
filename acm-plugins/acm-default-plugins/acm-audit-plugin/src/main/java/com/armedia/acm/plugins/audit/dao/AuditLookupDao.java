package com.armedia.acm.plugins.audit.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.audit.model.AcmAuditLookup;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.List;

public class AuditLookupDao extends AcmAbstractDao<AcmAuditLookup> {

    @Override
    protected Class<AcmAuditLookup> getPersistenceClass() {
        return AcmAuditLookup.class;
    }

    @Transactional
    public void deleteAllAuditsFormLookupTabel( ) throws SQLException {

        TypedQuery<AcmAuditLookup> selectQuery = getEm().createQuery("SELECT a FROM AcmAuditLookup a ",AcmAuditLookup.class);
        List<AcmAuditLookup> results;

        results = selectQuery.getResultList();
          if (!results.isEmpty()) {
                for (AcmAuditLookup aul : results) {
                    getEm().remove(aul);
                }
            }
    }
}

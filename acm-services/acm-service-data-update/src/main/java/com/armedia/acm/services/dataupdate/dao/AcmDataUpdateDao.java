package com.armedia.acm.services.dataupdate.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.dataupdate.model.AcmDataUpdateExecutorLog;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AcmDataUpdateDao extends AcmAbstractDao<AcmDataUpdateExecutorLog>
{
    @PersistenceContext
    private EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(AcmDataUpdateDao.class);

    @Override
    protected Class<AcmDataUpdateExecutorLog> getPersistenceClass()
    {
        return AcmDataUpdateExecutorLog.class;
    }
}

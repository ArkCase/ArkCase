package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.dataaccess.dao.AcmAccessControlEntryDao;
import com.armedia.acm.services.dataaccess.model.AcmAccess;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlEntry;
import com.armedia.acm.services.dataaccess.model.enums.AccessControlDecision;
import com.armedia.acm.services.dataaccess.model.enums.EntryAccessControlSavePolicy;
import com.armedia.acm.services.dataaccess.service.DataAccessEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class DataAccessEntryServiceImpl implements DataAccessEntryService {
    private AcmAccessControlEntryDao accessControlEntryDao;


    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public AcmAccessControlEntry save(AcmAccessControlEntry toSave, Authentication authentication) throws AcmUserActionFailedException {
        toSave.setCreator(authentication.getName());
        toSave.setModifier(authentication.getName());
        return getAccessControlEntryDao().save(toSave);
    }

    @Override
    @Transactional
    public AcmAccessControlEntry save(Long defaultAccessId, AcmAccessControlEntry toSave, Authentication authentication) throws AcmUserActionFailedException {
        AcmAccessControlEntry existing = getAccessControlEntryDao().find(defaultAccessId);

        if (existing == null) {
            throw new AcmUserActionFailedException("update", "entry access", defaultAccessId,
                    "Entry access with id '" + defaultAccessId + "' does not exist.", null);
        }

        // only the access decision, the discretionary access flag, and the modifier can be changed.
        existing.setModifier(authentication.getName());
        existing.setAccessDecision(toSave.getAccessDecision());
        existing.setAllowDiscretionaryUpdate(toSave.getAllowDiscretionaryUpdate());

        return getAccessControlEntryDao().save(existing, EntryAccessControlSavePolicy.OVERWRITE_EXISTING);
    }

    @Override
    public List<AcmAccessControlEntry> findByFields(Long objectId, String objectType, String objectStatus, String accessLevel) {
        return getAccessControlEntryDao().findByFields(objectId, objectType, objectStatus, accessLevel);
    }

    @Override
    public AcmAccess getAcmReadAccess(Long objectId, String objectType, String objectStatus) {
        return getAcmAccess(objectId, objectType, objectStatus, "read");
    }

    private AcmAccess getAcmAccess(Long objectId, String objectType, String objectStatus, String accessLevel) {
        List<AcmAccessControlEntry> results = accessControlEntryDao.findByFields(objectId, objectType, objectStatus, accessLevel);
        AcmAccess acmAccess = new AcmAccess(objectId, objectType, objectStatus, accessLevel);

        for (AcmAccessControlEntry entry : results) {
            AccessControlDecision entryAcd = AccessControlDecision.valueOf(entry.getAccessDecision());

            switch (entryAcd) {
                case GRANT:
                    acmAccess.addAllowAcl(entry.getAccessorId());
                    break;
                case DENY:
                    acmAccess.addDenyAcl(entry.getAccessorId());
                    break;
                default:
                    log.error("unknown access control decision %s", entry.getAccessorType());
            }
        }
        return acmAccess;
    }

    public AcmAccessControlEntryDao getAccessControlEntryDao() {
        return accessControlEntryDao;
    }

    public void setAccessControlEntryDao(AcmAccessControlEntryDao accessControlEntryDao) {
        this.accessControlEntryDao = accessControlEntryDao;
    }

}

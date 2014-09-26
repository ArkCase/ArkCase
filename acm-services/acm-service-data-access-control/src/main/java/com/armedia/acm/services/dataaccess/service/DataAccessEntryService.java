package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.dataaccess.model.AcmAccess;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlEntry;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface DataAccessEntryService {
    AcmAccessControlEntry save(
            AcmAccessControlEntry toSave,
            Authentication authentication) throws AcmUserActionFailedException;

    AcmAccessControlEntry save(
            Long defaultAccessId,
            AcmAccessControlEntry toSave,
            Authentication authentication) throws AcmUserActionFailedException;

    List<AcmAccessControlEntry> findByFields(Long objectId, String objectType, String objectStatus, String accessLevel);

    AcmAccess getAcmReadAccess(Long objectId, String objectType, String objectStatus);

}

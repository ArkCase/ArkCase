package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.model.UserOrg;
import org.springframework.security.core.Authentication;

/**
 * Created by nebojsha on 07.05.2015.
 */
public interface UserOrgService {
    public OutlookDTO retrieveOutlookPassword(Authentication authentication) throws AcmEncryptionException;

    public void saveOutlookPassword(Authentication authentication, OutlookDTO in) throws AcmEncryptionException;

    public UserOrg getUserOrgForUserId(String userId) throws AcmObjectNotFoundException;
}

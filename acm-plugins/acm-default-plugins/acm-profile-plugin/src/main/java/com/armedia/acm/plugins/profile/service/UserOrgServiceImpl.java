package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.model.UserOrg;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.Authentication;

import java.util.Base64;

/**
 * Created by nebojsha on 07.05.2015.
 */
public class UserOrgServiceImpl implements UserOrgService {

    private UserOrgDao userOrgDao;

    private AcmCryptoUtils acmCryptoUtils;

    @Override
    public OutlookDTO retrieveOutlookPassword(Authentication authentication) throws AcmEncryptionException {

        OutlookDTO retval = userOrgDao.retrieveOutlookPassword(authentication);

        //decrypt password and decode it from BASE64
        String md5Hex = DigestUtils.md5Hex(authentication.getCredentials().toString());

        byte[] decryptedPassword = acmCryptoUtils.decryptData(md5Hex.getBytes(), Base64.getDecoder().decode(retval.getOutlookPassword().getBytes()), true);

        retval.setOutlookPassword(new String(decryptedPassword));

        return retval;
    }

    @Override
    public void saveOutlookPassword(Authentication authentication, OutlookDTO in) throws AcmEncryptionException {

        //encrypt password and encode it to BASE64
        String md5Hex = DigestUtils.md5Hex(authentication.getCredentials().toString());

        byte[] encryptedPassword = acmCryptoUtils.encryptData(md5Hex.getBytes(), in.getOutlookPassword().getBytes(), true);
        in.setOutlookPassword(Base64.getEncoder().encodeToString(encryptedPassword));
        userOrgDao.saveOutlookPassword(authentication, in);

    }

    @Override
    public UserOrg getUserOrgForUserId(String userId) throws AcmObjectNotFoundException {
        return userOrgDao.getUserOrgForUserId(userId);
    }


    public UserOrgDao getUserOrgDao() {
        return userOrgDao;
    }

    public void setUserOrgDao(UserOrgDao userOrgDao) {
        this.userOrgDao = userOrgDao;
    }

    public AcmCryptoUtils getAcmCryptoUtils() {
        return acmCryptoUtils;
    }

    public void setAcmCryptoUtils(AcmCryptoUtils acmCryptoUtils) {
        this.acmCryptoUtils = acmCryptoUtils;
    }
}

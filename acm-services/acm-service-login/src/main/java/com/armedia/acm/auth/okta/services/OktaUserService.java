package com.armedia.acm.auth.okta.services;

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.user.OktaUser;
import com.armedia.acm.auth.okta.model.user.OktaUserProfile;

import java.util.Map;

public interface OktaUserService
{
    OktaUser createUser(OktaUser user) throws OktaException;

    OktaUser createUser(OktaUserProfile oktaUserProfile) throws OktaException;

    OktaUser updateUser(Map<String, String> profile, String userId) throws OktaException;

    OktaUser activateUser(String userId) throws OktaException;

    OktaUser getUser(String userId) throws OktaException;

    boolean deleteUser(OktaUser oktaUser) throws OktaException;

    boolean deleteUser(String userId) throws OktaException;
}

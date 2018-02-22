package com.armedia.acm.auth.okta.services;

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.ProviderType;
import com.armedia.acm.auth.okta.model.factor.Factor;
import com.armedia.acm.auth.okta.model.factor.FactorProfile;
import com.armedia.acm.auth.okta.model.factor.FactorType;
import com.armedia.acm.auth.okta.model.user.OktaUser;

public interface FactorLifecycleService
{
    Factor enroll(FactorType factorType, ProviderType provider, FactorProfile profile, OktaUser user) throws OktaException;

    Factor activate(String factorId, String passCode, OktaUser user) throws OktaException;

    Factor activate(String href, String passCode) throws OktaException;

    Factor activate(FactorType factorType, String passCode, OktaUser user) throws OktaException;

    void resetFactors(OktaUser user) throws OktaException;
}

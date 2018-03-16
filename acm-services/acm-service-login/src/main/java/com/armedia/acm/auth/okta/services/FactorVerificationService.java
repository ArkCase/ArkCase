package com.armedia.acm.auth.okta.services;

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.factor.FactorVerifyResult;

public interface FactorVerificationService
{
    FactorVerifyResult challenge(String userId, String factorId) throws OktaException;

    FactorVerifyResult verify(String userId, String factorId, String passoode) throws OktaException;
}

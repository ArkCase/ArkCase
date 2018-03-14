package com.armedia.acm.auth.okta.services;

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.factor.Factor;
import com.armedia.acm.auth.okta.model.factor.FactorType;
import com.armedia.acm.auth.okta.model.factor.SecurityQuestion;
import com.armedia.acm.auth.okta.model.user.OktaUser;

import java.util.List;

public interface FactorService
{
    Factor getFactor(String factorId, OktaUser user) throws OktaException;

    Factor getFactor(FactorType factorType, OktaUser user) throws OktaException;

    List<Factor> listEnrolledFactors(OktaUser user) throws OktaException;

    List<Factor> listAvailableFactors(OktaUser user) throws OktaException;

    List<SecurityQuestion> listSecurityQuestions(OktaUser user) throws OktaException;

    void deleteFactor(String factorId, OktaUser user) throws OktaException;

    void deleteFactor(FactorType factorType, OktaUser user) throws OktaException;
}

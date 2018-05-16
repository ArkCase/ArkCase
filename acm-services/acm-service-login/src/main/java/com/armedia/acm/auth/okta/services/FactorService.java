package com.armedia.acm.auth.okta.services;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

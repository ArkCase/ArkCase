package com.armedia.acm.auth.okta.services.impl;

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
import com.armedia.acm.auth.okta.model.ErrorResponse;
import com.armedia.acm.auth.okta.model.OktaAPIConstants;
import com.armedia.acm.auth.okta.model.ProviderType;
import com.armedia.acm.auth.okta.model.factor.Factor;
import com.armedia.acm.auth.okta.model.factor.FactorProfile;
import com.armedia.acm.auth.okta.model.factor.FactorType;
import com.armedia.acm.auth.okta.model.user.OktaUser;
import com.armedia.acm.auth.okta.services.FactorLifecycleService;
import com.armedia.acm.auth.okta.services.FactorService;
import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FactorLifecycleServiceImpl implements FactorLifecycleService
{
    private static final Logger LOGGER = LogManager.getLogger(FactorLifecycleServiceImpl.class);
    private OktaRestService oktaRestService;
    private FactorService factorService;

    @Override
    public Factor enroll(FactorType factorType, ProviderType provider, FactorProfile profile, OktaUser user) throws OktaException
    {
        Preconditions.checkNotNull(factorType, "factorType is null");
        Preconditions.checkNotNull(provider, "provider is null");

        if (user != null)
        {
            String apiPath = buildEnrollmentPath(factorType, user);

            // Builds enroll request body
            JSONObject body = new JSONObject();
            body.put(OktaAPIConstants.FACTOR_TYPE, factorType.getFactorType());
            body.put(OktaAPIConstants.PROVIDER, provider.name());
            JSONObject enrollProfile = buildEnrollmentProfile(factorType, profile);
            if (enrollProfile != null && enrollProfile.length() > 0)
            {
                body.put(OktaAPIConstants.PROFILE, enrollProfile);
            }

            ResponseEntity<Factor> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.POST, Factor.class, body.toString());
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                LOGGER.debug("Failed to enroll factor [{}, {}]", ErrorResponse.class.cast(exchange.getBody()).getErrorSummary(),
                        ErrorResponse.class.cast(exchange.getBody()).getErrorId());
                throw new OktaException(buildErrorMessage(exchange.getBody()));
            }

            return exchange.getBody();
        }
        return null;
    }

    private String buildEnrollmentPath(FactorType factorType, OktaUser user) throws OktaException
    {
        String apiPath = FactorType.SOFTWARE_TOKEN.equals(factorType) || FactorType.SMS.equals(factorType)
                ? String.format(OktaAPIConstants.ENROLL_FACTOR, user.getId())
                : String.format(OktaAPIConstants.ENROLL_FACTOR_ACTIVATE, user.getId());
        if (FactorType.SMS.equals(factorType))
        {
            LOGGER.debug("Checking for previously verified sms devices");
            Optional<Factor> sms = getFactorService().listAvailableFactors(user).stream()
                    .filter(factor -> FactorType.SMS.equals(factor.getFactorType())).findAny();
            if (sms.isPresent())
            {
                Factor smsFactor = sms.get();
                LOGGER.debug("Found previous sms device: [{}]", smsFactor);
                if (smsFactor.getEmbedded() != null)
                {
                    Object phones = smsFactor.getEmbedded().getOrDefault("phones", Collections.emptyList());
                    if (!List.class.cast(phones).isEmpty())
                    {
                        apiPath = String.format(OktaAPIConstants.ENROLL_FACTOR_ACTIVATE_UPDATE_PHONE, user.getId());
                    }
                }
            }
        }
        return apiPath;
    }

    @Override
    public Factor activate(String factorId, String passCode, OktaUser user) throws OktaException
    {
        Preconditions.checkArgument(!StringUtils.isEmpty(factorId), "factorId is null or empty");
        Preconditions.checkArgument(!StringUtils.isEmpty(passCode), "passCode is null or empty");

        if (user != null)
        {
            // Builds activation request body
            JSONObject body = new JSONObject();
            body.put(OktaAPIConstants.PASS_CODE, passCode);

            String apiPath = String.format(OktaAPIConstants.ACTIVATE_FACTOR, user.getId(), factorId);
            ResponseEntity<Factor> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.POST, Factor.class, body.toString());
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException(buildErrorMessage(exchange.getBody()));
            }

            return exchange.getBody();
        }
        return null;
    }

    @Override
    public Factor activate(String href, String passCode) throws OktaException
    {
        Preconditions.checkNotNull(href, "Missing activation link");
        Preconditions.checkNotNull(passCode, "Missing passCode");

        // Builds activation request body
        JSONObject body = new JSONObject();
        body.put(OktaAPIConstants.PASS_CODE, passCode);
        ResponseEntity<Factor> factorResponseEntity = getOktaRestService().doRestHref(href, HttpMethod.POST, Factor.class, body.toString());
        if (!factorResponseEntity.getStatusCode().is2xxSuccessful())
        {
            throw new OktaException(buildErrorMessage(factorResponseEntity.getBody()));
        }

        return factorResponseEntity.getBody();
    }

    @Override
    public Factor activate(FactorType factorType, String passCode, OktaUser user) throws OktaException
    {
        Preconditions.checkNotNull(factorType, "factorType is null");
        Preconditions.checkArgument(!StringUtils.isEmpty(passCode), "passCode is null or empty");

        Optional<Factor> factor = factorService.listEnrolledFactors(user).stream().filter(f -> factorType.equals(f.getFactorType()))
                .findAny();
        if (user != null && factor.isPresent())
        {
            // Builds activation request body
            JSONObject body = new JSONObject();
            body.put(OktaAPIConstants.PASS_CODE, passCode);

            String apiPath = String.format(OktaAPIConstants.ACTIVATE_FACTOR, user.getId(), factor.get().getId());
            ResponseEntity<Factor> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.POST, Factor.class, body.toString());
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException(buildErrorMessage(exchange.getBody()));
            }

            return exchange.getBody();
        }
        return null;
    }

    @Override
    public void resetFactors(OktaUser user) throws OktaException
    {
        if (user != null)
        {
            String apiPath = String.format(OktaAPIConstants.RESET_FACTORS, user.getId());
            ResponseEntity<Factor> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.POST, Factor.class, "{}");
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException(buildErrorMessage(exchange.getBody()));
            }
        }
    }

    private JSONObject buildEnrollmentProfile(FactorType factorType, FactorProfile profile) throws OktaException
    {
        JSONObject enrollProfile = new JSONObject();
        switch (factorType)
        {
        case EMAIL:
            if (StringUtils.isEmpty(profile.getEmail()))
            {
                throw new OktaException("Email Address is null or empty");
            }
            enrollProfile.put(OktaAPIConstants.EMAIL, profile.getEmail());
            break;
        case SMS:
            if (StringUtils.isEmpty(profile.getPhoneNumber()))
            {
                throw new OktaException("Phone Number is null or empty");
            }
            enrollProfile.put(OktaAPIConstants.PHONE_NUMBER, profile.getPhoneNumber());
        default:
            break;
        }
        return enrollProfile;
    }

    private String buildErrorMessage(Factor factor)
    {
        String errorMsg = "";
        if (factor != null)
        {
            if (!StringUtils.isEmpty(factor.getErrorSummary()))
            {
                errorMsg = factor.getErrorSummary();
            }
            else if (!StringUtils.isEmpty(factor.getErrorCode()))
            {
                errorMsg = factor.getErrorCode();
            }
        }
        return errorMsg;
    }

    public OktaRestService getOktaRestService()
    {
        return oktaRestService;
    }

    public void setOktaRestService(OktaRestService oktaRestService)
    {
        this.oktaRestService = oktaRestService;
    }

    public FactorService getFactorService()
    {
        return factorService;
    }

    public void setFactorService(FactorService factorService)
    {
        this.factorService = factorService;
    }
}

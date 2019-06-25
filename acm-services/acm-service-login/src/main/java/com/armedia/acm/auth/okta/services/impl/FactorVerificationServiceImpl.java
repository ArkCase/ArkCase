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
import com.armedia.acm.auth.okta.model.OktaAPIConstants;
import com.armedia.acm.auth.okta.model.factor.FactorVerifyResult;
import com.armedia.acm.auth.okta.services.FactorVerificationService;
import com.google.common.base.Preconditions;

import org.json.simple.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by dwu on 11/9/2017.
 */
public class FactorVerificationServiceImpl implements FactorVerificationService
{
    private static final Logger LOGGER = LogManager.getLogger(FactorVerificationServiceImpl.class);
    private OktaRestService oktaRestService;

    @Override
    public FactorVerifyResult verify(String userId, String factorId, String passCode) throws OktaException
    {
        Preconditions.checkNotNull(userId, "userId is null");
        Preconditions.checkNotNull(factorId, "factorId is null");

        JSONObject passCodeBody = new JSONObject();
        passCodeBody.put(OktaAPIConstants.PASS_CODE, passCode);
        String apiPath = String.format(OktaAPIConstants.VERIFY_FACTOR, userId, factorId);
        ResponseEntity<FactorVerifyResult> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.POST, FactorVerifyResult.class,
                passCodeBody.toJSONString());
        if (HttpStatus.OK.equals(exchange.getStatusCode()) || HttpStatus.FORBIDDEN.equals(exchange.getStatusCode()))
        {
            return exchange.getBody();
        }
        return null;
    }

    @Override
    public FactorVerifyResult challenge(String userId, String factorId) throws OktaException
    {
        Preconditions.checkNotNull(userId, "userId is null");
        Preconditions.checkNotNull(factorId, "factorId is null");

        String apiPath = String.format(OktaAPIConstants.VERIFY_FACTOR, userId, factorId);
        ResponseEntity<FactorVerifyResult> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.POST, FactorVerifyResult.class, "{}");
        System.out.println(exchange);
        if (HttpStatus.OK.equals(exchange.getStatusCode()))
        {
            return exchange.getBody();
        }
        else if (exchange.getStatusCode().is4xxClientError())
        {
            LOGGER.warn("Too many challenges recently? [{}]", exchange.getBody());
            return exchange.getBody();
        }
        return null;
    }

    public OktaRestService getOktaRestService()
    {
        return oktaRestService;
    }

    public void setOktaRestService(OktaRestService oktaRestService)
    {
        this.oktaRestService = oktaRestService;
    }

}

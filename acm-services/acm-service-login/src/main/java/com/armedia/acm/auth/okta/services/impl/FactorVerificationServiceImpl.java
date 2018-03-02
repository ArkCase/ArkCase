package com.armedia.acm.auth.okta.services.impl;

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.OktaAPIConstants;
import com.armedia.acm.auth.okta.model.factor.FactorVerifyResult;
import com.armedia.acm.auth.okta.services.FactorVerificationService;
import com.google.common.base.Preconditions;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by dwu on 11/9/2017.
 */
public class FactorVerificationServiceImpl implements FactorVerificationService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FactorVerificationServiceImpl.class);
    private OktaRestService oktaRestService;

    @Override
    public FactorVerifyResult verify(String userId, String factorId, String passCode) throws OktaException
    {
        Preconditions.checkNotNull(userId, "userId is null");
        Preconditions.checkNotNull(factorId, "factorId is null");

        JSONObject passCodeBody = new JSONObject();
        passCodeBody.put(OktaAPIConstants.PASS_CODE, passCode);
        String apiPath = String.format(OktaAPIConstants.VERIFY_FACTOR, userId, factorId);
        ResponseEntity<FactorVerifyResult> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.POST, FactorVerifyResult.class, passCodeBody.toJSONString());
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
        } else if (exchange.getStatusCode().is4xxClientError())
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

package com.armedia.acm.auth.okta.services.impl;

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.OktaAPIConstants;
import com.armedia.acm.auth.okta.model.factor.Factor;
import com.armedia.acm.auth.okta.model.factor.FactorStatus;
import com.armedia.acm.auth.okta.model.factor.FactorType;
import com.armedia.acm.auth.okta.model.factor.SecurityQuestion;
import com.armedia.acm.auth.okta.model.user.OktaUser;
import com.armedia.acm.auth.okta.services.FactorService;
import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FactorServiceImpl implements FactorService
{
    private OktaRestService oktaRestService;

    @Override
    public Factor getFactor(String factorId, OktaUser user) throws OktaException
    {
        Preconditions.checkArgument(!StringUtils.isEmpty(factorId), "factorId is null or empty");

        if (user != null)
        {
            String apiPath = String.format(OktaAPIConstants.GET_FACTOR, user.getId(), factorId);
            ResponseEntity<Factor> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.GET, Factor.class, "parameters");
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException(buildErrorMessage(exchange.getBody()));
            }

            return exchange.getBody();
        }
        return null;
    }

    @Override
    public Factor getFactor(FactorType factorType, OktaUser user) throws OktaException
    {
        Preconditions.checkNotNull(factorType, "factorType is null");

        Optional<Factor> factor = listEnrolledFactors(user).stream().filter(f -> factorType.equals(f.getFactorType())).findAny();
        if (user != null && factor.isPresent())
        {
            return factor.get();
        }
        return null;
    }

    @Override
    public List<Factor> listEnrolledFactors(OktaUser user) throws OktaException
    {
        if (user != null)
        {
            String apiPath = String.format(OktaAPIConstants.LIST_ENROLLED_FACTORS, user.getId());
            ResponseEntity<Factor[]> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.GET, Factor[].class, "parameters");
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException(exchange.toString());
            }

            if (exchange.getBody().length > 0)
            {
                List<Factor> factors = Arrays.asList(exchange.getBody());
                factors.forEach(factor -> factor.setFactorSummary(buildFactorSummary(factor)));
                return factors;
            }
        }
        return Collections.emptyList();
    }

    private String buildFactorSummary(Factor factor)
    {
        StringBuilder sb = new StringBuilder();
        if (factor != null && factor.getFactorType() != null)
        {
            FactorType type = factor.getFactorType();
            switch (type)
            {
            case EMAIL:
                sb.append("Email: ").append(factor.getProfile().getEmail());
                break;
            case SMS:
                String phoneNumber = factor.getProfile().getPhoneNumber();
                if (phoneNumber.length() > 4)
                {
                    String phoneSanitized = phoneNumber.substring(0, phoneNumber.length() - 4).replaceAll("[\\+0-9]", "*");
                    sb.append("Phone: ").append(phoneSanitized).append(StringUtils.substring(phoneNumber, phoneNumber.length() - 4));
                }
                else
                {
                    sb.append("Phone: ").append(phoneNumber);
                }
                break;
            case SOFTWARE_TOKEN:
                sb.append("TOTP");
                break;
            default:
                sb.append(factor.getFactorType());
            }

            return sb.toString();
        }
        return null;
    }

    @Override
    public List<Factor> listAvailableFactors(OktaUser user) throws OktaException
    {
        if (user != null)
        {
            String apiPath = String.format(OktaAPIConstants.LIST_AVAILABLE_ENROLLED_FACTORS, user.getId());
            ResponseEntity<Factor[]> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.GET, Factor[].class, "parameters");
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException(exchange.toString());
            }

            if (exchange.getBody().length > 0)
            {
                List<Factor> factorCatalog = Arrays.asList(exchange.getBody());

                List<Factor> factorsAlreadyEnrolled = listEnrolledFactors(user);
                if (factorsAlreadyEnrolled != null)
                {
                    if (!factorsAlreadyEnrolled.isEmpty())
                    {
                        List<FactorType> factorTypes = factorsAlreadyEnrolled.stream()
                                .filter(factor -> FactorStatus.ACTIVE.equals(factor.getStatus())
                                        || FactorStatus.ENROLLED.equals(factor.getStatus()))
                                .map(Factor::getFactorType)
                                .collect(Collectors.toList());
                        return factorCatalog.stream().filter(factor -> !factorTypes.contains(factor.getFactorType()))
                                .collect(Collectors.toList());
                    }
                    else
                    {
                        return factorCatalog;
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    public List<SecurityQuestion> listSecurityQuestions(OktaUser user) throws OktaException
    {
        if (user != null)
        {
            String apiPath = String.format(OktaAPIConstants.LIST_SECURITY_QUESTIONS, user.getId());
            ResponseEntity<SecurityQuestion[]> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.GET, SecurityQuestion[].class,
                    "parameters");
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException(exchange.toString());
            }

            if (exchange.getBody().length > 0)
            {
                return Arrays.asList(exchange.getBody());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteFactor(String factorId, OktaUser user) throws OktaException
    {
        Preconditions.checkArgument(!StringUtils.isEmpty(factorId), "factorId is null or empty");

        if (user != null)
        {
            String apiPath = String.format(OktaAPIConstants.GET_FACTOR, user.getId(), factorId);
            ResponseEntity<Factor> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.DELETE, Factor.class, "parameters");
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException(buildErrorMessage(exchange.getBody()));
            }
        }
    }

    @Override
    public void deleteFactor(FactorType factorType, OktaUser user) throws OktaException
    {
        Preconditions.checkNotNull(factorType, "factorType is null");

        Optional<Factor> factor = listEnrolledFactors(user).stream().filter(f -> factorType.equals(f.getFactorType())).findAny();
        if (user != null && factor.isPresent())
        {
            String apiPath = String.format(OktaAPIConstants.GET_FACTOR, user.getId(), factor.get().getId());
            ResponseEntity<Factor> exchange = oktaRestService.doRestCall(apiPath, HttpMethod.DELETE, Factor.class, "parameters");
            if (!exchange.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException(buildErrorMessage(exchange.getBody()));
            }
        }
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
}
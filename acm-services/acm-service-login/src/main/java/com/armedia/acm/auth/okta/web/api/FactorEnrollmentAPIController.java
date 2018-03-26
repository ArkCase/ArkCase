package com.armedia.acm.auth.okta.web.api;

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.factor.ActivateRequestDTO;
import com.armedia.acm.auth.okta.model.factor.Factor;
import com.armedia.acm.auth.okta.model.user.OktaUser;
import com.armedia.acm.auth.okta.services.FactorLifecycleService;
import com.armedia.acm.auth.okta.services.FactorService;
import com.armedia.acm.auth.okta.services.OktaUserService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by joseph.mcgrady on 11/10/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/okta/factor/enrollment", "/api/latest/plugin/okta/factor/enrollment"})
public class FactorEnrollmentAPIController
{
    private FactorService factorService;
    private FactorLifecycleService factorLifecycleService;
    private OktaUserService oktaUserService;
    private static final Logger LOGGER = LoggerFactory.getLogger(FactorEnrollmentAPIController.class);

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Factor> findEnrolledFactors(Authentication auth) throws OktaException
    {
        try
        {
            LOGGER.info("Retrieving enrolled factors for user [{}]", auth.getName());
            OktaUser user = getOktaUserService().getUser(auth.getName());
            return getFactorService().listEnrolledFactors(user);
        } catch (Exception e)
        {
            LOGGER.error("Failed to retrieve enrolled factors for user [{}]", auth.getName(), e);
            throw new OktaException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "available", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Factor> findAvailableFactors(Authentication auth) throws OktaException
    {
        try
        {
            LOGGER.info("Retrieving available factors");
            OktaUser user = getOktaUserService().getUser(auth.getName());
            return getFactorService().listAvailableFactors(user);
        } catch (Exception e)
        {
            LOGGER.error("Failed to retrieve available factors", e);
            throw new OktaException(e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Factor enrollFactor(@RequestBody Factor enrollRequestDTO, Authentication auth)
            throws OktaException
    {
        Preconditions.checkNotNull(enrollRequestDTO, "enrollRequestDTO is null");
        Preconditions.checkNotNull(enrollRequestDTO.getFactorType(), "factor type is null");
        Preconditions.checkNotNull(enrollRequestDTO.getProvider(), "provider is null");

        try
        {
            LOGGER.info("Enrolling user [{}] in factor [{}]", auth.getName(), enrollRequestDTO.getFactorType().name());
            OktaUser user = getOktaUserService().getUser(auth.getName());
            return getFactorLifecycleService().enroll(enrollRequestDTO.getFactorType(),
                    enrollRequestDTO.getProvider(), enrollRequestDTO.getProfile(), user);
        } catch (Exception e)
        {
            LOGGER.error("Failed to enroll user [{}] in factor [{}]", auth.getName(), enrollRequestDTO.getFactorType().name(), e);
            throw new OktaException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "activate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Factor activateFactor(@RequestBody ActivateRequestDTO activateRequestDTO, Authentication auth)
            throws OktaException
    {
        Preconditions.checkNotNull(activateRequestDTO, "activateRequestDTO is null");
        Preconditions.checkNotNull(activateRequestDTO.getFactorId(), "factor id is null");
        Preconditions.checkNotNull(activateRequestDTO.getActivationCode(), "activation code is null");

        try
        {
            LOGGER.info("Activating factor [{}] for user [{}]", activateRequestDTO.getFactorId(), auth.getName());
            OktaUser user = getOktaUserService().getUser(auth.getName());
            return getFactorLifecycleService().activate(activateRequestDTO.getFactorId(), activateRequestDTO.getActivationCode(), user);
        } catch (Exception e)
        {
            LOGGER.error("Failed to activate factor [{}] for user [{}]", activateRequestDTO.getFactorId(), auth.getName(), e);
            throw new OktaException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void activateFactor(Authentication auth) throws OktaException
    {
        try
        {
            LOGGER.info("Resetting factors for user [{}]", auth.getName());
            OktaUser user = getOktaUserService().getUser(auth.getName());
            getFactorLifecycleService().resetFactors(user);
        } catch (Exception e)
        {
            LOGGER.error("Failed to reset factors for user [{}]", auth.getName(), e);
            throw new OktaException(e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void deleteFactor(@RequestParam("factorId") String id, Authentication auth)
            throws OktaException
    {
        Preconditions.checkNotNull(id, "factor id is null");

        try
        {
            LOGGER.info("Deleting factor [{}] for user [{}]", id, auth.getName());
            OktaUser user = getOktaUserService().getUser(auth.getName());
            getFactorService().deleteFactor(id, user);
        } catch (Exception e)
        {
            LOGGER.error("Failed to delete factor [{}] for user [{}]", id, auth.getName(), e);
            throw new OktaException(e.getMessage(), e);
        }
    }

    public FactorService getFactorService()
    {
        return factorService;
    }

    public void setFactorService(FactorService factorService)
    {
        this.factorService = factorService;
    }

    public FactorLifecycleService getFactorLifecycleService()
    {
        return factorLifecycleService;
    }

    public void setFactorLifecycleService(FactorLifecycleService factorLifecycleService)
    {
        this.factorLifecycleService = factorLifecycleService;
    }

    public OktaUserService getOktaUserService()
    {
        return oktaUserService;
    }

    public void setOktaUserService(OktaUserService oktaUserService)
    {
        this.oktaUserService = oktaUserService;
    }
}
package com.armedia.acm.auth.okta.web;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmGrantedAuthority;
import com.armedia.acm.auth.LoginEvent;
import com.armedia.acm.auth.okta.auth.AcmMultiFactorConfig;
import com.armedia.acm.auth.okta.auth.OktaAuthenticationDetails;
import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.OktaAPIConstants;
import com.armedia.acm.auth.okta.model.ProviderType;
import com.armedia.acm.auth.okta.model.factor.Factor;
import com.armedia.acm.auth.okta.model.factor.FactorProfile;
import com.armedia.acm.auth.okta.model.factor.FactorResult;
import com.armedia.acm.auth.okta.model.factor.FactorStatus;
import com.armedia.acm.auth.okta.model.factor.FactorType;
import com.armedia.acm.auth.okta.model.factor.FactorVerifyResult;
import com.armedia.acm.auth.okta.model.factor.VerifyRequestDTO;
import com.armedia.acm.auth.okta.model.user.OktaUser;
import com.armedia.acm.auth.okta.model.user.OktaUserCredentials;
import com.armedia.acm.auth.okta.model.user.OktaUserProfile;
import com.armedia.acm.auth.okta.model.user.OktaUserStatus;
import com.armedia.acm.auth.okta.services.FactorLifecycleService;
import com.armedia.acm.auth.okta.services.FactorService;
import com.armedia.acm.auth.okta.services.FactorVerificationService;
import com.armedia.acm.auth.okta.services.OktaUserService;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.dao.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping({"/mfa"})
public class AuthLoginController implements ApplicationEventPublisherAware
{
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private AcmMultiFactorConfig multiFactorConfig;
    private OktaUserService oktaUserService;
    private FactorService factorService;
    private FactorLifecycleService factorLifecycleService;
    private FactorVerificationService factorVerificationService;
    private UserDao userDao;
    private ApplicationEventPublisher applicationEventPublisher;


    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public ModelAndView auth(Model model, HttpServletRequest request, Authentication authentication) throws OktaException
    {
        try
        {
            LOGGER.info("Looking up user and enrolled factors");
            AcmUser dbUser = getUserDao().findByUserId(authentication.getName());
            OktaUser user = getOktaUserService().getUser(dbUser.getUserId());
            if (user == null)
            {
                LOGGER.warn("User doesn't exist in Okta, creating user and activating the user");

                user = new OktaUser();

                OktaUserProfile profile = new OktaUserProfile();
                profile.setEmail(dbUser.getMail());
                profile.setLogin(dbUser.getUserId());
                profile.setFirstName(dbUser.getFirstName());
                profile.setLastName(dbUser.getLastName());

                user.setProfile(profile);

                OktaUserCredentials credentials = new OktaUserCredentials();
                credentials.setPassword((String) authentication.getCredentials());
                credentials.setProvider(ProviderType.OKTA);
                credentials.setProviderName(ProviderType.OKTA.getProviderType());

                user.setCredentials(credentials);

                LOGGER.debug("Creating activated user: [{}]", user);
                user = getOktaUserService().createUser(user);
                getOktaUserService().activateUser(user.getId());
                user = getOktaUserService().getUser(dbUser.getUserId());

                if (user == null)
                {
                    throw new OktaException("Couldn't create new user");
                }

                if (!OktaUserStatus.ACTIVE.equals(user.getStatus()))
                {
                    if (user.hasError())
                    {
                        throw new OktaException(user.getErrorSummary());
                    }

                    throw new OktaException("User created wasn't able to be activated");
                }

                LOGGER.debug("Created activated user: [{}]", user);
            }

            if (OktaUserStatus.DEPROVISIONED.equals(user.getStatus()) || OktaUserStatus.SUSPENDED.equals(user.getStatus()))
            {
                throw new OktaException("User is invalid or suspended, please contact support.");
            }

            // Get Factors
            List<Factor> factors = getFactorService().listEnrolledFactors(user);
            if (factors.isEmpty())
            {
                LOGGER.debug("No factors found for user, redirecting to enrollment page");
                return new ModelAndView("redirect:" + getMultiFactorConfig().getEnrollmentTargetUrl());
            } else
            {
                // Only display active factors
                factors = factors.stream().filter(factor -> FactorStatus.isActive(factor.getStatus())).collect(Collectors.toList());
                if (factors.isEmpty())
                {
                    return new ModelAndView("redirect:" + getMultiFactorConfig().getEnrollmentTargetUrl());
                }
            }

            model.addAttribute("factors", factors);
            model.addAttribute("enrollmentUrl", getMultiFactorConfig().getEnrollmentTargetUrl());

            // Update authentication with okta user details
            OktaAuthenticationDetails oktaAuthenticationDetails = new OktaAuthenticationDetails(user, request);
            AcmAuthentication acmAuthentication = new AcmAuthentication((Collection<AcmGrantedAuthority>) authentication.getAuthorities(), authentication.getCredentials(), oktaAuthenticationDetails, authentication.isAuthenticated(), authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(acmAuthentication);

            return new ModelAndView(getMultiFactorConfig().getSelectMethodTargetUrl(), model.asMap());
        } catch (Exception e)
        {
            // Don't display error page, return view with error embedded.
            LOGGER.error("Error on /auth page", e);
            model.addAttribute("factors", Collections.emptyList());
            model.addAttribute("error", e.getMessage());
            return new ModelAndView(getMultiFactorConfig().getSelectMethodTargetUrl(), model.asMap());
        }
    }

    /**
     * This controller method gets the challenge the user url and redirect it
     * to the enter passcode page.
     *
     * @param factorId
     * @param authentication
     * @return
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ModelAndView confirmAuth(Model existing, String factorId, Authentication authentication)
    {
        Map<String, Object> model = new HashMap<>();

        try
        {
            Object details = authentication.getDetails();
            if (!(details instanceof OktaAuthenticationDetails))
            {
                throw new OktaException("Didn't find login details of user");
            }

            OktaUser oktaUser = OktaAuthenticationDetails.class.cast(details).getOktaUser();
            Factor factor = getFactorService().getFactor(factorId, oktaUser);
            FactorVerifyResult result = getFactorVerificationService().challenge(oktaUser.getId(), factorId);
            if (result.hasError())
            {
                throw new OktaException("Failed to send challenge " + result.getErrorSummary());
            }

            //send the known user id and factor id to the new view
            model.put("factor", factorId);
            model.put("sendCode", !FactorType.SOFTWARE_TOKEN.equals(factor.getFactorType()));
            return new ModelAndView(getMultiFactorConfig().getVerifyMethodTargetUrl(), model);

        } catch (OktaException e)
        {
            LOGGER.error("Failed to send challenge code: " + e.getMessage(), e);
            model.put(OktaAPIConstants.ERROR, e.getMessage());
            return new ModelAndView(getMultiFactorConfig().getSelectMethodTargetUrl(), model);
        }
    }

    @RequestMapping(value = "/getcode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void getCode(@RequestBody VerifyRequestDTO verifyData, Authentication authentication, HttpServletResponse response) throws OktaException

    {
        Object details = authentication.getDetails();
        if (!(details instanceof OktaAuthenticationDetails))
        {
            throw new OktaException("Didn't find login details of user");
        }
        OktaUser oktaUser = OktaAuthenticationDetails.class.cast(details).getOktaUser();


        String userId = oktaUser.getId();
        String factorId = verifyData.getFactorId();
        LOGGER.info("Input userId [{}] and factorId [{}]", userId, factorId);

        try
        {

            FactorVerifyResult result = getFactorVerificationService().challenge(userId, factorId);
            if (result == null)
            {
                throw new OktaException("Failed to send new code");
            } else if (result.hasError())
            {
                throw new OktaException("Failed to send new code: " + result.getErrorSummary());
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (OktaException e)
        {
            LOGGER.error("Failed to execute get new challenge code.", e);
            throw e;
        }
    }

    /**
     * This controller handles the verify call to OKTA so that it can
     * send a verify code to the user's phone. This method is called by
     * Spring MVC framework (GET)
     *
     * @return
     */
    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ModelAndView verify()
    {
        //If someone refreshes this page, redirect to select auth page, should never come to verify page manually
        return new ModelAndView("redirect:" + getMultiFactorConfig().getSelectMethodTargetUrl());
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ModelAndView confirmVerify(String passcode, String factorId, Authentication authentication) throws OktaException
    {
        Map<String, Object> model = new HashMap<>();
        FactorVerifyResult result = null;

        String view = getMultiFactorConfig().getVerifyMethodTargetUrl();
        Object details = authentication.getDetails();
        if (!(details instanceof OktaAuthenticationDetails))
        {
            throw new OktaException("Didn't find login details of user");
        }
        OktaUser oktaUser = OktaAuthenticationDetails.class.cast(details).getOktaUser();
        String userId = oktaUser.getId();

        try
        {
            result = factorVerificationService.verify(userId, factorId, passcode);
            if (result == null)
            {
                LOGGER.error(OktaAPIConstants.INVALIDATION_FAILED + " Validation result was null");
                throw new OktaException(OktaAPIConstants.INVALIDATION_FAILED + " Validation result was null");
            } else
            {
                FactorResult status = result.getFactorResult();
                if (FactorResult.SUCCESS.equals(status))
                {
                    grantSuccessAuthorities();
                    view = "redirect:/home.html#!/welcome";
                    LOGGER.info("/verify status: " + status.name());
                } else
                {
                    throw new OktaException(OktaAPIConstants.INVALID_PASS_CODE);
                }
            }
        } catch (OktaException e)
        {
            LOGGER.error("Failed to execute 2nd factor challenge: " + e.getMessage(), e);
            handleVerifyError(userId, factorId, model, e);
        } catch (Exception ex)
        {
            LOGGER.error("Failed to execute 2nd factor challenge: " + ex.getMessage(), ex);
            handleVerifyError(userId, factorId, model, ex);
        }

        return new ModelAndView(view, model);
    }

    /**
     * Handle the verify Post call return errors process. It requires the a model with
     * relevant data
     *
     * @param userId
     * @param factorId
     * @param model
     * @param error
     */
    private void handleVerifyError(String userId, String factorId, Map model, Exception error)
    {
        //send the known user id and factor id to the new view
        model.put("factor", factorId);
        model.put("user", userId);
        model.put(OktaAPIConstants.ERROR, error.getMessage());
    }

    @RequestMapping(value = "/enroll", method = RequestMethod.GET)
    public ModelAndView enroll(Authentication authentication) throws OktaException
    {
        Map<String, Object> model = new HashMap<>();
        try
        {
            OktaUser user = getOktaUserService().getUser(authentication.getName());
            List<Factor> factors = factorService.listAvailableFactors(user);
            if (factors != null && !factors.isEmpty())
            {
                model.put("factors", factors);
            }
            return new ModelAndView(getMultiFactorConfig().getEnrollmentTargetUrl(), model);
        } catch (Exception e)
        {
            LOGGER.error("Error on /enroll page", e);
            model.put(OktaAPIConstants.ERROR, e.getMessage());
            return new ModelAndView(getMultiFactorConfig().getEnrollmentTargetUrl(), model);
        }
    }

    @RequestMapping(value = "/enroll", method = RequestMethod.POST)
    public ModelAndView confirmEnrollment(FactorType factor, HttpServletRequest request, Authentication authentication) throws OktaException
    {
        Map<String, Object> model = new HashMap<>();
        try
        {
            AcmUser dbUser = getUserDao().findByUserId(authentication.getName());
            OktaUser user = getOktaUserService().getUser(dbUser.getUserId());
            FactorProfile profile = new FactorProfile();

            switch (factor)
            {
                case EMAIL:
                    if (StringUtils.isEmpty(request.getParameter("emailAddress")))
                    {
                        throw new OktaException("Email Address is null or empty");
                    }
                    profile.setEmail(request.getParameter("emailAddress"));
                    getFactorLifecycleService().enroll(FactorType.EMAIL, ProviderType.OKTA, profile, user);
                    break;
                case SMS:
                    if (StringUtils.isEmpty(request.getParameter("phoneNumber")))
                    {
                        throw new OktaException("Phone Number is null or empty");
                    }
                    profile.setPhoneNumber(request.getParameter("phoneNumber"));
                    Factor factorEnroll = getFactorLifecycleService().enroll(FactorType.SMS, ProviderType.OKTA, profile, user);
                    if (factorEnroll != null && !FactorStatus.ACTIVE.equals(factorEnroll.getStatus()))
                    {
                        model.put("factors", Collections.singletonList(factorEnroll));
                        model.put("embedded", request.getParameter("phoneNumber"));
                        return new ModelAndView(getMultiFactorConfig().getEnrollmentTargetUrl(), model);
                    }
                    break;
                case SOFTWARE_TOKEN:
                    Factor enroll = getFactorLifecycleService().enroll(FactorType.SOFTWARE_TOKEN, ProviderType.OKTA, profile, user);
                    if (enroll != null)
                    {
                        putSharedSecret(enroll, model);
                        model.put("factors", Collections.singletonList(enroll));
                        return new ModelAndView(getMultiFactorConfig().getEnrollmentTargetUrl(), model);
                    }
                    break;
                default:
                    break;
            }

            return new ModelAndView("redirect:" + getMultiFactorConfig().getSelectMethodTargetUrl());
        } catch (Exception e)
        {
            LOGGER.error("Error confirming enrollment", e);
            addEnrollErrorData(model, e, authentication);
            return new ModelAndView(getMultiFactorConfig().getEnrollmentTargetUrl(), model);
        }
    }

    @RequestMapping(value = "/enroll/confirm", method = RequestMethod.POST)
    public ModelAndView confirmCodeEnrollment(String passCode, String factorHref, Authentication authentication) throws OktaException
    {
        Map<String, Object> model = new HashMap<>();
        LOGGER.info("Activating Software Token");
        try
        {
            Factor activate = getFactorLifecycleService().activate(factorHref, passCode);
            if (activate != null && FactorStatus.ACTIVE.equals(activate.getStatus()))
            {
                return new ModelAndView("redirect:" + getMultiFactorConfig().getSelectMethodTargetUrl());
            }

            return new ModelAndView("redirect:" + getMultiFactorConfig().getSelectMethodTargetUrl());
        } catch (Exception e)
        {
            LOGGER.error("Failed to confirm secondary enrollment activation", e);
            addEnrollErrorData(model, e, authentication);
            return new ModelAndView(getMultiFactorConfig().getEnrollmentTargetUrl(), model);
        }
    }

    private void addEnrollErrorData(Map<String, Object> model, Exception e, Authentication authentication)
    {
        model.put(OktaAPIConstants.ERROR, e.getMessage());
        try
        {
            OktaUser user = getOktaUserService().getUser(authentication.getName());
            List<Factor> factors = factorService.listAvailableFactors(user);
            if (factors != null && !factors.isEmpty())
            {
                model.put("factors", factors);
            }
        } catch (Exception ex)
        {
            LOGGER.error("Failed to retrieve available factors while processing previous error", ex);
        }
    }

    private void putSharedSecret(Factor enroll, Map<String, Object> model)
    {
        LinkedHashMap<String, Object> activation = (LinkedHashMap<String, Object>) enroll.get_embedded().get("activation");
        String sharedSecret = String.class.cast(activation.get("sharedSecret"));
        model.put("embedded", sharedSecret);
    }

    private void grantSuccessAuthorities()
    {
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        List<AcmGrantedAuthority> newAuthorities = contextAuth.getAuthorities().stream()
                .filter(auth -> auth instanceof AcmGrantedAuthority && !"ROLE_PRE_AUTHENTICATED".equalsIgnoreCase(auth.getAuthority()))
                .map(AcmGrantedAuthority.class::cast).collect(Collectors.toList());
        newAuthorities.add(new AcmGrantedAuthority("ROLE_AUTHENTICATED"));
        AcmAuthentication acmAuthentication = new AcmAuthentication(newAuthorities, contextAuth.getCredentials(), contextAuth.getDetails(), contextAuth.isAuthenticated(), contextAuth.getName());
        SecurityContextHolder.getContext().setAuthentication(acmAuthentication);
        //Publish login event on second factor auth success
        LoginEvent event = new LoginEvent(acmAuthentication);
        event.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(event);

    }

    public AcmMultiFactorConfig getMultiFactorConfig()
    {
        return multiFactorConfig;
    }

    public void setMultiFactorConfig(AcmMultiFactorConfig multiFactorConfig)
    {
        this.multiFactorConfig = multiFactorConfig;
    }

    public OktaUserService getOktaUserService()
    {
        return oktaUserService;
    }

    public void setOktaUserService(OktaUserService oktaUserService)
    {
        this.oktaUserService = oktaUserService;
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

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public FactorVerificationService getFactorVerificationService()
    {
        return factorVerificationService;
    }

    public void setFactorVerificationService(FactorVerificationService factorVerificationService)
    {
        this.factorVerificationService = factorVerificationService;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
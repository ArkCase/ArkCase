package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.portalgateway.model.PortalUser;
import com.armedia.acm.portalgateway.model.PortalUserCredentials;
import com.armedia.acm.portalgateway.model.UserRegistrationRequest;
import com.armedia.acm.portalgateway.model.UserRegistrationResponse;
import com.armedia.acm.portalgateway.model.UserResetRequest;
import com.armedia.acm.portalgateway.model.UserResetResponse;
import com.armedia.acm.portalgateway.service.PortalConfigurationService;
import com.armedia.acm.portalgateway.service.PortalUserServiceException;
import com.armedia.acm.portalgateway.service.PortalUserServiceProvider;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.ldap.syncer.AcmLdapSyncEvent;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.UserDTO;
import com.armedia.acm.services.users.service.AcmUserEventPublisher;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
import com.armedia.acm.services.users.service.ldap.LdapUserService;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.InvalidAttributeValueException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import gov.privacy.dao.PortalSARPersonDao;
import gov.privacy.dao.UserRegistrationRequestDao;
import gov.privacy.dao.UserResetRequestDao;
import gov.privacy.model.PortalSARPerson;
import gov.privacy.model.SARPerson;
import gov.privacy.model.UserRegistrationRequestRecord;
import gov.privacy.model.UserResetRequestRecord;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARPortalUserServiceProvider implements PortalUserServiceProvider
{

    public static final long DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000; // 1 day in milliseconds
    public static final long REGISTRATION_EXPIRATION = 90L * DAY_IN_MILLISECONDS; // 90 days in milliseconds

    private final Logger log = LogManager.getLogger(getClass());

    private AcmEmailSenderService emailSenderService;

    private UserRegistrationRequestDao registrationDao;

    private UserResetRequestDao resetDao;

    private PortalSARPersonDao portalPersonDao;

    private LdapUserService ldapUserService;

    private PersonDao personDao;

    private RequestAssignmentService requestAssignmentService;

    @Value("${portal.serviceProvider.directory.name}")
    private String directoryName;

    private NotificationDao notificationDao;

    private UserDao userDao;

    private SpringContextHolder acmContextHolder;

    private TranslationService translationService;

    private AcmUserEventPublisher acmUserEventPublisher;

    private OrganizationDao organizationDao;

    private PortalConfigurationService portalConfigurationService;

    private CorrespondenceTemplateManager templateManager;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#requestRegistration(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserRegistrationRequest)
     */
    @Override
    public UserRegistrationResponse regenerateRegistrationRequest(String portalId, UserRegistrationRequest registrationRequest)
            throws PortalUserServiceException
    {
        if (getPortalAcmUser(registrationRequest.getEmailAddress()) != null)
        {
            return UserRegistrationResponse.exists();
        }
        Optional<UserRegistrationRequestRecord> registrationRecord = registrationDao.findByEmail(registrationRequest.getEmailAddress(),
                portalId);

        if (registrationRecord.isPresent())
        {
            regenerateRegistration(portalId, registrationRecord.get());
            return UserRegistrationResponse.requestAccepted();
        }
        else
        {
            return UserRegistrationResponse.requestRequired();
        }
    }

    @Override
    public UserRegistrationResponse requestRegistration(String portalId, UserRegistrationRequest registrationRequest)
            throws PortalUserServiceException
    {
        if (getPortalAcmUser(registrationRequest.getEmailAddress()) != null)
        {
            return UserRegistrationResponse.exists();
        }
        Optional<UserRegistrationRequestRecord> registrationRecord = registrationDao.findByEmail(registrationRequest.getEmailAddress(),
                portalId);
        Optional<PortalSARPerson> registeredPerson = portalPersonDao.findByEmail(registrationRequest.getEmailAddress());

        if (isUserRejectedForPortal(portalId, registeredPerson))
        {
            return UserRegistrationResponse.rejected();
        }
        else if (registeredPerson.isPresent() && registeredPerson.get().getPortalRoles().containsKey(portalId))
        {
            return UserRegistrationResponse.exists();
        }
        else if (registrationRecord.isPresent() && isRegistrationRecordActive(registrationRecord.get().getRegistrationTime()))
        {
            return UserRegistrationResponse.pending(registrationRecord.get().getEmailAddress());
        }
        else if (registrationRecord.isPresent() && !isRegistrationRecordActive(registrationRecord.get().getRegistrationTime()))
        {
            return UserRegistrationResponse.requestExpired();
        }
        else
        {
            createRegistrationRecordAndSendEmail(portalId, registrationRequest);
            return UserRegistrationResponse.requestAccepted();
        }
    }

    private void createRegistrationRecordAndSendEmail(String portalId, UserRegistrationRequest registrationRequest)
    {
        String registrationKey = UUID.randomUUID().toString();
        createRegistrationRecord(registrationKey, registrationRequest.getEmailAddress(), System.currentTimeMillis(), portalId);
        String registrationLink = new String(Base64Utils.decodeFromString(registrationRequest.getRegistrationUrl()),
                Charset.forName("UTF-8")) + "/" + registrationKey + "/" + registrationRequest.getEmailAddress();

        String emailSubject = "";
        Template template = templateManager.findTemplate("portalRequestRegistrationLink.html");
        if (template != null)
        {
            emailSubject = template.getEmailSubject();
        }
        Notification notification = new Notification();
        notification.setTemplateModelName("portalRequestRegistrationLink");
        notification.setTitle(translationService.translate(NotificationConstants.PORTAL_REGISTRATION));
        notification.setCreator(registrationRequest.getEmailAddress());
        notification.setNote(registrationLink);
        notification.setEmailAddresses(registrationRequest.getEmailAddress());
        notification.setUser(SecurityContextHolder.getContext().getAuthentication().getName());
        notification.setParentType("USER");
        notification.setSubject(emailSubject);

        getNotificationDao().save(notification);
    }

    private void createRegistrationRecord(String registrationKey, String email, Long currentTime, String portalId)
    {
        UserRegistrationRequestRecord record = new UserRegistrationRequestRecord();

        record.setRegistrationKey(registrationKey);
        record.setRegistrationTime(currentTime);
        record.setEmailAddress(email);
        record.setPortalId(portalId);

        registrationDao.save(record);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#checkRegistrationStatus(java.lang.String,
     * java.lang.String)
     */
    @Override
    public UserRegistrationResponse checkRegistrationStatus(String portalId, String registrationId)
    {
        Optional<UserRegistrationRequestRecord> registrationRecord = registrationDao.findByRegistrationId(registrationId);
        if (!registrationRecord.isPresent())
        {
            return UserRegistrationResponse.requestRequired();
        }
        else if (isRegistrationRecordActive(registrationRecord.get().getRegistrationTime()))
        {
            return UserRegistrationResponse.pending(registrationRecord.get().getEmailAddress());
        }
        else
        {
            regenerateRegistration(portalId, registrationRecord.get());
            return UserRegistrationResponse.requestExpired();
        }
    }

    private void regenerateRegistration(String portalId, UserRegistrationRequestRecord registrationRecord)
    {
        String portalRegistrationUrl = Base64Utils
                .encodeToString((portalConfigurationService.getPortalConfiguration().getUrl() + "/portal/login/register").getBytes());

        UserRegistrationRequest newRegistrationRequest = new UserRegistrationRequest();
        newRegistrationRequest.setEmailAddress(registrationRecord.getEmailAddress());
        newRegistrationRequest.setRegistrationUrl(portalRegistrationUrl);

        registrationDao.delete(registrationRecord);
        createRegistrationRecordAndSendEmail(portalId, newRegistrationRequest);
    }

    private boolean isRegistrationRecordActive(long registrationTime)
    {
        return registrationTime + REGISTRATION_EXPIRATION > System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#registerUser(java.lang.String,
     * java.lang.String, com.armedia.acm.portalgateway.model.PortalUser, java.lang.String)
     */
    @Override
    @Transactional
    public UserRegistrationResponse registerUser(String portalId, String registrationId, PortalUser user, String password)
            throws PortalUserServiceException
    {
        String key = user.getEmail();
        if (getPortalAcmUser(key) != null)
        {
            return UserRegistrationResponse.exists();
        }
        Optional<UserRegistrationRequestRecord> registrationRecord = registrationDao.findByRegistrationId(registrationId);
        Optional<PortalSARPerson> registeredPerson = portalPersonDao.findByEmail(key);

        if (isUserRejectedForPortal(portalId, registeredPerson))
        {
            return UserRegistrationResponse.rejected();
        }
        else if (registeredPerson.isPresent() && registeredPerson.get().getPortalRoles().containsKey(portalId))
        {
            return UserRegistrationResponse.exists();
        }
        else if (!registrationRecord.isPresent())
        {
            return UserRegistrationResponse.requestRequired();
        }
        else if (registrationRecord.get().getRegistrationTime() + REGISTRATION_EXPIRATION < System.currentTimeMillis())
        {
            return UserRegistrationResponse.requestExpired();
        }
        else if (!registrationRecord.get().getRegistrationKey().equals(registrationId))
        {
            return UserRegistrationResponse.invalid();
        }
        else
        {
            PortalSARPerson person = getPortalSARPerson(portalId, user, registeredPerson);
            createPortalUser(portalId, user, person, password);
            registrationDao.delete(registrationRecord.get());
            return UserRegistrationResponse.accepted();
        }

    }

    private boolean isUserRejectedForPortal(String portalId, Optional<PortalSARPerson> registeredPerson)
    {
        return registeredPerson.isPresent() && registeredPerson.get().getPortalRoles().containsKey(portalId)
                && registeredPerson.get().getPortalRoles().get(portalId).equals(PortalUser.REJECTED_USER);
    }

    public PortalSARPerson getPortalSARPerson(String portalId, PortalUser user, Optional<PortalSARPerson> registeredPerson)
    {
        PortalSARPerson person;
        if (registeredPerson.isPresent())
        {
            person = registeredPerson.get();
            person.getPortalRoles().put(portalId, PortalUser.PENDING_USER);
        }
        else
        {
            user.setRole(PortalUser.PENDING_USER);
            person = portalPersonFromPortalUser(portalId, user);
        }
        return person;
    }

    @Override
    public UserRegistrationResponse registerUserFromPerson(String portalId, Long personId, Long requestId)
            throws PortalUserServiceException
    {
        SARPerson person = (SARPerson) personDao.find(personId);

        if (person == null)
        {
            return UserRegistrationResponse.invalid();
        }
        String emailAddress = person.getDefaultEmail().getValue();
        Optional<PortalSARPerson> registeredPerson = portalPersonDao.findByEmail(emailAddress);
        AcmUser acmUser = getPortalAcmUser(emailAddress);

        if (acmUser != null)
        {
            if (!registeredPerson.isPresent())
            {
                synchronizePortalUser(portalId, acmUser);
            }
            else
            {
                changePersonIntoPortalSARPerson(portalId, person);
            }
            return UserRegistrationResponse.exists();
        }
        else if (isUserRejectedForPortal(portalId, registeredPerson))
        {
            return UserRegistrationResponse.rejected();
        }
        else if (registeredPerson.isPresent() && registeredPerson.get().getPortalRoles().containsKey(portalId))
        {
            return UserRegistrationResponse.exists();
        }
        else
        {
            PortalSARPerson portalPerson = changePersonIntoPortalSARPerson(portalId, person);

            PortalUser portalUser = portaluserFromPortalPerson(portalId, portalPerson);

            createPortalUser(portalId, portalUser, portalPerson, null);

            UserResetRequest resetRequest = createUserResetRequest(portalUser, portalId);
            requestPasswordResetForRequester(portalId, resetRequest, requestId);

            return UserRegistrationResponse.accepted();
        }
    }

    private PortalSARPerson changePersonIntoPortalSARPerson(String portalId, Person person)
    {
        personDao.updatePersonClass(person.getId(), PortalSARPerson.class.getName());

        PortalSARPerson portalPerson = portalPersonDao.find(person.getId());

        portalPerson.getPortalRoles().put(portalId, PortalUser.PENDING_USER);
        return portalPerson;
    }

    @Override
    public UserResetResponse requestPasswordResetForRequester(String portalId, UserResetRequest resetRequest, Long requestId)
            throws PortalUserServiceException
    {
        String templateName = "portalUserCreatedFromArkcasePasswordResetLink";
        String emailTitle = translationService.translate(NotificationConstants.NEW_PORTAL_USER_PASSWORD_RESET_REQUEST);
        return requestPasswordReset(portalId, resetRequest, templateName, emailTitle, requestId);
    }

    public UserResetRequest createUserResetRequest(PortalUser user, String portalId)
    {
        UserResetRequest resetRequest = new UserResetRequest();
        resetRequest.setEmailAddress(user.getEmail());
        String baseUrl = Base64Utils
                .encodeToString((new String(portalConfigurationService.getPortalConfiguration().getUrl() + "/portal/login/reset")).getBytes());
        resetRequest.setResetUrl(baseUrl);
        return resetRequest;
    }

    public void createPortalUser(String portalId, PortalUser user, PortalSARPerson person, String password)
            throws PortalUserServiceException
    {
        try
        {
            UserDTO userDto = userDTOFromPortalUser(user,
                    password != null ? new String(Base64Utils.decodeFromString(password), StandardCharsets.UTF_8) : null,
                    portalConfigurationService.getPortalConfiguration().getGroupName());
            portalPersonDao.save(person);
            AcmUser acmUser = ldapUserService.createLdapUser(userDto, directoryName);
            getRequestAssignmentService().addPortalUserAsParticipantToExistingRequests(acmUser, person);
            acmUserEventPublisher.getApplicationEventPublisher().publishEvent(new AcmLdapSyncEvent(acmUser.getUserId()));
        }
        catch (Exception e)
        {
            log.debug(e.getMessage(), e);
            throw new PortalUserServiceException(String.format("Couldn't create LDAP user for %s", user.getEmail()), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#authenticateUser(java.lang.String,
     * java.lang.String)
     */
    @Override
    public PortalUser authenticateUser(String portalId, String credentials) throws PortalUserServiceException
    {
        // TODO Auto-generated method stubPortalUser user = new PortalUser();
        String[] usernamePassword = new String(Base64Utils.decodeFromString(credentials), Charset.forName("UTF-8")).split(":");
        String username = usernamePassword[0];
        String password = usernamePassword[1];

        AcmUser portalAcmUser = getPortalAcmUser(username);

        if (portalAcmUser == null)
        {
            throw new PortalUserServiceException(String.format("User %s doesn't exist!", username));
        }
        else
        {
            AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);
            String ldapUserId = StringUtils.substringBeforeLast(portalAcmUser.getUserId(), "@");

            if (ldapSyncConfig != null)
            {
                if (ldapSyncConfig.getUserIdAttributeName().equals("uid"))
                {
                    ldapUserId = portalAcmUser.getUid();
                }
                else if (ldapSyncConfig.getUserIdAttributeName().equals("sAMAccountName"))
                {
                    ldapUserId = portalAcmUser.getsAMAccountName();
                }
            }

            SARLdapAuthenticationService SARLdapAuthenticationService = getSARLdapAuthenticationService(directoryName);
            if (SARLdapAuthenticationService == null)
            {
                log.debug("LDAP authentication service problem");
                throw new PortalUserServiceException(
                        String.format("LDAP authentication service problem!"));
            }
            if (!SARLdapAuthenticationService.authenticate(ldapUserId, password))
            {
                log.debug("User %s provided wrong password!", portalAcmUser.getMail());
                throw new PortalUserServiceException(
                        String.format("User %s provided wrong password!", portalAcmUser.getMail()));
            }
            else
            {
                Optional<PortalSARPerson> portalUser = portalPersonDao.findByEmail(username);
                if (!portalUser.isPresent())
                {
                    Optional<Person> person = personDao.findByEmail(username);
                    if (!person.isPresent())
                    {
                        portalUser = Optional.of(synchronizePortalUser(portalId, portalAcmUser));
                    }
                    else
                    {
                        portalUser = Optional.of(changePersonIntoPortalSARPerson(portalId, person.get()));
                    }
                }
                PortalUser portalUserAuthenticated = portaluserFromPortalPerson(portalId, portalUser.get());
                portalUserAuthenticated.setAcmUserId(portalAcmUser.getUserId());
                return portalUserAuthenticated;
            }
        }
    }

    private PortalSARPerson synchronizePortalUser(String portalId, AcmUser acmUser) throws PortalUserServiceException
    {
        try
        {
            PortalSARPerson portalSARPerson = portalSARPersonFromAcmUser(acmUser);
            portalSARPerson.getPortalRoles().put(portalId, PortalUser.PENDING_USER);
            return portalPersonDao.save(portalSARPerson);
        }
        catch (Exception e)
        {
            log.error("Error synchronizing Portal User [{}]", acmUser.getMail(), e);
            throw new PortalUserServiceException(String.format("Error synchronizing Portal User %s", acmUser.getMail()));
        }
    }

    private PortalSARPerson portalSARPersonFromAcmUser(AcmUser acmUser)
    {
        PortalSARPerson person = new PortalSARPerson();
        person.setGivenName(acmUser.getFirstName());
        person.setFamilyName(acmUser.getLastName());
        person.setTitle(acmUser.getTitle());
        person.setCompany(acmUser.getCompany());
        PostalAddress address = new PostalAddress();
        address.setType("Business");
        person.getAddresses().add(address);
        person.setDefaultAddress(address);
        if (acmUser.getCompany() != null && !acmUser.getCompany().isEmpty())
        {
            PostalAddress orgAddress = new PostalAddress();
            orgAddress.setType("Business");
            Organization organization = new Organization();
            organization.setOrganizationValue(acmUser.getCompany());
            organization.setOrganizationType("Corporation");
            organization.getAddresses().add(orgAddress);
            person.getOrganizations().add(organization);
        }

        List<ContactMethod> contactMethods = new ArrayList<>();
        person.setContactMethods(contactMethods);
        if (acmUser.getMail() != null && !acmUser.getMail().isEmpty())
        {
            ContactMethod email = buildContactMethod("email", acmUser.getMail());
            person.getContactMethods().add(email);
            person.setDefaultEmail(email);
        }

        return person;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#requestPasswordReset(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserResetRequest)
     */
    @Override
    public UserResetResponse requestPasswordReset(String portalId, UserResetRequest resetRequest) throws PortalUserServiceException
    {
        String templateName = "portalPasswordResetRequestLink";
        String emailTitle = translationService.translate(NotificationConstants.PASSWORD_RESET_REQUEST);
        return requestPasswordReset(portalId, resetRequest, templateName, emailTitle, null);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#regeneratePasswordReset(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserResetRequest)
     */
    @Override
    public UserResetResponse regeneratePasswordReset(String portalId, UserResetRequest resetRequest) throws PortalUserServiceException
    {
        Optional<UserResetRequestRecord> resetRecord = resetDao.findByEmail(resetRequest.getEmailAddress());
        if (resetRecord.isPresent())
        {
            resetDao.delete(resetRecord.get());
            return requestPasswordReset(portalId, resetRequest);
        }
        else
        {
            return UserResetResponse.reqistrationRequired();
        }
    }

    @Override
    public UserResetResponse requestPasswordReset(String portalId, UserResetRequest resetRequest, String templateName, String emailTitle, Long requestId)
            throws PortalUserServiceException
    {
        AcmUser acmPortalUser = getPortalAcmUser(resetRequest.getEmailAddress());
        if (acmPortalUser == null)
        {
            return UserResetResponse.reqistrationRequired();
        }
        else
        {
            Optional<UserResetRequestRecord> resetRecord = resetDao.findByEmail(resetRequest.getEmailAddress());
            if (resetRecord.isPresent() && isRegistrationRecordActive(resetRecord.get().getRequestTime()))
            {
                return UserResetResponse.pending();
            }
            else
            {
                UserResetRequestRecord record = resetRecord.isPresent() ? resetRecord.get() : new UserResetRequestRecord();

                String resetKey = UUID.randomUUID().toString();
                record.setResetKey(resetKey);
                record.setRequestTime(System.currentTimeMillis());
                record.setEmailAddress(resetRequest.getEmailAddress());

                resetDao.save(record);

                String resetLink = new String(Base64Utils.decodeFromString(resetRequest.getResetUrl()), Charset.forName("UTF-8")) + "/"
                        + resetKey;

                Notification notification = new Notification();
                notification.setTemplateModelName(templateName);
                notification.setTitle(emailTitle);
                notification.setCreator(resetRequest.getEmailAddress());
                notification.setNote(resetLink);
                notification.setEmailAddresses(resetRequest.getEmailAddress());
                notification.setUser(SecurityContextHolder.getContext().getAuthentication().getName());
                notification.setParentType("USER");

                getNotificationDao().save(notification);

                return UserResetResponse.requestAccepted();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#checkPasswordResetStatus(java.lang.String,
     * java.lang.String)
     */
    @Override
    public UserResetResponse checkPasswordResetStatus(String portalId, String resetId)
    {
        Optional<UserResetRequestRecord> resetSearch = resetDao.findByResetId(resetId);
        if (!resetSearch.isPresent())
        {
            return UserResetResponse.requestRequired();
        }
        else
        {
            if (isRegistrationRecordActive(resetSearch.get().getRequestTime()))
            {
                return UserResetResponse.pending();
            }
            else
            {
                return UserResetResponse.requestExpired();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#resetPassword(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public UserResetResponse resetPassword(String portalId, String resetId, String password) throws PortalUserServiceException
    {
        Optional<UserResetRequestRecord> resetSearch = resetDao.findByResetId(resetId);
        if (!resetSearch.isPresent())
        {
            // TODO send invalid request instead
            return UserResetResponse.requestRequired();
        }
        else
        {
            UserResetRequestRecord reset = resetSearch.get();
            if (!reset.getResetKey().equals(resetId))
            {
                return UserResetResponse.invalid();
            }
            else if (isRegistrationRecordActive(reset.getRequestTime()))
            {
                AcmUser acmPortalUser = getPortalAcmUser(reset.getEmailAddress());
                if (acmPortalUser == null)
                {
                    throw new PortalUserServiceException(String.format("User %s doesn't exist!", reset.getEmailAddress()));
                }

                SARLdapAuthenticationService SARLdapAuthenticationService = getSARLdapAuthenticationService(directoryName);
                if (SARLdapAuthenticationService == null)
                {
                    log.debug("LDAP authentication service problem");
                    throw new PortalUserServiceException(
                            String.format("LDAP authentication service problem!"));
                }
                try
                {
                    SARLdapAuthenticationService.resetPortalUserPassword(acmPortalUser.getUserId(), password);
                }
                catch (AcmUserActionFailedException e)
                {
                    log.debug("Couldn't update password for LDAP user %s.", acmPortalUser.getMail());
                    throw new PortalUserServiceException(
                            String.format("Couldn't update password for LDAP user %s.", acmPortalUser.getMail()), e);
                }

                resetDao.delete(reset);
                return UserResetResponse.passwordUpdated();
            }
            else
            {
                return UserResetResponse.requestExpired();
            }
        }
    }

    @Override
    public UserResetResponse changePassword(String portalUserEmail, String portalUserId, String acmSystemUserId, PortalUserCredentials portalUserCredentials)
            throws PortalUserServiceException
    {

        SARLdapAuthenticationService SARLdapAuthenticationService = getSARLdapAuthenticationService(directoryName);
        if (SARLdapAuthenticationService == null)
        {
            log.debug("LDAP authentication service problem");
            throw new PortalUserServiceException(
                    String.format("LDAP authentication service problem!"));
        }
        try
        {
            SARLdapAuthenticationService.getLdapAuthenticateService().changeUserPassword(portalUserId, portalUserCredentials.getPassword(),
                    portalUserCredentials.getNewPassword());
        }

        catch (AcmUserActionFailedException e)
        {
            log.debug(String.format("Couldn't update password for LDAP user %s. Using configured system user %s.", portalUserId, acmSystemUserId));
            throw new PortalUserServiceException(String.format("Couldn't update password for user %s.", portalUserEmail), e);
        }
        catch (AuthenticationException e)
        {
            log.debug(String.format("Failed to authenticate! Wrong password for LDAP user %s. Using configured system user %s.", portalUserId, acmSystemUserId));
            return UserResetResponse.invalidCredentials();
        }
        catch (InvalidAttributeValueException e)
        {
            log.debug(String.format("Password policy error for LDAP user %s. Using configured system user %s.", portalUserId, acmSystemUserId));
            throw new PortalUserServiceException(String.format("Password fails quality checking policy for user %s.", portalUserEmail), e);
        }
        catch (Exception e)
        {
            throw new PortalUserServiceException("Unknown error occurred", e);
        }

        return UserResetResponse.passwordUpdated();
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#updateUser(java.lang.String,
     * com.armedia.acm.portalgateway.model.PortalUser)
     */
    @Override
    public PortalUser updateUser(String portalId, PortalUser user) throws PortalUserServiceException
    {
        Person person = getPersonDao().find(Long.valueOf(user.getPortalUserId()));

        person.setGivenName(user.getFirstName());
        person.setMiddleName(user.getMiddleName());
        person.setFamilyName(user.getLastName());
        person.setTitle(user.getPrefix());
        ((PortalSARPerson) person).setPosition(user.getPosition());
        person.getAddresses().get(0).setCountry(user.getCountry());
        person.getAddresses().get(0).setType(user.getAddressType());
        person.getAddresses().get(0).setCity(user.getCity());
        person.getAddresses().get(0).setState(user.getState());
        person.getAddresses().get(0).setStreetAddress(user.getAddress1());
        person.getAddresses().get(0).setStreetAddress2(user.getAddress2());
        person.getAddresses().get(0).setZip(user.getZipCode());
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
        {
            if (person.getContactMethods() != null && !person.getContactMethods().isEmpty())
            {
                ContactMethod phoneContact = person.getDefaultPhone();
                if (phoneContact != null)
                {
                    phoneContact.setValue(user.getPhoneNumber());
                }
                else
                {
                    ContactMethod newPhoneContact = buildContactMethod("phone", user.getPhoneNumber());
                    person.getContactMethods().add(newPhoneContact);
                    person.setDefaultPhone(newPhoneContact);
                }
            }
            else
            {
                List<ContactMethod> contactMethods = new ArrayList<>();
                contactMethods.add(buildContactMethod("phone", user.getPhoneNumber()));
                person.setContactMethods(contactMethods);
            }
        }
        else
        {
            person.getContactMethods().remove(person.getDefaultPhone());
            Optional<ContactMethod> otherPhoneContact = person.getContactMethods().stream()
                    .filter(cm -> cm.getType().equalsIgnoreCase("Phone"))
                    .findFirst();
            if (otherPhoneContact.isPresent())
            {
                person.setDefaultPhone(otherPhoneContact.get());
            }
            else
            {
                person.setDefaultPhone(null);
            }
        }

        for (PersonOrganizationAssociation poa : person.getOrganizationAssociations())
        {
            poa.setDefaultOrganization(false);
        }

        if (user.getOrganization() != null && !user.getOrganization().isEmpty())
        {
            findOrCreateOrganizationAndPersonOrganizationAssociation(person, user.getOrganization());
        }

        Person saved = personDao.save(person);

        return portaluserFromPortalPerson(portalId, (PortalSARPerson) saved);

    }

    @Override
    public PortalUser retrieveUser(String portalUserId, String portalId)
    {
        Person person = getPersonDao().find(Long.valueOf(portalUserId));
        return portaluserFromPortalPerson(portalUserId, (PortalSARPerson) person);
    }

    public Person findOrCreateOrganizationAndPersonOrganizationAssociation(Person person, String organizationName)
    {
        Organization organization = checkOrganizationByNameOrCreateNew(person.getGivenName(), person.getFamilyName(), organizationName);
        boolean organizationExists = false;

        for (Organization org : person.getOrganizations())
        {
            if (org.getId().equals(organization.getId()))
            {
                organizationExists = true;
                break;
            }
        }
        if (person.getOrganizationAssociations().isEmpty() || !organizationExists)
        {
            PersonOrganizationAssociation personOrganizationAssociation = addPersonOrganizationAssociation((PortalSARPerson) person,
                    organization);
            personOrganizationAssociation.setDefaultOrganization(true);
            person.getOrganizations().add(organization);
            person.getOrganizationAssociations().add(personOrganizationAssociation);
            person.setDefaultOrganization(personOrganizationAssociation);
        }
        else
        {
            List<PersonOrganizationAssociation> poas = person.getOrganizationAssociations();
            for (PersonOrganizationAssociation poa : poas)
            {
                if (poa.getOrganization().getOrganizationValue().equalsIgnoreCase(organization.getOrganizationValue()))
                {
                    poa.setDefaultOrganization(true);
                    person.setDefaultOrganization(poa);
                    break;
                }
            }
        }
        return person;
    }

    private Organization checkOrganizationByNameOrCreateNew(String firstName, String familyName, String organizationName)
    {
        List<Organization> organizationList = getOrganizationDao().findOrganizationsByName(organizationName);

        if (organizationList == null)
        {
            Organization newOrganization = new Organization();
            newOrganization.setOrganizationValue(organizationName);
            newOrganization.setOrganizationType("Corporation");
            return newOrganization;
        }
        else if (organizationList.size() == 1)
        {
            return organizationList.get(0);
        }
        else
        {
            for (Organization existingOrganization : organizationList)
            {
                for (PersonOrganizationAssociation poa : existingOrganization.getPersonAssociations())
                {
                    if (poa.getPerson().getGivenName().toLowerCase().startsWith(firstName.toLowerCase())
                            && poa.getPerson().getFamilyName().equalsIgnoreCase(familyName))
                    {
                        return existingOrganization;
                    }
                }
            }
            Organization newOrganization = new Organization();
            newOrganization.setOrganizationValue(organizationName);
            newOrganization.setOrganizationType("Corporation");
            return newOrganization;

        }
    }

    private PersonOrganizationAssociation addPersonOrganizationAssociation(PortalSARPerson person, Organization organization)
    {
        PersonOrganizationAssociation personOrganizationAssociation = new PersonOrganizationAssociation();
        personOrganizationAssociation.setOrganization(organization);
        personOrganizationAssociation.setDefaultOrganization(false);
        personOrganizationAssociation.setPerson(person);
        personOrganizationAssociation.setPersonToOrganizationAssociationType("owner");
        personOrganizationAssociation.setOrganizationToPersonAssociationType("owned");
        if (person.getOrganizationAssociations().isEmpty())
        {
            personOrganizationAssociation.setDefaultOrganization(true);
        }

        return personOrganizationAssociation;
    }

    /**
     * @param user
     * @param group
     * @param password
     * @return
     */
    private UserDTO userDTOFromPortalUser(PortalUser user, String password, String group)
    {
        UserDTO userDTO = new UserDTO();

        userDTO.setUserId(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setMail(user.getEmail());
        userDTO.setPassword(password);
        // userDTO.setCurrentPassword(password);
        userDTO.setGroupNames(Arrays.asList(group));

        return userDTO;
    }

    /**
     * @param portalId
     * @param person
     * @return
     */
    private PortalUser portaluserFromPortalPerson(String portalId, PortalSARPerson person)
    {
        PortalUser user = new PortalUser();

        user.setPortalUserId(person.getId().toString());
        user.setFirstName(person.getGivenName());
        user.setMiddleName(person.getMiddleName());
        user.setLastName(person.getFamilyName());
        user.setPrefix(person.getTitle());
        user.setPosition(person.getPosition());
        ContactMethod phoneContact = person.getDefaultPhone();
        if (phoneContact != null && phoneContact.getValue() != null && !phoneContact.getValue().isEmpty())
        {
            user.setPhoneNumber(phoneContact.getValue());
        }
        PostalAddress address = person.getDefaultAddress();
        if (address == null)
        {
            if (person.getAddresses() != null && !person.getAddresses().isEmpty())
            {
                address = person.getAddresses().get(0);
            }
        }
        if (address != null)
        {
            user.setCity(address.getCity());
            user.setCountry(address.getCountry());
            user.setAddressType(address.getType());
            user.setState(address.getState());
            user.setAddress1(address.getStreetAddress());
            user.setAddress2(address.getStreetAddress2());
            user.setZipCode(address.getZip());
        }

        if (person.getDefaultOrganization() != null)
        {
            user.setOrganization(person.getDefaultOrganization().getOrganization().getOrganizationValue());
        }
        user.setEmail(person.getDefaultEmail().getValue());

        if (StringUtils.isBlank(person.getPortalRoles().get(portalId)))
        {
            user.setRole(PortalUser.PENDING_USER);
        }
        else
        {
            user.setRole(person.getPortalRoles().get(portalId));
        }

        if (person.getDefaultPicture() != null)
        {
            user.setEcmFileId(person.getDefaultPicture().getFileId());
        }
        else
        {
            user.setEcmFileId(null);
        }
        return user;
    }

    /**
     * @param portalId
     * @param user
     * @return
     */
    private PortalSARPerson portalPersonFromPortalUser(String portalId, PortalUser user)
    {
        PortalSARPerson person = new PortalSARPerson();

        String portalUserId = user.getPortalUserId();
        if (portalUserId != null && !portalUserId.isEmpty())
        {
            person.setId(Long.valueOf(portalUserId));
        }

        person.setGivenName(user.getFirstName());
        person.setMiddleName(user.getMiddleName());
        person.setFamilyName(user.getLastName());
        person.setTitle(user.getPrefix());
        person.setPosition(user.getPosition());
        PostalAddress address = new PostalAddress();
        address.setCity(user.getCity());
        address.setCountry(user.getCountry());
        address.setState(user.getState());
        address.setStreetAddress(user.getAddress1());
        address.setStreetAddress2(user.getAddress2());
        address.setZip(user.getZipCode());
        address.setType(user.getAddressType());
        person.getAddresses().add(address);
        person.setDefaultAddress(address);

        PostalAddress orgAddress = new PostalAddress();
        orgAddress.setType("Business");
        if (user.getOrganization() != null)
        {
            findOrCreateOrganizationAndPersonOrganizationAssociation(person, user.getOrganization());
            person.getOrganizations().get(0).getAddresses().add(orgAddress);
        }

        List<ContactMethod> contactMethods = new ArrayList<>();
        person.setContactMethods(contactMethods);

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
        {
            ContactMethod phone = buildContactMethod("phone", user.getPhoneNumber());
            person.getContactMethods().add(phone);
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty())
        {
            ContactMethod email = buildContactMethod("email", user.getEmail());
            person.getContactMethods().add(email);
            person.setDefaultEmail(email);
        }

        person.getPortalRoles().put(portalId, user.getRole());

        return person;
    }

    private ContactMethod buildContactMethod(String type, String value)
    {
        ContactMethod contactMethod = new ContactMethod();
        contactMethod.setType(type);
        contactMethod.setValue(value);
        return contactMethod;
    }

    private void sendEmail(String email, String subject, String template, String requestUrl, String requestKey)
            throws PortalUserServiceException
    {
        EmailBuilder<String> emailBuilder = getEmailBuilder(subject);
        EmailBodyBuilder<String> emailBodyBuilder = getEmailBodyBuilder(template,
                new String(Base64Utils.decodeFromString(requestUrl), Charset.forName("UTF-8")),
                requestKey);
        sendEmail(email, emailBuilder, emailBodyBuilder);
    }

    private EmailBuilder<String> getEmailBuilder(String subject)
    {
        return (emailData, messageProps) -> {
            messageProps.put("to", emailData);
            messageProps.put("subject", subject);
        };
    }

    private EmailBodyBuilder<String> getEmailBodyBuilder(String template, String registrationRequest, String registrationKey)
    {
        return (emailData) -> {
            return String.format(template, registrationRequest, registrationKey);
        };
    }

    /**
     * @param email
     * @throws PortalUserServiceException
     *
     */
    private void sendEmail(String email, EmailBuilder<String> emailBuilder, EmailBodyBuilder<String> emailBodyBuilder)
            throws PortalUserServiceException
    {
        try
        {
            emailSenderService.sendPlainEmail(Stream.of(email), emailBuilder, emailBodyBuilder);
        }
        catch (Exception e)
        {
            throw new PortalUserServiceException("Failed sending email to: [" + email + "].", e);
        }
    }

    private AcmLdapSyncConfig getLdapSyncConfig(String directoryName)
    {
        return acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).get(String.format("%s_sync", directoryName));
    }

    private SARLdapAuthenticationService getSARLdapAuthenticationService(String directoryName)
    {
        LdapAuthenticateService ldapAuthenticateService = acmContextHolder.getAllBeansOfType(LdapAuthenticateService.class)
                .get(String.format("%s_ldapAuthenticateService", directoryName));
        return ldapAuthenticateService != null ? SARLdapAuthenticationService.getInstance(ldapAuthenticateService) : null;
    }

    private AcmUser getPortalAcmUser(String username)
    {
        AcmUser acmUser = null;
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);
        if (ldapSyncConfig != null)
        {
            acmUser = userDao.findByPrefixAndEmailAddressAndValidState(ldapSyncConfig.getUserPrefix(), username);
        }
        return acmUser;
    }

    /**
     * @param emailSenderService
     *            the emailSenderService to set
     */
    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    /**
     * @param registrationDao
     *            the registrationDao to set
     */
    public void setRegistrationDao(UserRegistrationRequestDao registrationDao)
    {
        this.registrationDao = registrationDao;
    }

    /**
     * @param resetDao
     *            the resetDao to set
     */
    public void setResetDao(UserResetRequestDao resetDao)
    {
        this.resetDao = resetDao;
    }

    /**
     * @param portalPersonDao
     *            the portalPersonDao to set
     */
    public void setPortalPersonDao(PortalSARPersonDao portalPersonDao)
    {
        this.portalPersonDao = portalPersonDao;
    }

    /**
     * @param ldapUserService
     *            the ldapUserService to set
     */
    public void setLdapUserService(LdapUserService ldapUserService)
    {
        this.ldapUserService = ldapUserService;
    }

    /**
     * @param directoryName
     *            the directoryName to set
     */
    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /**
     * @param acmContextHolder
     *            the acmContextHolder to set
     */
    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }

    public void setAcmUserEventPublisher(AcmUserEventPublisher acmUserEventPublisher)
    {
        this.acmUserEventPublisher = acmUserEventPublisher;
    }

    public OrganizationDao getOrganizationDao()
    {
        return organizationDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao)
    {
        this.organizationDao = organizationDao;
    }

    public RequestAssignmentService getRequestAssignmentService()
    {
        return requestAssignmentService;
    }

    public void setRequestAssignmentService(RequestAssignmentService requestAssignmentService)
    {
        this.requestAssignmentService = requestAssignmentService;
    }

    public PortalConfigurationService getPortalConfigurationService()
    {
        return portalConfigurationService;
    }

    public void setPortalConfigurationService(PortalConfigurationService portalConfigurationService)
    {
        this.portalConfigurationService = portalConfigurationService;
    }

    public CorrespondenceTemplateManager getTemplateManager()
    {
        return templateManager;
    }

    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
    }
}

package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.portalgateway.model.PortalInfo;
import com.armedia.acm.portalgateway.model.PortalUser;
import com.armedia.acm.portalgateway.model.PortalUserCredentials;
import com.armedia.acm.portalgateway.model.UserRegistrationRequest;
import com.armedia.acm.portalgateway.model.UserRegistrationResponse;
import com.armedia.acm.portalgateway.model.UserResetRequest;
import com.armedia.acm.portalgateway.model.UserResetResponse;
import com.armedia.acm.portalgateway.service.PortalInfoDAO;
import com.armedia.acm.portalgateway.service.PortalUserServiceException;
import com.armedia.acm.portalgateway.service.PortalUserServiceProvider;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.UserDTO;
import com.armedia.acm.services.users.service.ldap.LdapUserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Base64Utils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import gov.foia.dao.PortalFOIAPersonDao;
import gov.foia.dao.UserRegistrationRequestDao;
import gov.foia.dao.UserResetRequestDao;
import gov.foia.model.PortalFOIAPerson;
import gov.foia.model.UserRegistrationRequestRecord;
import gov.foia.model.UserResetRequestRecord;

import javax.naming.AuthenticationException;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 12, 2018
 *
 */
public class FOIAPortalUserServiceProvider implements PortalUserServiceProvider
{

    public static final int REGISTRATION_EXPIRATION = 24 * 60 * 60 * 1000;

    private AcmEmailSenderService emailSenderService;

    @Value("${foia.portalserviceprovider.registrationrequest.template}")
    private String registrationRequestEmailTemplate;

    @Value("${foia.portalserviceprovider.passwordresetrequest.template}")
    private String passwordResetRequestEmailTemplate;

    private UserRegistrationRequestDao registrationDao;

    private UserResetRequestDao resetDao;

    private PortalFOIAPersonDao portalPersonDao;

    private LdapUserService ldapUserService;

    private PortalInfoDAO portalInfoDAO;

    private PersonDao personDao;

    @Value("${foia.portalserviceprovider.directory.name}")
    private String directoryName;

    private FOIALdapAuthenticationService ldapAuthenticateService;

    private NotificationDao notificationDao;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#requestRegistration(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserRegistrationRequest)
     */
    @Override
    public UserRegistrationResponse requestRegistration(String portalId, UserRegistrationRequest registrationRequest)
            throws PortalUserServiceException
    {
        Optional<UserRegistrationRequestRecord> registrationRecord = registrationDao.findByEmail(registrationRequest.getEmailAddress(),
                portalId);

        if (registrationRecord.isPresent()
                && registrationRecord.get().getRegistrationTime() + REGISTRATION_EXPIRATION > System.currentTimeMillis())
        {
            return UserRegistrationResponse.pending(registrationRecord.get().getEmailAddress());
        }
        else
        {
            Optional<PortalFOIAPerson> registeredPerson = portalPersonDao.findByEmail(registrationRequest.getEmailAddress());

            if (!registeredPerson.isPresent() || !registeredPerson.get().getPortalRoles().containsKey(portalId))
            {
                UserRegistrationRequestRecord record = new UserRegistrationRequestRecord();

                String registrationKey = UUID.randomUUID().toString();
                record.setRegistrationKey(registrationKey);
                record.setRegistrationTime(System.currentTimeMillis());
                record.setEmailAddress(registrationRequest.getEmailAddress());
                record.setPortalId(portalId);

                registrationDao.save(record);

                String registrationLink = new String(Base64Utils.decodeFromString(registrationRequest.getRegistrationUrl()), Charset.forName("UTF-8")) + "/" + registrationKey;

                Notification notification = new Notification();
                notification.setTemplateModelName("portalRequestRegistrationLink");
                notification.setTitle("Request registration");
                notification.setCreator(registrationRequest.getEmailAddress());
                notification.setNote(registrationLink);
                notification.setEmailAddresses(registrationRequest.getEmailAddress());
                notification.setUser(SecurityContextHolder.getContext().getAuthentication().getName());

                getNotificationDao().save(notification);

                return UserRegistrationResponse.requestAccepted();
            }
            else if (registeredPerson.get().getPortalRoles().get(portalId).equals(PortalUser.REJECTED_USER))
            {
                return UserRegistrationResponse.rejected();
            }
            else
            {
                return UserRegistrationResponse.exists();
            }
        }
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
        String emailAddress = registrationRecord.get().getEmailAddress();
        if (!registrationRecord.isPresent())
        {
            return UserRegistrationResponse.requestRequired();
        }
        else
        {
            if (registrationRecord.get().getRegistrationTime() + REGISTRATION_EXPIRATION > System.currentTimeMillis())
            {
                return UserRegistrationResponse.pending(emailAddress);
            }
            else
            {
                return UserRegistrationResponse.requestExpired();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#registerUser(java.lang.String,
     * java.lang.String, com.armedia.acm.portalgateway.model.PortalUser, java.lang.String)
     */
    @Override
    public UserRegistrationResponse registerUser(String portalId, String registrationId, PortalUser user, String password)
            throws PortalUserServiceException
    {
        String key = user.getEmail();
        Optional<UserRegistrationRequestRecord> registrationRecord = registrationDao.findByRegistrationId(registrationId);
        Optional<PortalFOIAPerson> registeredPedrson = portalPersonDao.findByEmail(key);

        if (registeredPedrson.isPresent() && registeredPedrson.get().getPortalRoles().containsKey(portalId))
        {
            if (registeredPedrson.get().getPortalRoles().get(portalId).equals(PortalUser.REJECTED_USER))
            {
                return UserRegistrationResponse.rejected();
            }
            else
            {
                return UserRegistrationResponse.exists();
            }
        }
        else
        {
            if (!registrationRecord.isPresent())
            {
                return UserRegistrationResponse.requestRequired();
            }
            else if (registrationRecord.get().getRegistrationTime() + REGISTRATION_EXPIRATION < System.currentTimeMillis())
            {
                return UserRegistrationResponse.requestExpired();
            }
            else
            {
                UserRegistrationRequestRecord record = registrationRecord.get();

                if (!record.getRegistrationKey().equals(registrationId))
                {
                    return UserRegistrationResponse.invalid();
                }
                else
                {
                    PortalFOIAPerson person;
                    if (registeredPedrson.isPresent())
                    {
                        person = registeredPedrson.get();
                        person.getPortalRoles().put(portalId, PortalUser.PENDING_USER);
                    }
                    else
                    {
                        user.setRole(PortalUser.PENDING_USER);
                        person = portalPersonFromPortalUser(portalId, user);
                    }

                    try
                    {
                        PortalInfo portalInfo = portalInfoDAO.findByPortalId(portalId);
                        UserDTO userDto = userDTOFromPortalUser(user,
                                new String(Base64Utils.decodeFromString(password), Charset.forName("UTF-8")),
                                portalInfo.getGroup().getName());
                        portalPersonDao.save(person);
                        ldapUserService.createLdapUser(userDto, directoryName);
                    }
                    catch (AcmUserActionFailedException | AcmLdapActionFailedException e)
                    {
                        throw new PortalUserServiceException(String.format("Couldn't create LDAP user for %s", user.getEmail()), e);
                    }

                    registrationDao.delete(record);
                    return UserRegistrationResponse.accepted();
                }
            }
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

        Optional<PortalFOIAPerson> portalUser = portalPersonDao.findByEmail(username);
        if (!portalUser.isPresent())
        {
            throw new PortalUserServiceException(String.format("User %s doesn't exist!", username));
        }
        else if (!ldapAuthenticateService.authenticate(username, password))
        {
            throw new PortalUserServiceException(String.format("User %s provided wrong password!", username));
        }
        else
        {
            return portaluserFromPortalPerson(portalId, portalUser.get());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#requestPasswordReset(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserResetRequest)
     */
    @Override
    public UserResetResponse requestPasswordReset(String portalId, UserResetRequest resetRequest) throws PortalUserServiceException
    {
        Optional<UserResetRequestRecord> resetRecord = resetDao.findByEmail(resetRequest.getEmailAddress());
        if (!isRegisteredUser(resetRequest.getEmailAddress()))
        {
            return UserResetResponse.reqistrationRequired();
        }
        else if (resetRecord.isPresent() && resetRecord.get().getRequestTime() + REGISTRATION_EXPIRATION > System.currentTimeMillis())
        {
            return UserResetResponse.pending();
        }
        else
        {
            UserResetRequestRecord record = new UserResetRequestRecord();

            String resetKey = UUID.randomUUID().toString();
            record.setResetKey(resetKey);
            record.setRequestTime(System.currentTimeMillis());
            record.setEmailAddress(resetRequest.getEmailAddress());

            resetDao.save(record);

            String resetLink = new String(Base64Utils.decodeFromString(resetRequest.getResetUrl()), Charset.forName("UTF-8")) + "/" + resetKey;

            Notification notification = new Notification();
            notification.setTemplateModelName("portalPasswordResetRequestLink");
            notification.setTitle("Request password reset");
            notification.setCreator(resetRequest.getEmailAddress());
            notification.setNote(resetLink);
            notification.setEmailAddresses(resetRequest.getEmailAddress());
            notification.setUser(SecurityContextHolder.getContext().getAuthentication().getName());

            getNotificationDao().save(notification);

            return UserResetResponse.requestAccepted();
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
            resetSearch.get();
            if (resetSearch.get().getRequestTime() + REGISTRATION_EXPIRATION > System.currentTimeMillis())
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
            else if (reset.getRequestTime() + REGISTRATION_EXPIRATION > System.currentTimeMillis())
            {
                String key = reset.getEmailAddress();
                Optional<PortalFOIAPerson> registeredPedrson = portalPersonDao.findByEmail(key);

                try
                {
                    ldapAuthenticateService.resetPortalUserPassword(registeredPedrson.get().getDefaultEmail().getValue(), password);
                }
                catch (AcmUserActionFailedException e)
                {
                    throw new PortalUserServiceException(String.format("Couldn't update password for LDAP user %s.", key), e);
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
    public UserResetResponse changePassword(String portalId, String userId, PortalUserCredentials portalUserCredentials) throws PortalUserServiceException
    {

             try
                {
                    ldapAuthenticateService.changeUserPassword(userId, portalUserCredentials.getPassword(), portalUserCredentials.getNewPassword());
                }

                catch (AcmUserActionFailedException e)
                {
                    if(e.getCause() instanceof AuthenticationException)
                    {
                        return UserResetResponse.invalidCredentials();
                    }
                   throw new PortalUserServiceException(String.format("Couldn't update password for LDAP user %s.", userId), e);

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
        // TODO Auto-generated method stub
        Person person = getPersonDao().find(Long.valueOf(user.getPortalUserId()));

        person.setGivenName(user.getFirstName());
        person.setMiddleName(user.getMiddleName());
        person.setFamilyName(user.getLastName());
        person.setTitle(user.getPrefix());
        ((PortalFOIAPerson) person).setPosition(user.getPosition());
        person.getOrganizations().get(0).setOrganizationValue(user.getOrganization());


        person.getAddresses().get(0).setCity(user.getCity());
        person.getAddresses().get(0).setState(user.getState());
        person.getAddresses().get(0).setStreetAddress(user.getAddress1());
        person.getAddresses().get(0).setStreetAddress2(user.getAddress2());
        person.getAddresses().get(0).setZip(user.getZipCode());
        person.getContactMethods().stream().filter(cm -> cm.getType().equals("Phone")).findFirst().get().setValue(user.getPhoneNumber());

        personDao.save(person);

        return portaluserFromPortalPerson(portalId, (PortalFOIAPerson) person);

    }

    @Override
    public PortalUser retrieveUser(String portalUserId, String portalId)
    {
        Person person = getPersonDao().find(Long.valueOf(portalUserId));
        return portaluserFromPortalPerson(portalUserId, (PortalFOIAPerson) person);
    }

    /**
     * @param user
     * @param group
     * @param password2
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
     * @param emailAddress
     * @return
     */
    private boolean isRegisteredUser(String emailAddress)
    {
        Optional<PortalFOIAPerson> registeredPerson = portalPersonDao.findByEmail(emailAddress);
        return registeredPerson.isPresent();
    }

    /**
     * @param portalId
     * @param portalFOIAPerson
     * @return
     */
    private PortalUser portaluserFromPortalPerson(String portalId, PortalFOIAPerson person)
    {
        PortalUser user = new PortalUser();

        user.setPortalUserId(person.getId().toString());
        user.setFirstName(person.getGivenName());
        user.setMiddleName(person.getMiddleName());
        user.setLastName(person.getFamilyName());
        user.setPrefix(person.getTitle());
        user.setPosition(person.getPosition());
        user.setPhoneNumber(person.getContactMethods().stream().filter(cm -> cm.getType().equals("Phone")).findFirst().get().getValue());

        PostalAddress address = person.getDefaultAddress();
        user.setCity(address.getCity());
        user.setCountry(address.getCountry());
        user.setState(address.getState());
        user.setAddress1(address.getStreetAddress());
        user.setAddress2(address.getStreetAddress2());
        user.setZipCode(address.getZip());
        user.setOrganization(person.getOrganizations().get(0).getOrganizationValue());
        user.setEmail(person.getDefaultEmail().getValue());

        user.setRole(person.getPortalRoles().get(portalId));

        if(person.getDefaultPicture() != null)
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
    private PortalFOIAPerson portalPersonFromPortalUser(String portalId, PortalUser user)
    {
        PortalFOIAPerson person = new PortalFOIAPerson();

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

        Organization organization = new Organization();
        organization.setOrganizationValue(user.getOrganization() != null ? user.getOrganization() : " ");
        organization.setOrganizationType("Corporation");
        person.getOrganizations().add(organization);

        PostalAddress address = new PostalAddress();
        address.setCity(user.getCity());
        address.setCountry(user.getCountry());
        address.setState(user.getState());
        address.setStreetAddress(user.getAddress1());
        address.setStreetAddress2(user.getAddress2());
        address.setZip(user.getZipCode());
        address.setType("Business");
        person.getAddresses().add(address);
        person.setDefaultAddress(address);

        PostalAddress orgAddress = new PostalAddress();
        orgAddress.setType("Business");
        organization.getAddresses().add(orgAddress);

        // the UI expects the contact methods in this order: Phone, Fax, Email
        ContactMethod phone = buildContactMethod("Phone", user.getPhoneNumber());
        person.getContactMethods().add(phone);
        ContactMethod fax = buildContactMethod("Fax", null);
        person.getContactMethods().add(fax);
        ContactMethod email = buildContactMethod("Email", user.getEmail());
        person.getContactMethods().add(email);
        person.setDefaultEmail(email);

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

    /**
     * @param emailSenderService
     *            the emailSenderService to set
     */
    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    /**
     * @param registrationRequestEmailTemplate
     *            the registrationRequestEmailTemplate to set
     */
    public void setRegistrationRequestEmailTemplate(String registrationRequestEmailTemplate)
    {
        this.registrationRequestEmailTemplate = registrationRequestEmailTemplate;
    }

    /**
     * @param passwordResetRequestEmailTemplate
     *            the passwordResetRequestEmailTemplate to set
     */
    public void setPasswordResetRequestEmailTemplate(String passwordResetRequestEmailTemplate)
    {
        this.passwordResetRequestEmailTemplate = passwordResetRequestEmailTemplate;
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
    public void setPortalPersonDao(PortalFOIAPersonDao portalPersonDao)
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
     * @param portalInfoDAO
     *            the portalInfoDAO to set
     */
    public void setPortalInfoDAO(PortalInfoDAO portalInfoDAO)
    {
        this.portalInfoDAO = portalInfoDAO;
    }

    /**
     * @param directoryName
     *            the directoryName to set
     */
    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    /**
     * @param ldapAuthenticateService
     *            the ldapAuthenticateService to set
     */
    public void setLdapAuthenticateService(FOIALdapAuthenticationService ldapAuthenticateService)
    {
        this.ldapAuthenticateService = ldapAuthenticateService;
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
}

package com.armedia.acm.plugins.profile.service;

/*-
 * #%L
 * ACM Default Plugin: Profile
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

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.service.OrganizationService;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.ProfileConfig;
import com.armedia.acm.plugins.profile.model.ProfileDTO;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserOrgServiceImpl implements UserOrgService
{
    private Logger log = LogManager.getLogger(getClass());

    private UserOrgDao userOrgDao;

    private UserDao userDao;

    private OrganizationService organizationService;

    private ProfileConfig profileConfig;

    private MuleContextManager muleContextManager;

    private ProfileEventPublisher eventPublisher;

    private AcmGroupDao groupDao;

    private GroupService groupService;

    private EcmFileConfig ecmFileConfig;

    @Override
    public UserOrg getUserOrgForUserId(String userId)
    {
        return userOrgDao.findByUserId(userId);
    }

    @Override
    public String getProfileLocation()
    {
        return profileConfig.getUserProfileRootFolder();
    }

    @Override
    @Transactional
    public UserOrg saveUserOrgTransaction(UserOrg userOrgInfo, Authentication authentication) throws MuleException
    {
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("acmUser", authentication);
        messageProps.put("configRef", muleContextManager.getMuleContext().getRegistry().lookupObject(ecmFileConfig.getDefaultCmisId()));

        MuleMessage received = getMuleContextManager().send("vm://saveUserOrg.in", userOrgInfo, messageProps);

        UserOrg saved = received.getPayload(UserOrg.class);
        MuleException e = received.getInboundProperty("saveException");

        if (e != null)
        {
            throw e;
        }
        return saved;
    }

    private ProfileDTO createProfileDTO(UserOrg userOrgInfo, Set<AcmGroup> groups)
    {
        ProfileDTO profileDTO = new ProfileDTO();

        List<String> groupsNames = groups.stream()
                .map(AcmGroup::getName)
                .collect(Collectors.toList());

        profileDTO.setUserOrgId(userOrgInfo.getUserOrgId());

        profileDTO.setGroups(groupsNames);

        AcmUser user = userOrgInfo.getUser();
        profileDTO.setUserId(user.getUserId());
        profileDTO.setEmail(user.getMail());
        profileDTO.setFirstName(user.getFirstName());
        profileDTO.setLastName(user.getLastName());
        profileDTO.setFullName(user.getFullName());

        profileDTO.setCity(userOrgInfo.getCity());
        profileDTO.setFax(userOrgInfo.getFax());
        profileDTO.setFirstAddress(userOrgInfo.getFirstAddress());
        profileDTO.setImAccount(userOrgInfo.getImAccount());
        profileDTO.setImSystem(userOrgInfo.getImSystem());

        Organization organization = userOrgInfo.getOrganization();
        if (organization != null)
        {
            profileDTO.setCompanyName(organization.getOrganizationValue());
        }
        profileDTO.setLocation(userOrgInfo.getLocation());
        profileDTO.setMainOfficePhone(userOrgInfo.getMainOfficePhone());
        profileDTO.setMobilePhoneNumber(userOrgInfo.getMobilePhoneNumber());
        profileDTO.setOfficePhoneNumber(userOrgInfo.getOfficePhoneNumber());
        profileDTO.setSecondAddress(userOrgInfo.getSecondAddress());
        profileDTO.setState(userOrgInfo.getState());
        profileDTO.setWebsite(userOrgInfo.getWebsite());
        profileDTO.setZip(userOrgInfo.getZip());
        profileDTO.setEcmFileId(userOrgInfo.getEcmFileId());
        profileDTO.setEcmSignatureFileId(userOrgInfo.getEcmSignatureFileId());
        profileDTO.setTitle(userOrgInfo.getTitle());
        profileDTO.setLangCode(user.getLang());

        return profileDTO;
    }

    @Override
    @Transactional
    public ProfileDTO getProfileInfo(String userId, Authentication authentication)
    {
        UserOrg userOrg = getUserOrgForUserId(userId);
        if (userOrg == null)
        {
            log.info("Profile info for the user [{}] is not found", userId);
            // add only user data like full name, email, userId , groups
            userOrg = createUserOrgForUser(userId);
            try
            {
                userOrg = saveUserOrgTransaction(userOrg, authentication);
                getEventPublisher().publishProfileEvent(userOrg, authentication, true, true);
            }
            catch (MuleException e)
            {
                log.error("UserOrg for user [{}] was not saved. {}", userId, e);
                getEventPublisher().publishProfileEvent(userOrg, authentication, true, false);
            }
        }

        AcmUser user = userDao.findByUserId(userId);
        return createProfileDTO(userOrg, user.getGroups());
    }

    @Override
    @Transactional
    public ProfileDTO saveUserOrgInfo(ProfileDTO profile, Authentication authentication)
    {
        String userId = authentication.getName();
        UserOrg userOrg = getUserOrgForUserId(userId);
        boolean userOrgCreated = false;
        if (userOrg == null)
        {
            log.debug("Creating userOrg for [{}]", userId);
            userOrg = createUserOrgForUser(userId);
            userOrgCreated = true;
        }
        log.info("Updating userOrg for [{}]", userId);
        userOrg = updateUserOrg(profile, userOrg);
        boolean userOrgTransactionSuccess = true;
        try
        {
            userOrg = saveUserOrgTransaction(userOrg, authentication);
        }
        catch (MuleException e)
        {
            log.error("UserOrg for user [{}] was not saved. {}", userId, e);
            userOrgTransactionSuccess = false;
        }
        getEventPublisher().publishProfileEvent(userOrg, authentication, userOrgCreated, userOrgTransactionSuccess);
        return profile;
    }

    private UserOrg createUserOrgForUser(String userId)
    {
        UserOrg userOrg = new UserOrg();
        AcmUser user = userDao.findByUserId(userId);
        userOrg.setUser(user);
        return userOrg;
    }

    private UserOrg updateUserOrg(ProfileDTO profile, UserOrg userOrg)
    {
        userOrg.setWebsite(profile.getWebsite());
        userOrg.setZip(profile.getZip());
        userOrg.setState(profile.getState());
        userOrg.setSecondAddress(profile.getSecondAddress());
        userOrg.setCity(profile.getCity());
        userOrg.setFax(profile.getFax());
        userOrg.setFirstAddress(profile.getFirstAddress());
        userOrg.setImAccount(profile.getImAccount());
        userOrg.setImSystem(profile.getImSystem());
        userOrg.setLocation(profile.getLocation());
        userOrg.setMainOfficePhone(profile.getMainOfficePhone());
        userOrg.setOfficePhoneNumber(profile.getOfficePhoneNumber());
        userOrg.setMobilePhoneNumber(profile.getMobilePhoneNumber());
        userOrg.setEcmFileId(profile.getEcmFileId());
        userOrg.setEcmSignatureFileId(profile.getEcmSignatureFileId());
        userOrg.setTitle(profile.getTitle());

        userOrg = updateUserOrgOrganization(profile.getCompanyName(), userOrg);

        return userOrg;
    }

    private UserOrg updateUserOrgOrganization(String companyName, UserOrg userOrg)
    {
        AcmUser user = userOrg.getUser();
        if (StringUtils.isNotBlank(companyName))
        {
            // update the user organization to it or create new organization and update the user organization relation
            Organization organization = organizationService.findOrCreateOrganization(companyName, user.getUserId());
            userOrg.setOrganization(organization);
        }
        else
        {
            // companyName is blank, remove user's organization
            userOrg.setOrganization(null);
        }
        return userOrg;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public OrganizationService getOrganizationService()
    {
        return organizationService;
    }

    public void setOrganizationService(OrganizationService organizationService)
    {
        this.organizationService = organizationService;
    }

    public UserOrgDao getUserOrgDao()
    {
        return userOrgDao;
    }

    public void setUserOrgDao(UserOrgDao userOrgDao)
    {
        this.userOrgDao = userOrgDao;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public ProfileEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ProfileEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public ProfileConfig getProfileConfig()
    {
        return profileConfig;
    }

    public void setProfileConfig(ProfileConfig profileConfig)
    {
        this.profileConfig = profileConfig;
    }

    public EcmFileConfig getEcmFileConfig()
    {
        return ecmFileConfig;
    }

    public void setEcmFileConfig(EcmFileConfig ecmFileConfig)
    {
        this.ecmFileConfig = ecmFileConfig;
    }
}

package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapGroupDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.event.LdapUserCreatedEvent;
import com.armedia.acm.services.users.model.event.LdapUserUpdatedEvent;
import com.armedia.acm.services.users.model.event.SetPasswordEmailEvent;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.Directory;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.model.ldap.UserDTO;
import com.armedia.acm.services.users.service.group.GroupService;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.armedia.acm.services.users.model.ldap.MapperUtils.prefixTrailingDot;

public class LdapUserService implements ApplicationEventPublisherAware
{
    private Logger log = LogManager.getLogger(getClass());

    private SpringLdapDao ldapDao;

    private UserDao userDao;

    private GroupService groupService;

    private SpringLdapUserDao ldapUserDao;

    private SpringLdapGroupDao ldapGroupDao;

    private SpringContextHolder acmContextHolder;

    private ApplicationEventPublisher eventPublisher;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    public void publishSetPasswordEmailEvent(AcmUser user)
    {
        log.debug("Publish send set password email for user: [{}]", user.getUserId());
        SetPasswordEmailEvent setPasswordEmailEvent = new SetPasswordEmailEvent(user);
        setPasswordEmailEvent.setSucceeded(true);
        eventPublisher.publishEvent(setPasswordEmailEvent);
    }

    public void publishUserCreatedEvent(HttpSession httpSession, Authentication authentication, AcmUser user, boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        LdapUserCreatedEvent event = new LdapUserCreatedEvent(user, succeeded, ipAddress, authentication);
        eventPublisher.publishEvent(event);
    }

    public void publishUserUpdatedEvent(HttpSession httpSession, Authentication authentication, AcmUser user, boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        LdapUserUpdatedEvent event = new LdapUserUpdatedEvent(user, succeeded, ipAddress, authentication);
        eventPublisher.publishEvent(event);
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser createLdapUser(UserDTO userDto, String directoryName)
            throws AcmUserActionFailedException, AcmLdapActionFailedException, ValidatorException {
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);

        if (!emailValidator.isValid(userDto.getMail()))
        {
            throw new ValidatorException("Invalid email submitted!");
        }

        String userId = MapperUtils.buildUserId(userDto.getUserId(), ldapSyncConfig);

        AcmUser user = checkExistingUser(userId);

        if (user == null)
        {
            user = userDto.toAcmUser(userId, userDao.getDefaultUserLang());
        }
        else
        {
            user = userDto.updateAcmUser(user);
        }

        String userIdNoDomain = StringUtils.substringBeforeLast(userId, "@");
        String dn = Directory.valueOf(ldapSyncConfig.getDirectoryType())
                .buildDnForUserEntry(userIdNoDomain, ldapSyncConfig);
        user.setDistinguishedName(dn);
        user.setUserDirectoryName(directoryName);
        user.setUserState(AcmUserState.VALID);
        if ("uid".equalsIgnoreCase(ldapSyncConfig.getUserIdAttributeName()))
        {
            user.setUid(userIdNoDomain);
        }
        else if ("sAMAccountName".equalsIgnoreCase(ldapSyncConfig.getUserIdAttributeName()))
        {
            user.setsAMAccountName(userIdNoDomain);
        }

        Set<AcmGroup> groups = new HashSet<>();

        if (StringUtils.isNotBlank(ldapSyncConfig.getUserControlGroup()))
        {
            userDto.getGroupNames().add(ldapSyncConfig.getUserControlGroup());
        }

        for (String groupName : userDto.getGroupNames())
        {
            AcmGroup group = groupService.findByName(groupName, FlushModeType.COMMIT);
            if (group != null)
            {
                groups.add(group);
                group.addUserMember(user);
                log.debug("Set User [{}] as member of Group [{}]", user.getUserId(), group.getName());
            }
        }

        log.debug("Saving new User [{}] with DN [{}] in database", user.getUserId(), user.getDistinguishedName());

        AcmUser acmUser = userDao.save(user);
        userDao.getEntityManager().flush();

        ldapUserDao.createUserEntry(acmUser, userDto.getPassword(), ldapSyncConfig);

        try
        {
            LdapTemplate ldapTemplate = ldapDao.buildLdapTemplate(ldapSyncConfig);
            // passwordExpirationDate is set by ldap after the entry is there
            LdapUser userEntry = ldapUserDao.findUserByLookup(dn, ldapTemplate, ldapSyncConfig);
            acmUser.setPasswordExpirationDate(userEntry.getPasswordExpirationDate());
            acmUser.setUserPrincipalName(userEntry.getUserPrincipalName());
            userDao.save(acmUser);
            userDao.getEntityManager().flush();

            Set<String> groupDns = groups.stream()
                    .filter(AcmGroup::isLdapGroup)
                    .map(AcmGroup::getDistinguishedName)
                    .collect(Collectors.toSet());

            ldapGroupDao.addMemberToGroups(acmUser.getDistinguishedName(), groupDns, ldapSyncConfig);
        }
        catch (AcmLdapActionFailedException | PersistenceException e)
        {
            log.error("Adding User [{}] as member to groups in LDAP failed! Rollback changes.", acmUser.getUserId(), e);

            ldapUserDao.deleteUserEntry(acmUser.getDistinguishedName(), ldapSyncConfig);
            throw new AcmUserActionFailedException("create LDAP user", null, null, "Creating LDAP user failed!", e);
        }

        return acmUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser addUserInGroups(String userId, List<String> groups, String directory)
            throws AcmLdapActionFailedException, AcmObjectNotFoundException
    {
        Set<String> groupsToUpdate = new HashSet<>();
        AcmUser user = userDao.findByUserId(userId);
        if (user == null)
        {
            throw new AcmObjectNotFoundException("USER", null, "User " + userId + " was not found");
        }
        for (String groupName : groups)
        {
            AcmGroup group = groupService.addUserMemberToGroup(user, groupName, true);
            if (group.isLdapGroup())
            {
                groupsToUpdate.add(group.getDistinguishedName());
            }
        }

        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directory);

        ldapGroupDao.addMemberToGroups(user.getDistinguishedName(), groupsToUpdate, ldapSyncConfig);

        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser editLdapUser(AcmUser acmUser, String userId, String directory) throws AcmLdapActionFailedException, ValidatorException
    {
        if (!emailValidator.isValid(acmUser.getMail()))
        {
            throw new ValidatorException("Invalid email submitted!");
        }
        log.debug("Saving updated User [{}] in database", acmUser.getUserId());
        AcmUser existingUser = userDao.findByUserId(userId);
        existingUser.setFirstName(acmUser.getFirstName());
        existingUser.setLastName(acmUser.getLastName());
        existingUser.setFullName(String.format("%s %s", acmUser.getFirstName(), acmUser.getLastName()));
        existingUser.setMail(acmUser.getMail());
        acmUser = userDao.save(existingUser);
        userDao.getEntityManager().flush();
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directory);

        log.debug("Update User [{}] with DN [{}] in LDAP", acmUser.getUserId(), acmUser.getDistinguishedName());
        ldapUserDao.updateUserEntry(acmUser, ldapSyncConfig);
        log.debug("User [{}] with DN [{}] successfully updated in DB and LDAP", acmUser.getUserId(), acmUser.getDistinguishedName());
        return acmUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser cloneLdapUser(String userId, UserDTO user, String directory)
            throws AcmUserActionFailedException, AcmLdapActionFailedException, ValidatorException {
        log.debug("Creating new user [{}] as a clone of [{}]", user.getUserId(), userId);
        AcmUser existingUser = userDao.findByUserId(userId);
        user.setGroupNames(existingUser.getGroupNames().collect(Collectors.toList()));
        return createLdapUser(user, directory);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AcmUser> addExistingLdapUsersToGroup(List<String> userIds, String directoryName, String groupName)
            throws AcmLdapActionFailedException, AcmObjectNotFoundException
    {
        List<AcmUser> ldapUsers = new ArrayList<>();
        for (String userId : userIds)
        {
            AcmUser existingUser = userDao.findByUserId(userId);

            if (existingUser == null)
            {
                throw new AcmObjectNotFoundException("USER", null, "User " + userId + " not found");
            }

            log.debug("Adding Group [{}] to User [{}]", groupName, userId);
            AcmGroup ldapGroup = groupService.addUserMemberToGroup(existingUser, groupName, true);

            AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);

            try
            {
                ldapGroupDao.addMemberToGroup(existingUser.getDistinguishedName(), ldapGroup.getDistinguishedName(), ldapSyncConfig);
                log.debug("Group [{}] with DN [{}] modified in LDAP", ldapGroup.getName(), ldapGroup.getDistinguishedName());
                ldapUsers.add(existingUser);
            }
            catch (AcmLdapActionFailedException e)
            {
                for (AcmUser user1 : ldapUsers)
                {
                    log.debug("Rollback updates on Group [{}] with DN [{}]", ldapGroup.getName(), ldapGroup.getDistinguishedName());
                    ldapGroupDao.removeMemberFromGroup(user1.getDistinguishedName(), ldapGroup.getDistinguishedName(), ldapSyncConfig);
                }
                throw new AcmLdapActionFailedException("Failed to add users to group", e);
            }
        }
        return ldapUsers;
    }

    private AcmLdapSyncConfig getLdapSyncConfig(String directoryName)
    {
        return acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).get(String.format("%s_sync", directoryName));
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser deleteAcmUser(String userId, String directory) throws AcmLdapActionFailedException
    {
        log.debug("Mark User: [{}] INVALID in database", userId);
        AcmUser user = userDao.markUserInvalid(userId);

        List<AcmGroup> lookupGroups = groupService.findByUserMember(user);

        for (AcmGroup group : lookupGroups)
        {
            group.removeUserMember(user);
            groupService.saveAndFlush(group);
        }

        String dnToDelete = MapperUtils.stripBaseFromDn(user.getDistinguishedName(), AcmLdapConstants.DC_DELETED);
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directory);
        ldapUserDao.deleteUserEntry(dnToDelete, ldapSyncConfig);
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser removeUserFromGroups(String userId, List<String> groups, String directory)
            throws AcmLdapActionFailedException, AcmObjectNotFoundException
    {
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directory);

        Set<String> groupsDnToUpdate = new HashSet<>();
        String controlGroup = ldapSyncConfig.getUserControlGroup();

        // prevent removing the user from the control group if configured
        if (groups.contains(controlGroup))
        {
            throw new AcmLdapActionFailedException(String.format("'%s' group is a required system group and can't be removed.", controlGroup));
        }

        for (String groupName : groups)
        {
            AcmGroup group = groupService.removeUserMemberFromGroup(userId, groupName, true);
            if (group.isLdapGroup())
            {
                groupsDnToUpdate.add(group.getDistinguishedName());
            }
        }

        AcmUser user = userDao.findByUserId(userId);

        ldapGroupDao.removeMemberFromGroups(user.getDistinguishedName(), groupsDnToUpdate, ldapSyncConfig);

        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public void moveExistingLdapUsersToGroup(String newAcmGroup, String oldAcmGroup, String directoryName)
            throws AcmLdapActionFailedException, AcmObjectNotFoundException
    {

        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String controlGroup = ldapSyncConfig.getUserControlGroup();

        if (oldAcmGroup.equals(controlGroup))
        {
            throw new AcmLdapActionFailedException(
                    String.format("'%s' group is a required system group and can't be removed.", controlGroup));
        }

        String userPrefix = prefixTrailingDot(ldapSyncConfig.getUserPrefix());
        List<AcmUser> acmUsers = userDao.findByPrefix(userPrefix);

        List<AcmUser> filteredAcmUsers = acmUsers.stream()
                .filter(
                        acmUser -> acmUser.getGroups().stream()
                                .anyMatch(acmGroup -> acmGroup.getName().contains(oldAcmGroup)))
                .collect(Collectors.toList());

        AcmGroup oldGroup = null;
        AcmGroup newGroup = groupService.findByName(newAcmGroup);
        for (AcmUser acmUser : filteredAcmUsers)
        {
            AcmGroup group = groupService.removeUserMemberFromGroup(acmUser.getUserId(), oldAcmGroup, true);
            if (group.isLdapGroup())
            {
                oldGroup = group;
            }
            log.debug("Adding Group [{}] to User [{}]", newAcmGroup, acmUser);
            groupService.addUserMemberToGroup(acmUser, newAcmGroup, true);
        }

        if (oldGroup == null)
        {
            return;
        }

        List<AcmUser> addedLdapUsers = new ArrayList<>();
        List<AcmUser> removedLdapUsers = new ArrayList<>();
        for (AcmUser acmUser : filteredAcmUsers)
        {
            try
            {
                ldapGroupDao.addMemberToGroup(acmUser.getDistinguishedName(), newGroup.getDistinguishedName(), ldapSyncConfig);
                addedLdapUsers.add(acmUser);
                ldapGroupDao.removeMemberFromGroup(acmUser.getDistinguishedName(), oldGroup.getDistinguishedName(), ldapSyncConfig);
                removedLdapUsers.add(acmUser);
                log.debug("Group [{}] with DN [{}] modified in LDAP", newGroup.getName(), newGroup.getDistinguishedName());

            }
            catch (AcmLdapActionFailedException e)
            {
                for (AcmUser user1 : addedLdapUsers)
                {
                    log.debug("Rollback updates on Group [{}] with DN [{}]", newAcmGroup, newGroup.getDistinguishedName());
                    ldapGroupDao.removeMemberFromGroup(user1.getDistinguishedName(), newGroup.getDistinguishedName(), ldapSyncConfig);
                }
                for (AcmUser user1 : removedLdapUsers)
                {
                    log.debug("Rollback updates on Group [{}] with DN [{}]", oldGroup, oldGroup.getDistinguishedName());
                    ldapGroupDao.addMemberToGroup(user1.getDistinguishedName(), oldGroup.getDistinguishedName(), ldapSyncConfig);
                }
                throw new AcmLdapActionFailedException("Failed to add users to group", e);
            }
        }

    }

    public AcmUser findByPasswordResetToken(String token)
    {
        return userDao.findByPasswordResetToken(token);
    }

    /**
     * Check if user already exists with the same user identifier.
     * If the user exists and its status is either "INVALID" or "DELETED",
     * we need to remove that user's group membership
     *
     * @param userId
     *            user identifier
     * @throws NameAlreadyBoundException
     *             if a user exists and its status is "VALID"
     */
    @Transactional
    public AcmUser checkExistingUser(String userId)
    {
        AcmUser existing = userDao.findByUserId(userId);
        if (existing == null)
        {
            return null;
        }

        if (AcmUserState.VALID == existing.getUserState())
        {
            throw new NameAlreadyBoundException(null);
        }
        else
        {
            // INVALID or DELETED user, remove current group membership
            // we have to do this, otherwise new user will be associated with new groups,
            // but also existing ones (which we do not want)
            existing.getGroups().forEach(group -> group.getUserMembers(false).remove(existing));
            existing.setGroups(new HashSet<>());
        }
        return existing;
    }

    public void setLdapDao(SpringLdapDao ldapDao)
    {
        this.ldapDao = ldapDao;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public void setLdapUserDao(SpringLdapUserDao ldapUserDao)
    {
        this.ldapUserDao = ldapUserDao;
    }

    public void setLdapGroupDao(SpringLdapGroupDao ldapGroupDao)
    {
        this.ldapGroupDao = ldapGroupDao;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

}

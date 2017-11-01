package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.event.LdapUserCreatedEvent;
import com.armedia.acm.services.users.model.event.LdapUserUpdatedEvent;
import com.armedia.acm.services.users.model.event.SetPasswordEmailEvent;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.Directory;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.model.ldap.UserDTO;
import com.armedia.acm.services.users.service.AcmUserRoleService;
import com.armedia.acm.services.users.service.RetryExecutor;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LdapUserService implements ApplicationEventPublisherAware
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private SpringLdapDao ldapDao;
    private UserDao userDao;
    private AcmGroupDao groupDao;
    private AcmUserRoleService userRoleService;
    private SpringLdapUserDao ldapUserDao;
    private SpringContextHolder acmContextHolder;
    private LdapEntryTransformer userTransformer;
    private ApplicationEventPublisher eventPublisher;

    public void publishSetPasswordEmailEvent(AcmUser user)
    {
        log.debug("Publish send set password email...");
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
            throws AcmUserActionFailedException, AcmLdapActionFailedException
    {
        AcmUser user = checkExistingUser(userDto.getUserId());

        if (user == null)
        {
            user = userDto.toAcmUser(userDto.getUserId(), userDao.getDefaultUserLang());
        } else
        {
            user = userDto.updateAcmUser(user);
        }

        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);

        String dn = buildDnForUser(user.getFullName(), userDto.getUserId(), ldapSyncConfig);
        user.setDistinguishedName(dn);
        user.setUserDirectoryName(directoryName);
        user.setUserState(AcmUserState.VALID);
        if ("uid".equalsIgnoreCase(ldapSyncConfig.getUserIdAttributeName()))
        {
            user.setUid(user.getUserId());
        } else if ("sAMAccountName".equalsIgnoreCase(ldapSyncConfig.getUserIdAttributeName()))
        {
            user.setsAMAccountName(user.getUserId());
        }
        //set the domain defined in the config to the userId
        if (StringUtils.isNotEmpty(ldapSyncConfig.getUserDomain()))
        {
            user.setUserId(user.getUserId() + "@" + ldapSyncConfig.getUserDomain());
        }

        Set<AcmGroup> groups = new HashSet<>();

        for (String groupName : userDto.getGroupNames())
        {
            AcmGroup group = groupDao.findByName(groupName);
            if (group != null)
            {
                groups.add(group);
                group.addUserMember(user);
            }
            log.debug("Set User [{}] as member of Group [{}]", user.getUserId(), group);
        }

        log.debug("Saving new User [{}] with DN [{}] in database", user.getUserId(), user.getDistinguishedName());

        AcmUser acmUser = userDao.save(user);
        userDao.getEntityManager().flush();

        userRoleService.saveValidUserRolesPerAddedUserGroups(acmUser.getUserId(), groups);

        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        try
        {
            String password = userDto.getPassword();

            DirContextAdapter context;
            if (password == null)
            {
                password = MapperUtils.generatePassword();
            }
            context = userTransformer.createContextForNewUserEntry(directoryName, acmUser, password,
                    ldapSyncConfig.getBaseDC(), ldapSyncConfig.getUserDomain());

            log.debug("Ldap User Context [{}]", context.getAttributes());
            log.debug("Save User [{}] with DN [{}] in LDAP", acmUser.getUserId(), acmUser.getDistinguishedName());
            new RetryExecutor().retry(() -> ldapTemplate.bind(context));
        }
        catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
        }
        try
        {
            // passwordExpirationDate is set by ldap after the entry is there
            LdapUser userEntry = ldapUserDao.findUserByLookup(dn, ldapTemplate, ldapSyncConfig);
            acmUser.setPasswordExpirationDate(userEntry.getPasswordExpirationDate());
            acmUser.setUserPrincipalName(userEntry.getUserPrincipalName());
            userDao.save(acmUser);
            userDao.getEntityManager().flush();

            setUserAsMemberToLdapGroups(acmUser, acmUser.getLdapGroups(), directoryName);
        }
        catch (Exception e)
        {
            log.error("Adding User [{}] as member to groups in LDAP failed! Rollback changes.", acmUser.getUserId(), e);
            try
            {
                new RetryExecutor().retry(() -> ldapTemplate
                        .unbind(MapperUtils.stripBaseFromDn(acmUser.getDistinguishedName(), ldapSyncConfig.getBaseDC())));
            }
            catch (Exception ee)
            {
                log.warn("Rollback failed", e);
            }
            log.debug("User entry with DN [{}] deleted from LDAP", acmUser.getDistinguishedName());
            throw new AcmUserActionFailedException("create LDAP user", null, null, "Creating LDAP user failed!", e);
        }
        return acmUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser addUserInGroups(String userId, List<String> groups, String directory) throws AcmUserActionFailedException
    {
        AcmUser existingUser = userDao.findByUserId(userId);

        Set<AcmGroup> groupsToUpdate = new HashSet<>();

        groups.forEach(groupName ->
        {
            AcmGroup group = groupDao.findByName(groupName);
            if (group == null) // probably an ad-hoc group, where internal name contains UUID suffix
            {
                group = groupDao.findByMatchingName(groupName);
            }
            if (group != null)
            {
                log.debug("Set User [{}] as member of Group [{}]", existingUser.getUserId(), group);
                existingUser.addGroup(group);
                groupsToUpdate.add(group);
            }
        });
        if (!groupsToUpdate.isEmpty())
        {
            log.debug("Saving User [{}] with DN [{}] in database", existingUser.getUserId(), existingUser.getDistinguishedName());
            userDao.save(existingUser);
            userDao.getEntityManager().flush();
            userRoleService.saveValidUserRolesPerAddedUserGroups(userId, groupsToUpdate);
        }

        Set<AcmGroup> ldapGroupsToUpdate = groupsToUpdate.stream()
                .filter(AcmGroup::isLdapGroup)
                .collect(Collectors.toSet());
        setUserAsMemberToLdapGroups(existingUser, ldapGroupsToUpdate, directory);

        return existingUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser removeUserFromGroups(String userId, List<String> groups, String directory) throws AcmUserActionFailedException
    {
        Set<AcmGroup> groupsToUpdate = new HashSet<>();

        AcmUser acmUser = userDao.findByUserId(userId);

        groups.forEach(groupName ->
        {
            AcmGroup group = groupDao.findByName(groupName);
            if (group == null) // probably an ad-hoc group, where internal name contains UUID suffix
            {
                group = groupDao.findByMatchingName(groupName);
            }

            if (group != null)
            {
                acmUser.removeGroup(group);
                log.debug("Removing User [{}] from Group [{}]", acmUser.getUserId(), group);
                groupsToUpdate.add(group);
            }
        });

        userDao.getEntityManager().flush();

        userRoleService.saveInvalidUserRolesPerRemovedUserGroups(acmUser, groupsToUpdate);

        removeUserAsMemberFromLdapGroups(acmUser, groupsToUpdate, directory);
        return acmUser;
    }

    private void setUserAsMemberToLdapGroups(AcmUser ldapUser, Set<AcmGroup> ldapGroups, String directory)
            throws AcmUserActionFailedException
    {
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directory);
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

        List<AcmGroup> updatedGroups = new ArrayList<>();
        for (AcmGroup group : ldapGroups)
        {
            String groupDnStrippedBase = MapperUtils.stripBaseFromDn(group.getDistinguishedName(), ldapSyncConfig.getBaseDC());
            log.debug("Add User [{}] with DN [{}] as member in Group [{}] with DN [{}] in LDAP", ldapUser.getUserId(),
                    ldapUser.getDistinguishedName(), group.getName(), group.getDistinguishedName());
            try
            {
                DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                        .retryResult(() -> ldapTemplate.lookupContext(groupDnStrippedBase));
                groupContext.addAttributeValue("member", ldapUser.getDistinguishedName());
                new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                updatedGroups.add(group);
            }
            catch (Exception e)
            {
                log.debug("Adding user [{}] with DN [{}] to LDAP group(s) failed! Rollback changes on updated ldap groups",
                        ldapUser.getUserId(), ldapUser.getDistinguishedName());
                updatedGroups.forEach(updatedGroup ->
                {
                    String updatedGroupDnStrippedBase = MapperUtils.stripBaseFromDn(updatedGroup.getDistinguishedName(),
                            ldapSyncConfig.getBaseDC());
                    try
                    {
                        DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                                .retryResult(() -> ldapTemplate.lookupContext(updatedGroupDnStrippedBase));
                        groupContext.removeAttributeValue("member", ldapUser.getDistinguishedName());
                        new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                        log.debug("Rollback changes for Group [{}] with DN [{}]", updatedGroup.getName(),
                                updatedGroup.getDistinguishedName());
                    }
                    catch (Exception e1)
                    {
                        log.warn("Failed to rollback changes for Group [{}] with DN [{}]", updatedGroup.getName(),
                                updatedGroup.getDistinguishedName(), e1);
                    }
                });
                throw new AcmUserActionFailedException("updating LDAP Group failed", "LDAP_GROUP", null,
                        "updating LDAP Group with new member failed", e);
            }
        }
    }

    private void removeUserAsMemberFromLdapGroups(AcmUser ldapUser, Set<AcmGroup> ldapGroups, String directory)
            throws AcmUserActionFailedException
    {
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directory);
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

        List<AcmGroup> updatedGroups = new ArrayList<>();

        for (AcmGroup group : ldapGroups)
        {
            String groupDnStrippedBase = MapperUtils.stripBaseFromDn(group.getDistinguishedName(), ldapSyncConfig.getBaseDC());
            log.debug("Remove User [{}] with DN [{}] as member in Group [{}] with DN [{}] in LDAP", ldapUser.getUserId(),
                    ldapUser.getDistinguishedName(), group.getName(), group.getDistinguishedName());
            try
            {
                DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                        .retryResult(() -> ldapTemplate.lookupContext(groupDnStrippedBase));
                // a workaround for removing group members
                // groupContext.removeAttributeValue("member", ldapUser.getDistinguishedName()) is not
                // working on AD because of DN case sensitivity
                String[] members = groupContext.getStringAttributes("member");
                String member = Arrays.stream(members)
                        .filter(m -> m.equalsIgnoreCase(ldapUser.getDistinguishedName()))
                        .findFirst()
                        .orElse(null);
                groupContext.removeAttributeValue("member", member);
                new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                updatedGroups.add(group);
            }
            catch (Exception e)
            {
                log.debug("Removing user [{}] with DN [{}] from LDAP group(s) failed! Rollback changes on updated ldap groups",
                        ldapUser.getUserId(), ldapUser.getDistinguishedName());
                updatedGroups.forEach(updatedGroup ->
                {
                    String updatedGroupDnStrippedBase = MapperUtils.stripBaseFromDn(updatedGroup.getDistinguishedName(),
                            ldapSyncConfig.getBaseDC());
                    try
                    {
                        DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                                .retryResult(() -> ldapTemplate.lookupContext(updatedGroupDnStrippedBase));
                        groupContext.addAttributeValue("member", ldapUser.getDistinguishedName());
                        new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                        log.debug("Rollback changes for Group [{}] with DN [{}]", updatedGroup.getName(),
                                updatedGroup.getDistinguishedName());
                    }
                    catch (Exception e1)
                    {
                        log.warn("Failed to rollback changes for Group [{}] with DN [{}]", updatedGroup.getName(),
                                updatedGroup.getDistinguishedName(), e1);
                    }
                });
                throw new AcmUserActionFailedException("updating LDAP Group failed", "LDAP_GROUP", null,
                        "updating LDAP Group with new member failed", e);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser editLdapUser(AcmUser acmUser, String userId, String directory) throws AcmLdapActionFailedException
    {
        log.debug("Saving updated User [{}] in database", acmUser.getUserId());
        AcmUser existingUser = userDao.findByUserId(userId);
        existingUser.setFirstName(acmUser.getFirstName());
        existingUser.setLastName(acmUser.getLastName());
        existingUser.setFullName(String.format("%s %s", acmUser.getFirstName(), acmUser.getLastName()));
        existingUser.setMail(acmUser.getMail());
        acmUser = getUserDao().save(existingUser);
        getUserDao().getEntityManager().flush();
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directory);
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        String strippedBaseDdUserDn = MapperUtils.
                stripBaseFromDn(acmUser.getDistinguishedName(), ldapSyncConfig.getBaseDC());
        try
        {
            DirContextOperations context = new RetryExecutor<DirContextOperations>()
                    .retryResult(() -> ldapTemplate.lookupContext(strippedBaseDdUserDn));
            DirContextOperations editContext = userTransformer.createContextForEditUserEntry(context, acmUser, directory);
            log.debug("Update User [{}] with DN [{}] in LDAP", acmUser.getUserId(), acmUser.getDistinguishedName());
            new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(editContext));
            log.debug("User [{}] with DN [{}] successfully updated in DB and LDAP", acmUser.getUserId(), acmUser.getDistinguishedName());
        }
        catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
        }
        return acmUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser cloneLdapUser(String userId, UserDTO user, String directory)
            throws AcmUserActionFailedException, AcmLdapActionFailedException
    {
        log.debug("Creating new user [{}] as a clone of [{}]", user.getUserId(), userId);
        AcmUser existingUser = userDao.findByUserId(userId);
        user.setGroupNames(existingUser.getGroupNames().collect(Collectors.toList()));
        return createLdapUser(user, directory);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AcmUser> addExistingLdapUsersToGroup(List<AcmUser> acmUsers, String directoryName, String groupName)
            throws AcmUserActionFailedException, AcmLdapActionFailedException
    {
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        AcmGroup ldapGroup = groupDao.findByName(groupName);
        List<AcmUser> ldapUsers = new ArrayList<>();
        for (AcmUser user : acmUsers)
        {
            AcmUser existingUser = userDao.findByUserId(user.getUserId());
            log.debug("Adding Group [{}] to User [{}]", groupName, user.getUserId());
            existingUser.addGroup(ldapGroup);
            log.debug("Saving updated User [{}]", user.getUserId());
            AcmUser savedUser = userDao.save(existingUser);
            userDao.getEntityManager().flush();

            userRoleService.saveValidUserRolesPerAddedUserGroups(user.getUserId(), new HashSet<>(Arrays.asList(ldapGroup)));

            String strippedBaseDCGroupDn = MapperUtils.stripBaseFromDn(ldapGroup.getDistinguishedName(), ldapSyncConfig.getBaseDC());
            try
            {
                DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                        .retryResult(() -> ldapTemplate.lookupContext(strippedBaseDCGroupDn));
                groupContext.addAttributeValue("member", savedUser.getDistinguishedName());
                log.debug("Modify Group [{}] with DN [{}] with new LDAP member [{}] in LDAP", ldapGroup.getName(),
                        ldapGroup.getDistinguishedName(), savedUser.getUserId(), savedUser.getDistinguishedName());
                new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                log.debug("Group [{}] with DN [{}] modified in LDAP", ldapGroup.getName(), ldapGroup.getDistinguishedName());
            }
            catch (Exception e)
            {
                throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
            }
            ldapUsers.add(savedUser);
        }
        return ldapUsers;
    }

    private AcmLdapSyncConfig getLdapSyncConfig(String directoryName)
    {
        return acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directoryName));
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser deleteAcmUser(String userId, String directory) throws AcmLdapActionFailedException
    {
        AcmUser existingUser = userDao.findByUserId(userId);
        List<AcmGroup> lookupGroups = groupDao.findByUserMember(existingUser);

        for (AcmGroup group : lookupGroups)
        {
            group.removeUserMember(existingUser);
            groupDao.save(group);
        }

        log.debug("Mark User: [{}] INVALID in database", existingUser.getUserId());
        userDao.markUserInvalid(userId);

        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directory);
        userRoleService.saveInvalidUserRolesPerRemovedUserGroups(existingUser, new HashSet<>(lookupGroups));

        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        try
        {
            lookupGroups = lookupGroups.stream()
                    .filter(AcmGroup::isLdapGroup)
                    .collect(Collectors.toList());

            removeUserAsMemberFromLdapGroups(existingUser, new HashSet<>(lookupGroups), directory);
            log.debug("Deleting User [{}] with DN [{}] in LDAP", existingUser.getUserId(), existingUser.getDistinguishedName());
            new RetryExecutor().retry(() -> ldapTemplate
                    .unbind(MapperUtils.stripBaseFromDn(existingUser.getDistinguishedName(), ldapSyncConfig.getBaseDC())));
            log.debug("User [{}] with DN [{}] successfully deleted in DB and LDAP", existingUser.getUserId(),
                    existingUser.getDistinguishedName());
        }
        catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
        }
        return existingUser;
    }

    private String buildDnForUser(String userFullName, String userId, AcmLdapSyncConfig syncConfig)
    {

        String uidAttr = String.format("%s=%s", "uid", userId);
        String cnAttr = String.format("%s=%s", "cn", userFullName);
        String dnAttr = Directory.openldap.name().equals(syncConfig.getDirectoryType()) ? uidAttr : cnAttr;
        return String.format("%s,%s,%s", dnAttr, syncConfig.getUserSearchBase(), syncConfig.getBaseDC());
    }

    public AcmUser findByToken(String token)
    {
        return userDao.findByPasswordResetToken(token);
    }

    /**
     * Check if user already exists with the same user identifier.
     * If the user exists and its status is either "INVALID" or "DELETED",
     * we need to remove that user's group membership
     *
     * @param userId user identifier
     * @throws AcmLdapActionFailedException if a user exists and its status is "VALID"
     */
    private AcmUser checkExistingUser(String userId) throws AcmLdapActionFailedException
    {
        AcmUser existing = userDao.findByUserId(userId);
        if (existing == null) return null;

        if (AcmUserState.VALID == existing.getUserState())
        {
            // FIXME: use some more appropriate exception here
            throw new AcmLdapActionFailedException(String.format("User [%s] already exists and is active user", userId));
        } else
        {
            // INVALID or DELETED user, remove current group membership
            // we have to do this, otherwise new user will be associated with new groups,
            // but also existing ones (which we do not want)
            // TODO: AcmUser.setGroups() should take care of that
            existing.getGroups().forEach(group ->
            {
                group.removeUserMember(existing);
                groupDao.save(group);
            });
        }
        return existing;
    }

    public SpringLdapDao getLdapDao()
    {
        return ldapDao;
    }

    public void setLdapDao(SpringLdapDao ldapDao)
    {
        this.ldapDao = ldapDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public AcmUserRoleService getUserRoleService()
    {
        return userRoleService;
    }

    public void setUserRoleService(AcmUserRoleService userRoleService)
    {
        this.userRoleService = userRoleService;
    }

    public SpringLdapUserDao getLdapUserDao()
    {
        return ldapUserDao;
    }

    public void setLdapUserDao(SpringLdapUserDao ldapUserDao)
    {
        this.ldapUserDao = ldapUserDao;
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public LdapEntryTransformer getUserTransformer()
    {
        return userTransformer;
    }

    public void setUserTransformer(LdapEntryTransformer userTransformer)
    {
        this.userTransformer = userTransformer;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }
}

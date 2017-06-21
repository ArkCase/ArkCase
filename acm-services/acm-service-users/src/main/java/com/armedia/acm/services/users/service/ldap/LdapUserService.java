package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.service.RetryExecutor;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LdapUserService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private SpringLdapDao ldapDao;
    private UserDao userDao;
    private AcmGroupDao groupDao;
    private SpringLdapUserDao ldapUserDao;
    private SpringContextHolder acmContextHolder;
    private LdapEntryTransformer userTransformer;

    @Transactional(rollbackFor = Exception.class)
    public AcmUser createLdapUser(AcmUser user, List<String> groupNames, String password, String directoryName)
            throws AcmUserActionFailedException, AcmLdapActionFailedException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directoryName));
        Map<String, String> roleToGroup = ldapSyncConfig.getRoleToGroupMap();
        Map<String, List<String>> groupToRoleMap = LdapSyncService.reverseRoleToGroupMap(roleToGroup);
        String userFullName = String.format("%s %s", user.getFirstName(), user.getLastName());
        String dn = buildDnForUser(userFullName, user.getUserId(), ldapSyncConfig);

        user.setFullName(userFullName);
        user.setDistinguishedName(dn);
        user.setUserDirectoryName(directoryName);
        user.setUserState("VALID");
        if ("uid".equalsIgnoreCase(ldapSyncConfig.getUserIdAttributeName()))
        {
            user.setUid(user.getUserId());
        } else if ("sAMAccountName".equalsIgnoreCase(ldapSyncConfig.getUserIdAttributeName()))
        {
            user.setsAMAccountName(user.getUserId());
        }

        groupNames.forEach(groupName ->
        {
            AcmGroup group = getGroupDao().findByName(groupName);
            user.addGroup(group);
            log.debug("Set User:{} as member of Group:{}", user.getUserId(), group.getName());
        });
        log.debug("Saving new User:{} with DN:{} in database", user.getUserId(), user.getDistinguishedName());
        AcmUser ldapUser = getUserDao().save(user);
        getUserDao().getEntityManager().flush();
        saveUserRolesInDb(ldapUser.getUserId(), ldapUser.getGroups(), groupToRoleMap);
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        try
        {
            DirContextAdapter context = userTransformer
                    .createContextForNewUserEntry(directoryName, user, password, ldapSyncConfig.getBaseDC());
            log.debug("Ldap User Context: {}", context.getAttributes());
            log.debug("Save User:{} with DN:{} in LDAP", ldapUser.getUserId(), ldapUser.getDistinguishedName());
            new RetryExecutor().retry(() -> ldapTemplate.bind(context));
        } catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
        }
        try
        {
            // passwordExpirationDate is set by ldap after the entry is there
            AcmUser userEntry = getLdapUserDao().findUserByLookup(dn, ldapTemplate, ldapSyncConfig);
            ldapUser.setPasswordExpirationDate(userEntry.getPasswordExpirationDate());
            getUserDao().save(ldapUser);
            getUserDao().getEntityManager().flush();

            setUserAsMemberToLdapGroups(ldapUser, new ArrayList<>(ldapUser.getGroups()), ldapTemplate, ldapSyncConfig.getBaseDC());
        } catch (Exception e)
        {
            log.error("Adding User:{} as member to groups in LDAP failed! Rollback changes.", user.getUserId(), e);
            try
            {
                new RetryExecutor().retry(() -> ldapTemplate
                        .unbind(MapperUtils.stripBaseFromDn(user.getDistinguishedName(), ldapSyncConfig.getBaseDC())));
            } catch (Exception ee)
            {
                log.warn("Rollback failed", e);
            }
            log.debug("User entry DN:{} deleted from LDAP", user.getDistinguishedName());
            throw new AcmUserActionFailedException("create LDAP user", null, null, "Creating LDAP user failed!", e);
        }
        return ldapUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser addUserMembersInLdapGroup(String userId, List<String> groups, String directory) throws AcmUserActionFailedException
    {

        AcmUser existingUser = getUserDao().findByUserId(userId);

        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directory));
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

        List<AcmGroup> acmGroups = new ArrayList<>();

        groups.forEach(groupName ->
        {
            AcmGroup group = getGroupDao().findByName(groupName);
            existingUser.addGroup(group);
            acmGroups.add(group);
            log.debug("Set User:{} as member of Group:{}", existingUser.getUserId(), group.getName());
        });
        log.debug("Saving User:{} with DN:{} in database", existingUser.getUserId(), existingUser.getDistinguishedName());
        AcmUser ldapUser = getUserDao().save(existingUser);
        getUserDao().getEntityManager().flush();

        setUserAsMemberToLdapGroups(ldapUser, acmGroups, ldapTemplate, ldapSyncConfig.getBaseDC());

        return ldapUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser removeUserMembersInLdapGroup(String userId, List<String> groups, String directory) throws AcmUserActionFailedException
    {

        AcmUser existingUser = getUserDao().findByUserId(userId);

        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directory));
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

        List<AcmGroup> acmGroups = new ArrayList<>();

        groups.forEach(groupName ->
        {
            AcmGroup group = getGroupDao().findByName(groupName);
            existingUser.removeGroup(group);
            acmGroups.add(group);
            log.debug("Set Group:{} to be removed.", group);
        });
        log.debug("Saving User:{} with DN:{} in database", existingUser.getUserId(), existingUser.getDistinguishedName());
        AcmUser ldapUser = getUserDao().save(existingUser);
        getUserDao().getEntityManager().flush();

        removeUserAsMemberToLdapGroups(ldapUser, acmGroups, ldapTemplate, ldapSyncConfig.getBaseDC());

        return ldapUser;
    }

    private void saveUserRolesInDb(String userId, Set<AcmGroup> groups, Map<String, List<String>> groupToRoleMap)
    {
        Set<String> ldapUserRoles = groups.stream().filter(g -> groupToRoleMap.containsKey(g.getName()))
                .flatMap(g -> groupToRoleMap.get(g.getName()).stream()).collect(Collectors.toSet());
        ldapUserRoles.forEach(role ->
        {
            AcmUserRole userRole = new AcmUserRole();
            userRole.setUserId(userId);
            userRole.setRoleName(role);
            userRole.setUserRoleState("VALID");
            log.debug("Saving AcmUserRole:{} for User:{}", userRole.getRoleName(), userId);
            getUserDao().saveAcmUserRole(userRole);
        });
        getUserDao().getEntityManager().flush();
        log.debug("User roles for User:{} saved", userId);
    }

    private void setUserAsMemberToLdapGroups(AcmUser ldapUser, List<AcmGroup> groups, LdapTemplate ldapTemplate, String baseDC)
            throws AcmUserActionFailedException
    {
        List<AcmGroup> updatedGroups = new ArrayList<>();
        for (AcmGroup group : groups)
        {
            String groupDnStrippedBase = MapperUtils.stripBaseFromDn(group.getDistinguishedName(), baseDC);
            log.debug("Add User:{} with DN:{} as member in Group:{} with DN:{} in LDAP", ldapUser.getUserId(),
                    ldapUser.getDistinguishedName(), group.getName(), group.getDistinguishedName());
            try
            {
                DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                        .retryResult(() -> ldapTemplate.lookupContext(groupDnStrippedBase));
                groupContext.addAttributeValue("member", ldapUser.getDistinguishedName());
                new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                updatedGroups.add(group);
            } catch (Exception e)
            {
                log.debug("Ldap operation failed! Rollback changes on updated ldap groups");
                updatedGroups.forEach(updatedGroup ->
                {
                    String updatedGroupDnStrippedBase = MapperUtils.stripBaseFromDn(updatedGroup.getDistinguishedName(), baseDC);
                    try
                    {
                        DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                                .retryResult(() -> ldapTemplate.lookupContext(updatedGroupDnStrippedBase));
                        groupContext.removeAttributeValue("member", ldapUser.getDistinguishedName());
                        new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                        log.debug("Rollback changes for group:{} with DN:{}", updatedGroup.getName(), updatedGroup.getDistinguishedName());
                    } catch (Exception e1)
                    {
                        log.warn("Failed to rollback changes for group:{} with DN:{}", updatedGroup.getName(),
                                updatedGroup.getDistinguishedName(), e1);
                    }
                });
                throw new AcmUserActionFailedException("updating LDAP Group failed", "LDAP_GROUP", null,
                        "updating LDAP Group with new member failed", e);
            }
        }
    }

    private void removeUserAsMemberToLdapGroups(AcmUser ldapUser, List<AcmGroup> groups, LdapTemplate ldapTemplate, String baseDC)
            throws AcmUserActionFailedException
    {
        List<AcmGroup> updatedGroups = new ArrayList<>();
        for (AcmGroup group : groups)
        {
            String groupDnStrippedBase = MapperUtils.stripBaseFromDn(group.getDistinguishedName(), baseDC);
            log.debug("Remove User:{} with DN:{} as member in Group:{} with DN:{} in LDAP", ldapUser.getUserId(),
                    ldapUser.getDistinguishedName(), group.getName(), group.getDistinguishedName());
            try
            {
                DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                        .retryResult(() -> ldapTemplate.lookupContext(groupDnStrippedBase));
                groupContext.removeAttributeValue("member", ldapUser.getDistinguishedName());
                new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                updatedGroups.add(group);
            } catch (Exception e)
            {
                log.debug("Ldap operation failed! Rollback changes on updated ldap groups");
                updatedGroups.forEach(updatedGroup ->
                {
                    String updatedGroupDnStrippedBase = MapperUtils.stripBaseFromDn(updatedGroup.getDistinguishedName(), baseDC);
                    try
                    {
                        DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                                .retryResult(() -> ldapTemplate.lookupContext(updatedGroupDnStrippedBase));
                        groupContext.addAttributeValue("member", ldapUser.getDistinguishedName());
                        new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                        log.debug("Rollback changes for group:{} with DN:{}", updatedGroup.getName(), updatedGroup.getDistinguishedName());
                    } catch (Exception e1)
                    {
                        log.warn("Failed to rollback changes for group:{} with DN:{}", updatedGroup.getName(),
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
        log.debug("Saving edited User:{} in database", acmUser.getUserId());
        AcmUser existingUser = getUserDao().findByUserId(userId);
        existingUser.setFirstName(acmUser.getFirstName());
        existingUser.setLastName(acmUser.getLastName());
        existingUser.setFullName(String.format("%s %s", acmUser.getFirstName(), acmUser.getLastName()));
        existingUser.setMail(acmUser.getMail());
        acmUser = getUserDao().save(existingUser);
        getUserDao().getEntityManager().flush();
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directory));
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        String strippedBaseDdUserDn = MapperUtils.
                stripBaseFromDn(acmUser.getDistinguishedName(), ldapSyncConfig.getBaseDC());
        try
        {
            DirContextOperations context = new RetryExecutor<DirContextOperations>()
                    .retryResult(() -> ldapTemplate.lookupContext(strippedBaseDdUserDn));
            DirContextOperations editContext = userTransformer.createContextForEditUserEntry(context, acmUser, directory);
            log.debug("Modify User:{} with DN:{} in LDAP", acmUser.getUserId(), acmUser.getDistinguishedName());
            new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(editContext));
            log.debug("User:{} with DN:{} successfully edited in DB and LDAP", acmUser.getUserId(), acmUser.getDistinguishedName());
        } catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
        }
        return acmUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser cloneLdapUser(String userId, AcmUser acmUser, String password, String directory)
            throws AcmUserActionFailedException, AcmLdapActionFailedException
    {
        log.debug("Creating new user [{}] as a clone of [{}]", userId, acmUser.getUserId());
        AcmUser existingUser = getUserDao().findByUserId(userId);
        List<AcmGroup> groups = new ArrayList<>(existingUser.getGroups());
        List<String> newGroups = new ArrayList<>(groups.size());
        for (AcmGroup group : groups)
        {
            newGroups.add(group.getName());
        }

        return createLdapUser(acmUser, newGroups, password, directory);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AcmUser> addExistingLdapUsersToGroup(List<AcmUser> acmUsers, String directoryName, String groupName)
            throws AcmUserActionFailedException, AcmLdapActionFailedException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directoryName));
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        AcmGroup ldapGroup = getGroupDao().findByName(groupName);
        List<AcmUser> ldapUsers = new ArrayList<>();
        for (AcmUser user : acmUsers)
        {
            AcmUser existingUser = getUserDao().findByUserId(user.getUserId());
            log.debug("Adding Group:{} to User:{}", groupName, user.getUserId());
            existingUser.addGroup(ldapGroup);
            log.debug("Saving edited User:{}", user.getUserId());
            AcmUser savedUser = getUserDao().save(existingUser);
            getUserDao().getEntityManager().flush();
            String strippedBaseDCGroupDn = MapperUtils.stripBaseFromDn(ldapGroup.getDistinguishedName(), ldapSyncConfig.getBaseDC());
            DirContextOperations groupContext;
            try
            {
                DirContextOperations gc = new RetryExecutor<DirContextOperations>()
                        .retryResult(() -> ldapTemplate.lookupContext(strippedBaseDCGroupDn));
                gc.addAttributeValue("member", savedUser.getDistinguishedName());
                log.debug("Modify group:{} with DN:{} with new ldap member:{} in LDAP", ldapGroup.getName(),
                        ldapGroup.getDistinguishedName(), savedUser.getUserId(), savedUser.getDistinguishedName());
                new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(gc));
                log.debug("Group:{} with DN:{} modified in LDAP", ldapGroup.getName(), ldapGroup.getDistinguishedName());
                groupContext = gc;
            } catch (Exception e)
            {
                throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
            }
            String strippedBaseDCUserDn = MapperUtils.stripBaseFromDn(savedUser.getDistinguishedName(), ldapSyncConfig.getBaseDC());
            // set memberOf only for OpenLdap, AD sets this property automatically
            if (AcmLdapConstants.LDAP_OPENLDAP.equals(ldapSyncConfig.getDirectoryType()))
            {
                try
                {
                    DirContextOperations uc = new RetryExecutor<DirContextOperations>()
                            .retryResult(() -> ldapTemplate.lookupContext(strippedBaseDCUserDn));
                    uc.addAttributeValue("memberOf", ldapGroup.getDistinguishedName());
                    log.debug("Update User:{} with DN:{} as member of ldap group:{} in LDAP", savedUser.getUserId(),
                            savedUser.getDistinguishedName(), ldapGroup.getDistinguishedName());
                    new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(uc));
                    log.debug("User:{} with DN:{} modified in LDAP", savedUser.getUserId(), savedUser.getDistinguishedName());
                } catch (Exception e)
                {
                    log.debug("Updating user:{} failed! Rollback ldap changes for group:{}", savedUser.getDistinguishedName(),
                            ldapGroup.getDistinguishedName());
                    groupContext.removeAttributeValue("member", savedUser.getDistinguishedName());
                    try
                    {
                        new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                    } catch (Exception ee)
                    {
                        log.warn("Rollback failed", ee);
                    }
                    throw new AcmUserActionFailedException("updating LDAP User with new memberOf attribute failed", "LDAP_GROUP", null,
                            "updating LDAP User with new memberOf attribute failed", e);
                }
            }
            ldapUsers.add(savedUser);
        }
        return ldapUsers;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmUser removeLdapUser(String userId, String directory) throws AcmLdapActionFailedException
    {
        AcmUser existingUser = getUserDao().findByUserId(userId);
        List<AcmGroup> lookupGroups = getGroupDao().findByUserMember(existingUser);

        if (lookupGroups != null)
        {
            for (AcmGroup group : lookupGroups)
            {
                group.removeMember(existingUser);
                getGroupDao().save(group);
            }
        }

        log.debug("Removing User:{} from database", existingUser.getUserId());
        getUserDao().markUserAsDeleted(userId);

        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directory));
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

        try
        {
            log.debug("Deleting User:{} with DN:{} in LDAP", existingUser.getUserId(), existingUser.getDistinguishedName());
            new RetryExecutor().retry(() -> ldapTemplate
                    .unbind(MapperUtils.stripBaseFromDn(existingUser.getDistinguishedName(), ldapSyncConfig.getBaseDC())));
            log.debug("User:{} with DN:{} successfully deleted in DB and LDAP", existingUser.getUserId(),
                    existingUser.getDistinguishedName());
        } catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
        }
        return existingUser;
    }

    private String buildDnForUser(String userFullName, String userId, AcmLdapSyncConfig syncConfig)
    {
        String uidAttr = String.format("%s=%s", "uid", userId);
        String cnAttr = String.format("%s=%s", "cn", userFullName);
        String dnAttr = AcmLdapConstants.LDAP_OPENLDAP.equals(syncConfig.getDirectoryType()) ? uidAttr : cnAttr;
        return String.format("%s,%s,%s", dnAttr, syncConfig.getUserSearchBase(), syncConfig.getBaseDC());
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

    public SpringLdapUserDao getLdapUserDao()
    {
        return ldapUserDao;
    }

    public void setLdapUserDao(SpringLdapUserDao ldapUserDao)
    {
        this.ldapUserDao = ldapUserDao;
    }
}
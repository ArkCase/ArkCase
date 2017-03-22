package com.armedia.acm.services.users.service.ldap;


import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
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

    private SpringContextHolder acmContextHolder;

    private LdapEntryTransformer userTransformer;

    @Transactional
    public AcmUser createLdapUser(AcmUser user, List<String> groupNames, String password, String directoryName) throws AcmUserActionFailedException, AcmLdapActionFailedException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directoryName));

        Map<String, String> roleToGroup = ldapSyncConfig.getRoleToGroupMap();
        Map<String, List<String>> groupToRoleMap = LdapSyncService.reverseRoleToGroupMap(roleToGroup);

        String dn = buildDnForUser(user.getUserId(), ldapSyncConfig.getUserSearchBase(), ldapSyncConfig.getBaseDC());
        user.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));
        user.setDistinguishedName(dn);
        user.setUserDirectoryName(directoryName);
        user.setUserState("VALID");
        user.setUid(user.getUserId());

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

        DirContextAdapter context = userTransformer.createContextForNewUserEntry(directoryName, user, password,
                ldapSyncConfig.getBaseDC());
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        try
        {
            log.debug("Save User:{} with DN:{} in LDAP", ldapUser.getUserId(), ldapUser.getDistinguishedName());
            new RetryExecutor().retry(() -> ldapTemplate.bind(context));
        } catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
        }

        try
        {
            setUserAsMemberToLdapGroups(ldapUser, ldapTemplate, ldapSyncConfig.getBaseDC());
        } catch (Exception e)
        {
            log.error("Adding User:{} as member to groups in LDAP failed! Rollback changes.", user.getUserId(), e);
            try
            {
                new RetryExecutor().retry(() -> ldapTemplate.unbind(MapperUtils.stripBaseFromDn(user.getDistinguishedName(),
                        ldapSyncConfig.getBaseDC())));
            } catch (Exception ee)
            {
                log.warn("Rollback failed", e);
            }
            log.debug("User entry DN:{} deleted from LDAP", user.getDistinguishedName());
            throw new AcmUserActionFailedException("create LDAP user", null, null, "Creating LDAP user failed!", e);
        }
        return ldapUser;
    }

    private void saveUserRolesInDb(String userId, Set<AcmGroup> groups, Map<String, List<String>> groupToRoleMap)
    {
        Set<String> ldapUserRoles = groups.stream()
                .filter(g -> groupToRoleMap.containsKey(g.getName()))
                .flatMap(g -> groupToRoleMap.get(g.getName()).stream())
                .collect(Collectors.toSet());

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

    private void setUserAsMemberToLdapGroups(AcmUser ldapUser, LdapTemplate ldapTemplate, String baseDC)
            throws AcmUserActionFailedException
    {
        List<AcmGroup> updatedGroups = new ArrayList<>();
        for (AcmGroup group : ldapUser.getGroups())
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
                            String updatedGroupDnStrippedBase = MapperUtils
                                    .stripBaseFromDn(updatedGroup.getDistinguishedName(), baseDC);
                            try
                            {
                                DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                                        .retryResult(() -> ldapTemplate.lookupContext(updatedGroupDnStrippedBase));
                                groupContext.removeAttributeValue("member", ldapUser.getDistinguishedName());
                                new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                                log.debug("Rollback changes for group:{} with DN:{}", updatedGroup.getName(),
                                        updatedGroup.getDistinguishedName());
                            } catch (Exception e1)
                            {
                                log.warn("Failed to rollback changes for group:{} with DN:{}", updatedGroup.getName(),
                                        updatedGroup.getDistinguishedName(), e1);
                            }
                        }
                );
                throw new AcmUserActionFailedException("updating LDAP Group failed", "LDAP_GROUP", null,
                        "updating LDAP Group with new member failed", e);
            }
        }
    }

    @Transactional
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

    @Transactional
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

            String strippedBaseDCUserDn = MapperUtils.stripBaseFromDn(savedUser.getDistinguishedName(),
                    ldapSyncConfig.getBaseDC());
            DirContextOperations userContext;
            try
            {
                DirContextOperations uc = new RetryExecutor<DirContextOperations>()
                        .retryResult(() -> ldapTemplate.lookupContext(strippedBaseDCUserDn));
                uc.addAttributeValue("memberOf", ldapGroup.getDistinguishedName());
                log.debug("Update User:{} with DN:{} as member of ldap group:{} in LDAP", savedUser.getUserId(),
                        savedUser.getDistinguishedName(), ldapGroup.getDistinguishedName());
                new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(uc));
                log.debug("User:{} with DN:{} modified in LDAP", savedUser.getUserId(), savedUser.getDistinguishedName());
                userContext = uc;
            } catch (Exception e)
            {
                throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
            }

            String strippedBaseDCGroupDn = MapperUtils.stripBaseFromDn(ldapGroup.getDistinguishedName(),
                    ldapSyncConfig.getBaseDC());
            try
            {
                DirContextOperations groupContext = new RetryExecutor<DirContextOperations>()
                        .retryResult(() -> ldapTemplate.lookupContext(strippedBaseDCGroupDn));
                groupContext.addAttributeValue("member", savedUser.getDistinguishedName());
                log.debug("Modify group:{} with DN:{} with new ldap member:{} in LDAP", ldapGroup.getName(),
                        ldapGroup.getDistinguishedName(), savedUser.getUserId(), savedUser.getDistinguishedName());
                new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(groupContext));
                log.debug("Group:{} with DN:{} modified in LDAP", ldapGroup.getName(), ldapGroup.getDistinguishedName());
            } catch (Exception e)
            {
                log.debug("Updating group:{} failed! Rollback ldap changes for user:{}",
                        ldapGroup.getDistinguishedName(), savedUser.getDistinguishedName());
                userContext.removeAttributeValue("memberOf", ldapGroup.getDistinguishedName());
                try
                {
                    new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(userContext));
                } catch (Exception ee)
                {
                    log.warn("Rollback failed", ee);
                }
                throw new AcmUserActionFailedException("updating LDAP Group with new member failed", "LDAP_GROUP", null,
                        "updating LDAP Group with new member failed", e);
            }
            ldapUsers.add(savedUser);
        }
        return ldapUsers;
    }

    private String buildDnForUser(String userId, String userSearchBase, String baseDC)
    {
        return String.format("uid=%s,%s,%s", userId, userSearchBase, baseDC);
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
}

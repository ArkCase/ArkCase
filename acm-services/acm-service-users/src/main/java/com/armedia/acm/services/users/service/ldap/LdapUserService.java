package com.armedia.acm.services.users.service.ldap;


import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Name;
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

    @Transactional
    public AcmUser createLdapUser(AcmUser user, List<String> groupNames, String password, String directoryName) throws AcmUserActionFailedException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directoryName));

        Map<String, String> roleToGroup = ldapSyncConfig.getRoleToGroupMap();
        Map<String, List<String>> groupToRoleMap = LdapSyncService.reverseRoleToGroupMap(roleToGroup);

        Name dn = buildDnForUser(user.getUserId(), ldapSyncConfig.getUserSearchBase(), ldapSyncConfig.getBaseDC());
        user.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));
        user.setDistinguishedName(dn.toString());
        user.setUserDirectoryName(ldapSyncConfig.getDirectoryName());
        user.setUserState("VALID");
        user.setUid(user.getUserId());

        groupNames.forEach(groupName ->
        {
            AcmGroup group = getGroupDao().findByName(groupName);
            user.addGroup(group);
            log.debug("Set User:{} as member of Group:{}", user.getUserId(), group.getName());
        });

        AcmUser ldapUser = getUserDao().save(user);
        log.debug("User:{} saved", user.getUserId());

        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

        saveUserRolesInDb(ldapUser.getUserId(), ldapUser.getGroups(), groupToRoleMap);

        DirContextAdapter context = createContextForNewUser(user, password, ldapSyncConfig.getBaseDC());
        log.debug("Save User:{} with DN:{} in LDAP", ldapUser.getUserId(), ldapUser.getDistinguishedName());
        ldapTemplate.bind(context);

        try
        {
            setUserAsMemberToLdapGroups(ldapUser, ldapTemplate, ldapSyncConfig.getBaseDC());
        } catch (Exception e)
        {
            log.error("Adding User:{} as member to groups in LDAP failed! Rollback changes.", user.getUserId(), e);
            ldapTemplate.unbind(MapperUtils.stripBaseFromDn(user.getDistinguishedName(), ldapSyncConfig.getBaseDC()));
            log.debug("User entry DN:{} deleted from LDAP", user.getDistinguishedName());
            throw new AcmUserActionFailedException("create LDAP user", null, null, "Creating LDAP user failed!", e);
        }
        return ldapUser;
    }

    private void saveUserRolesInDb(String userId, Set<AcmGroup> groups, Map<String, List<String>> groupToRoleMap)
    {

        Set<String> ldapUserRoles = groups.stream().
                flatMap(g ->
                        groupToRoleMap.get(g.getName()).stream()
                ).collect(Collectors.toSet());

        ldapUserRoles.forEach(role ->
        {
            AcmUserRole userRole = new AcmUserRole();
            userRole.setUserId(userId);
            userRole.setRoleName(role);
            userRole.setUserRoleState("VALID");
            log.debug("Saving AcmUserRole:{} for User:{}", userRole.getRoleName(), userId);
            getUserDao().saveAcmUserRole(userRole);
        });
        log.debug("User roles for User:{} saved", userId);
    }

    private void setUserAsMemberToLdapGroups(AcmUser ldapUser, LdapTemplate ldapTemplate, String baseDC)
    {
        ldapUser.getGroups().forEach(group ->
        {
            String groupDnStrippedBase = MapperUtils.stripBaseFromDn(group.getDistinguishedName(), baseDC);
            log.debug("Add User:{} with DN:{} as member of Group:{} with DN:{} in LDAP", ldapUser.getUserId(),
                    ldapUser.getDistinguishedName(), group.getName(), group.getDistinguishedName());
            DirContextOperations groupContext = ldapTemplate.lookupContext(groupDnStrippedBase);
            groupContext.addAttributeValue("member", ldapUser.getDistinguishedName());
            ldapTemplate.modifyAttributes(groupContext);
        });
    }

    @Transactional
    public AcmUser editLdapUser(AcmUser acmUser, String directoryName)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directoryName));
        log.debug("Saving edited User:{} in database", acmUser.getUserId());
        AcmUser existingUser = getUserDao().findByUserId(acmUser.getUserId());
        existingUser.setFirstName(acmUser.getFirstName());
        existingUser.setLastName(acmUser.getLastName());
        existingUser.setFullName(String.format("%s %s", acmUser.getFirstName(), acmUser.getLastName()));
        existingUser.setMail(acmUser.getMail());
        acmUser = getUserDao().save(existingUser);
        log.debug("User:{} saved", acmUser.getUserId());

        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

        DirContextOperations context = ldapTemplate.lookupContext(MapperUtils.stripBaseFromDn(acmUser.getDistinguishedName(), ldapSyncConfig.getBaseDC()));
        context = createContextForEditUser(acmUser, context);
        log.debug("Modify User:{} with DN:{} in LDAP", acmUser.getUserId(), acmUser.getDistinguishedName());
        ldapTemplate.modifyAttributes(context);
        log.debug("User:{} with DN:{} successfully edited", acmUser.getUserId(), acmUser.getDistinguishedName());
        return acmUser;
    }

    @Transactional
    public List<AcmUser> addExistingLdapUsersToGroup(List<AcmUser> acmUsers, String directoryName, String groupName)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).
                get(String.format("%s_sync", directoryName));

        AcmGroup ldapGroup = getGroupDao().findByName(groupName);

        List<AcmUser> ldapUsers = acmUsers.stream().map(user ->
        {
            AcmUser existingUser = getUserDao().findByUserId(user.getUserId());
            log.debug("Adding Group:{} to User:{}", groupName, user.getUserId());
            existingUser.addGroup(ldapGroup);
            log.debug("Saving edited User:{} in database...", user.getUserId());
            AcmUser savedUser = getUserDao().save(existingUser);

            LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

            String strippedBaseDCUserDn = MapperUtils.stripBaseFromDn(savedUser.getDistinguishedName(), ldapSyncConfig.getBaseDC());
            DirContextOperations context = ldapTemplate.lookupContext(strippedBaseDCUserDn);
            context.addAttributeValue("memberOf", ldapGroup.getDistinguishedName());
            log.debug("Modify User:{} with DN:{} in LDAP", user.getUserId(), user.getDistinguishedName());
            ldapTemplate.modifyAttributes(context);
            log.debug("User:{} with DN:{} modified in LDAP", user.getUserId(), user.getDistinguishedName());
            return savedUser;
        }).collect(Collectors.toList());
        return ldapUsers;
    }

    private DirContextOperations createContextForEditUser(AcmUser user, DirContextOperations context)
    {
        context.setAttributeValue("cn", String.format("%s %s", user.getFirstName(), user.getLastName()));
        context.setAttributeValue("givenName", user.getFirstName());
        context.setAttributeValue("sn", user.getLastName());
        context.setAttributeValue("mail", user.getMail());
        return context;
    }

    private DirContextAdapter createContextForNewUser(AcmUser user, String password, String baseDC)
    {
        DirContextAdapter context = new DirContextAdapter(MapperUtils.stripBaseFromDn(user.getDistinguishedName(), baseDC));
        context.setAttributeValues("objectClass", new String[]{"top", "person", "inetOrgPerson",
                "organizationalPerson", "posixAccount", "uacPerson", "shadowAccount"});
        context.setAttributeValue("cn", String.format("%s %s", user.getFirstName(), user.getLastName()));
        context.setAttributeValue("givenName", user.getFirstName());
        context.setAttributeValue("sn", user.getLastName());
        context.setAttributeValue("uid", user.getUserId());
        context.setAttributeValue("mail", user.getMail());
        user.getGroups().forEach(group ->
                context.setAttributeValue("memberOf", group.getDistinguishedName()));
        context.setAttributeValue("userAccountControl", "1");
        context.setAttributeValue("userPassword", password);
        long timestamp = System.currentTimeMillis();
        context.setAttributeValue("uidNumber", Long.toString(timestamp));
        context.setAttributeValue("gidNumber", Long.toString(timestamp));
        context.setAttributeValue("homeDirectory", String.format("/home/%s", user.getUserId()));
        context.setAttributeValue("shadowWarning", "7");
        context.setAttributeValue("shadowLastChange", "12994");
        context.setAttributeValue("shadowMax", "99999");
        return context;
    }

    private Name buildDnForUser(String userId, String userSearchBase, String baseDC)
    {
        String dnPath = String.format("uid=%s,%s,%s", userId, userSearchBase, baseDC);
        return new DistinguishedName(dnPath);
    }

    public String getUserDirectoryName(String userId) throws AcmObjectNotFoundException
    {
        AcmUser user = getUserDao().findByUserId(userId);
        if (user != null)
        {
            return user.getUserDirectoryName();
        }
        throw new AcmObjectNotFoundException("USER", null, String.format("User with id:[%s] not found", userId));
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

}

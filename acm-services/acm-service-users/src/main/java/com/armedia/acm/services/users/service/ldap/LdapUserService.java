package com.armedia.acm.services.users.service.ldap;


import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
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
import java.util.stream.Collectors;

public class LdapUserService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private SpringLdapDao ldapDao;

    private UserDao userDao;

    private AcmGroupDao groupDao;

    private SpringContextHolder acmContextHolder;

    @Transactional
    public AcmUser createLdapUser(AcmUser user, String groupName, String password)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).get("armedia_sync");

        Map<String, String> roleToGroup = ldapSyncConfig.getRoleToGroupMap();
        Map<String, List<String>> groupToRoleMap = LdapSyncService.reverseRoleToGroupMap(roleToGroup);

        Name dn = buildDnForUser(user.getUserId(), ldapSyncConfig.getUserSearchBase());
        user.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));
        user.setDistinguishedName(dn.toString());
        user.setUserDirectoryName(ldapSyncConfig.getDirectoryName());

        AcmGroup group = getGroupDao().findByName(groupName);
        user.addGroup(group);

        log.debug("Saving User:{} in database...", user.getUserId());
        AcmUser ldapUser = getUserDao().save(user);
        log.debug("User saved");

        List<String> userRoles = groupToRoleMap.get(groupName);
        userRoles.forEach(role ->
        {
            AcmUserRole userRole = new AcmUserRole();
            userRole.setUserId(ldapUser.getUserId());
            userRole.setRoleName(role);
            userRole.setUserRoleState("VALID");
            log.debug("Saving AcmUserRole: {}", userRole);
            getUserDao().saveAcmUserRole(userRole);
        });
        log.debug("User roles saved");

        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);
        DirContextAdapter context = createContextForNewUser(user, password,
                group.getDistinguishedName());
        log.debug("Save in LDAP...");
        ldapTemplate.bind(context);
        return ldapUser;
    }

    @Transactional
    public AcmUser editLdapUser(AcmUser acmUser)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).get("armedia_sync");
        log.debug("Saving edited User:{} in database...", acmUser.getUserId());
        AcmUser existingUser = getUserDao().findByUserId(acmUser.getUserId());
        existingUser.setFirstName(acmUser.getFirstName());
        existingUser.setLastName(acmUser.getLastName());
        existingUser.setFullName(String.format("%s %s", acmUser.getFirstName(), acmUser.getLastName()));
        existingUser.setMail(acmUser.getMail());
        acmUser = getUserDao().saveAcmUser(existingUser);

        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

        DirContextOperations context = ldapTemplate.lookupContext(acmUser.getDistinguishedName());
        context = createContextForEditUser(acmUser, context);
        log.debug("Modify User in LDAP...");
        ldapTemplate.modifyAttributes(context);
        return acmUser;
    }

    @Transactional
    public List<AcmUser> addExistingLdapUsersToGroup(List<AcmUser> acmUsers, String groupName)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).get("armedia_sync");

        AcmGroup ldapGroup = getGroupDao().findByName(groupName);

        List<AcmUser> ldapUsers = acmUsers.stream().map(user ->
        {
            AcmUser existingUser = getUserDao().findByUserId(user.getUserId());
            log.debug("Adding Group:{} to User:{}", groupName, user.getUserId());
            existingUser.addGroup(ldapGroup);
            log.debug("Saving edited User:{} in database...", user.getUserId());
            AcmUser savedUser = getUserDao().saveAcmUser(existingUser);

            LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(ldapSyncConfig);

            DirContextOperations context = ldapTemplate.lookupContext(savedUser.getDistinguishedName());
            context.addAttributeValue("memberOf", ldapGroup.getDistinguishedName());
            log.debug("Modify User in LDAP...");
            ldapTemplate.modifyAttributes(context);
            log.debug("LDAP change made");
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

    private DirContextAdapter createContextForNewUser(AcmUser user, String password, String groupDn)
    {
        DirContextAdapter context = new DirContextAdapter(user.getDistinguishedName());
        context.setAttributeValues("objectClass", new String[]{"top", "person", "inetOrgPerson",
                "organizationalPerson", "simulatedMicrosoftSecurityPrincipal", "uacPerson"});
        context.setAttributeValue("cn", String.format("%s %s", user.getFirstName(), user.getLastName()));
        context.setAttributeValue("givenName", user.getFirstName());
        context.setAttributeValue("sn", user.getLastName());
        context.setAttributeValue("sAMAccountName", user.getUserId());
        context.setAttributeValue("mail", user.getMail());
        context.setAttributeValue("memberOf", groupDn);
        context.setAttributeValue("userAccountControl", "1");
        context.setAttributeValue("userPassword", password);
        return context;
    }

    private Name buildDnForUser(String userId, String userSearchBase)
    {
        String dnPath = String.format("sAMAccountName=%s,%s", userId, userSearchBase);
        return new DistinguishedName(dnPath);
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

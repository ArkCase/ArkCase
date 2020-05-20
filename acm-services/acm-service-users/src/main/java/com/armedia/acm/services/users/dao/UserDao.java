package com.armedia.acm.services.users.dao;

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

import com.armedia.acm.core.LanguageSettingsConfig;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class UserDao extends AcmAbstractDao<AcmUser>
{
    private static String DEFAULT_LOCALE_CODE = null;
    @PersistenceContext
    private EntityManager entityManager;
    private LanguageSettingsConfig languageSettingsConfig;
    private Logger log = LogManager.getLogger(getClass());

    public void init()
    {
        if (languageSettingsConfig != null)
        {
            if (StringUtils.isNotBlank(languageSettingsConfig.getLocaleCode()))
            {
                DEFAULT_LOCALE_CODE = languageSettingsConfig.getLocaleCode();
            }
            else if (StringUtils.isNotBlank(languageSettingsConfig.getDefaultLocale()))
            {
                DEFAULT_LOCALE_CODE = languageSettingsConfig.getDefaultLocale();
            }
        }
        else
        {
            DEFAULT_LOCALE_CODE = "en";
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AcmUser save(AcmUser acmUser)
    {
        // Converting from LdapUser to AcmUser lang property is null, so we need to set it
        if (acmUser.getLang() == null)
        {
            // get lang from existing user
            AcmUser existingUser = findByUserId(acmUser.getUserId());
            if (existingUser != null)
            {
                acmUser.setLang(existingUser.getLang());
            }
            else
            {
                // set default lang
                acmUser.setLang(DEFAULT_LOCALE_CODE);
            }
        }
        return super.save(acmUser);
    }

    public String getDefaultUserLang()
    {
        return DEFAULT_LOCALE_CODE;
    }

    public AcmUser findByUserId(String userId)
    {
        userId = userId.toLowerCase();
        return getEntityManager().find(AcmUser.class, userId);
    }

    public AcmUser findByUserIdAnyCase(String userId)
    {
        String jpql = "SELECT u FROM AcmUser u WHERE LOWER(u.userId) = :lowerUserId";

        String userIdLcs = userId.toLowerCase();
        TypedQuery<AcmUser> query = getEm().createQuery(jpql, AcmUser.class);
        query.setParameter("lowerUserId", userIdLcs);

        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            log.warn("There is no user with id [{}]", userIdLcs);
        }
        catch (NonUniqueResultException e)
        {
            log.warn("There is no unique user found with userId [{}]. More than one user has this name", userIdLcs);
        }
        catch (Exception e)
        {
            log.error("Error while retrieving user by user id [{}]", userIdLcs, e);
        }
        return null;
    }

    @Cacheable(value = "quiet-user-cache")
    public AcmUser quietFindByUserId(String userId)
    {
        if (userId == null || userId.trim().isEmpty())
        {
            return null;
        }

        try
        {
            AcmUser user = findByUserId(userId);
            if (user != null)
            {
                return user;
            }
        }
        catch (PersistenceException pe)
        {
            log.error("Could not find user record: {}", pe.getMessage(), pe);
        }

        return null;
    }

    public List<AcmRole> findAllRoles()
    {
        Query roleQuery = getEntityManager().createQuery("SELECT role FROM AcmRole role");

        return roleQuery.getResultList();
    }

    public List<AcmRole> findAllRolesByRoleType(AcmRoleType acmRoleType)
    {
        Query roleQuery = getEntityManager().createQuery("SELECT role FROM AcmRole role WHERE role.roleType= :roleType");
        roleQuery.setParameter("roleType", acmRoleType);
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }

    public void markAllUsersInvalid(String directoryName)
    {
        Query markInvalid = getEntityManager()
                .createQuery("UPDATE AcmUser au set au.userState = :state, au.modified = :now WHERE au.userDirectoryName = :directoryName");
        markInvalid.setParameter("state", AcmUserState.INVALID);
        markInvalid.setParameter("now", new Date());
        markInvalid.setParameter("directoryName", directoryName);
        markInvalid.executeUpdate();
    }

    public AcmRole saveAcmRole(AcmRole in)
    {
        AcmRole existing = getEntityManager().find(AcmRole.class, in.getRoleName());
        if (existing == null)
        {
            log.debug("Saving AcmRole [{}]", in.getRoleName());
            getEntityManager().persist(in);
        }
        return in;
    }

    public void deleteAcmRole(String roleName)
    {
        AcmRole existing = entityManager.find(AcmRole.class, roleName);
        if (existing != null)
        {
            entityManager.remove(existing);
        }
    }

    @Transactional
    public AcmUser markUserInvalid(String id)
    {
        AcmUser user = findByUserId(id);
        if (user != null)
        {
            user.setUserState(AcmUserState.INVALID);
            user.setDeletedAt(new Date());
        }
        return user;
    }

    public boolean isUserPasswordExpired(String principal)
    {
        log.debug("Check password expiration for user [{}]", principal);

        AcmUser user = findByUserId(principal);
        if (user == null)
        {
            log.debug("User [{}] not found!", principal);
            return false;
        }

        if (user.getUserState() != AcmUserState.VALID)
        {
            return false;
        }
        LocalDate userPasswordExpirationDate = user.getPasswordExpirationDate();
        if (userPasswordExpirationDate != null)
        {
            return userPasswordExpirationDate.isBefore(LocalDate.now());
        }
        log.info("Password expiration date is not set for user [{}]", principal);
        return false;
    }

    public AcmUser findByPasswordResetToken(String token)
    {
        String select = "SELECT user FROM AcmUser user WHERE user.passwordResetToken.token = :token";
        TypedQuery<AcmUser> query = getEm().createQuery(select, AcmUser.class);
        query.setParameter("token", token);
        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException e)
        {
            log.error("User with password reset token: [{}] not found!", token);
            return null;
        }
    }

    public AcmUser findByUid(String uid)
    {
        String select = "SELECT user FROM AcmUser user WHERE user.uid = :uid";
        TypedQuery<AcmUser> query = getEm().createQuery(select, AcmUser.class);
        query.setParameter("uid", uid);
        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException e)
        {
            log.error("User with uid : [{}] not found!", uid);
            return null;
        }
    }

    public List<AcmUser> findByEmailAddress(String email)
    {
        String select = "SELECT user FROM AcmUser user WHERE LOWER(user.mail) = :email";
        TypedQuery<AcmUser> query = getEm().createQuery(select, AcmUser.class);
        query.setParameter("email", email.toLowerCase());
        return query.getResultList();
    }

    public AcmUser findByUserIdAndEmailAddress(String userId, String email)
    {
        String select = "SELECT user FROM AcmUser user WHERE user.userId = :userId AND LOWER(user.mail) = :email";
        TypedQuery<AcmUser> query = getEm().createQuery(select, AcmUser.class);
        query.setParameter("email", email.toLowerCase());
        query.setParameter("userId", userId.toLowerCase());
        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            log.warn("There is no user with id [{}] and email [{}]", userId, email);
        }
        catch (NonUniqueResultException e)
        {
            log.warn("There is no unique user found with userId [{}] and email [{}]. More than one user has this name or address", userId,
                    email);
        }
        catch (Exception e)
        {
            log.error("Error while retrieving user by user id [{}] and email [{}]", userId, email, e);
        }
        return null;
    }

    public AcmUser findByPrefixAndEmailAddress(String userPrefix, String email)
    {
        String select = "SELECT user FROM AcmUser user WHERE LOWER(user.mail) = :email";
        if (StringUtils.isNotBlank(userPrefix))
        {
            select += " AND user.userId LIKE :userId";
        }

        TypedQuery<AcmUser> query = getEm().createQuery(select, AcmUser.class);
        query.setParameter("email", email.toLowerCase());
        if (StringUtils.isNotBlank(userPrefix))
        {
            query.setParameter("userId", userPrefix.toLowerCase() + "%");
        }

        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            log.warn("There is no user with prefix [{}] and email [{}]", userPrefix, email);
        }
        catch (NonUniqueResultException e)
        {
            log.warn("There is no unique user found with prefix [{}] and email [{}]. More than one user has this name or address",
                    userPrefix,
                    email);
        }
        catch (Exception e)
        {
            log.error("Error while retrieving user by prefix [{}] and email [{}]", userPrefix, email, e);
        }
        return null;
    }

    public List<AcmUser> findByPrefix(String prefix)
    {
        TypedQuery<AcmUser> users = getEm()
                .createQuery("SELECT acmUser FROM AcmUser acmUser "
                        + "WHERE acmUser.userId LIKE :prefix", AcmUser.class);
        users.setParameter("prefix", prefix + "%");
        return users.getResultList();
    }

    public List<AcmUser> findByDirectory(String directoryName)
    {
        TypedQuery<AcmUser> allUsersInDirectory = getEm()
                .createQuery("SELECT DISTINCT acmUser FROM AcmUser acmUser LEFT JOIN FETCH acmUser.groups "
                        + "WHERE acmUser.userDirectoryName = :directoryName", AcmUser.class);
        allUsersInDirectory.setParameter("directoryName", directoryName);
        return allUsersInDirectory.getResultList();
    }

    public List<AcmUser> findByState(AcmUserState state)
    {
        TypedQuery<AcmUser> allUsersByState = getEm()
                .createQuery("SELECT acmUser FROM AcmUser acmUser WHERE acmUser.userState = :state",
                        AcmUser.class);
        allUsersByState.setParameter("state", state);
        return allUsersByState.getResultList();
    }

    public AcmUser findBysAMAccountName(String sAMAccountName)
    {
        String select = "SELECT acmUser FROM AcmUser acmUser WHERE acmUser.sAMAccountName = :sAMAccountName";
        TypedQuery<AcmUser> query = getEm().createQuery(select, AcmUser.class);
        query.setParameter("sAMAccountName", sAMAccountName);
        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException e)
        {
            log.error("User with sAMAccount Name : [{}] not found!", sAMAccountName);
            return null;
        }
    }

    public AcmUser findByUserPrincipalName(String userPrincipalName)
    {
        String select = "SELECT acmUser FROM AcmUser acmUser WHERE acmUser.userPrincipalName = :userPrincipalName";
        TypedQuery<AcmUser> query = getEm().createQuery(select, AcmUser.class);
        query.setParameter("userPrincipalName", userPrincipalName);
        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException e)
        {
            log.error("User with user principal name : [{}] not found!", userPrincipalName);
            return null;
        }
    }

    public AcmUser findByDistinguishedName(String distinguishedName)
    {
        String select = "SELECT acmUser FROM AcmUser acmUser WHERE acmUser.distinguishedName = :distinguishedName";
        TypedQuery<AcmUser> query = getEm().createQuery(select, AcmUser.class);
        query.setParameter("distinguishedName", distinguishedName);
        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException e)
        {
            log.error("User with distinguished name : [{}] not found!", distinguishedName);
            return null;
        }
    }

    @Transactional
    public AcmUser persistUser(AcmUser acmUser)
    {
        acmUser.setLang(DEFAULT_LOCALE_CODE);
        getEm().persist(acmUser);
        return acmUser;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    protected void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    protected Class getPersistenceClass()
    {
        return AcmUser.class;
    }

    public Long getUserCount(LocalDateTime until)
    {
        String queryText = "SELECT COUNT(acmUser) FROM AcmUser acmUser where acmUser.created < :until";

        Query query = getEm().createQuery(queryText);
        query.setParameter("until", Date.from(ZonedDateTime.of(until, ZoneId.systemDefault()).toInstant()));
        return (Long) query.getSingleResult();
    }

    /**
     * @param languageSettingsConfig
     *            the languageSettingsConfig to set
     */
    public void setLanguageSettingsConfig(LanguageSettingsConfig languageSettingsConfig)
    {
        this.languageSettingsConfig = languageSettingsConfig;
    }
}
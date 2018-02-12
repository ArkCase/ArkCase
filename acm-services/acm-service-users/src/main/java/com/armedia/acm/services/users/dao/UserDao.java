package com.armedia.acm.services.users.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.config.model.AcmConfig;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class UserDao extends AcmAbstractDao<AcmUser>
{
    @PersistenceContext
    private EntityManager entityManager;

    private Cache quietUserLookupCache;

    private List<AcmConfig> configList;

    private static String DEFAULT_LOCALE_CODE = null;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void init()
    {
        Optional<AcmConfig> localeSettings = configList.stream().filter(config -> config.getConfigName().equals("languageSettings"))
                .findFirst();
        if (localeSettings.isPresent())
        {
            String settings = localeSettings.get().getConfigAsJson();
            Configuration configuration = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS)
                    .jsonProvider(new JacksonJsonNodeJsonProvider()).mappingProvider(new JacksonMappingProvider()).build();
            DEFAULT_LOCALE_CODE = JsonPath.using(configuration).parse(settings).read("$.defaultLocale", String.class);
        }
        else
        {
            DEFAULT_LOCALE_CODE = Locale.getDefault().getLanguage();
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
            Cache.ValueWrapper found = getQuietUserLookupCache().get(userId);

            if (found != null && found.get() != null)
            {
                return (AcmUser) found.get();
            }
            else
            {
                AcmUser user = findByUserId(userId);
                if (user != null)
                {
                    getQuietUserLookupCache().put(userId, user);
                    return user;
                }
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

        return (List<AcmRole>) roleQuery.getResultList();
    }

    public List<AcmRole> findAllRolesByRoleType(AcmRoleType acmRoleType)
    {
        Query roleQuery = getEntityManager().createQuery("SELECT role FROM AcmRole role WHERE role.roleType= :roleType");
        roleQuery.setParameter("roleType", acmRoleType.getRoleName());
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

    public Cache getQuietUserLookupCache()
    {
        return quietUserLookupCache;
    }

    public void setQuietUserLookupCache(Cache quietUserLookupCache)
    {
        this.quietUserLookupCache = quietUserLookupCache;
    }

    public List<AcmConfig> getConfigList()
    {
        return configList;
    }

    public void setConfigList(List<AcmConfig> configList)
    {
        this.configList = configList;
    }
}

package com.armedia.acm.services.users.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.config.model.AcmConfig;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.AcmUserRolePrimaryKey;
import com.armedia.acm.services.users.model.AcmUserRoleState;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
        } else
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
            } else
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
            log.warn("There is no unique user found with iod [{}]. More than one user has this name", userIdLcs);
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
            } else
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

        List<AcmRole> retval = roleQuery.getResultList();

        return retval;
    }

    public List<AcmRole> findAllRolesByRoleType(AcmRoleType acmRoleType)
    {
        Query roleQuery = getEntityManager().createQuery("SELECT role FROM AcmRole role WHERE role.roleType= :roleType");
        roleQuery.setParameter("roleType", acmRoleType.getRoleName());
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }

    public List<AcmRole> findAllRolesByUser(String userId)
    {
        Query roleQuery = getEntityManager().createQuery("SELECT acmRole FROM AcmRole acmRole " + "WHERE acmRole.roleName IN "
                + "(SELECT userRole.roleName FROM AcmUserRole userRole " + "WHERE userRole.userId= :userId "
                + "AND userRole.userRoleState = :userRoleState)");
        roleQuery.setParameter("userId", userId);
        roleQuery.setParameter("userRoleState", AcmUserRoleState.VALID);
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }

    public List<AcmRole> findAllRolesByUserAndRoleType(String userId, AcmRoleType acmRoleType)
    {
        Query roleQuery = getEntityManager().createQuery("SELECT acmRole FROM AcmRole acmRole " + "WHERE acmRole.roleName IN "
                + "(SELECT userRole.roleName FROM AcmUserRole userRole " + "WHERE userRole.userId= :userId "
                + "AND userRole.userRoleState = :userRoleState) " + "AND acmRole.roleType = :roleType");
        roleQuery.setParameter("userId", userId);
        roleQuery.setParameter("roleType", acmRoleType.getRoleName());
        roleQuery.setParameter("userRoleState", AcmUserRoleState.VALID);
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }

    public List<AcmUser> findUsersWithRoles(List<String> roles)
    {
        Query usersWithRole = getEntityManager().createQuery("SELECT user FROM AcmUser user, AcmUserRole role "
                + "WHERE user.userId = role.userId " + "AND user.userState = :userState " + "AND role.userRoleState = :userRoleState "
                + "AND role.roleName IN :roleNames " + "ORDER BY user.lastName, user.firstName");
        usersWithRole.setParameter("userState", AcmUserState.VALID);
        usersWithRole.setParameter("roleNames", roles);
        usersWithRole.setParameter("userRoleState", AcmUserRoleState.VALID);

        List<AcmUser> retval = usersWithRole.getResultList();

        return retval;
    }

    public List<AcmUser> findUserWithRole(String role)
    {
        Query usersWithRole = getEntityManager().createQuery("SELECT user FROM AcmUser user, AcmUserRole role "
                + "WHERE user.userId = role.userId " + "AND user.userState = :userState " + "AND role.userRoleState = :userRoleState "
                + "AND role.roleName = :roleName " + "ORDER BY user.lastName, user.firstName");
        usersWithRole.setParameter("userState", AcmUserState.VALID);
        usersWithRole.setParameter("roleName", role);
        usersWithRole.setParameter("userRoleState", AcmUserRoleState.VALID);

        List<AcmUser> retval = usersWithRole.getResultList();

        return retval;
    }

    public List<AcmUser> findByFullNameKeyword(String keyword)
    {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AcmUser> query = builder.createQuery(AcmUser.class);
        Root<AcmUser> user = query.from(AcmUser.class);

        query.select(user);

        query.where(builder.and(builder.like(builder.lower(user.<String>get("fullName")), "%" + keyword.toLowerCase() + "%"),
                builder.equal(user.<String>get("userState"), AcmUserState.VALID)));

        query.orderBy(builder.asc(user.get("fullName")));

        TypedQuery<AcmUser> dbQuery = getEntityManager().createQuery(query);
        List<AcmUser> results = dbQuery.getResultList();

        return results;
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

    public void markAllRolesInvalid(String directoryName)
    {
        Query markInvalid = getEntityManager().createQuery("UPDATE AcmUserRole aur set aur.userRoleState = :state WHERE aur.userId IN "
                + "( SELECT au.userId FROM AcmUser au WHERE au.userDirectoryName = :directoryName )");
        markInvalid.setParameter("state", AcmUserRoleState.INVALID);
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

    public AcmUserRole saveAcmUserRole(AcmUserRole userRole)
    {
        log.debug("Saving AcmUserRole [{}] for User [{}]", userRole.getRoleName(), userRole.getUserId());
        AcmUserRolePrimaryKey key = new AcmUserRolePrimaryKey();
        key.setRoleName(userRole.getRoleName());
        key.setUserId(userRole.getUserId());
        AcmUserRole existing = getEntityManager().find(AcmUserRole.class, key);

        if (existing == null)
        {
            getEntityManager().persist(userRole);
            return userRole;
        }

        if (!Objects.equals(existing.getUserRoleState(), userRole.getUserRoleState()))
        {
            existing.setUserRoleState(userRole.getUserRoleState());
        }

        return userRole;
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
        try
        {
            AcmUser user = findByUserIdAnyCase(principal);
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
        }
        catch (NoResultException | NonUniqueResultException e)
        {
            log.debug("User [{}] not found!", principal);
        }
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
            log.error("User with password reset token: [{}] not found!", token, e.getMessage());
            return null;
        }
    }

    public List<AcmUser> findByDirectory(String directoryName)
    {
        TypedQuery<AcmUser> allUsersInDirectory = getEm()
                .createQuery("SELECT DISTINCT acmUser FROM AcmUser acmUser LEFT JOIN FETCH acmUser.groups "
                        + "WHERE acmUser.userDirectoryName = :directoryName", AcmUser.class);
        allUsersInDirectory.setParameter("directoryName", directoryName);
        return allUsersInDirectory.getResultList();
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

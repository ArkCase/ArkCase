package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.AcmUserRolePrimaryKey;
import com.armedia.acm.services.users.model.RoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
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

public class UserDao extends AcmAbstractDao<AcmUser>
{
    @PersistenceContext
    private EntityManager entityManager;

    private Cache quietUserLookupCache;

    private Logger log = LoggerFactory.getLogger(getClass());

    public AcmUser findByUserId(String userId)
    {
        return getEntityManager().find(AcmUser.class, userId);
    }

    public AcmUser findByUserIdAnyCase(String userId)
    {
        String jpql = "SELECT u " + "FROM AcmUser u " + "WHERE LOWER(u.userId) = :lowerUserId";
        TypedQuery<AcmUser> query = getEm().createQuery(jpql, AcmUser.class);

        query.setParameter("lowerUserId", userId.toLowerCase());

        AcmUser user = query.getSingleResult();
        return user;
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

        } catch (PersistenceException pe)
        {
            log.error("Could not find user record: " + pe.getMessage(), pe);
        }

        return null;
    }

    public List<AcmRole> findAllRoles()
    {
        Query roleQuery = getEntityManager().createQuery("SELECT role FROM AcmRole role");

        List<AcmRole> retval = roleQuery.getResultList();

        return retval;
    }

    public List<AcmRole> findAllRolesByRoleType(RoleType roleType)
    {
        Query roleQuery = getEntityManager().createQuery("SELECT role FROM AcmRole role WHERE role.roleType= :roleType");
        roleQuery.setParameter("roleType", roleType.getRoleName());
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }

    public List<AcmRole> findAllRolesByUser(String userId)
    {
        Query roleQuery = getEntityManager().createQuery("SELECT acmRole FROM AcmRole acmRole " + "WHERE acmRole.roleName IN "
                + "(SELECT userRole.roleName FROM AcmUserRole userRole " + "WHERE userRole.userId= :userId "
                + "AND userRole.userRoleState = :userRoleState)");
        roleQuery.setParameter("userId", userId);
        roleQuery.setParameter("userRoleState", "VALID");
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }

    public List<AcmRole> findAllRolesByUserAndRoleType(String userId, RoleType roleType)
    {
        Query roleQuery = getEntityManager().createQuery("SELECT acmRole FROM AcmRole acmRole " + "WHERE acmRole.roleName IN "
                + "(SELECT userRole.roleName FROM AcmUserRole userRole " + "WHERE userRole.userId= :userId "
                + "AND userRole.userRoleState = :userRoleState) " + "AND acmRole.roleType = :roleType");
        roleQuery.setParameter("userId", userId);
        roleQuery.setParameter("roleType", roleType.getRoleName());
        roleQuery.setParameter("userRoleState", "VALID");
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }

    public List<AcmUser> findUsersWithRoles(List<String> roles)
    {
        Query usersWithRole = getEntityManager().createQuery("SELECT user FROM AcmUser user, AcmUserRole role "
                + "WHERE user.userId = role.userId " + "AND user.userState = :userState " + "AND role.userRoleState = :userRoleState "
                + "AND role.roleName IN :roleNames " + "ORDER BY user.lastName, user.firstName");
        usersWithRole.setParameter("userState", "VALID");
        usersWithRole.setParameter("roleNames", roles);
        usersWithRole.setParameter("userRoleState", "VALID");

        List<AcmUser> retval = usersWithRole.getResultList();

        return retval;
    }

    public List<AcmUser> findUserWithRole(String role)
    {
        Query usersWithRole = getEntityManager().createQuery("SELECT user FROM AcmUser user, AcmUserRole role "
                + "WHERE user.userId = role.userId " + "AND user.userState = :userState " + "AND role.userRoleState = :userRoleState "
                + "AND role.roleName = :roleName " + "ORDER BY user.lastName, user.firstName");
        usersWithRole.setParameter("userState", "VALID");
        usersWithRole.setParameter("roleName", role);
        usersWithRole.setParameter("userRoleState", "VALID");

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
                builder.equal(user.<String>get("userState"), "VALID")));

        query.orderBy(builder.asc(user.get("fullName")));

        TypedQuery<AcmUser> dbQuery = getEntityManager().createQuery(query);
        List<AcmUser> results = dbQuery.getResultList();

        return results;
    }

    public void markAllUsersInvalid(String directoryName)
    {
        Query markInvalid = getEntityManager()
                .createQuery("UPDATE AcmUser au set au.userState = :state, au.modified = :now WHERE au.userDirectoryName = :directoryName");
        markInvalid.setParameter("state", "INVALID");
        markInvalid.setParameter("now", new Date());
        markInvalid.setParameter("directoryName", directoryName);
        markInvalid.executeUpdate();
    }

    public void markAllRolesInvalid(String directoryName)
    {
        Query markInvalid = getEntityManager().createQuery("UPDATE AcmUserRole aur set aur.userRoleState = :state WHERE aur.userId IN "
                + "( SELECT au.userId FROM AcmUser au WHERE au.userDirectoryName = :directoryName )");
        markInvalid.setParameter("state", "INVALID");
        markInvalid.setParameter("directoryName", directoryName);
        markInvalid.executeUpdate();
    }

    public AcmRole saveAcmRole(AcmRole in)
    {

        AcmRole existing = getEntityManager().find(AcmRole.class, in.getRoleName());
        if (existing == null)
        {

            getEntityManager().persist(in);
            getEntityManager().flush();
        }
        return in;
    }

    public AcmUserRole saveAcmUserRole(AcmUserRole userRole)
    {
        AcmUserRolePrimaryKey key = new AcmUserRolePrimaryKey();
        key.setRoleName(userRole.getRoleName());
        key.setUserId(userRole.getUserId());
        AcmUserRole existing = getEntityManager().find(AcmUserRole.class, key);

        if (existing == null)
        {
            getEntityManager().persist(userRole);
            getEntityManager().flush();
            return userRole;
        }

        existing.setUserRoleState("VALID");
        getEntityManager().persist(existing);

        return existing;
    }

    @Transactional
    public AcmUser markUserAsDeleted(String name)
    {
        String jpql = "SELECT user FROM AcmUser user WHERE user.userId = :userId";
        TypedQuery<AcmUser> query = getEm().createQuery(jpql, AcmUser.class);

        query.setParameter("userId", name);

        AcmUser markedUser = query.getSingleResult();
        markedUser.setUserState("INVALID");
        markedUser.setDeletedAt(new Date());
        getEntityManager().persist(markedUser);

        return markedUser;
    }

    public boolean isUserPasswordExpired(String principal)
    {
        log.debug("Check password expiration for user: {}", principal);
        try
        {
            AcmUser user = findByUserIdAnyCase(principal);
            LocalDate userPasswordExpirationDate = user.getPasswordExpirationDate();
            if (userPasswordExpirationDate != null)
            {
                return userPasswordExpirationDate.isBefore(LocalDate.now());
            }
            log.info("Expiration date not set for user : {}", user.getUserId());
        } catch (NoResultException | NonUniqueResultException e)
        {
            log.debug("User: {} not found!", principal);
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
        } catch (NoResultException | NonUniqueResultException e)
        {
            log.error("User with password reset token: {} not found!", token, e.getMessage());
            return null;
        }
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
}

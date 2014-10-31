package com.armedia.acm.services.users.dao.ldap;


import com.armedia.acm.services.users.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.Date;
import java.util.List;

public class UserDao
{
    @PersistenceContext
    private EntityManager entityManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    public AcmUser findByUserId(String userId)
    {
        return getEntityManager().find(AcmUser.class, userId);
    }

    public List<AcmRole> findAllRoles()
    {
        Query roleQuery = getEntityManager().createQuery("SELECT role FROM AcmRole role");

        List<AcmRole> retval = roleQuery.getResultList();

        return retval;
    }

    public List<AcmRole> findAllRolesByRoleType(RoleType roleType) {
        Query roleQuery = getEntityManager().createQuery("SELECT role FROM AcmRole role WHERE role.roleType= :roleType");
        roleQuery.setParameter("roleType",roleType.getRoleName());
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }

    public List<AcmRole> findAllRolesByUser(String userId) {
        Query roleQuery = getEntityManager().createQuery("SELECT acmRole FROM AcmRole acmRole " +
                    "WHERE acmRole.roleName IN " +
                    "(SELECT userRole.roleName FROM AcmUserRole userRole " +
                    "WHERE userRole.userId= :userId " +
                    "AND userRole.userRoleState = :userRoleState)");
        roleQuery.setParameter("userId",userId);
        roleQuery.setParameter("userRoleState","VALID");
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }
    public List<AcmRole> findAllRolesByUserAndRoleType(String userId,RoleType roleType) {
        Query roleQuery = getEntityManager().createQuery("SELECT acmRole FROM AcmRole acmRole " +
                "WHERE acmRole.roleName IN " +
                "(SELECT userRole.roleName FROM AcmUserRole userRole " +
                "WHERE userRole.userId= :userId " +
                "AND userRole.userRoleState = :userRoleState) " +
                "AND acmRole.roleType = :roleType");
        roleQuery.setParameter("userId",userId);
        roleQuery.setParameter("roleType",roleType.getRoleName());
        roleQuery.setParameter("userRoleState","VALID");
        List<AcmRole> retval = roleQuery.getResultList();
        return retval;
    }

    public List<AcmUser> findUsersWithRoles(List<String> roles)
    {
        Query usersWithRole = getEntityManager().createQuery(
                "SELECT user FROM AcmUser user, AcmUserRole role " +
                        "WHERE user.userId = role.userId " +
                        "AND user.userState = :userState " +
                        "AND role.userRoleState = :userRoleState " +
                        "AND role.roleName IN :roleNames " +
                        "ORDER BY user.lastName, user.firstName");
        usersWithRole.setParameter("userState", "VALID");
        usersWithRole.setParameter("roleNames", roles);
        usersWithRole.setParameter("userRoleState", "VALID");

        List<AcmUser> retval = usersWithRole.getResultList();

        return retval;
    }

    public List<AcmUser> findUserWithRole(String role)
    {
        Query usersWithRole = getEntityManager().createQuery(
                "SELECT user FROM AcmUser user, AcmUserRole role " +
                        "WHERE user.userId = role.userId " +
                        "AND user.userState = :userState " +
                        "AND role.userRoleState = :userRoleState " +
                        "AND role.roleName = :roleName " +
                        "ORDER BY user.lastName, user.firstName");
        usersWithRole.setParameter("userState", "VALID");
        usersWithRole.setParameter("roleName", role);
        usersWithRole.setParameter("userRoleState", "VALID");

        List<AcmUser> retval = usersWithRole.getResultList();

        return retval;
    }
    
    public List<AcmUser> findByFullNameKeyword(String keyword) {
    	CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<AcmUser> query = builder.createQuery(AcmUser.class);
    	Root<AcmUser> user = query.from(AcmUser.class);
    	
    	query.select(user);
    	
    	query.where(
    			builder.and(
    					builder.like(
    							builder.lower(user.<String>get("fullName")), "%" + keyword.toLowerCase() + "%"
    					),
    					builder.equal(user.<String>get("userState"), "VALID")
    			)
    	);
    	
    	query.orderBy(builder.asc(user.get("fullName")));
    	
    	TypedQuery<AcmUser> dbQuery = getEntityManager().createQuery(query);
    	List<AcmUser> results = dbQuery.getResultList();
    	
    	return results;
    }

    public void markAllUsersInvalid(String directoryName)
    {
        Query markInvalid = getEntityManager().createQuery(
                "UPDATE AcmUser au set au.userState = :state, au.userModified = :now WHERE au.userDirectoryName = :directoryName"
        );
        markInvalid.setParameter("state", "INVALID");
        markInvalid.setParameter("now", new Date());
        markInvalid.setParameter("directoryName", directoryName);
        markInvalid.executeUpdate();
    }

    public void markAllRolesInvalid(String directoryName)
    {
        Query markInvalid = getEntityManager().createQuery(
                "UPDATE AcmUserRole aur set aur.userRoleState = :state WHERE aur.userId IN " +
                        "( SELECT au.userId FROM AcmUser au WHERE au.userDirectoryName = :directoryName )");
        markInvalid.setParameter("state", "INVALID");
        markInvalid.setParameter("directoryName", directoryName);
        markInvalid.executeUpdate();
    }

    public AcmRole saveAcmRole(AcmRole in)
    {

        AcmRole existing = getEntityManager().find(AcmRole.class, in.getRoleName());
        if ( existing == null )
        {

            getEntityManager().persist(in);
            getEntityManager().flush();
        }
        return in;
    }

    public AcmUser saveAcmUser(AcmUser user)
    {
        AcmUser existing = getEntityManager().find(AcmUser.class, user.getUserId());
        if ( existing == null )
        {
            getEntityManager().persist(user);
            getEntityManager().flush();
            return user;
        }

        existing.setUserState("VALID");
        existing.setUserDirectoryName(user.getUserDirectoryName());
        existing.setLastName(user.getLastName());
        existing.setFirstName(user.getFirstName());
        existing.setMail(user.getMail());
        getEntityManager().persist(existing);

        return existing;

    }

    public AcmUserRole saveAcmUserRole(AcmUserRole userRole)
    {
        AcmUserRolePrimaryKey key = new AcmUserRolePrimaryKey();
        key.setRoleName(userRole.getRoleName());
        key.setUserId(userRole.getUserId());
        AcmUserRole existing = getEntityManager().find(AcmUserRole.class, key);

        if ( existing == null )
        {
            getEntityManager().persist(userRole);
            getEntityManager().flush();
            return userRole;
        }

        existing.setUserRoleState("VALID");
        getEntityManager().persist(existing);

        return existing;
    }


    public EntityManager getEntityManager()
    {
        return entityManager;
    }


}

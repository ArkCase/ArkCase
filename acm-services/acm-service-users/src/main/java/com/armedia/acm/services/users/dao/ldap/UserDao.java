package com.armedia.acm.services.users.dao.ldap;


import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.AcmUserRolePrimaryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    public void markAllUsersInvalid()
    {
        Query markInvalid = getEntityManager().createQuery(
                "UPDATE AcmUser set userState = :state, userModified = :now"
        );
        markInvalid.setParameter("state", "INVALID");
        markInvalid.setParameter("now", new Date());
        markInvalid.executeUpdate();
    }

    public void markAllRolesInvalid()
    {
        Query markInvalid = getEntityManager().createQuery(
                "UPDATE AcmUserRole set userRoleState = :state"
        );
        markInvalid.setParameter("state", "INVALID");
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

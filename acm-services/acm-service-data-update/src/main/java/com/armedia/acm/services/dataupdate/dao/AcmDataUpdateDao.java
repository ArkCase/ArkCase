package com.armedia.acm.services.dataupdate.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.dataupdate.model.AcmDataUpdateExecutorLog;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AcmDataUpdateDao extends AcmAbstractDao<AcmDataUpdateExecutorLog>
{
    @PersistenceContext
    private EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(AcmDataUpdateDao.class);

    @Override
    protected Class<AcmDataUpdateExecutorLog> getPersistenceClass()
    {
        return AcmDataUpdateExecutorLog.class;
    }

    public int updateModifier(Class persistenceClass, String oldModifier, String modifier)
    {
        Query update = em.createQuery("UPDATE " + persistenceClass.getSimpleName()
                + " e set e.modifier = :modifier"
                + " WHERE e.modifier = :oldModifier");
        update.setParameter("modifier", modifier);
        update.setParameter("oldModifier", oldModifier);
        return update.executeUpdate();
    }

    public int updateCreator(Class persistenceClass, String oldCreator, String creator)
    {
        Query update = em.createQuery("UPDATE " + persistenceClass.getSimpleName()
                + " e set e.creator = :creator"
                + " WHERE e.creator = :oldCreator");
        update.setParameter("creator", creator);
        update.setParameter("oldCreator", oldCreator);
        return update.executeUpdate();
    }

    public int updateAssignmentOldAssignee(String assignee, String newAssignee)
    {
        Query update = em.createQuery("UPDATE AcmAssignment ass"
                + " set ass.oldAssignee = :newAssignee"
                + " WHERE ass.oldAssignee = :assignee");
        update.setParameter("newAssignee", newAssignee);
        update.setParameter("assignee", assignee);
        return update.executeUpdate();
    }

    public int updateAssignmentNewAssignee(String assignee, String newAssignee)
    {
        Query update = em.createQuery("UPDATE AcmAssignment ass"
                + " set ass.newAssignee = :newAssignee"
                + " WHERE ass.newAssignee = :assignee");
        update.setParameter("newAssignee", newAssignee);
        update.setParameter("assignee", assignee);
        return update.executeUpdate();
    }

    public int updateAuditEventUserId(String userId, String newUserId)
    {
        Query update = em.createQuery("UPDATE AuditEvent ae"
                + " set ae.userId = :newUserId"
                + " WHERE ae.userId = :userId");
        update.setParameter("newUserId", newUserId);
        update.setParameter("userId", userId);
        return update.executeUpdate();
    }

    public int updateNoteAuthor(String author, String newAuthor)
    {
        Query update = em.createQuery("UPDATE Note n"
                + " set n.author = :newAuthor"
                + " WHERE n.author = :author");
        update.setParameter("newAuthor", newAuthor);
        update.setParameter("author", author);
        return update.executeUpdate();
    }

    public int updateNotificationUser(String userId, String newUserId)
    {
        Query update = em.createQuery("UPDATE Notification n"
                + " set n.user = :newUserId"
                + " WHERE n.user = :userId");
        update.setParameter("newUserId", newUserId);
        update.setParameter("userId", userId);
        return update.executeUpdate();
    }

    public int updateParticipantLdapId(String userId, String newUserId)
    {
        Query update = em.createQuery("UPDATE AcmParticipant ap"
                + " set ap.participantLdapId = :newUserId"
                + " WHERE ap.participantLdapId = :userId");
        update.setParameter("newUserId", newUserId);
        update.setParameter("userId", userId);
        return update.executeUpdate();
    }

    public int updateSignatureSignedBy(String userId, String newUserId)
    {
        Query update = em.createQuery("UPDATE Signature s"
                + " set s.signedBy = :newUserId"
                + " WHERE s.signedBy = :userId");
        update.setParameter("newUserId", newUserId);
        update.setParameter("userId", userId);
        return update.executeUpdate();
    }

    public int updateSubscriptionEventOwnerId(String userId, String newUserId)
    {
        Query update = em.createQuery("UPDATE AcmSubscriptionEvent sub"
                + " set sub.subscriptionOwner = :newUserId"
                + " WHERE sub.subscriptionOwner = :userId");
        update.setParameter("newUserId", newUserId);
        update.setParameter("userId", userId);
        return update.executeUpdate();
    }

    public int updateSubscriptionEventUserId(String userId, String newUserId)
    {
        Query update = em.createQuery("UPDATE AcmSubscriptionEvent sub"
                + " set sub.eventUser = :newUserId"
                + " WHERE sub.eventUser = :userId");
        update.setParameter("newUserId", newUserId);
        update.setParameter("userId", userId);
        return update.executeUpdate();
    }

    public int updateSubscriptionUserId(String userId, String newUserId)
    {
        Query update = em.createQuery("UPDATE AcmSubscription sub"
                + " set sub.userId = :newUserId"
                + " WHERE sub.userId = :userId");
        update.setParameter("newUserId", newUserId);
        update.setParameter("userId", userId);
        return update.executeUpdate();
    }

    public int updateUserActionUserId(String userId, String newUserId)
    {
        Query update = em.createQuery("UPDATE AcmUserAction au"
                + " set au.userId = :newUserId"
                + " WHERE au.userId = :userId");
        update.setParameter("newUserId", newUserId);
        update.setParameter("userId", userId);
        return update.executeUpdate();
    }

    public int updateCostsheetUser(AcmUser newUser, String oldUserId)
    {
        Query update = em.createQuery(
                "UPDATE AcmCostsheet ac"
                        + " set ac.user = :newUser"
                        + " WHERE ac.user.userId = :userId");
        update.setParameter("newUser", newUser);
        update.setParameter("userId", oldUserId);
        return update.executeUpdate();
    }

    public int updateTimesheetUser(AcmUser newUser, String oldUserId)
    {
        Query updateAllWhereGivenUser = em.createQuery(
                "UPDATE AcmTimesheet at "
                        + "SET at.user = :newUser "
                        + "WHERE at.user.userId = :userId");
        updateAllWhereGivenUser.setParameter("userId", oldUserId);
        updateAllWhereGivenUser.setParameter("newUser", newUser);
        return updateAllWhereGivenUser.executeUpdate();
    }

    public int updateUserPreferenceUser(AcmUser newUser, String oldUserId)
    {
        Query updateAllWhereGivenUser = em.createQuery(
                "UPDATE UserPreference up "
                        + "SET up.user = :newUser "
                        + "WHERE up.user.userId = :userId");
        updateAllWhereGivenUser.setParameter("userId", oldUserId);
        updateAllWhereGivenUser.setParameter("newUser", newUser);
        return updateAllWhereGivenUser.executeUpdate();
    }

    public int updateUserOrgUser(AcmUser newUser, String oldUserId)
    {
        Query updateAllWhereGivenUser = em.createQuery(
                "UPDATE UserOrg org "
                        + "SET org.user = :newUser "
                        + "WHERE org.user.userId = :userId");
        updateAllWhereGivenUser.setParameter("userId", oldUserId);
        updateAllWhereGivenUser.setParameter("newUser", newUser);
        return updateAllWhereGivenUser.executeUpdate();
    }

    public int updateGroupSupervisor(AcmUser newUser, String oldUserId)
    {
        Query updateAllWhereGivenSupervisor = em.createQuery(
                "UPDATE AcmGroup ag "
                        + "SET ag.supervisor = :newUser "
                        + "WHERE ag.supervisor.userId = :userId");
        updateAllWhereGivenSupervisor.setParameter("userId", oldUserId);
        updateAllWhereGivenSupervisor.setParameter("newUser", newUser);
        return updateAllWhereGivenSupervisor.executeUpdate();
    }

    public int updateDashboardOwner(AcmUser newUser, String oldUserId)
    {
        Query updateAllWhereGivenDashboardOwner = em.createQuery(
                "UPDATE Dashboard dash "
                        + "SET dash.dashboardOwner = :newUser "
                        + "WHERE dash.dashboardOwner.userId = :userId");
        updateAllWhereGivenDashboardOwner.setParameter("userId", oldUserId);
        updateAllWhereGivenDashboardOwner.setParameter("newUser", newUser);
        return updateAllWhereGivenDashboardOwner.executeUpdate();
    }

    public int updateAuditLogTrackId(AcmUser newUser, String oldUserId)
    {
        Query updateAuditLogTrackId = em.createQuery(
                "UPDATE AuditEvent event "
                        + "SET event.trackId = concat(:newUserId, '|', substring(event.trackId, locate('|', event.trackId) + 1))"
                        + "WHERE event.userId = :userId");
        updateAuditLogTrackId.setParameter("userId", oldUserId);
        updateAuditLogTrackId.setParameter("newUserId", newUser.getUserId());
        return updateAuditLogTrackId.executeUpdate();
    }

    @Transactional
    public void updateCreatorAndModifierToAllAcmEntities(Map<String, String> newOldUserId)
    {
        log.debug("Finding all entity classes");
        Set<EntityType<?>> entities = em.getMetamodel().getEntities();

        Set<EntityType<?>> acmEntities = entities.stream()
                .filter(entityType -> AcmEntity.class.isAssignableFrom(entityType.getJavaType()))
                .peek(entityType -> log.debug("Found entity [{}] to update", entityType.getJavaType().getSimpleName()))
                .collect(Collectors.toSet());

        newOldUserId.forEach((newUserId, oldUserId) -> acmEntities.forEach(entity -> {
            log.debug("Updating creator and modifier in [{}]", entity.getJavaType().getSimpleName());
            int num = updateCreator(entity.getJavaType(), oldUserId, newUserId);
            log.debug("Creator updated in [{}] rows", num);
            num = updateModifier(entity.getJavaType(), oldUserId, newUserId);
            log.debug("Modifier updated in [{}] rows", num);
        }));
    }

    public void setUserIdsAsDn(String directoryName)
    {
        Query markInvalid = getEm()
                .createQuery("UPDATE AcmUser au "
                        + "set au.modified = :now, "
                        + "au.distinguishedName = au.userId "
                        + "WHERE au.userDirectoryName = :directoryName");
        markInvalid.setParameter("directoryName", directoryName);
        markInvalid.setParameter("now", new Date());
        markInvalid.executeUpdate();
    }

    public int markInactiveActiveAdHocGroupsWithUUID()
    {
        Query update = em.createQuery("UPDATE AcmGroup ag "
                + "SET ag.status = :newStatus "
                + "WHERE ag.status = :status "
                + "AND ag.name LIKE '%-UUID-%'"
                + "AND LENGTH(ag.name) > 42 "
                + "AND ag.type = :groupType");
        update.setParameter("newStatus", AcmGroupStatus.INACTIVE);
        update.setParameter("groupType", AcmGroupType.ADHOC_GROUP);
        update.setParameter("status", AcmGroupStatus.ACTIVE);
        return update.executeUpdate();
    }

    public List<AcmGroup> findAllActiveAdHocGroupsWithUUID()
    {
        TypedQuery<AcmGroup> findQuery = em.createQuery("SELECT ag "
                + "FROM AcmGroup ag "
                + "WHERE ag.type = :groupType "
                + "AND ag.status = :status "
                + "AND ag.name LIKE '%-UUID-%' "
                + "AND LENGTH(ag.name) > 42", AcmGroup.class);
        findQuery.setParameter("groupType", AcmGroupType.ADHOC_GROUP);
        findQuery.setParameter("status", AcmGroupStatus.ACTIVE);
        return findQuery.getResultList();
    }

    public void updateUserMembershipForAdHocGroups()
    {
        StoredProcedureQuery query = em.createStoredProcedureQuery("UpdateUserMembershipForAdHocGroups");
        query.execute();
    }

    public void updateGroupMembershipForAdHocGroups()
    {
        StoredProcedureQuery query = em.createStoredProcedureQuery("UpdateGroupMembershipForAdHocGroups");
        query.execute();
    }
}

package com.armedia.acm.services.dataupdate.dao;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserIdGroupNameDomainUpdateDao
{
    private static final Logger log = LogManager.getLogger(UserIdGroupNameDomainUpdateDao.class);
    @PersistenceContext
    private EntityManager em;

    public int setUserIdAsDn(String directoryName)
    {
        Query update = em.createQuery("UPDATE AcmUser user "
                + "set user.distinguishedName = user.userId "
                + "WHERE user.userDirectoryName = :directoryName");
        update.setParameter("directoryName", directoryName);
        return update.executeUpdate();
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

    public int updateOutlookPasswordUserId(String userId, String newUserId)
    {
        Query update = em.createQuery("UPDATE OutlookPassword op"
                + " set op.userId = :newUserId"
                + " WHERE op.userId = :userId");
        update.setParameter("newUserId", newUserId);
        update.setParameter("userId", userId.toUpperCase());
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

    public int updateActHiActinst(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_HI_ACTINST "
                + "SET ASSIGNEE_ = ? "
                + "WHERE ASSIGNEE_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateActHiAttachment(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_HI_ATTACHMENT "
                + "SET USER_ID_ = ? "
                + "WHERE USER_ID_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateActHiComment(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_HI_COMMENT "
                + "SET USER_ID_ = ? "
                + "WHERE USER_ID_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateUserIdActHiIdentitylink(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_HI_IDENTITYLINK "
                + "SET USER_ID_ = ? "
                + "WHERE USER_ID_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateGroupIdActHiIdentityLink(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_HI_IDENTITYLINK "
                + "SET GROUP_ID_ = ? "
                + "WHERE GROUP_ID_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateAssigneeActHiTaskinst(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_HI_TASKINST "
                + "SET ASSIGNEE_ = ? "
                + "WHERE ASSIGNEE_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateOwnerActHiTaskinst(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_HI_TASKINST "
                + "SET OWNER_ = ? "
                + "WHERE OWNER_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateGroupIdActRuIdentitylink(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_RU_IDENTITYLINK "
                + "SET GROUP_ID_ = ? "
                + "WHERE GROUP_ID_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateUserIdActRuIdentityLink(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_RU_IDENTITYLINK "
                + "SET USER_ID_ = ? "
                + "WHERE USER_ID_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateOwnerActRuTask(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_RU_TASK "
                + "SET OWNER_ = ? "
                + "WHERE OWNER_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateAssigneeActRuTask(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_RU_TASK "
                + "SET ASSIGNEE_ = ? "
                + "WHERE ASSIGNEE_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
    }

    public int updateLockOwnerActRuJob(String userId, String newUserId)
    {
        Query query = em.createNativeQuery("UPDATE ACT_RU_JOB "
                + "SET LOCK_OWNER_ = ? "
                + "WHERE LOCK_OWNER_ = ?");
        query.setParameter(1, newUserId);
        query.setParameter(2, userId);
        return query.executeUpdate();
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
}

package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.dataupdate.dao.AcmDataUpdateDao;
import com.armedia.acm.services.dataupdate.model.AcmUserUpdateHolder;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.service.ldap.LdapSyncService;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserIdGroupNameDomainUpdateExecutor implements AcmDataUpdateExecutor
{
    private static final Logger log = LoggerFactory.getLogger(UserIdGroupNameDomainUpdateExecutor.class);

    private UserDao userDao;

    private AcmDataUpdateDao dataUpdateDao;

    private SpringContextHolder contextHolder;

    private Function<AcmUser, String> userIdStripDomain = user -> StringUtils.substringBeforeLast(user.getUserId(), "@");

    @Override
    public String getUpdateId()
    {
        return "core-userId-groupName-domain-update1";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute()
    {
        Map<String, LdapSyncService> ldapSyncServices = contextHolder.getAllBeansOfType(LdapSyncService.class);
        ldapSyncServices.forEach((beanId, service) -> {
            if (beanId.endsWith("_ldapSyncJob"))
            {
                String directoryName = service.getLdapSyncConfig().getDirectoryName();
                dataUpdateDao.setUserIdsAsDn(directoryName);
                service.setSyncEnabled(true);
                service.ldapSync();
            }
        });

        List<AcmUser> validUsers = userDao.findByState(AcmUserState.VALID);

        List<AcmUser> invalidUsers = userDao.findByState(AcmUserState.INVALID);
        Set<String> invalidUserIds = invalidUsers.stream()
                .map(AcmUser::getUserId)
                .collect(Collectors.toSet());

        Predicate<AcmUser> userUpdatedWithDomain = user -> {
            String userName = userIdStripDomain.apply(user);
            return invalidUserIds.contains(userName);
        };

        Set<AcmUserUpdateHolder> userUpdateHolderSet = validUsers.stream()
                .filter(userUpdatedWithDomain)
                .map(user -> new AcmUserUpdateHolder(userIdStripDomain.apply(user), user.getUserId(), user))
                .collect(Collectors.toSet());

        Map<String, String> newOldUserIds = userUpdateHolderSet.stream()
                .collect(Collectors.toMap(AcmUserUpdateHolder::getNewId, AcmUserUpdateHolder::getOldId));

        log.debug("Updating 'creator' and 'modifier' for AcmEntity(s)");
        dataUpdateDao.updateCreatorAndModifierToAllAcmEntities(newOldUserIds);

        log.debug("Updating 'newAssignee' for AcmAssignments");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> dataUpdateDao.updateAssignmentNewAssignee(oldUserId, newUserId)
        );

        log.debug("Updating 'oldAssignee' for AcmAssignments");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> dataUpdateDao.updateAssignmentOldAssignee(oldUserId, newUserId)
        );

        log.debug("Updating 'userId' for AuditEvent");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> dataUpdateDao.updateAuditEventUserId(oldUserId, newUserId)
        );

        log.debug("Updating 'author' for Note");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> dataUpdateDao.updateNoteAuthor(oldUserId, newUserId)
        );

        log.debug("Updating 'user' for Notification");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> dataUpdateDao.updateNotificationUser(oldUserId, newUserId)
        );

        log.debug("Updating 'participantLdapId' for AcmParticipant");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> dataUpdateDao.updateParticipantLdapId(oldUserId, newUserId)
        );

        log.debug("Updating 'signedBy' for Signature");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateSignatureSignedBy(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'subscriptionOwner' for SubscriptionEvent");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateSubscriptionEventOwnerId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'eventUser' for SubscriptionEvent");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateSubscriptionEventUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'userId' for SubscriptionEvent");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateSubscriptionUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'userId' for AcmUserAction");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateUserActionUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'user' for AcmCostsheet");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateCostsheetUser(user, userIdStripDomain.apply(user));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'user' for AcmTimesheet");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateTimesheetUser(user, userIdStripDomain.apply(user));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'user' for UserPreference");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateUserPreferenceUser(user, userIdStripDomain.apply(user));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'user' for UserOrg");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateUserOrgUser(user, userIdStripDomain.apply(user));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'supervisor' for AcmGroup");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateGroupSupervisor(user, userIdStripDomain.apply(user));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'dashboardOwner' for AcmDashboard");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateDashboardOwner(user, userIdStripDomain.apply(user));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setDataUpdateDao(AcmDataUpdateDao dataUpdateDao)
    {
        this.dataUpdateDao = dataUpdateDao;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}

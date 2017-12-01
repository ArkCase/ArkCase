package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.dataupdate.dao.AcmDataUpdateDao;
import com.armedia.acm.services.dataupdate.model.AcmUserUpdateHolder;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.service.ldap.LdapSyncService;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Specific implementation of {@link AcmDataUpdateExecutor} to initiate ldap sync
 * which will insert all LDAP users and groups with appended domain. Once the sync
 * is finished, all tables where userId or groupName is found are updated with new
 * values of these entities.
 */
public class UserIdGroupNameDomainUpdateExecutor implements AcmDataUpdateExecutor
{
    private static final Logger log = LoggerFactory.getLogger(UserIdGroupNameDomainUpdateExecutor.class);

    private UserDao userDao;

    private AcmGroupDao groupDao;

    private AcmDataUpdateDao dataUpdateDao;

    private SpringContextHolder contextHolder;

    private Function<String, String> idStripDomain = it -> StringUtils.substringBeforeLast(it, "@");

    @Override
    public String getUpdateId()
    {
        return "core-userId-groupName-domain-update";
    }

    @Override
    @Transactional
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

        Set<AcmUserUpdateHolder> userUpdateHolderSet = getUpdateUserHolders();

        Map<String, String> newOldUserIds = userUpdateHolderSet.stream()
                .collect(Collectors.toMap(AcmUserUpdateHolder::getNewId, AcmUserUpdateHolder::getOldId));

        log.debug("Updating 'creator' and 'modifier' for AcmEntity(s)");
        dataUpdateDao.updateCreatorAndModifierToAllAcmEntities(newOldUserIds);

        log.debug("Updating 'newAssignee' for AcmAssignments");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateAssignmentNewAssignee(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'oldAssignee' for AcmAssignments");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateAssignmentOldAssignee(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'userId' for AuditEvent");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateAuditEventUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'author' for Note");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateNoteAuthor(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'user' for Notification");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateNotificationUser(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'participantLdapId' for AcmParticipant");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateParticipantLdapId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
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

        log.debug("Updating 'userId' for OutlookPassword");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = dataUpdateDao.updateOutlookPasswordUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'user' for AcmCostsheet");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateCostsheetUser(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'user' for AcmTimesheet");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateTimesheetUser(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'user' for UserPreference");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateUserPreferenceUser(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'user' for UserOrg");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateUserOrgUser(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'supervisor' for AcmGroup");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateGroupSupervisor(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'dashboardOwner' for AcmDashboard");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = dataUpdateDao.updateDashboardOwner(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        Map<String, AcmUserUpdateHolder> userHolderByOldId = userUpdateHolderSet.stream()
                .collect(Collectors.toMap(AcmUserUpdateHolder::getOldId, Function.identity()));

        List<AcmGroup> adHocGroups = groupDao.findByTypeWithUsers(AcmGroupType.ADHOC_GROUP);

        log.debug("Updating user members for 'ADHOC' groups");
        adHocGroups.stream()
                .filter(group -> !group.getUserMembers().isEmpty())
                .forEach(group -> {
                    Set<AcmUser> userMembers = group.getUserMembers();
                    group.setUserMembers(new HashSet<>());
                    userMembers.forEach(user -> {
                        if (userHolderByOldId.containsKey(user.getUserId()))
                        {
                            AcmUserUpdateHolder userUpdateHolder = userHolderByOldId.get(user.getUserId());
                            group.addUserMember(userUpdateHolder.getNewUser());
                            log.debug("Add [{}] user to [{}] group", userUpdateHolder.getNewUser().getUserId(), group.getName());
                        }
                    });
                    groupDao.save(group);
                });

        Map<String, String> newOldGroupName = getNewToOldGroupNames();

        log.debug("Updating 'participantLdapId' for AcmParticipant");
        newOldGroupName.forEach(
                (newGroupName, oldGroupName) -> {
                    int rows = dataUpdateDao.updateParticipantLdapId(oldGroupName, newGroupName);
                    log.debug("For groupName: [{}] affected [{}] rows", newGroupName, rows);
                }
        );
    }

    private Map<String, String> getNewToOldGroupNames()
    {
        List<AcmGroup> activeLdapGroups = groupDao.findByStatusAndType(AcmGroupStatus.ACTIVE, AcmGroupType.LDAP_GROUP);
        List<AcmGroup> inactiveLdapGroups = groupDao.findByStatusAndType(AcmGroupStatus.INACTIVE, AcmGroupType.LDAP_GROUP);

        Set<String> inactiveLdapGroupNames = inactiveLdapGroups.stream()
                .map(AcmGroup::getName)
                .collect(Collectors.toSet());

        Predicate<AcmGroup> groupUpdatedWithDomain = group -> {
            String groupName = idStripDomain.apply(group.getName());
            return inactiveLdapGroupNames.contains(groupName);
        };

        return activeLdapGroups.stream()
                .filter(groupUpdatedWithDomain)
                .collect(Collectors.toMap(AcmGroup::getName, group -> idStripDomain.apply(group.getName())));
    }

    private Set<AcmUserUpdateHolder> getUpdateUserHolders()
    {
        List<AcmUser> validUsers = userDao.findByState(AcmUserState.VALID);

        List<AcmUser> invalidUsers = userDao.findByState(AcmUserState.INVALID);

        Set<String> invalidUserIds = invalidUsers.stream()
                .map(AcmUser::getUserId)
                .collect(Collectors.toSet());

        Predicate<AcmUser> userUpdatedWithDomain = user -> {
            String userName = idStripDomain.apply(user.getUserId());
            return invalidUserIds.contains(userName);
        };

        return validUsers.stream()
                .filter(userUpdatedWithDomain)
                .map(user -> new AcmUserUpdateHolder(idStripDomain.apply(user.getUserId()), user.getUserId(), user))
                .collect(Collectors.toSet());
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

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }
}

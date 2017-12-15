package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.dataupdate.dao.UserIdGroupNameDomainUpdateDao;
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
 * Specific implementation of {@link AcmDataUpdateExecutor} which will execute after
 * ldap sync which will insert all LDAP users and groups with appended domain. Once
 * the sync is finished, all tables where userId or groupName is found are updated
 * with new values of these entities.
 */
public class UserIdGroupNameDomainUpdateExecutor implements AcmDataUpdateExecutor
{
    private static final Logger log = LoggerFactory.getLogger(UserIdGroupNameDomainUpdateExecutor.class);

    private UserDao userDao;

    private AcmGroupDao groupDao;

    private UserIdGroupNameDomainUpdateDao userIdGroupNameDomainUpdateDao;

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
                service.ldapSync();
            }
        });

        Set<AcmUserUpdateHolder> userUpdateHolderSet = getUpdateUserHolders();

        Map<String, String> newOldUserIds = userUpdateHolderSet.stream()
                .collect(Collectors.toMap(AcmUserUpdateHolder::getNewId, AcmUserUpdateHolder::getOldId));

        log.debug("Updating 'creator' and 'modifier' for AcmEntity(s)");
        userIdGroupNameDomainUpdateDao.updateCreatorAndModifierToAllAcmEntities(newOldUserIds);

        log.debug("Updating 'newAssignee' for AcmAssignments");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateAssignmentNewAssignee(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'oldAssignee' for AcmAssignments");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateAssignmentOldAssignee(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'userId' for AuditEvent");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateAuditEventUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'author' for Note");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateNoteAuthor(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'user' for Notification");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateNotificationUser(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'participantLdapId' for AcmParticipant");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateParticipantLdapId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'signedBy' for Signature");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateSignatureSignedBy(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'subscriptionOwner' for SubscriptionEvent");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateSubscriptionEventOwnerId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'eventUser' for SubscriptionEvent");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateSubscriptionEventUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'userId' for SubscriptionEvent");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateSubscriptionUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'userId' for AcmUserAction");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateUserActionUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'userId' for OutlookPassword");
        newOldUserIds.forEach(
                (newUserId, oldUserId) -> {
                    int rows = userIdGroupNameDomainUpdateDao.updateOutlookPasswordUserId(oldUserId, newUserId);
                    log.debug("For userId: [{}] affected [{}] rows", newUserId, rows);
                }
        );

        log.debug("Updating 'user' for AcmCostsheet");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = userIdGroupNameDomainUpdateDao.updateCostsheetUser(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'user' for AcmTimesheet");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = userIdGroupNameDomainUpdateDao.updateTimesheetUser(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'user' for UserPreference");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = userIdGroupNameDomainUpdateDao.updateUserPreferenceUser(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'user' for UserOrg");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = userIdGroupNameDomainUpdateDao.updateUserOrgUser(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'supervisor' for AcmGroup");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = userIdGroupNameDomainUpdateDao.updateGroupSupervisor(user, idStripDomain.apply(user.getUserId()));
            log.debug("For userId: [{}] affected [{}] rows", user.getUserId(), rows);
        });

        log.debug("Updating 'dashboardOwner' for AcmDashboard");
        userUpdateHolderSet.forEach(holder -> {
            AcmUser user = holder.getNewUser();
            int rows = userIdGroupNameDomainUpdateDao.updateDashboardOwner(user, idStripDomain.apply(user.getUserId()));
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
                    int rows = userIdGroupNameDomainUpdateDao.updateParticipantLdapId(oldGroupName, newGroupName);
                    log.debug("Affected [{}] rows for groupName: [{}]", rows, newGroupName);
                }
        );

        log.debug("Updating ASSIGNEE_ in ACT_HI_ACTINST table");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateActHiActinst(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating USER_ID_ in ACT_HI_ATTACHMENT table");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateActHiAttachment(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating USER_ID_ in ACT_HI_COMMENT table");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateActHiComment(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating USER_ID_ in ACT_HI_IDENTITYLINK table");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateUserIdActHiIdentitylink(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating GROUP_ID_ in ACT_HI_IDENTITYLINK table");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateGroupIdActHiIdentityLink(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating GROUP_ID_ in ACT_HI_TASKINST table");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateAssigneeActHiTaskinst(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating OWNER_ in ACT_HI_TASKINST table");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateOwnerActHiTaskinst(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating GROUP_ID_ in ACT_RU_IDENTITYLINK table");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateGroupIdActRuIdentitylink(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating USER_ID_ in ACT_RU_IDENTITYLINK table");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateUserIdActRuIdentityLink(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating OWNER_ in ACT_RU_TASK tables");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateOwnerActRuTask(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating ASSIGNEE_ in ACT_RU_TASK tables");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateAssigneeActRuTask(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });

        log.debug("Updating LOCK_OWNER_ in ACT_RU_TASK tables");
        newOldUserIds.forEach((newUserId, oldUserId) -> {
            int rows = userIdGroupNameDomainUpdateDao.updateLockOwnerActRuJob(oldUserId, newUserId);
            log.debug("Affected [{}] rows for userId: [{}]", rows, newUserId);
        });
    }

    private Map<String, String> getNewToOldGroupNames()
    {
        List<AcmGroup> activeLdapGroups = groupDao.findByStatusAndType(AcmGroupStatus.ACTIVE, AcmGroupType.LDAP_GROUP);
        List<AcmGroup> inactiveLdapGroups = groupDao.findByStatusAndType(AcmGroupStatus.INACTIVE, AcmGroupType.LDAP_GROUP);

        Set<String> inactiveLdapGroupNames = inactiveLdapGroups.stream()
                .map(AcmGroup::getName)
                .map(String::toUpperCase)
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
                .map(String::toLowerCase)
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

    public void setUserIdGroupNameDomainUpdateDao(
            UserIdGroupNameDomainUpdateDao userIdGroupNameDomainUpdateDao)
    {
        this.userIdGroupNameDomainUpdateDao = userIdGroupNameDomainUpdateDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}

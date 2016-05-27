package com.armedia.acm.services.notification.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantConstants;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ncuculova
 */
public class ParticipantsNotified implements UsersNotified
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private TaskDao taskDao;
    private UserDao userDao;
    private AcmGroupDao groupDao;
    private CaseFileDao caseFileDao;
    private ComplaintDao complaintDao;

    private Notification setNewNotification(Object obj[])
    {
        Notification notification = new Notification();
        notification.setTitle((String) obj[0]);
        notification.setNote((String) obj[1]);
        notification.setType((String) obj[2]);
        notification.setParentId((Long) obj[3]);
        notification.setParentType((String) obj[4]);
        notification.setParentName((String) obj[5]);
        notification.setParentTitle((String) obj[6]);
        notification.setRelatedObjectId((Long) obj[7]);
        notification.setRelatedObjectType((String) obj[8]);
        notification.setStatus(NotificationConstants.STATUS_NEW);
        notification.setAction(NotificationConstants.ACTION_DEFAULT);
        notification.setData("{\"usr\":\"/plugin/" + ((String) obj[4]).toLowerCase() + "/" + obj[3] + "\"}");
        return notification;
    }

    /**
     * Find the users that should be notified on specific event for TASK, CASE_FILE or COMPLAINT and set notification
     * for each
     *
     * @param notification     The result row of the notification
     * @param parentObjectId   The id of the parent
     * @param parentObjectType The object type of the parent
     * @return List of notifications
     */

    @Override
    public List<Notification> getNotifications(Object[] notification, Long parentObjectId, String parentObjectType)
    {
        List<Notification> notifications = new ArrayList<>();

        if (parentObjectType.equals(TaskConstants.OBJECT_TYPE))
        {
            try
            {
                AcmTask task = getTaskDao().findById(parentObjectId);
                Set<AcmUser> receivers = filterParticipants(task.getParticipants());
                notifications.addAll(setNotificationForUsers(notification, receivers));
            } catch (AcmTaskException e)
            {
                logger.warn("Task not found", e);
            }
        } else if (parentObjectType.equals(CaseFileConstants.OBJECT_TYPE))
        {
            CaseFile caseFile = getCaseFileDao().find(parentObjectId);
            if (caseFile != null)
            {
                Set<AcmUser> receivers = filterParticipants(caseFile.getParticipants());
                notifications.addAll(setNotificationForUsers(notification, receivers));
            }
        } else if (parentObjectType.equals(ComplaintConstants.OBJECT_TYPE))
        {
            Complaint complaint = getComplaintDao().find(parentObjectId);
            if (complaint != null)
            {
                Set<AcmUser> receivers = filterParticipants(complaint.getParticipants());
                notifications.addAll(setNotificationForUsers(notification, receivers));
            }
        }
        return notifications;
    }

    private Set<AcmUser> filterParticipants(List<AcmParticipant> participants)
    {
        // We want only unique receivers
        Set<AcmUser> receivers = new HashSet<>();
        for (AcmParticipant participant : participants)
        {
            if (participant.getParticipantType().equals(ParticipantConstants.PARTICIPANT_TYPE_GROUP))
            {
                AcmGroup group = getGroupDao().findByName(participant.getParticipantLdapId());
                if (group != null)
                {
                    receivers.addAll(group.getMembers());
                }
            } else if (!participant.getParticipantType().equals(ParticipantConstants.SPECIAL_PARTICIPANT_TYPE))
            {
                AcmUser user = getUserDao().findByUserId(participant.getParticipantLdapId());
                if (user != null)
                {
                    receivers.add(user);
                }
            }
        }
        return receivers;
    }

    private List<Notification> setNotificationForUsers(Object obj[], Set<AcmUser> users)
    {
        List<Notification> notifications = new ArrayList<>();
        for (AcmUser user : users)
        {
            Notification notification = setNewNotification(obj);
            notification.setUserEmail(user.getMail());
            notification.setUser(user.getUserId());
            notifications.add(notification);
        }
        return notifications;
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }
}

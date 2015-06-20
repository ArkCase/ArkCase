/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.spring.SpringContextHolder;

import java.util.Map;
import java.util.Properties;

/**
 * @author riste.tutureski
 *
 */
public class SendExecutor implements Executor {

	private SpringContextHolder springContextHolder;

    private Properties notificationProperties;

    private AcmApplication acmAppConfiguration;

	@Override
	public Notification execute(Notification notification) 
	{
		replaceFormatPlaceholders(notification);

		// Get all registered senders
		Map<String, NotificationSender> senders = getSpringContextHolder().getAllBeansOfType(NotificationSender.class);
		
		if (senders != null)
		{
			for (NotificationSender sender : senders.values())
			{
				// Send notification
				notification = sender.send(notification);
			}
		}
				
		return notification;
	}

    private void replaceFormatPlaceholders(Notification notification)
    {

        String objectTypeLabelPlaceholder = NotificationConstants.OBJECT_TYPE_LABEL_PLACEHOLDER;
        String anchorPlaceholder = NotificationConstants.ANCHOR_PLACEHOLDER;

        if ( notification.getTitle() != null && notification.getTitle().contains(objectTypeLabelPlaceholder))
        {
            String updatedTitle = replaceObjectTypeLabel(notification.getTitle(), objectTypeLabelPlaceholder,
                    notification.getParentType());
            notification.setTitle(updatedTitle);
        }

        if ( notification.getNote() != null && notification.getNote().contains(objectTypeLabelPlaceholder))
        {
            String updatedNote = replaceObjectTypeLabel(notification.getNote(), objectTypeLabelPlaceholder,
                    notification.getParentType());
            notification.setNote(updatedNote);
        }

        if ( notification.getNote() != null && notification.getNote().contains(anchorPlaceholder))
        {
            String updatedNote = replaceAnchor(notification.getNote(), anchorPlaceholder, notification.getParentType(),
                    notification.getParentId());
            notification.setNote(updatedNote);
        }


    }

    private String replaceAnchor(String withPlaceholder, String anchorPlaceholder, String parentType, Long parentId)
    {
        String keyBaseUrl = "arkcase.url.base";

        String baseUrl = getNotificationProperties().getProperty(keyBaseUrl);

        // find the object type from the ACM application configuration, and get the URL from the object type
        for ( AcmObjectType objectType : getAcmAppConfiguration().getObjectTypes() )
        {
            if ( objectType.getName().equals(parentType) )
            {
                String objectUrl = objectType.getUrl() + parentId;
                String url = baseUrl + objectUrl;

                String withAnchor = withPlaceholder.replace(anchorPlaceholder, url);
                return withAnchor;
            }
        }

        return withPlaceholder;

    }

    private String replaceObjectTypeLabel(String withPlaceholder, String placeholder, String parentType)
    {
        String keyLabel = parentType + ".label";
        String objectTypeLabel = getNotificationProperties().getProperty(keyLabel);
        String withObjectType = withPlaceholder.replace(placeholder, objectTypeLabel);

        return withObjectType;
    }

    public SpringContextHolder getSpringContextHolder() {
		return springContextHolder;
	}

	public void setSpringContextHolder(SpringContextHolder springContextHolder) {
		this.springContextHolder = springContextHolder;
	}


    public Properties getNotificationProperties()
    {
        return notificationProperties;
    }

    public void setNotificationProperties(Properties notificationProperties)
    {
        this.notificationProperties = notificationProperties;
    }

    public AcmApplication getAcmAppConfiguration()
    {
        return acmAppConfiguration;
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }
}

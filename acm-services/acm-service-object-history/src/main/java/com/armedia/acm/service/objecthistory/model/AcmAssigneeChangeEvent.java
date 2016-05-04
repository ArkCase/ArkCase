/**
 *
 */
package com.armedia.acm.service.objecthistory.model;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class AcmAssigneeChangeEvent extends AcmEvent
{

    private static final long serialVersionUID = -969356637766220472L;

    public static final String EVENT_TYPE = "com.armedia.acm.object.assignee.change";

    public AcmAssigneeChangeEvent(AcmAssignment source, String userId)
    {
        super(source);

        setObjectId(source.getObjectId());
        setObjectType(source.getObjectType());
        setEventDate(new Date());
        setEventType(EVENT_TYPE);
        setUserId(userId);

        Map<String, Object> eventProperties = new HashMap<String, Object>();

        eventProperties.put("objectTitle", source.getObjectTitle());
        eventProperties.put("objectName", source.getObjectName());
        eventProperties.put("newAssignee", source.getNewAssignee());
        eventProperties.put("oldAssignee", source.getOldAssignee());

        setEventProperties(eventProperties);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}

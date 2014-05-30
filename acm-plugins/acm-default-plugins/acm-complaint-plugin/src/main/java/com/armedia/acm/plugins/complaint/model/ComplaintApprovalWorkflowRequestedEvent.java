package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.event.AcmEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 5/30/14.
 */
public class ComplaintApprovalWorkflowRequestedEvent extends AcmEvent
{

    public ComplaintApprovalWorkflowRequestedEvent(Complaint source)
    {
        super(source);

        setEventType("com.armedia.acm.complaint.submitted-for-review");
        setObjectId(source.getComplaintId());
        setEventDate(new Date());
        setObjectType("COMPLAINT");

        Map<String, Object> props = new HashMap<>();
        props.put("approvers", source.getApprovers());
        props.put("complaintNumber", source.getComplaintNumber());
        setEventProperties(props);
    }
}

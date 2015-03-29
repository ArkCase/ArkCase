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

    private static final long serialVersionUID = 3245444955915156438L;

    public ComplaintApprovalWorkflowRequestedEvent(Complaint source)
    {
        super(source);

        setEventType("com.armedia.acm.complaint.submittedForReview");
        setObjectId(source.getComplaintId());
        setEventDate(new Date());
        setObjectType("COMPLAINT");

        Map<String, Object> props = new HashMap<>();
        props.put("approvers", source.getApprovers());
        props.put("complaintNumber", source.getComplaintNumber());
        props.put("complaintTitle", source.getComplaintTitle());
        setEventProperties(props);

        setSucceeded(true);
    }
}

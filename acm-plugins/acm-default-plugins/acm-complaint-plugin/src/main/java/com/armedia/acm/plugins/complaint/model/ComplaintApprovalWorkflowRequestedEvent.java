package com.armedia.acm.plugins.complaint.model;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.core.model.AcmEvent;

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

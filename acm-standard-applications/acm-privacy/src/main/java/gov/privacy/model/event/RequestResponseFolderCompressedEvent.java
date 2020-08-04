package gov.privacy.model.event;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

import gov.privacy.model.SubjectAccessRequest;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 */
public class RequestResponseFolderCompressedEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.casefile.response.folder.compressed";

    public RequestResponseFolderCompressedEvent(Object source, String ipAddress)
    {
        super(source);

        if (source instanceof SubjectAccessRequest)
        {
            setBaseProperties(source, ipAddress);
            setEventDescription("In Full");
        }
    }

    public RequestResponseFolderCompressedEvent(Object source, int pageCount, String ipAddress)
    {
        super(source);

        if (source instanceof SubjectAccessRequest)
        {
            setBaseProperties(source, ipAddress);
            setEventDescription("Limited to " + pageCount + " pages");
        }
    }

    private void setBaseProperties(Object source, String ipAddress)
    {
        SubjectAccessRequest subjectAccessRequest = (SubjectAccessRequest) source;

        setObjectId(subjectAccessRequest.getId());
        setObjectType(subjectAccessRequest.getObjectType());
        setUserId(AuthenticationUtils.getUsername());
        setIpAddress(AuthenticationUtils.getUserIpAddress());
        setEventType(EVENT_TYPE);
        setEventDate(new Date());
        setSucceeded(true);
        setIpAddress(ipAddress);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}

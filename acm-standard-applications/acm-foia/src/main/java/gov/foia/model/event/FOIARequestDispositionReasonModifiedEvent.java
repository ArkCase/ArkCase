package gov.foia.model.event;

/*-
 * #%L
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

import gov.foia.model.DispositionReason;

public class FOIARequestDispositionReasonModifiedEvent extends AcmEvent
{

    private static final long serialVersionUID = 2601901328541042900L;
    private static final String EVENT_TYPE = "com.armedia.acm.casefile.disposition.reason";
    private static final String OBJECT_TYPE = "DISPOSITION_REASON";

    public FOIARequestDispositionReasonModifiedEvent(DispositionReason source)
    {
        super(source);
        setObjectType(OBJECT_TYPE);
        setObjectId(source.getId());
        setEventDate(new Date());
        setUserId(source.getModifier());
    }

    @Override
    public String getEventType()
    {
        return String.format("%s", EVENT_TYPE);
    }
}

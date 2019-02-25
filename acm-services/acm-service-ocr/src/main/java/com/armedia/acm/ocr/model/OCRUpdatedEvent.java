package com.armedia.acm.ocr.model;

/*-
 * #%L
 * ACM Service: OCR
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

import java.util.Date;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRUpdatedEvent extends OCREvent
{
    public OCRUpdatedEvent(OCR source)
    {
        super(source);
        setEventDate(new Date());
        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setParentObjectId(source.getEcmFileVersion().getId());
        setParentObjectType(source.getEcmFileVersion().getObjectType());
        setParentObjectName(source.getEcmFileVersion().getFile().getFileName());
    }

    @Override
    public String getEventType()
    {
        return OCRConstants.OCR_UPDATED_EVENT;
    }
}

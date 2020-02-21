package com.armedia.acm.services.users.model;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.objectonverter.DateFormats;

/**
 * Created by manoj.dhungana on 7/21/2015.
 */
public interface AcmUsersConstants
{
    String SOLR_DATE_FORMAT = DateFormats.DEFAULT_DATE_FORMAT;

    /**
     * Date format for date-only fields, where the UI does not send a time component, but only the date.
     */
    String ISO_DATE_FORMAT = "yyyy-MM-dd";

    int USER_ID_MIN_CHAR_LENGTH = 3;

    String OCR_SYSTEM_USER = "OCR_SERVICE";

    String TRANSCRIBE_SYSTEM_USER = "TRANSCRIBE_SERVICE";

}

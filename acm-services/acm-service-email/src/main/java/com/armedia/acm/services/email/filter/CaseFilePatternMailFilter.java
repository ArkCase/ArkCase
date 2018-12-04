package com.armedia.acm.services.email.filter;

/*-
 * #%L
 * ACM Service: Email
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaseFilePatternMailFilter extends AcmObjectPatternMailFilter
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String objectTypeRegexPattern;

    public CaseFilePatternMailFilter(String objectIdRegexPattern, String objectTypeRegexPattern)
    {

        super(objectIdRegexPattern, objectTypeRegexPattern);
        this.objectTypeRegexPattern = objectTypeRegexPattern;

    }

    @Override
    public boolean accept(Message message) throws MessagingException, IOException
    {

        boolean matchesFilter = false;
        Pattern pattern = Pattern.compile(String.format("%s", objectTypeRegexPattern));

        String subject = message.getSubject();
        if (!StringUtils.isEmpty(subject))
        {
            Matcher matcher = pattern.matcher(subject);
            matchesFilter = matcher.find();

            log.debug("Message with subject '{}' matches required pattern: '{}'", message.getSubject(), matchesFilter);
        }

        return matchesFilter;

    }
}

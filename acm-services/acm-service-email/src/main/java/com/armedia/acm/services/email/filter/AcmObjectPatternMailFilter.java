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


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.util.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Acm Object Pattern based mail filter Filters mail related to acm object by detecting title pattern
 * 
 * @author dame.gjorgjievski
 *
 */
public class AcmObjectPatternMailFilter
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private final String objectIdRegexPattern;
    private final String objectTypeRegexPattern;

    public AcmObjectPatternMailFilter(String objectIdRegexPattern, String objectTypeRegexPattern)
    {
        this.objectIdRegexPattern = objectIdRegexPattern;
        this.objectTypeRegexPattern = objectTypeRegexPattern;
    }

    /**
     * Match pattern against email content to be passed as filter output
     * 
     * @param message
     * @return
     * @throws MessagingException
     */
    public boolean accept(Message message) throws MessagingException, IOException
    {
        boolean matchesFilter = false;
        Pattern pattern = Pattern.compile(String.format("%s", objectIdRegexPattern));

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

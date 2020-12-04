package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;

import java.util.Optional;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on October, 2020
 */
public class DefaultOriginalEmailExtractor implements OriginalEmailExtractorStrategy
{
    private transient final Logger log = LogManager.getLogger(getClass());

    /**
     *
     * Default fallback strategy, returns the original message
     *
     * @param message
     *            Email message
     * @return Original or forwarded email
     */
    @Override
    public Optional<Message> getForwardedMessage(Message message)
    {
        log.info("No forwarded email found, the sent mail will be used.");
        return Optional.of(message);
    }
}

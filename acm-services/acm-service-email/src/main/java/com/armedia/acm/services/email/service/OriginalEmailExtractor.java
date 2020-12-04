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

import java.util.List;
import java.util.Optional;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on October, 2020
 */
public class OriginalEmailExtractor
{
    private transient final Logger log = LogManager.getLogger(getClass());

    /**
     * List of strategies that will be executed, reorder the list to change the priority
     */
    private List<OriginalEmailExtractorStrategy> originalEmailExtractorStrategies;

    /**
     * 
     * Tries extracting the forwarded email from the original email using different extraction strategies. If no
     * forwarded message is found or the currently implemented strategies cannot successfully retrieve the forwarded
     * email, the original email will be returned
     * 
     * @param message
     *            Email message
     * @return Original or forwarded email
     */
    public Message extractMessage(Message message)
    {
        for (OriginalEmailExtractorStrategy originalEmailExtractor : originalEmailExtractorStrategies)
        {
            Optional<Message> originalMessage = originalEmailExtractor.getForwardedMessage(message);

            if (originalMessage.isPresent())
            {
                log.info("The forwarded email successfully retrieved using the [{}] strategy",
                        originalEmailExtractor.getClass().getSimpleName());
                return originalMessage.get();
            }
            else
            {
                log.info("The forwarded email couldn't be retrieved using the [{}] strategy",
                        originalEmailExtractor.getClass().getSimpleName());
            }
        }
        log.info("A forwarded email couldn't be retrieved from the message");
        return message;
    }

    public List<OriginalEmailExtractorStrategy> getOriginalEmailExtractorStrategies()
    {
        return originalEmailExtractorStrategies;
    }

    public void setOriginalEmailExtractorStrategies(List<OriginalEmailExtractorStrategy> originalEmailExtractorStrategies)
    {
        this.originalEmailExtractorStrategies = originalEmailExtractorStrategies;
    }
}

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

import com.armedia.acm.service.MimeMessageParser;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on October, 2020
 */
public class EmailBodyContentEmailExtractor implements OriginalEmailExtractorStrategy
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private static final Pattern emailRegexPattern = Pattern.compile("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$)");

    public static final String FROM_IDENTIFIER = "From:";
    public static final String SUBJECT_IDENTIFIER = "Subject:";

    /**
     *
     * Changes the origin, subject and text content of the original message if the information is found as part of the
     * inner body contents of the email. This is a simple implementation which doesn't take into account all possible
     * email clients and languages, therefore the end result might not be perfect. Using the send as attachment method
     * is more accurate as it will retain all original data. Custom implementations of the body parsing for different
     * clients are advised for best results if the send as attachment method is not used.
     *
     * @param message
     *            Email message
     * @return Email with changed metadata or empty optional
     */
    @Override
    public Optional<Message> getForwardedMessage(Message message)
    {
        try
        {
            String formattedStringContent = MimeMessageParser.getFormattedStringContent(message);
            String unescapedStringContent = StringEscapeUtils.unescapeHtml(formattedStringContent);

            String[] lines = unescapedStringContent.split("[\r\n]+");

            int currentLine;

            for (currentLine = 0; currentLine < lines.length; currentLine++)
            {
                String line = lines[currentLine];
                String originalEmailAddress = getOriginalEmailAddress(line);
                String originalSubject = getOriginalSubject(line);

                if (areForwardingParametersFound(originalSubject, originalEmailAddress))
                {
                    String originalTextContent = getRemainingTextContent(lines, currentLine);

                    Message updatedMessage = getUpdatedMessage(message, originalSubject, originalEmailAddress, originalTextContent);

                    return Optional.of(updatedMessage);
                }
            }
        }
        catch (MessagingException | IOException e)
        {
            log.error("The original email couldn't be extracted from the body content!");
        }

        return Optional.empty();
    }

    private String getRemainingTextContent(String[] lines, int currentLine)
    {
        List<String> originalMessageLines = Arrays.stream(lines).skip(currentLine).collect(Collectors.toList());
        return String.join("\r\n", originalMessageLines);
    }

    private String getOriginalSubject(String line)
    {
        String originalSubject = "";
        if (line.contains(SUBJECT_IDENTIFIER))
        {
            int indexOfContent = line.indexOf(SUBJECT_IDENTIFIER) + SUBJECT_IDENTIFIER.length();
            originalSubject = line.substring(indexOfContent).trim();
        }
        return originalSubject;
    }

    private String getOriginalEmailAddress(String line)
    {
        String originalEmailAddress = "";
        if (line.contains(FROM_IDENTIFIER))
        {
            int indexOfContent = line.indexOf(FROM_IDENTIFIER) + FROM_IDENTIFIER.length();
            String fromAddress = line.substring(indexOfContent).trim();

            Matcher match = emailRegexPattern.matcher(fromAddress);

            if (match.find())
            {
                originalEmailAddress = match.group();
            }
        }
        return originalEmailAddress;
    }

    private boolean areForwardingParametersFound(String originalSubject, String originalEmailAddress)
    {
        return StringUtils.isNotBlank(originalEmailAddress) && StringUtils.isNotBlank(originalSubject);
    }

    private Message getUpdatedMessage(Message message, String originalSubject, String originalEmailAddress, String originalTextContent)
            throws IOException, MessagingException
    {
        Message updatedMessage = (Message) MimeMessageParser.setBodyPart(message, originalTextContent);

        InternetAddress internetAddress = new InternetAddress(originalEmailAddress);
        updatedMessage.setFrom(internetAddress);
        updatedMessage.setSubject(originalSubject);
        return updatedMessage;
    }
}

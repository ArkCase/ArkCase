package com.armedia.arkcase.email.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Entity Pattern based mail filter
 * 
 * @author dame.gjorgjievski
 *
 */
public class AcmEntityPatternMailFilter
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private final String entityIdRegexPattern;
    private final String entityTypeRegexPattern;

    public AcmEntityPatternMailFilter(String entityIdRegexPattern, String entityTypeRegexPattern)
    {
        this.entityIdRegexPattern = entityIdRegexPattern;
        this.entityTypeRegexPattern = entityTypeRegexPattern;
    }

    /**
     * Match pattern against email content to be passed as filter output
     * 
     * @param message
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean accept(Message message) throws MessagingException, IOException
    {
        boolean matchesFilter = false;
        Pattern pattern = Pattern.compile(String.format("%s %s", entityTypeRegexPattern, entityIdRegexPattern));

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

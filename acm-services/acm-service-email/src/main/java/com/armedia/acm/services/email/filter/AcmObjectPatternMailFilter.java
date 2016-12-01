package com.armedia.acm.services.email.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private transient final Logger log = LoggerFactory.getLogger(getClass());

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
     * @throws IOException
     */
    public boolean accept(Message message) throws MessagingException, IOException
    {
        boolean matchesFilter = false;
        Pattern pattern = Pattern.compile(String.format("%s %s", objectTypeRegexPattern, objectIdRegexPattern));

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

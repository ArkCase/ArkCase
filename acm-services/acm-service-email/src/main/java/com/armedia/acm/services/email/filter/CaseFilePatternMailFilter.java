package com.armedia.acm.services.email.filter;

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

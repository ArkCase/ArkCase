package com.armedia.acm.plugins.complaint.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MessageSelector implementation
 */

public class ComplaintMailFilter
{
    private String complaintNumberRegexPattern;
    private String complaintObjectTypeRegexPattern;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    // if true the message will be passed along to the output-channel
    public boolean accept(Message message) throws MessagingException
    {

        String subject = message.getSubject();
        if (StringUtils.isEmpty(subject))
        {
            return false;
        }

        Pattern pattern = Pattern.compile(String.format("%s %s", complaintObjectTypeRegexPattern, complaintNumberRegexPattern));
        Matcher matcher = pattern.matcher(subject);

        boolean matchesComplaintFilter = matcher.find();

        log.debug("Message with subject '{}' matches a case number: '{}'", message.getSubject(), matchesComplaintFilter);

        return matchesComplaintFilter;
    }

    public void setComplaintNumberRegexPattern(String caseNumberRegexPattern)
    {
        this.complaintNumberRegexPattern = caseNumberRegexPattern;
    }

    public void setComplaintObjectTypeRegexPattern(String complaintObjectTypeRegexPattern)
    {
        this.complaintObjectTypeRegexPattern = complaintObjectTypeRegexPattern;
    }
}

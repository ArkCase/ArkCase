package com.armedia.acm.plugins.casefile.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nebojsha on 25.06.2015.
 */
public class CaseFileMailFilter {
    private String caseNumberRegexPattern;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public boolean accept(Message message) throws MessagingException {

        String subject = message.getSubject();
        if (subject == null || subject.length() < 1)
            return false;
        Pattern pattern = Pattern.compile(caseNumberRegexPattern);
        Matcher matcher = pattern.matcher(subject);

        boolean matchesCaseFilter = matcher.find();

        if ( log.isDebugEnabled())
        {
            log.debug("Message with subject '{}' matches a case number: {}", message.getSubject(), matchesCaseFilter);
        }

        return matchesCaseFilter;
    }

    public void setCaseNumberRegexPattern(String caseNumberRegexPattern) {
        this.caseNumberRegexPattern = caseNumberRegexPattern;
    }
}

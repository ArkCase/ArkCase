package com.armedia.acm.plugins.casefile.filter;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nebojsha on 25.06.2015.
 */
public class CaseFileMailFilter {
    private String caseNumberRegexPattern;

    public boolean accept(Message message) throws MessagingException {
        String subject = message.getSubject();
        if (subject == null || subject.length() < 1)
            return false;
        Pattern pattern = Pattern.compile(caseNumberRegexPattern);
        Matcher matcher = pattern.matcher(subject);
        return matcher.find();
    }

    public void setCaseNumberRegexPattern(String caseNumberRegexPattern) {
        this.caseNumberRegexPattern = caseNumberRegexPattern;
    }
}

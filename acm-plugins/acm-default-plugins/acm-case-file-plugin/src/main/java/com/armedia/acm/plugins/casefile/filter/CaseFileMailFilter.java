package com.armedia.acm.plugins.casefile.filter;

import com.armedia.acm.plugins.casefile.model.CaseFileConstants;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nebojsha on 25.06.2015.
 */
public class CaseFileMailFilter {

    public boolean accept(Message message) throws MessagingException {
        String subject = message.getSubject();
        if (subject == null || subject.length() < 1)
            return false;
        Pattern pattern = Pattern.compile(CaseFileConstants.CASE_FILE_NUMBER_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(subject);
        return matcher.find();
    }
}

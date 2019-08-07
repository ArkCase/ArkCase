package com.armedia.acm.audit.model;

import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author ivana.shekerova on 8/7/2019.
 */
public class AuditPatternsConfig implements InitializingBean
{
    @Value("${audit.patterns}")
    private String auditPatterns;

    @Value("${audit.substitution}")
    private String auditSubstitution;

    private JSONUnmarshaller jsonUnmarshaller;

    private List<Pattern> patternsList = new ArrayList<>();

    public String getAuditPatterns()
    {
        return auditPatterns;
    }

    public void setAuditPatterns(String auditPatterns)
    {
        this.auditPatterns = auditPatterns;
    }

    public String getAuditSubstitution()
    {
        return auditSubstitution;
    }

    public void setAuditSubstitution(String auditSubstitution)
    {
        this.auditSubstitution = auditSubstitution;
    }

    public List<Pattern> getPatternsList()
    {
        return patternsList;
    }

    public void setPatternsList(List<Pattern> patternsList)
    {
        this.patternsList = patternsList;
    }

    @Override
    public void afterPropertiesSet()
    {

        patternsList = jsonUnmarshaller.unmarshall(auditPatterns, List.class);
        // .map(line -> Pattern.compile(line.toString())).collect(Collectors.toList());
    }

    @JsonIgnore
    public JSONUnmarshaller getJsonUnmarshaller()
    {
        return jsonUnmarshaller;
    }

    public void setJsonUnmarshaller(JSONUnmarshaller jsonUnmarshaller)
    {
        this.jsonUnmarshaller = jsonUnmarshaller;
    }
}

package com.armedia.acm.audit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds static patterns loaded from file '$user.home/.arkcase/acm/auditPatterns.properties'. The substitution string is
 * default
 * "$1*****$3".
 * <p>
 * Created by Bojan Milenkoski on 30.5.2016
 */
public class AuditPatternsSubstitution
{
    private final static Logger LOG = LoggerFactory.getLogger(AuditPatternsSubstitution.class);

    private static List<Pattern> PATTERNS = new ArrayList<>();
    private static String SUBSTITUTION = "$1*****$3";
    private static String PATTERNS_FILENAME = System.getProperty("user.home") + "/.arkcase/acm/auditPatterns.properties";

    static
    {
        try (Stream<String> stream = Files.lines(Paths.get(PATTERNS_FILENAME)))
        {
            PATTERNS = stream.filter(line -> line.trim().length() > 0 && !line.startsWith("#"))
                    .map(line -> Pattern.compile(line.toString())).collect(Collectors.toList());
        }
        catch (IOException e)
        {
            LOG.error("Exception reading patterns from file: " + PATTERNS_FILENAME, e);
        }
    }

    public static List<Pattern> getPatterns()
    {
        return PATTERNS;
    }

    public static String getSubstitution()
    {
        return SUBSTITUTION;
    }
}

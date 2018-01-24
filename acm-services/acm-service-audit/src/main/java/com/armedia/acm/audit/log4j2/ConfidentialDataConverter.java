package com.armedia.acm.audit.log4j2;

import com.armedia.acm.audit.service.AuditPatternsSubstitution;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class is used to replace matches in the AuditEvents and other Strings with a substitution String. The text is
 * matched against the
 * patterns set in this class. The patterns are initially loaded statically from
 * "$user.home/.arkcase/acm/auditPatterns.properties" file.
 * This class is static and not used as a Spring bean because it is used in log4j logging.
 * <p>
 * Created by Bojan Milenkoski on 26.5.2016
 */
@Plugin(name = "confidential", category = PatternConverter.CATEGORY)
@ConverterKeys({ "confidential" })
public class ConfidentialDataConverter extends LogEventPatternConverter
{

    private List<Pattern> patterns;
    private String substitution;
    private List<PatternFormatter> formatters;

    private ConfidentialDataConverter(final List<Pattern> patterns, final String substitution, final List<PatternFormatter> formatters)
    {
        super("confidential", "confidential");
        this.patterns = patterns;
        this.substitution = substitution;
        this.formatters = formatters;
    }

    public static ConfidentialDataConverter newInstance(final Configuration config, final String[] options)
    {
        if (options.length != 1)
        {
            LOGGER.error("Incorrect number of options on replace. Expected 1 received " + options.length);
            return null;
        }

        final PatternParser parser = PatternLayout.createPatternParser(config);
        final List<PatternFormatter> formatters = parser.parse(options[0]);

        return new ConfidentialDataConverter(AuditPatternsSubstitution.getPatterns(), AuditPatternsSubstitution.getSubstitution(),
                formatters);
    }

    public static ConfidentialDataConverter newInstanceWithoutFormatters()
    {
        return new ConfidentialDataConverter(AuditPatternsSubstitution.getPatterns(), AuditPatternsSubstitution.getSubstitution(),
                new ArrayList<>());
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo)
    {
        final StringBuilder buf = new StringBuilder();
        for (final PatternFormatter formatter : formatters)
        {
            formatter.format(event, buf);
        }

        String confidentialOutput = convert(buf.toString());

        toAppendTo.append(confidentialOutput);
    }

    /**
     * Replaces matches in the text with a substitution String. The text is matched against the patterns set in this
     * class.
     *
     * @param text
     *            the text to convert
     * @return the converted text
     */
    public String convert(final String text)
    {
        String confidentialOutput = text;
        for (Pattern pattern : patterns)
        {
            confidentialOutput = pattern.matcher(confidentialOutput).replaceAll(substitution);
        }

        return confidentialOutput;
    }

    public List<Pattern> getPatterns()
    {
        return patterns;
    }

    public void setPatterns(List<Pattern> patterns)
    {
        this.patterns = patterns;
    }

    public String getSubstitution()
    {
        return substitution;
    }

    public void setSubstitution(String substitution)
    {
        this.substitution = substitution;
    }
}

package com.armedia.acm.audit.log4j2;

/*-
 * #%L
 * ACM Service: Audit Library
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

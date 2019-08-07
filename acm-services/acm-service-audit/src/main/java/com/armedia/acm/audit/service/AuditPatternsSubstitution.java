// package com.armedia.acm.audit.service;
//
/// *-
// * #%L
// * ACM Service: Audit Library
// * %%
// * Copyright (C) 2014 - 2018 ArkCase LLC
// * %%
// * This file is part of the ArkCase software.
// *
// * If the software was purchased under a paid ArkCase license, the terms of
// * the paid license agreement will prevail. Otherwise, the software is
// * provided under the following open source license terms:
// *
// * ArkCase is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * ArkCase is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
// * #L%
// */
//
// import com.armedia.acm.audit.model.AuditPatternsConfig;
//
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
//
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.regex.Pattern;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;
//
/// **
// * Holds static patterns loaded from file '$user.home/.arkcase/acm/auditPatterns.properties'. The substitution string
// is
// * default
// * "$1*****$3".
// * <p>
// * Created by Bojan Milenkoski on 30.5.2016
// */
// public class AuditPatternsSubstitution
// {
// private final static Logger LOG = LogManager.getLogger(AuditPatternsSubstitution.class);
//
// private static List<Pattern> PATTERNS = new ArrayList<>();
// private static String SUBSTITUTION = "$1*****$3";
// // private static String PATTERNS_FILENAME = System.getProperty("user.home") +
// // "/.arkcase/acm/auditPatterns.properties";
// private AuditPatternsConfig auditPatternsConfig;
//
// static
// {
//// try (Stream<String> stream = Files.lines(Paths.get(PATTERNS_FILENAME)))
//// {
//// PATTERNS = stream.filter(line -> line.trim().length() > 0 && !line.startsWith("#"))
//// .map(line -> Pattern.compile(line.toString())).collect(Collectors.toList());
//// }
//// catch (IOException e)
//// {
//// LOG.error("Exception reading patterns from file: " + PATTERNS_FILENAME, e);
//// }
//// PATTERNS = auditPatternsConfig.getAuditPatterns();
// }
//
// public static List<Pattern> getPatterns()
// {
// return PATTERNS;
// }
//
// public static String getSubstitution()
// {
// return SUBSTITUTION;
// }
//
// public AuditPatternsConfig getAuditPatternsConfig()
// {
// return auditPatternsConfig;
// }
//
// public void setAuditPatternsConfig(AuditPatternsConfig auditPatternsConfig)
// {
// this.auditPatternsConfig = auditPatternsConfig;
// }
// }

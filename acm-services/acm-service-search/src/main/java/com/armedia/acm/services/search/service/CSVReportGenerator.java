package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.services.search.model.ReportGenerator;
import com.armedia.acm.services.search.model.SearchConstants;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate csv report
 */
public class CSVReportGenerator extends ReportGenerator
{
    private static final String LF = "\n";
    private static final String CR = "\r";
    private static final String ENCLOSE_FORMATTER = "\"%s\"";
    private static final String REPLACE_QUOTES_PATTERN = "\"";
    private static final String REPLACEMENT_FOR_QUOTES_PATTERN = "\"\"";
    private static final String QUOTES_CONSTANT = "\"";
    private static final String OBJECT_TYPE_CASE_FILE = "CASE_FILE";

    /**
     * ISO 8601 Date/Time pattern used by Solr (yyyy-MM-ddTHH:mm:ssZ).
     */
    private static final String ISO8601_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$";

    /**
     * Formatter for parsing Solr *_tdt fields.
     */
    private static final DateTimeFormatter SOLR_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * Formatter for formatting dates and times so Excel recognizes them.
     */
    private static final DateTimeFormatter EXCEL_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("M-dd-yy hh:mm a");

    private transient final Logger log = LogManager.getLogger(getClass());

    @Override
    public String generateReport(String[] requestedFields, String[] titles, String jsonData, int timeZoneOffsetinMinutes)
    {
        JSONObject jsonResult = new JSONObject(jsonData);
        JSONObject jsonResponse = jsonResult.getJSONObject("response");
        JSONArray jsonDocs = jsonResponse.getJSONArray("docs");

        StringBuilder sb = new StringBuilder();

        List<String> headers = new ArrayList<>();
        for (String title : titles)
        {
            headers.add(purifyForCSV(title));
        }

        String headersLine = String.join(SearchConstants.SEPARATOR_COMMA, headers);
        sb.append(headersLine);
        sb.append("\n");

        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = new DecimalFormat("###,###.###");

        for (int i = 0; i < jsonDocs.length(); i++)
        {
            JSONObject data = jsonDocs.getJSONObject(i);

            for (String field : requestedFields)
            {

                if (data.has(field))
                {
                    Object value = data.get(field);

                    if (value instanceof String)
                    {
                        String stringValue = data.getString(field);
                        // if related_object_number existed, use related_object_number instead of parent number. For AFDP-5767
                        if (field.equals("parent_number_lcs") && data.has("related_object_number_s"))
                        {
                            value = data.get("related_object_number_s");
                            if (value instanceof String)
                            {
                                stringValue = data.getString("related_object_number_s");
                            }
                        }

                        if (field.equals("object_type_s") && stringValue.equals(OBJECT_TYPE_CASE_FILE) && data.has("object_sub_type_s"))
                        {
                            stringValue = data.getString("object_sub_type_s");
                        }

                        // check if this is Solr Date/Time field in expected format
                        if (field.endsWith("_tdt") && stringValue.matches(ISO8601_PATTERN))
                        {
                            // transform into Excel-recognizable format
                            try
                            {
                                LocalDateTime localDateTime = LocalDateTime.parse(stringValue, SOLR_DATE_TIME_PATTERN);
                                stringValue = timeZoneAdjust(localDateTime, timeZoneOffsetinMinutes);
                            }
                            catch (DateTimeException e)
                            {
                                log.warn("[{}] cannot be parsed as Solr date/time value, exporting as it is", stringValue);
                            }
                        }
                        sb.append(purifyForCSV(stringValue));
                    }
                    else if (value instanceof Integer || value instanceof Long)
                    {
                        String formattedNumber = nf.format(data.getLong(field));
                        sb.append(String.format(purifyForCSV(formattedNumber)));
                    }
                    else if (value instanceof Double || value instanceof Float)
                    {
                        String formattedNumber = df.format(data.getDouble(field));
                        sb.append(purifyForCSV(formattedNumber));
                    }
                    else if (value instanceof Boolean)
                    {
                        sb.append(purifyForCSV(Boolean.toString(data.getBoolean(field))));
                    }
                    else if (value instanceof JSONArray)
                    {
                        JSONArray jsonArray = data.getJSONArray(field);
                        sb.append(purifyForCSV(jsonArray.toString()));
                    }
                }
                sb.append(SearchConstants.SEPARATOR_COMMA);
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getReportContentType()
    {
        return "text/csv;charset=UTF-8";
    }

    @Override
    public String generateReportName(String name)
    {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return String.format("%s-%s.csv", name, formatter.format(new Date()));
    }

    /**
     * Override original generateReport, return a UTC Time value. Add for AFDP-5769
     */
    @Override
    public String generateReport(String[] requestedFields, String[] titles, String jsonData)
    {
        return generateReport(requestedFields, titles, jsonData, 0);
    }

    /**
     * Encloses new lines or value if contains SEPARATOR or if value contains ".
     * for more information: https://tools.ietf.org/html/rfc4180
     *
     * @param value actual value
     * @return value with enclosed new lines, separator or ". If null or empty string returns as is
     */
    private String purifyForCSV(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return value;
        }
        boolean shouldEnclose = false;
        // if value contains " should be escaped with another "
        if (value.contains(QUOTES_CONSTANT))
        {
            value = value.replaceAll(REPLACE_QUOTES_PATTERN, REPLACEMENT_FOR_QUOTES_PATTERN);
            shouldEnclose = true;
        }

        // enclose field with "" if contains the separator, LF or CR
        if (value.contains(SearchConstants.SEPARATOR_COMMA) || value.contains(LF) || value.contains(CR) || shouldEnclose)
        {
            // enclose the value
            return String.format(ENCLOSE_FORMATTER, value);
        }
        return value;
    }

    /**
     * Time zone process for AFDP-5769
     *
     * @param localDateTime           service time
     * @param timeZoneOffsetinMinutes timeZone received from client. Should be format like"240" "-480"
     */
    private String timeZoneAdjust(LocalDateTime localDateTime, int timeZoneOffsetinMinutes)
    {
        int adjTimeZone = ~(timeZoneOffsetinMinutes / 60) + 1;
        LocalDateTime adjDateTime = localDateTime.plusHours(adjTimeZone);

        return adjDateTime.format(EXCEL_DATE_TIME_PATTERN);
    }

}

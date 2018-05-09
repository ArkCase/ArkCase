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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final DateTimeFormatter EXCEL_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String generateReport(String[] requestedFields, String[] titles, String jsonData)
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

        String headersLine = headers.stream().collect(Collectors.joining(SearchConstants.SEPARATOR_COMMA));
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
                        // check if this is Solr Date/Time field in expected format
                        if (field.endsWith("_tdt") && stringValue.matches(ISO8601_PATTERN))
                        {
                            // transform into Excel-recognizable format
                            try
                            {
                                LocalDateTime localDateTime = LocalDateTime.parse(stringValue, SOLR_DATE_TIME_PATTERN);
                                stringValue = localDateTime.format(EXCEL_DATE_TIME_PATTERN);
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
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
        return String.format("%s-%s.csv", name, formatter.format(new Date()));
    }

    /**
     * Encloses new lines or value if contains SEPARATOR or if value contains ".
     * for more information: https://tools.ietf.org/html/rfc4180
     *
     * @param value
     *            actual value
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

}

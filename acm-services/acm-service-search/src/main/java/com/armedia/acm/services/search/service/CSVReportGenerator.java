package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.ReportGenerator;
import com.armedia.acm.services.search.model.SearchConstants;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public byte[] generateReport(String[] requestedFields, String jsonData)
    {
        JSONObject jsonResult = new JSONObject(jsonData);
        JSONObject jsonResponse = jsonResult.getJSONObject("response");
        JSONArray jsonDocs = jsonResponse.getJSONArray("docs");

        JSONObject headerFields = findHeaderFields();

        StringBuilder sb = new StringBuilder();

        List<String> headers = new ArrayList<>();
        for (String field : requestedFields)
        {
            if (headerFields.has(field))
            {
                headers.add(purifyForCSV(headerFields.getString(field)));
            } else
            {
                log.warn("Field '{}' not found in searchPlugin.properties", field);
            }
        }

        String headersLine = headers.stream().collect(Collectors.joining(SearchConstants.SEPARATOR_COMMA));
        sb.append(headersLine);
        sb.append("\n");

        for (int i = 0; i < jsonDocs.length(); i++)
        {
            JSONObject data = jsonDocs.getJSONObject(i);
            for (String field : requestedFields)
            {
                if (data.has(field))
                {
                    Object value = data.get(field);

                    if(value instanceof String)
                    {
                        sb.append(purifyForCSV(data.getString(field)));
                    }

                    if(value instanceof Integer){
                        sb.append(purifyForCSV(Integer.toString(data.getInt(field))));
                    }
                }
                sb.append(SearchConstants.SEPARATOR_COMMA);
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
        }
        return sb.toString().getBytes();
    }

    public String getReportContentType()
    {
        return "text/csv";
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
        //if value contains " should be escaped with another "
        if (value.contains(QUOTES_CONSTANT))
        {
            value = value.replaceAll(REPLACE_QUOTES_PATTERN, REPLACEMENT_FOR_QUOTES_PATTERN);
            shouldEnclose = true;
        }

        //enclose field with "" if contains the separator, LF or CR
        if (value.contains(SearchConstants.SEPARATOR_COMMA) || value.contains(LF) || value.contains(CR) || shouldEnclose)
        {
            //enclose the value
            return String.format(ENCLOSE_FORMATTER, value);
        }
        return value;
    }

}

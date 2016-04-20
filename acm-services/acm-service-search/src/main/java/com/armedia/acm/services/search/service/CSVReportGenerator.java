package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.ReportGenerator;
import com.armedia.acm.services.search.model.SearchConstants;
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
                headers.add(headerFields.getString(field));
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
                    sb.append(data.getString(field));
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

}

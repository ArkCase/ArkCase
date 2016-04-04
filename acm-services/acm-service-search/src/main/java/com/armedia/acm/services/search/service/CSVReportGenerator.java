package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.ReportGenerator;
import com.armedia.acm.services.search.model.SearchConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generate csv report
 */
public class CSVReportGenerator extends ReportGenerator
{
    @Override
    public byte[] generateReport(String[] requestedFields, String jsonData)
    {
        if (requestedFields.length == 0)
        {
            requestedFields = new String[]{"Object Number", "Object Type", "Modified"};
        }
        JSONObject jsonResult = new JSONObject(jsonData);
        JSONObject jsonResponse = jsonResult.getJSONObject("response");
        JSONArray jsonDocs = jsonResponse.getJSONArray("docs");

        JSONObject fields = findFields();

        StringBuilder sb = new StringBuilder();
        for (String field : requestedFields)
        {
            sb.append(field);
            sb.append(SearchConstants.SEPARATOR_COMMA);
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");
        for (int i = 0; i < jsonDocs.length(); i++)
        {
            JSONObject data = jsonDocs.getJSONObject(i);
            for (String field : requestedFields)
            {
                String f = fields.getString(field);
                if (data.has(f))
                {
                    sb.append(data.getString(f));
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

package com.armedia.acm.plugins.report.niem;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVToMapReader
{

    public List<Map<String, String>> getDataMapFromCSV(File csvFile) throws IOException
    {
        CSVParser parser = new CSVParser(
                new InputStreamReader(new FileInputStream(csvFile)),
                CSVFormat.DEFAULT.withFirstRecordAsHeader());

        List<Map<String, String>> data = extractDataMapFromCSVRecords(parser.getRecords(), parser.getHeaderNames());

        return data;
    }

    private List<Map<String, String>> extractDataMapFromCSVRecords(List<CSVRecord> records, List<String> headers)
    {
        List<Map<String, String>> data = new ArrayList<>();
        for (CSVRecord record : records)
        {
            Map<String, String> map = new LinkedHashMap<>();

            for (int j = 0; j < record.size(); j++)
            {
                map.put(headers.get(j), record.get(j));
            }
            data.add(map);

        }
        return data;
    }
}

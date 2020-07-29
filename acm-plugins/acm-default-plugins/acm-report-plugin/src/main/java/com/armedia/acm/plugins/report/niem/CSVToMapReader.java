package com.armedia.acm.plugins.report.niem;

/*-
 * #%L
 * ACM Default Plugin: report
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

            for (int j = 0; j < headers.size(); j++)
            {
                map.put(headers.get(j), record.get(j));
            }
            data.add(map);

        }
        return data;
    }
}

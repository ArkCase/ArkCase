package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NiemExportUtils
{

    public static final String DEFAULT_CSV_EXPORT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DEFAULT_XLSX_EXPORT_FORMAT = "MM/dd/yyyy";
    public static final String DEFAULT_NIEM_EXPECTED_FORMAT = "yyyy-MM-dd";

    /**
     * 
     * Converts a CSV file into a list of mapped values
     * 
     * @param csvFile
     *            File to map
     * @return List of mapped values, where the table headers are the keys for each entry
     * @throws IOException
     */
    public static List<Map<String, String>> getDataMapFromCSVFile(File csvFile) throws IOException
    {
        CSVParser parser = new CSVParser(
                new InputStreamReader(new FileInputStream(csvFile)),
                CSVFormat.DEFAULT.withFirstRecordAsHeader());

        return extractDataMapFromCSVRecords(parser.getRecords(), parser.getHeaderNames());
    }

    /**
     *
     * Converts a CSV input stream into a list of mapped values
     * 
     * @param csvInputStream
     *            CSV Input Stream to map
     * @return List of mapped values, where the table headers are the keys for each entry
     * @throws IOException
     */
    public static List<Map<String, String>> getDataMapFromCSVInputStream(InputStream csvInputStream) throws IOException
    {
        CSVParser parser = new CSVParser(
                new InputStreamReader(csvInputStream),
                CSVFormat.DEFAULT.withFirstRecordAsHeader());

        return extractDataMapFromCSVRecords(parser.getRecords(), parser.getHeaderNames());
    }

    private static List<Map<String, String>> extractDataMapFromCSVRecords(List<CSVRecord> records, List<String> headers)
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

    /**
     *
     * Converts a CSV or XLSX generated date string into the NIEM expected date format
     * 
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static String formatDateToNiemExpectedFormat(String dateString) throws ParseException
    {
        SimpleDateFormat csvDateFormat = new SimpleDateFormat(DEFAULT_CSV_EXPORT_FORMAT);
        SimpleDateFormat xlsxDateFormat = new SimpleDateFormat(DEFAULT_XLSX_EXPORT_FORMAT);
        SimpleDateFormat niemDateFormat = new SimpleDateFormat(DEFAULT_NIEM_EXPECTED_FORMAT);

        if (isDateParsable(csvDateFormat, dateString))
        {
            return niemDateFormat.format(csvDateFormat.parse(dateString));
        }
        else if (isDateParsable(xlsxDateFormat, dateString))
        {
            return niemDateFormat.format(xlsxDateFormat.parse(dateString));
        }
        else
        {
            return dateString;
        }

    }

    /**
     *
     * Returns the current date in NIEM expected format
     * 
     * @return String current date in NIEM expected format
     */
    public static String currentDateInNiemFormat()
    {
        SimpleDateFormat niemDateFormat = new SimpleDateFormat(DEFAULT_NIEM_EXPECTED_FORMAT);

        return niemDateFormat.format(new Date());
    }

    private static boolean isDateParsable(SimpleDateFormat dateFormat, String dateString)
    {
        try
        {
            dateFormat.parse(dateString);
            return true;
        }
        catch (ParseException e)
        {
            return false;
        }
    }

}

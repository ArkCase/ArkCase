package com.armedia.acm.plugins.report.niem;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class NiemExportUtils
{

    public final String DEFAULT_CSV_EXPECTED_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public final String DEFAULT_NIEM_EXPECTED_FORMAT = "yyyy-MM-dd";

    public String formatDateToNiemExpectedDate(String dateOfReceipt) throws ParseException
    {
        SimpleDateFormat csvDateFormat = new SimpleDateFormat(DEFAULT_CSV_EXPECTED_FORMAT);
        SimpleDateFormat niemDateFormat = new SimpleDateFormat(DEFAULT_NIEM_EXPECTED_FORMAT);

        return niemDateFormat.format(csvDateFormat.parse(dateOfReceipt));
    }

}

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

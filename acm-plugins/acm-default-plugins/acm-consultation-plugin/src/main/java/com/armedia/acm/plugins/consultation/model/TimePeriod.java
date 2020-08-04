package com.armedia.acm.plugins.consultation.model;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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

public enum TimePeriod
{

    ONE_YEAR(365, "one year"), SEVEN_DAYS(7, "seven days"), THIRTY_DAYS(30, "thirty days");

    private int numOfDays;
    private String nDays;

    TimePeriod(int numOfDays, String days)
    {
        this.numOfDays = numOfDays;
        this.nDays = days;
    }

    public static TimePeriod getNumberOfDays(int days)
    {
        for (TimePeriod attribute : values())
        {
            if (attribute.getNumOfDays() == days)
            {
                return attribute;
            }
        }
        return null;
    }

    public int getNumOfDays()
    {
        return numOfDays;
    }

    public String getnDays()
    {
        return nDays;
    }

}

package com.armedia.acm.plugins.task.model;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

/**
 * Created by marjan.stefanoski on 8/29/2014.
 */
public enum NumberOfDays
{
    ONE_DAY(1, "one day"), SEVEN_DAYS(7, "seven days"), THIRTY_DAYS(30, "thirty days"), ZERO(0, "zerro days");

    private int numOfDays;
    private String nDays;
    NumberOfDays(int numOfDays, String days)
    {
        this.numOfDays = numOfDays;
        this.nDays = days;
    }

    public static NumberOfDays getNumberOfDays(int days)
    {
        for (NumberOfDays attribute : values())
        {
            if (attribute.getNumOfDays() == days)
            {
                return attribute;
            }
        }
        return NumberOfDays.ZERO;
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
